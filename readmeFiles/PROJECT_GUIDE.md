# 📖 프로젝트 완벽 가이드

## 🎯 이 가이드를 읽는 순서

프로젝트를 처음 접한다면 **이 순서대로** 읽으세요:

```
1️⃣ 여기 (PROJECT_GUIDE.md) ← 지금 읽는 파일
   └─ 프로젝트 전체 개요

2️⃣ PROJECT_OVERVIEW.md
   └─ 프로젝트 체크리스트 & 빠른 시작

3️⃣ CRAWLING_QUICK_START.md
   └─ 크롤링 실제 사용 방법

4️⃣ API.md (필요시)
   └─ 모든 API 엔드포인트 상세

5️⃣ DATABASE.md (필요시)
   └─ 데이터베이스 스키마 상세
```

---

## 🏗️ 프로젝트 구성 요약

### 📁 폴더 구조

```
dangdang-crawling/
├── src/main/java/com/dangdang/crawling/
│   └── biz/
│       ├── api/              👈 REST API (HTTP 요청 처리)
│       ├── dto/              👈 데이터 정의 (요청/응답)
│       ├── mapper/           👈 데이터베이스 접근
│       └── service/
│           ├── impl/         👈 서비스 구현 (작업 관리)
│           ├── crawler/      👈 크롤링 실행 (Jsoup, Selenium)
│           └── scheduler/    👈 자동 스케줄링 (Quartz)
│
├── readmeFiles/              👈 📚 문서들
│   ├── PROJECT_GUIDE.md      👈 **지금 읽는 파일**
│   ├── PROJECT_OVERVIEW.md
│   ├── CRAWLING_QUICK_START.md
│   ├── API.md
│   ├── DATABASE.md
│   ├── ARCHITECTURE.md
│   ├── SETUP.md
│   └── GIT_GUIDE.md
│
├── src/main/resources/
│   ├── application.yaml      👈 설정 (MySQL, 포트 등)
│   ├── mapper/
│   │   ├── CrawlingJob.xml   👈 SQL 쿼리
│   │   └── CrawlingResult.xml
│   └── mapper/ddl/
│       └── schema.sql        👈 테이블 생성 SQL
│
└── build.gradle              👈 의존성 설정
```

---

## 🚀 5분 안에 시작하기

### 1️⃣ 데이터베이스 테이블 생성 (1분)

```bash
# MySQL에서 테이블 생성
mysql -u root -p dangdang_crawling < src/main/resources/mapper/ddl/schema.sql
```

### 2️⃣ 서버 실행 (1분)

```bash
./gradlew bootRun
```

서버가 시작되면 이 메시지가 나타납니다:
```
Tomcat started on port 8081 (http) with context path '/api'
```

### 3️⃣ 크롤링 작업 생성 (1분)

```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "구글 타이틀 크롤링",
    "targetUrl": "https://www.google.com",
    "selector": "title",
    "active": true
  }'
```

응답:
```json
{
  "jobId": 1,
  "jobName": "구글 타이틀 크롤링",
  ...
}
```

### 4️⃣ 크롤링 실행 (1분)

```bash
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
```

응답:
```json
{
  "status": "SUCCESS",
  "itemCount": 1,
  "createdAt": "2026-02-27T11:00:00"
}
```

### 5️⃣ 결과 확인 (1분)

```bash
curl http://localhost:8081/api/crawling/results/job/1
```

---

## 📊 데이터 흐름

```
┌─────────────────┐
│  사용자 (You)   │
└────────┬────────┘
         │ HTTP 요청
         ▼
┌────────────────────────┐
│  CrawlingController    │ ← API
│  (HTTP 요청 처리)       │
└────────┬───────────────┘
         │ 
         ▼
┌────────────────────────┐
│ CrawlingJobService     │ ← 비즈니스 로직
│ (작업 관리)            │
└────────┬───────────────┘
         │
         ▼
┌────────────────────────┐
│ JsoupCrawlerService    │ ← 크롤링 실행
│ (HTML 파싱, 요소 추출)  │
└────────┬───────────────┘
         │ 데이터
         ▼
┌────────────────────────┐
│ CrawlingResultMapper   │ ← DB 저장
│ (MySQL)                │
└────────┬───────────────┘
         │
         ▼
┌─────────────────┐
│  응답 (결과)     │
└─────────────────┘
```

---

## 🎛️ 세 가지 크롤링 방식

