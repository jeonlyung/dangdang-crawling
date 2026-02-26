# DangDang Crawling Server

Spring Boot 기반 웹 크롤링 서버입니다. MyBatis를 활용한 데이터 관리와 Quartz 스케줄러를 통한 자동화된 크롤링을 지원합니다.

## 📚 기술 스택

- **Framework**: Spring Boot 3.3.6
- **Language**: Java 17
- **Build Tool**: Gradle
- **ORM**: MyBatis 3.0.3
- **Crawling**: Jsoup 1.17.2, Selenium 4.18.1
- **Database**: MySQL 8.0+
- **Scheduler**: Quartz
- **WebClient**: Spring WebFlux
- **Logging**: SLF4J with Logback

## 📁 프로젝트 구조

```
dangdang-crawling/
├── src/main/java/com/dangdang/crawling/
│   ├── DangdangCrawlingApplication.java          # 메인 애플리케이션
│   ├── biz/                                       # 비즈니스 로직 계층
│   │   ├── controller/
│   │   │   └── CrawlingController.java           # REST API
│   │   ├── domain/
│   │   │   └── entity/                           # 도메인 엔티티
│   │   │       ├── CrawlingJob.java
│   │   │       └── CrawlingResult.java
│   │   ├── dto/                                   # 데이터 전송 객체
│   │   │   ├── CrawlingJobDto.java
│   │   │   └── CrawlingResultDto.java
│   │   ├── mapper/                                # MyBatis Mapper
│   │   │   ├── CrawlingJobMapper.java
│   │   │   └── CrawlingResultMapper.java
│   │   └── service/                               # 비즈니스 로직
│   │       ├── CrawlingJobService.java
│   │       ├── CrawlingResultService.java
│   │       ├── impl/
│   │       │   ├── CrawlingJobServiceImpl.java
│   │       │   └── CrawlingResultServiceImpl.java
│   │       ├── crawler/
│   │       │   ├── JsoupCrawlerService.java
│   │       │   └── SeleniumCrawlerService.java
│   │       └── scheduler/
│   │           └── CrawlingScheduler.java
│   └── global/
│       ├── config/
│       │   └── ApplicationConfig.java
│       └── exception/
│           └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.yaml                          # Spring Boot 설정
│   ├── mapper/                                    # MyBatis XML 매퍼
│   │   ├── CrawlingJob.xml
│   │   └── CrawlingResult.xml
│   ├── static/                                    # 정적 리소스
│   └── templates/                                 # Thymeleaf 템플릿
├── readmeFiles/                                   # 문서 폴더
│   ├── API.md                                     # API 명세
│   ├── DATABASE.md                                # 데이터베이스 스키마
│   ├── SETUP.md                                   # 설치 및 실행 가이드
│   └── ARCHITECTURE.md                            # 아키텍처 가이드
├── build.gradle                                   # Gradle 설정
├── settings.gradle
├── README.md                                      # 이 파일
├── PROJECT_STRUCTURE.md                           # 프로젝트 구조 상세
└── REFACTORING_REPORT.md                          # 리팩토링 보고서
```

## 🚀 빠른 시작

### 사전 요구사항

- Java 17+
- MySQL 8.0+
- Gradle 9.0+

### 설치 및 실행

자세한 설치 가이드는 [SETUP.md](./readmeFiles/SETUP.md)를 참고하세요.

```bash
# 프로젝트 클론
git clone <repository-url>
cd dangdang-crawling

# 의존성 설치 및 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

## 📖 문서

| 문서 | 설명 |
|------|------|
| [API.md](./readmeFiles/API.md) | REST API 명세 및 사용 예제 |
| [DATABASE.md](./readmeFiles/DATABASE.md) | 데이터베이스 스키마 및 쿼리 |
| [SETUP.md](./readmeFiles/SETUP.md) | 환경 설정 및 실행 가이드 |
| [ARCHITECTURE.md](./readmeFiles/ARCHITECTURE.md) | 프로젝트 아키텍처 설명 |
| [PROJECT_STRUCTURE.md](./PROJECT_STRUCTURE.md) | 폴더 구조 상세 가이드 |

## 🔌 API 엔드포인트

### 크롤링 작업 관리

```
GET    /api/crawling/jobs              # 모든 작업 조회
GET    /api/crawling/jobs/active       # 활성 작업 조회
GET    /api/crawling/jobs/{jobId}      # 특정 작업 조회
POST   /api/crawling/jobs              # 작업 생성
PUT    /api/crawling/jobs/{jobId}      # 작업 수정
DELETE /api/crawling/jobs/{jobId}      # 작업 삭제
POST   /api/crawling/jobs/{jobId}/execute  # 작업 실행
```

### 크롤링 결과 조회

```
GET    /api/crawling/results           # 모든 결과 조회
GET    /api/crawling/results/job/{jobId}  # 작업별 결과 조회
GET    /api/crawling/results/{resultId}   # 특정 결과 조회
```

자세한 API 문서는 [API.md](./readmeFiles/API.md)를 참고하세요.

## 🏗️ 아키텍처

계층형 아키텍처를 따르고 있습니다:

- **Controller**: HTTP 요청/응답 처리
- **Service**: 비즈니스 로직 구현
- **Mapper**: 데이터 접근 계층 (MyBatis)
- **Entity**: 도메인 모델
- **DTO**: 데이터 전송 객체

자세한 아키텍처 설명은 [ARCHITECTURE.md](./readmeFiles/ARCHITECTURE.md)를 참고하세요.

## 🔄 워크플로우

```
HTTP Request
    ↓
Controller (DTO 수신)
    ↓
Service (DTO ↔ Entity 변환 및 비즈니스 로직)
    ↓
Mapper (SQL 실행)
    ↓
Database
    ↓
응답 데이터 반환 (DTO)
    ↓
HTTP Response
```

## 📝 주요 기능

- ✅ HTML/XML 크롤링 (Jsoup)
- ✅ JavaScript 렌더링 지원 (Selenium)
- ✅ 자동 스케줄링 (Quartz)
- ✅ RESTful API
- ✅ MyBatis ORM
- ✅ 크롤링 작업 관리
- ✅ 결과 데이터 저장 및 조회

## 🛠️ 개발 팁

### 로컬 개발 환경 설정

```bash
# application.yaml 파일 생성 및 설정
cp src/main/resources/application.yaml.example src/main/resources/application.yaml
```

### 데이터베이스 초기화

```bash
# DDL 실행
mysql -u root -p < src/main/resources/mapper/ddl/schema.sql
```

### 테스트 실행

```bash
./gradlew test
```

## 📋 버전 정보

- **현재 버전**: 0.0.1-SNAPSHOT
- **Java**: 17
- **Spring Boot**: 3.3.6

## 📧 연락처

문의사항이 있으면 이슈를 등록해주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다.
