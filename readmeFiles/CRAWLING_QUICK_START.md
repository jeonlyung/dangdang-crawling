# 🕷️ 크롤링 시작 가이드

## 📋 프로젝트 구성 요약

### 폴더 구조

```
dangdang-crawling/
├── biz/
│   ├── api/              ← REST API (컨트롤러)
│   ├── dto/              ← 데이터 전송 객체 (요청/응답)
│   ├── mapper/           ← 데이터베이스 접근 (MyBatis)
│   └── service/          ← 비즈니스 로직 (크롤링 실행)
│       ├── impl/         ← 서비스 구현
│       ├── crawler/      ← 크롤링 로직 (Jsoup, Selenium)
│       └── scheduler/    ← 자동 스케줄링 (Quartz)
└── global/
    ├── config/          ← 설정
    └── exception/       ← 예외 처리
```

### 계층별 역할

| 계층 | 파일 | 역할 |
|------|------|------|
| **API** | `CrawlingController.java` | HTTP 요청 처리 |
| **Service** | `CrawlingJobServiceImpl.java` | 크롤링 작업 관리 |
| **Crawler** | `JsoupCrawlerService.java` | 실제 크롤링 실행 |
| **Mapper** | `CrawlingJobMapper.java` | DB 저장/조회 |
| **DTO** | `CrawlingJobDto.java` | 데이터 정의 |

---

## 🔧 크롤링하는 방법

### 방법 1️⃣: API로 즉시 크롤링

**1단계: 크롤링 작업 생성**

```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "당당 도서 크롤링",
    "targetUrl": "https://search.dangdang.com/",
    "selector": ".search_result_item",
    "description": "당당 도서 검색 결과 크롤링",
    "active": true
  }'
```

**응답**
```json
{
  "jobId": 1,
  "jobName": "당당 도서 크롤링",
  "targetUrl": "https://search.dangdang.com/",
  "selector": ".search_result_item",
  "description": "당당 도서 검색 결과 크롤링",
  "active": true,
  "createdAt": "2026-02-27T10:30:00",
  "updatedAt": "2026-02-27T10:30:00"
}
```

**2단계: 작업 실행 (즉시 크롤링)**

```bash
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
```

**응답 (크롤링 결과)**
```json
{
  "resultId": 1,
  "jobId": 1,
  "itemCount": 42,
  "status": "SUCCESS",
  "content": null,
  "errorMessage": null,
  "createdAt": "2026-02-27T10:35:00"
}
```

**3단계: 결과 조회**

```bash
# 모든 결과 조회
curl http://localhost:8081/api/crawling/results

# 특정 작업의 결과 조회
curl http://localhost:8081/api/crawling/results/job/1

# 특정 결과 상세 조회
curl http://localhost:8081/api/crawling/results/1
```

---

### 방법 2️⃣: 자동 스케줄링으로 정기적 크롤링

**Cron 표현식으로 주기 설정**

```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "정기 크롤링",
    "targetUrl": "https://search.dangdang.com/",
    "selector": ".search_result_item",
    "active": true,
    "cronExpression": "0 0 * * * ?"
  }'
```

| Cron 표현식 | 실행 시간 |
|------------|---------|
| `0 0 * * * ?` | 매일 자정 |
| `0 0 0 * * ?` | 매일 00:00 |
| `0 0 12 * * ?` | 매일 정오 |
| `0 0/30 * * * ?` | 30분마다 |
| `0 0 * * MON ?` | 매주 월요일 |

---

## 📊 크롤링 흐름

```
1. API 요청
   ↓
2. CrawlingController
   ├─ 요청 검증
   ├─ Service 호출
   └─ 응답 반환
   ↓
3. CrawlingJobServiceImpl
   ├─ 작업 조회
   ├─ JsoupCrawlerService 호출 (실제 크롤링)
   └─ 결과 생성
   ↓
4. JsoupCrawlerService
   ├─ URL 접속
   ├─ HTML 파싱
   ├─ CSS 선택자로 요소 추출
   └─ 데이터 반환
   ↓
5. CrawlingResultMapper
   ├─ 결과를 DB에 저장
   └─ 저장된 데이터 반환
   ↓
6. 응답 완료
```

---

## 🎯 주요 API 엔드포인트

### 작업 관리

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/api/crawling/jobs` | 모든 작업 조회 |
| GET | `/api/crawling/jobs/active` | 활성 작업만 조회 |
| GET | `/api/crawling/jobs/{jobId}` | 특정 작업 조회 |
| POST | `/api/crawling/jobs` | 작업 생성 |
| PUT | `/api/crawling/jobs/{jobId}` | 작업 수정 |
| DELETE | `/api/crawling/jobs/{jobId}` | 작업 삭제 |
| POST | `/api/crawling/jobs/{jobId}/execute` | **작업 실행 (크롤링)** |

### 결과 조회

| 메서드 | 엔드포인트 | 설명 |
|--------|-----------|------|
| GET | `/api/crawling/results` | 모든 결과 조회 |
| GET | `/api/crawling/results/job/{jobId}` | 작업별 결과 조회 |
| GET | `/api/crawling/results/{resultId}` | 특정 결과 조회 |

---

## 💡 CSS 선택자 (Selector) 예제

크롤링할 요소를 지정하는 `selector` 값 예제:

```javascript
// 클래스로 선택
".product-item"          // class="product-item" 선택
".item.active"           // class="item active" 선택

