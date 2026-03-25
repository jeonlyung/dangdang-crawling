# 🚀 프로젝트 시작 체크리스트

## ✅ 완료된 것들

### 1️⃣ 프로젝트 구조
- ✅ Mapper (MyBatis) - 데이터베이스 접근
- ✅ DTO - 데이터 전송 객체
- ✅ Service - 비즈니스 로직 (인터페이스 + 구현)
- ✅ Controller (API) - REST 엔드포인트
- ✅ Crawler (Jsoup, Selenium) - 실제 크롤링 로직

### 2️⃣ 기술 스택
- ✅ Spring Boot 3.3.6
- ✅ Java 17
- ✅ MyBatis 3.0.3
- ✅ Jsoup 1.17.2 (정적 페이지 크롤링)
- ✅ Selenium 4.18.1 (동적 페이지 크롤링)
- ✅ Quartz (자동 스케줄링)
- ✅ MySQL 8.0+
- ✅ Gradle 빌드

### 3️⃣ 데이터베이스
- ✅ `crawling_job` 테이블 스키마 (DDL 작성)
- ✅ `crawling_result` 테이블 스키마 (DDL 작성)
- ✅ 외래키 설정 (crawling_result → crawling_job)
- ✅ 인덱싱 (성능 최적화)

### 4️⃣ API 엔드포인트
- ✅ 작업 관리 (CRUD)
  - ✅ GET `/api/crawling/jobs` - 모든 작업 조회
  - ✅ GET `/api/crawling/jobs/active` - 활성 작업 조회
  - ✅ GET `/api/crawling/jobs/{jobId}` - 특정 작업 조회
  - ✅ POST `/api/crawling/jobs` - 작업 생성
  - ✅ PUT `/api/crawling/jobs/{jobId}` - 작업 수정
  - ✅ DELETE `/api/crawling/jobs/{jobId}` - 작업 삭제
  - ✅ POST `/api/crawling/jobs/{jobId}/execute` - 크롤링 실행

- ✅ 결과 조회
  - ✅ GET `/api/crawling/results` - 모든 결과 조회
  - ✅ GET `/api/crawling/results/job/{jobId}` - 작업별 결과 조회
  - ✅ GET `/api/crawling/results/{resultId}` - 특정 결과 조회

### 5️⃣ 문서화
- ✅ README.md (프로젝트 개요)
- ✅ API.md (API 명세서)
- ✅ DATABASE.md (데이터베이스 스키마)
- ✅ ARCHITECTURE.md (아키텍처 설명)
- ✅ SETUP.md (설치 가이드)
- ✅ GIT_GUIDE.md (Git 커밋 규칙)
- ✅ **CRAWLING_QUICK_START.md** (크롤링 사용법) - 새로 추가!

### 6️⃣ 설정
- ✅ application.yaml (MySQL, Redis 설정)
- ✅ Redis 헬스체크 비활성화 (Redis 없는 환경 대응)
- ✅ MyBatis 매퍼 설정
- ✅ 로깅 설정

---

## 📋 지금 바로 할 수 있는 것

### 1단계: 데이터베이스 설정

```bash
# 테이블 생성
mysql -u root -p dangdang_crawling < src/main/resources/mapper/ddl/schema.sql
```

### 2단계: 서버 실행

```bash
./gradlew bootRun
```

### 3단계: 크롤링 작업 생성

```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "테스트 크롤링",
    "targetUrl": "https://www.google.com",
    "selector": "title",
    "active": true
  }'
```

### 4단계: 크롤링 실행

```bash
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
```

### 5단계: 결과 확인

```bash
curl http://localhost:8081/api/crawling/results/job/1
```

---

## 🎯 사용 시나리오

### 시나리오 1: 즉시 크롤링
1. API로 작업 생성
2. API로 즉시 실행
3. 결과 조회

### 시나리오 2: 정기적 크롤링
1. Cron 표현식과 함께 작업 생성
2. Quartz 스케줄러가 자동 실행
3. DB에 결과 저장

---

## 📁 프로젝트 구조 한눈에

