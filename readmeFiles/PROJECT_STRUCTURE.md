# 프로젝트 폴더 구조 가이드

## SpringBoot-Thymeleaf 표준 구조로 변경 완료

### 1. 폴더 구조

```
dangdang-crawling/
├── src/main/java/com/dangdang/crawling/
│   ├── DangdangCrawlingApplication.java       # Spring Boot 메인 애플리케이션
│   ├── biz/                                    # 비즈니스 로직 계층
│   │   ├── controller/                         # REST API Controller
│   │   │   └── CrawlingController.java
│   │   ├── domain/                             # 도메인 계층
│   │   │   └── entity/                         # 엔티티 클래스들
│   │   │       ├── CrawlingJob.java           # 크롤링 작업 엔티티
│   │   │       └── CrawlingResult.java        # 크롤링 결과 엔티티
│   │   ├── dto/                                # 데이터 전송 객체 (Request/Response)
│   │   │   ├── CrawlingJobDto.java
│   │   │   └── CrawlingResultDto.java
│   │   ├── mapper/                             # MyBatis Mapper 인터페이스
│   │   │   ├── CrawlingJobMapper.java
│   │   │   └── CrawlingResultMapper.java
│   │   └── service/                            # 비즈니스 로직 계층
│   │       ├── CrawlingJobService.java         # 서비스 인터페이스
│   │       ├── CrawlingResultService.java      # 서비스 인터페이스
│   │       ├── impl/                           # 서비스 구현체
│   │       │   ├── CrawlingJobServiceImpl.java
│   │       │   └── CrawlingResultServiceImpl.java
│   │       ├── crawler/                        # 크롤링 로직
│   │       │   ├── JsoupCrawlerService.java   # Jsoup 크롤러
│   │       │   └── SeleniumCrawlerService.java # Selenium 크롤러
│   │       └── scheduler/                      # 스케줄링
│   │           └── CrawlingScheduler.java     # Quartz 스케줄러
│   └── global/                                 # 전역 설정
│       ├── config/                             # 설정 클래스
│       └── exception/                          # 예외 처리
├── src/main/resources/
│   ├── application.yaml                        # Spring Boot 설정
│   ├── mapper/                                 # MyBatis XML 매퍼 파일
│   │   ├── CrawlingJob.xml
│   │   └── CrawlingResult.xml
│   ├── static/                                 # 정적 리소스
│   └── templates/                              # Thymeleaf 템플릿 (향후 추가 예정)
└── build.gradle                                # Gradle 설정
```

### 2. 계층별 역할

#### Entity (도메인 계층)
- **CrawlingJob**: 크롤링 작업 정보를 저장하는 엔티티
- **CrawlingResult**: 크롤링 결과를 저장하는 엔티티
- 데이터베이스와 직접 대응되는 객체

#### DTO (데이터 전송 객체)
- **CrawlingJobDto**: 클라이언트와의 Request/Response 용
- **CrawlingResultDto**: 클라이언트와의 Request/Response 용
- Entity와의 변환 메서드 포함 (`fromEntity()`, `toEntity()`)

#### Service (비즈니스 로직)
- **CrawlingJobService**: 크롤링 작업 관리 인터페이스
- **CrawlingJobServiceImpl**: 크롤링 작업 관리 구현체
- **CrawlingResultService**: 크롤링 결과 관리 인터페이스
- **CrawlingResultServiceImpl**: 크롤링 결과 관리 구현체
- DTO ↔ Entity 변환 처리

#### Mapper (데이터 접근 계층)
- **CrawlingJobMapper**: 크롤링 작업 MyBatis Mapper
- **CrawlingResultMapper**: 크롤링 결과 MyBatis Mapper
- Entity 객체를 매개변수로 사용 (DTO 대신)

#### Controller (표현 계층)
- **CrawlingController**: REST API 엔드포인트 제공
- DTO를 요청/응답으로 사용
- Service를 통해 비즈니스 로직 호출

### 3. 데이터 흐름

```
클라이언트 (JSON)
    ↓
CrawlingController (DTO 수신)
    ↓
CrawlingJobService / CrawlingResultService (DTO ↔ Entity 변환)
    ↓
CrawlingJobMapper / CrawlingResultMapper (SQL 실행)
    ↓
Database
```

### 4. API 엔드포인트

#### 크롤링 작업 API
- `GET /api/crawling/jobs` - 모든 작업 조회
- `GET /api/crawling/jobs/active` - 활성 작업 조회
- `GET /api/crawling/jobs/{jobId}` - 특정 작업 조회
- `POST /api/crawling/jobs` - 작업 생성
- `PUT /api/crawling/jobs/{jobId}` - 작업 수정
- `DELETE /api/crawling/jobs/{jobId}` - 작업 삭제
- `POST /api/crawling/jobs/{jobId}/execute` - 작업 실행

#### 크롤링 결과 API
- `GET /api/crawling/results` - 모든 결과 조회
- `GET /api/crawling/results/job/{jobId}` - 특정 작업의 결과 조회
- `GET /api/crawling/results/{resultId}` - 특정 결과 조회

### 5. 주요 변경사항

- DTO와 Entity를 분리하여 관심사 분리
- Service 인터페이스 추가로 느슨한 결합 구현
- Mapper를 DTO 대신 Entity를 사용하도록 변경
- Controller에서 Service 주입으로 변경
- 표준 REST API 설계 적용 (`/api` 접두사 추가)

### 6. 향후 개선 사항

- Thymeleaf 템플릿 추가 (Web UI)
- Exception Handler 추가 (Global Exception 처리)
- Validation 추가 (Request DTO에 @Valid 적용)
- Logging 체계 정리
- Unit Test 작성