// ID로 선택
"#main-content"          // id="main-content" 선택

// 태그로 선택
"div"                    // 모든 <div> 선택
"a"                      // 모든 <a> (링크) 선택
"li"                     // 모든 <li> (목록 항목) 선택

// 조합
"div.product > a"        // <div class="product"> 안의 <a> 선택
"li:nth-child(1)"        // 첫 번째 <li> 선택
"a[href*='dangdang']"    // href에 'dangdang'을 포함하는 <a> 선택
```

**Selector 찾는 방법:**
1. 브라우저에서 크롤링할 사이트 열기
2. F12 개발자 도구 열기
3. 원하는 요소 우클릭 → "검사" (Inspect)
4. HTML에서 `class="..."` 또는 `id="..."` 확인
5. 해당 값을 selector에 입력

---

## 🛠️ 크롤링 예제

### 예제 1: 당당 도서 크롤링

```bash
# 1. 작업 생성
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "당당 도서",
    "targetUrl": "https://search.dangdang.com/",
    "selector": ".search_result_item",
    "active": true
  }'
# → jobId: 1 반환

# 2. 크롤링 실행
curl -X POST http://localhost:8081/api/crawling/jobs/1/execute
# → 크롤링 결과 반환

# 3. 결과 확인
curl http://localhost:8081/api/crawling/results/job/1
```

### 예제 2: 매일 자정에 자동 크롤링

```bash
curl -X POST http://localhost:8081/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "매일 자정 크롤링",
    "targetUrl": "https://example.com/news",
    "selector": ".news-item",
    "active": true,
    "cronExpression": "0 0 * * * ?"
  }'
```

---

## ⚙️ 기술 상세

### 크롤링 방식

#### 1. **Jsoup** (권장)
- 정적 HTML 페이지 크롤링
- 빠르고 가볍다
- JavaScript 렌더링 불필요한 경우 사용

#### 2. **Selenium**
- JavaScript 렌더링 필요한 동적 페이지
- 실제 브라우저처럼 동작
- 느리지만 복잡한 상황 처리 가능

### 데이터베이스 테이블

#### crawling_job 테이블
```
job_id           : 작업 ID (자동증가)
job_name         : 작업명
target_url       : 크롤링할 URL
selector         : CSS 선택자
description      : 설명
active           : 활성화 여부
cron_expression  : 스케줄 (Quartz Cron)
created_at       : 생성시간
updated_at       : 수정시간
```

#### crawling_result 테이블
```
result_id        : 결과 ID
job_id           : 작업 ID (외래키)
content          : 크롤링된 내용
item_count       : 추출된 항목 수
status           : SUCCESS / FAILED / PARTIAL
error_message    : 오류 메시지
created_at       : 생성시간
updated_at       : 수정시간
```

---

## 🚨 에러 처리

### 흔한 에러와 해결법

| 에러 | 원인 | 해결 |
|------|------|------|
| `No static resource` | URL 경로 오류 | `/api/crawling/jobs` 확인 |
| `Table not found` | DB 테이블 없음 | `src/main/resources/mapper/ddl/schema.sql` 실행 |
| `Connection refused` | DB 연결 실패 | MySQL 실행 확인 |
| `404 Not Found` | API 경로 오류 | 경로 재확인 |
| `Timeout` | URL 접속 실패 | targetUrl 유효성 확인 |

---

## 📝 빠른 테스트

### Postman으로 테스트

1. **작업 생성** (POST)
   - URL: `http://localhost:8081/api/crawling/jobs`
   - Body:
   ```json
   {
     "jobName": "Test Crawling",
     "targetUrl": "https://www.google.com",
     "selector": "title",
     "active": true
   }
   ```

2. **크롤링 실행** (POST)
   - URL: `http://localhost:8081/api/crawling/jobs/1/execute`

3. **결과 확인** (GET)
   - URL: `http://localhost:8081/api/crawling/results/job/1`

---

## 📚 더 알아보기

자세한 내용은 다음 문서를 참고하세요:
- [API 명세](./readmeFiles/API.md) - 모든 API 상세 설명
- [데이터베이스](./readmeFiles/DATABASE.md) - 스키마 상세
- [아키텍처](./readmeFiles/ARCHITECTURE.md) - 설계 패턴
- [설치 가이드](./readmeFiles/SETUP.md) - 환경 설정

---

**질문이 있으면 README.md의 관련 섹션을 확인하세요!**