```
src/main/java/com/dangdang/crawling/
│
├── biz/                          # 비즈니스 로직
│   ├── api/
│   │   └── CrawlingController    # ← API 요청 처리
│   ├── dto/
│   │   ├── CrawlingJobDto        # ← 작업 데이터
│   │   └── CrawlingResultDto     # ← 결과 데이터
│   ├── mapper/
│   │   ├── CrawlingJobMapper     # ← DB 쿼리
│   │   └── CrawlingResultMapper
│   └── service/
│       ├── impl/
│       │   ├── CrawlingJobServiceImpl    # ← 작업 관리
│       │   └── CrawlingResultServiceImpl # ← 결과 관리
│       ├── crawler/
│       │   ├── JsoupCrawlerService      # ← 크롤링 (정적)
│       │   └── SeleniumCrawlerService   # ← 크롤링 (동적)
│       └── scheduler/
│           └── CrawlingScheduler        # ← 자동 실행
│
└── global/
    ├── config/                   # 설정
    └── exception/                # 에러 처리
```

---

## 🔄 데이터 흐름

```
사용자 요청 (HTTP)
    ↓
CrawlingController (API)
    ├─ 요청 검증
    └─ Service 호출
    ↓
CrawlingJobServiceImpl
    ├─ 작업 조회
    └─ JsoupCrawlerService 호출
    ↓
JsoupCrawlerService
    ├─ URL 접속
    ├─ HTML 파싱
    ├─ CSS 선택자로 요소 추출
    └─ 데이터 반환
    ↓
CrawlingResultMapper
    ├─ DB에 저장
    └─ 결과 반환
    ↓
응답 (HTTP JSON)
```

---

## 💾 데이터베이스 테이블

### crawling_job (작업 정보)
```
job_id (PK)      | 작업 ID
job_name         | 작업명 (예: "당당 도서 크롤링")
target_url       | 크롤링할 URL (예: https://search.dangdang.com/)
selector         | CSS 선택자 (예: .search_result_item)
description      | 설명
active           | 활성화 여부 (true/false)
cron_expression  | 자동 실행 스케줄 (예: "0 0 * * * ?")
created_at       | 생성 시간
updated_at       | 수정 시간
```

### crawling_result (실행 결과)
```
result_id (PK)   | 결과 ID
job_id (FK)      | 작업 ID (외래키)
content          | 크롤링된 HTML 내용
item_count       | 추출된 항목 수 (예: 42)
status           | 상태 (SUCCESS/FAILED/PARTIAL)
error_message    | 오류 메시지 (실패시)
created_at       | 생성 시간
updated_at       | 수정 시간
```

---

## 🛠️ 주요 기능

### ✨ 기능 1: 즉시 크롤링
API로 요청 → 바로 크롤링 실행 → 결과 반환

### ✨ 기능 2: 정기적 크롤링
Cron 스케줄 설정 → Quartz 스케줄러 → 자동 실행

### ✨ 기능 3: 결과 저장 및 조회
모든 크롤링 결과를 DB에 저장 → 언제든 조회 가능

### ✨ 기능 4: 다양한 크롤링 방식
- Jsoup: 정적 페이지 (빠름)
- Selenium: 동적 페이지 (JavaScript 렌더링)

---

## 🎓 다음 학습 순서

1. **API.md** - 모든 API 엔드포인트 이해
2. **CRAWLING_QUICK_START.md** - 크롤링 실제 사용법
3. **DATABASE.md** - 데이터 구조 이해
4. **ARCHITECTURE.md** - 코드 구조 이해

---

## ✍️ 요약

| 항목 | 상태 |
|------|------|
| 프로젝트 구성 | ✅ 완료 |
| API 개발 | ✅ 완료 |
| 데이터베이스 스키마 | ✅ 완료 |
| 크롤링 로직 | ✅ 완료 |
| 자동 스케줄링 | ✅ 완료 |
| 문서화 | ✅ 완료 |
| 빌드/실행 | ✅ 완료 |

**지금 바로 시작하세요!** 👉 `CRAWLING_QUICK_START.md` 읽기