### 방식 1️⃣: 즉시 크롤링
```
API 요청
  ↓
즉시 크롤링 실행
  ↓
결과 반환
```

**언제 사용?** 지금 바로 원하는 데이터를 크롤링할 때

**예시:**
```bash
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
```

### 방식 2️⃣: 정기적 크롤링 (매일)
```
작업 생성 (Cron: "0 0 * * * ?")
  ↓
Quartz 스케줄러 대기
  ↓
매일 자정 자동 실행
  ↓
결과 저장
```

**언제 사용?** 매일 정해진 시간에 자동으로 크롤링할 때

**예시:**
```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "매일 자동 크롤링",
    "targetUrl": "https://example.com",
    "selector": ".item",
    "active": true,
    "cronExpression": "0 0 * * * ?"  # 매일 자정
  }'
```

### 방식 3️⃣: 동적 페이지 크롤링
```
Selenium 브라우저 열기
  ↓
JavaScript 실행
  ↓
페이지 로드 대기
  ↓
요소 추출
  ↓
결과 반환
```

**언제 사용?** JavaScript로 동적으로 로드되는 페이지일 때

**기술:** Selenium WebDriver

---

## 🔧 핵심 개념 3가지

### 개념 1️⃣: CSS 선택자 (Selector)
크롤링할 HTML 요소를 지정하는 방법

```javascript
// 클래스로 선택
".product-item"

// ID로 선택
"#main-content"

// 태그로 선택
"div", "a", "li"

// 조합
"div.product > a"
"li:nth-child(1)"
```

**찾는 방법:**
1. 웹사이트 열기
2. F12 개발자 도구 열기
3. 원하는 요소 우클릭 → "검사" (Inspect)
4. `class="..."` 또는 `id="..."` 확인
5. 해당 값을 selector에 입력

### 개념 2️⃣: Cron 표현식 (스케줄)
자동 실행 시간을 지정하는 방법

```
"0 0 * * * ?"      → 매일 자정
"0 0 12 * * ?"     → 매일 정오
"0 0/30 * * * ?"   → 30분마다
"0 0 * * MON ?"    → 매주 월요일
```

### 개념 3️⃣: DTO (Data Transfer Object)
데이터를 요청/응답할 때 사용하는 객체

```json
{
  "jobId": 1,
  "jobName": "크롤링 작업",
  "targetUrl": "https://example.com",
  "selector": ".item",
  "active": true,
  "createdAt": "2026-02-27T11:00:00"
}
```

---

## 📱 API 사용 예제

### 📌 작업 생성 (POST)
```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "테스트",
    "targetUrl": "https://google.com",
    "selector": "title",
    "active": true
  }'
```

### 📌 작업 조회 (GET)
```bash
# 모든 작업
curl http://localhost:8081/api/crawling/jobs

# 활성 작업만
curl http://localhost:8081/api/crawling/jobs/active

# 특정 작업
curl http://localhost:8081/api/crawling/jobs/1
```

### 📌 작업 수정 (PUT)
```bash
curl -X PUT http://localhost:8081/api/crawling/jobs/1 \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "수정된 이름",
    "targetUrl": "https://example.com",
    "selector": ".new-selector",
    "active": true
  }'
```

### 📌 작업 삭제 (DELETE)
```bash
curl -X DELETE http://localhost:8081/api/crawling/jobs/1
```

### 📌 크롤링 실행 (POST)
```bash
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
```

### 📌 결과 조회 (GET)
```bash
# 모든 결과
curl http://localhost:8081/api/crawling/results

# 작업별 결과
curl http://localhost:8081/api/crawling/results/job/1

# 특정 결과
curl http://localhost:8081/api/crawling/results/1
```

---

## 🗂️ 데이터베이스 테이블

### crawling_job 테이블 (작업 정보)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `job_id` | BIGINT | 작업 ID (자동증가) |
| `job_name` | VARCHAR(255) | 작업명 |
| `target_url` | VARCHAR(2048) | 크롤링할 URL |
| `selector` | VARCHAR(1024) | CSS 선택자 |
| `description` | TEXT | 설명 |
| `active` | BOOLEAN | 활성화 여부 |
| `cron_expression` | VARCHAR(100) | 스케줄 |
| `created_at` | TIMESTAMP | 생성시간 |
| `updated_at` | TIMESTAMP | 수정시간 |

