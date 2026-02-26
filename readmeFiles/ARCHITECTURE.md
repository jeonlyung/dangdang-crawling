# 아키텍처 가이드

## 목차

1. [개요](#개요)
2. [계층 구조](#계층-구조)
3. [주요 컴포넌트](#주요-컴포넌트)
4. [데이터 흐름](#데이터-흐름)
5. [설계 패턴](#설계-패턴)
6. [의존성 주입](#의존성-주입)
7. [에러 처리](#에러-처리)

---

## 개요

DangDang Crawling Server는 **계층형 아키텍처(Layered Architecture)** 패턴을 따릅니다.

```
┌─────────────────────────────────┐
│     Presentation Layer          │
│     (Controller / REST API)     │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│     Business Logic Layer        │
│     (Service / Business Rules)  │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│     Data Access Layer           │
│     (Mapper / MyBatis)          │
└────────────┬────────────────────┘
             │
┌────────────▼────────────────────┐
│     Database Layer              │
│     (MySQL)                     │
└─────────────────────────────────┘
```

---

## 계층 구조

### 1. Presentation Layer (표현 계층)

**역할**: HTTP 요청/응답 처리

**위치**: `biz/controller/`

**주요 클래스**:
- `CrawlingController`: RESTful API 엔드포인트 제공

**책임**:
- HTTP 요청 수신
- DTO 유효성 검증
- Service 호출
- HTTP 응답 반환

```java
@RestController
@RequestMapping("/api/crawling")
public class CrawlingController {
    private final CrawlingJobService crawlingJobService;
    
    @GetMapping("/jobs")
    public ResponseEntity<List<CrawlingJobDto>> getAllJobs() {
        return ResponseEntity.ok(crawlingJobService.getAllJobs());
    }
}
```

---

### 2. Business Logic Layer (비즈니스 로직 계층)

**역할**: 핵심 비즈니스 로직 구현

**위치**: `biz/service/`

**주요 클래스**:
- `CrawlingJobService`: 크롤링 작업 관리 인터페이스
- `CrawlingJobServiceImpl`: 크롤링 작업 관리 구현
- `CrawlingResultService`: 크롤링 결과 관리 인터페이스
- `CrawlingResultServiceImpl`: 크롤링 결과 관리 구현
- `JsoupCrawlerService`: Jsoup 기반 크롤링 로직
- `SeleniumCrawlerService`: Selenium 기반 크롤링 로직
- `CrawlingScheduler`: Quartz 스케줄링 로직

**책임**:
- DTO ↔ Entity 변환
- 비즈니스 로직 구현
- Mapper 호출
- 트랜잭션 관리
- 예외 처리

```java
@Service
public class CrawlingJobServiceImpl implements CrawlingJobService {
    private final CrawlingJobMapper crawlingJobMapper;
    
    @Override
    public CrawlingJobDto getJobById(Long jobId) {
        CrawlingJob job = crawlingJobMapper.selectById(jobId);
        return CrawlingJobDto.fromEntity(job);
    }
}
```

---

### 3. Data Access Layer (데이터 접근 계층)

**역할**: 데이터베이스 접근 추상화

**위치**: `biz/mapper/`

**주요 클래스**:
- `CrawlingJobMapper`: 크롤링 작업 MyBatis Mapper
- `CrawlingResultMapper`: 크롤링 결과 MyBatis Mapper

**책임**:
- SQL 실행
- 데이터 조회 및 수정
- Entity 객체 매핑

```java
@Mapper
public interface CrawlingJobMapper {
    List<CrawlingJob> selectAll();
    CrawlingJob selectById(Long jobId);
    void insert(CrawlingJob crawlingJob);
    void update(CrawlingJob crawlingJob);
    void delete(Long jobId);
}
```

---

### 4. Database Layer (데이터베이스 계층)

**역할**: 데이터 저장 및 조회

**위치**: MySQL Database

**주요 테이블**:
- `crawling_job`: 크롤링 작업 저장
- `crawling_result`: 크롤링 결과 저장

---

## 주요 컴포넌트

### Domain Layer (도메인 계층)

**위치**: `biz/domain/entity/`

**주요 클래스**:
- `CrawlingJob`: 크롤링 작업 엔티티
- `CrawlingResult`: 크롤링 결과 엔티티

**특징**:
- 데이터베이스 테이블과 1:1 매핑
- 비즈니스 로직 포함 가능
- 불변 설계 권장

```java
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingJob {
    private Long jobId;
    private String jobName;
    private String targetUrl;
    private String selector;
    // ...
}
```

### DTO Layer (데이터 전송 계층)

**위치**: `biz/dto/`

**주요 클래스**:
- `CrawlingJobDto`: 크롤링 작업 요청/응답 DTO
- `CrawlingResultDto`: 크롤링 결과 요청/응답 DTO

**특징**:
- Controller ↔ Service 데이터 전송
- Entity와 분리된 구조
- 변환 메서드 포함

```java
@Getter
@Builder
public class CrawlingJobDto {
    private Long jobId;
    private String jobName;
    // ...
    
    public static CrawlingJobDto fromEntity(CrawlingJob entity) {
        // Entity → DTO 변환
    }
    
    public CrawlingJob toEntity() {
        // DTO → Entity 변환
    }
}
```

---

## 데이터 흐름

### 전형적인 요청 처리 흐름

```
1. HTTP Request 수신
   ↓
2. Controller 처리
   ├─ DTO 유효성 검증
   ├─ Service 메서드 호출
   └─ 응답 반환
   ↓
3. Service 처리
   ├─ DTO → Entity 변환
   ├─ 비즈니스 로직 실행
   ├─ Mapper 호출
   └─ Entity → DTO 변환
   ↓
4. Mapper 처리
   ├─ SQL 생성 (MyBatis)
   ├─ 데이터베이스 접근
   └─ 결과 반환
   ↓
5. HTTP Response 반환
```

### 예제: 크롤링 작업 조회

```
GET /api/crawling/jobs/1
     ↓
CrawlingController.getJob(1)
     ↓
CrawlingJobService.getJobById(1)
     ├─ CrawlingJobMapper.selectById(1)
     │  └─ SELECT * FROM crawling_job WHERE job_id = 1
     └─ Entity → DTO 변환
     ↓
CrawlingJobDto 응답
```

---

## 설계 패턴

### 1. Service Interface Pattern

비즈니스 로직 인터페이스를 분리하여 느슨한 결합 구현:

```java
// 인터페이스
public interface CrawlingJobService {
    List<CrawlingJobDto> getAllJobs();
    CrawlingJobDto getJobById(Long jobId);
    // ...
}

// 구현체
@Service
public class CrawlingJobServiceImpl implements CrawlingJobService {
    @Override
    public List<CrawlingJobDto> getAllJobs() {
        // 구현
    }
}
```

### 2. Mapper Pattern (MyBatis)

데이터 접근 로직을 인터페이스로 추상화:

```java
@Mapper
public interface CrawlingJobMapper {
    List<CrawlingJob> selectAll();
    // MyBatis가 XML 파일의 쿼리와 매핑
}
```

### 3. DTO Conversion Pattern

Entity와 DTO 간의 변환 메서드 제공:

```java
// Entity → DTO
CrawlingJobDto dto = CrawlingJobDto.fromEntity(entity);

// DTO → Entity
CrawlingJob entity = dto.toEntity();
```

### 4. Dependency Injection Pattern

Spring의 생성자 주입을 사용한 의존성 관리:

```java
@Service
@RequiredArgsConstructor
public class CrawlingJobServiceImpl implements CrawlingJobService {
    private final CrawlingJobMapper crawlingJobMapper;
    // 의존성이 자동으로 주입됨
}
```

---

## 의존성 주입

### Constructor Injection (권장)

```java
@Service
@RequiredArgsConstructor
public class CrawlingJobServiceImpl implements CrawlingJobService {
    private final CrawlingJobMapper crawlingJobMapper;
    private final JsoupCrawlerService jsoupCrawlerService;
    
    // 테스트하기 쉽고, final로 불변성 보장
}
```

### Spring Bean 등록

```yaml
# application.yaml
spring:
  context:
    initializer: ...  # 필요시 추가 설정
```

---

## 에러 처리

### Global Exception Handler

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return ResponseEntity.internalServerError().build();
    }
}
```

### Service에서의 예외 처리

```java
@Service
public class CrawlingJobServiceImpl implements CrawlingJobService {
    
    @Override
    public CrawlingResultDto executeCrawling(Long jobId) {
        try {
            // 크롤링 로직
        } catch (IOException e) {
            // 예외 처리
            return CrawlingResultDto.builder()
                .status("FAILED")
                .errorMessage(e.getMessage())
                .build();
        }
    }
}
```

---

## 확장성

### 새로운 크롤러 추가

1. **CrawlerService 인터페이스 생성**
```java
public interface CrawlerService {
    List<String> crawl(String url, String selector);
}
```

2. **구현체 작성**
```java
@Service
public class CustomCrawlerService implements CrawlerService {
    @Override
    public List<String> crawl(String url, String selector) {
        // 구현
    }
}
```

3. **Service에 주입**
```java
@Service
public class CrawlingJobServiceImpl implements CrawlingJobService {
    private final CrawlerService crawlerService;
}
```

---

## 성능 최적화

### 1. 인덱싱

```sql
-- 자주 조회되는 컬럼에 인덱스 생성
CREATE INDEX idx_active ON crawling_job(active);
CREATE INDEX idx_created_at ON crawling_result(created_at);
```

### 2. 캐싱 (선택사항)

```java
@Service
public class CrawlingJobServiceImpl implements CrawlingJobService {
    
    @Cacheable("crawlingJobs")
    @Override
    public List<CrawlingJobDto> getAllJobs() {
        return crawlingJobMapper.selectAll()
            .stream()
            .map(CrawlingJobDto::fromEntity)
            .toList();
    }
}
```

### 3. 페이징

```java
// 향후 추가 예정
public Page<CrawlingJobDto> getJobs(Pageable pageable) {
    // 구현
}
```

---

## 배포 고려사항

### 프로덕션 체크리스트

- ✅ 데이터베이스 마이그레이션 완료
- ✅ 환경 변수 설정
- ✅ 로깅 설정
- ✅ 보안 설정 검토
- ✅ 성능 테스트 완료
- ✅ 모니터링 설정

---

## 참고 자료

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [MyBatis Documentation](https://mybatis.org/mybatis-3/)
- [Clean Code Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

