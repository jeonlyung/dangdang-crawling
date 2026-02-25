# DangDang Crawling Server

Spring Boot 기반 웹 크롤링 서버입니다.

## 기술 스택

- **Framework**: Spring Boot 3.3.6
- **Java**: 17
- **Build Tool**: Gradle
- **Crawling**: Jsoup, Selenium
- **Database**: MySQL, MyBatis
- **Caching**: Redis
- **Scheduler**: Quartz

## 프로젝트 구조

```
src/main/java/com/dangdang/crawling/
├── biz/                          # 비즈니스 로직
│   ├── crawler/                  # 크롤링 로직
│   │   ├── JsoupCrawlerService.java
│   │   └── SeleniumCrawlerService.java
│   ├── scheduler/                # 스케줄러
│   │   └── CrawlingScheduler.java
│   ├── api/                      # REST API Controller
│   │   └── CrawlingController.java
│   └── common/
│       ├── dto/
│       │   ├── CrawlingJobDto.java
│       │   └── CrawlingResultDto.java
│       ├── entity/
│       └── mapper/
└── global/                       # 전역 설정
    ├── config/
    │   └── ApplicationConfig.java
    └── exception/
        └── GlobalExceptionHandler.java
```

## 실행 방법

```bash
./gradlew bootRun
```

## 빌드

```bash
./gradlew build
```

## API 엔드포인트

### 즉시 크롤링 실행
```
POST /api/crawling/execute
```

### 크롤링 상태 조회
```
GET /api/crawling/status/{jobId}
```

### 크롤링 결과 조회
```
GET /api/crawling/result/{jobId}
```