### crawling_result 테이블 (실행 결과)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `result_id` | BIGINT | 결과 ID |
| `job_id` | BIGINT | 작업 ID (외래키) |
| `content` | LONGTEXT | 크롤링된 내용 |
| `item_count` | INT | 항목 수 |
| `status` | VARCHAR(50) | SUCCESS/FAILED |
| `error_message` | VARCHAR(1024) | 오류 메시지 |
| `created_at` | TIMESTAMP | 생성시간 |
| `updated_at` | TIMESTAMP | 수정시간 |

---

## 🛠️ 기술 스택

| 항목 | 버전 |
|------|------|
| Spring Boot | 3.3.6 |
| Java | 17 |
| MyBatis | 3.0.3 |
| Jsoup | 1.17.2 |
| Selenium | 4.18.1 |
| Quartz | 2.3.2 |
| MySQL | 8.0+ |
| Gradle | 9.0+ |

---

## ⚠️ 흔한 실수와 해결법

### ❌ 실수 1: "No static resource" 에러
```
원인: URL 경로가 잘못됨
해결: /api/crawling/jobs 경로 확인
```

### ❌ 실수 2: "Table not found" 에러
```
원인: 데이터베이스 테이블 없음
해결: mysql -u root -p dangdang_crawling < schema.sql 실행
```

### ❌ 실수 3: 크롤링이 안 됨
```
원인: selector가 잘못됨
해결: 개발자 도구(F12)에서 요소 검사하여 올바른 selector 확인
```

### ❌ 실수 4: 포트 충돌
```
원인: 다른 서비스가 8081 포트 사용 중
해결: application.yaml의 port 변경 또는 충돌 서비스 종료
```

---

## 🎓 학습 로드맵

```
1. 기본 개념 이해
   └─ 이 파일 읽기 ✅

2. 실제 사용법 배우기
   └─ CRAWLING_QUICK_START.md 읽기

3. 심화 학습
   ├─ API.md (모든 엔드포인트)
   ├─ DATABASE.md (데이터 구조)
   ├─ ARCHITECTURE.md (코드 구조)
   └─ GIT_GUIDE.md (팀 협업)

4. 실습
   └─ 실제로 크롤링 작업 생성 및 실행
```

---

## 💡 팁과 트릭

### 💡 팁 1: Postman으로 편하게 테스트하기
Postman 설치 후:
1. New → Request
2. 메서드(GET, POST 등) 선택
3. URL 입력
4. Body (JSON) 입력
5. Send 클릭

### 💡 팁 2: cURL 명령 저장하기
자주 사용하는 명령을 shell script로 저장:
```bash
#!/bin/bash
# crawling.sh
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
```

그 다음:
```bash
chmod +x crawling.sh
./crawling.sh
```

### 💡 팁 3: 로그 확인하기
실행 중 로그는 콘솔에 출력됩니다:
```
[INFO] Getting all crawling jobs
[ERROR] Connection refused: job not found
```

---

## 🎯 다음 단계

### 초급 (지금)
- ✅ 프로젝트 이해
- ✅ 테이블 생성
- ✅ 서버 실행
- ⬜️ 첫 크롤링 작업 실행

### 중급
- ⬜️ 여러 작업 생성
- ⬜️ Cron으로 자동 스케줄링
- ⬜️ 결과 분석

### 고급
- ⬜️ Selenium으로 동적 페이지 크롤링
- ⬜️ 커스텀 로직 추가
- ⬜️ 성능 최적화

---

## 📚 더 많은 정보

| 문서 | 내용 |
|------|------|
| **PROJECT_OVERVIEW.md** | 프로젝트 체크리스트 |
| **CRAWLING_QUICK_START.md** | 크롤링 실제 사용법 |
| **API.md** | 모든 API 엔드포인트 |
| **DATABASE.md** | DB 스키마 상세 |
| **ARCHITECTURE.md** | 코드 구조 설명 |
| **SETUP.md** | 개발 환경 구축 |
| **GIT_GUIDE.md** | Git 커밋 규칙 |

---

## 🚀 준비됐나요?

**다음 단계:**
1. `src/main/resources/mapper/ddl/schema.sql` 실행
2. `./gradlew bootRun` 으로 서버 시작
3. `CRAWLING_QUICK_START.md` 읽기
4. 첫 크롤링 작업 생성하기!

**질문이 있으면:**
1. README.md 읽기
2. 관련 문서 검색
3. API.md의 예제 확인

---

**축하합니다! 🎉 이제 크롤링 서버를 사용할 준비가 됐습니다.**


