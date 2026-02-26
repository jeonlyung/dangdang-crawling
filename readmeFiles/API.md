# API 명세

## 개요

DangDang Crawling Server는 RESTful API를 통해 크롤링 작업을 관리하고 결과를 조회합니다.

## 기본 정보

- **Base URL**: `http://localhost:8080/api/crawling`
- **Content-Type**: `application/json`
- **Response Format**: JSON

## 크롤링 작업 API

### 1. 모든 크롤링 작업 조회

**요청**
```http
GET /jobs
```

**응답 (200 OK)**
```json
[
  {
    "jobId": 1,
    "jobName": "Dangdang Books Crawling",
    "targetUrl": "https://search.dangdang.com/",
    "selector": ".search-item",
    "description": "당당 도서 검색 크롤링",
    "active": true,
    "cronExpression": "0 0 * * * ?",
    "createdAt": "2026-02-26T10:30:00",
    "updatedAt": "2026-02-26T10:30:00"
  }
]
```

---

### 2. 활성화된 크롤링 작업 조회

**요청**
```http
GET /jobs/active
```

**응답 (200 OK)**
```json
[
  {
    "jobId": 1,
    "jobName": "Dangdang Books Crawling",
    "targetUrl": "https://search.dangdang.com/",
    "selector": ".search-item",
    "description": "당당 도서 검색 크롤링",
    "active": true,
    "cronExpression": "0 0 * * * ?",
    "createdAt": "2026-02-26T10:30:00",
    "updatedAt": "2026-02-26T10:30:00"
  }
]
```

---

### 3. 특정 크롤링 작업 조회

**요청**
```http
GET /jobs/{jobId}
```

**경로 매개변수**
| 매개변수 | 타입 | 설명 |
|---------|------|------|
| jobId | Long | 크롤링 작업 ID |

**응답 (200 OK)**
```json
{
  "jobId": 1,
  "jobName": "Dangdang Books Crawling",
  "targetUrl": "https://search.dangdang.com/",
  "selector": ".search-item",
  "description": "당당 도서 검색 크롤링",
  "active": true,
  "cronExpression": "0 0 * * * ?",
  "createdAt": "2026-02-26T10:30:00",
  "updatedAt": "2026-02-26T10:30:00"
}
```

---

### 4. 크롤링 작업 생성

**요청**
```http
POST /jobs
Content-Type: application/json

{
  "jobName": "New Crawling Job",
  "targetUrl": "https://example.com",
  "selector": ".item",
  "description": "Example crawling job",
  "active": true,
  "cronExpression": "0 0 * * * ?"
}
```

**요청 필드**
| 필드 | 타입 | 설명 | 필수 |
|------|------|------|------|
| jobName | String | 작업명 | ✓ |
| targetUrl | String | 대상 URL | ✓ |
| selector | String | CSS 선택자 | ✓ |
| description | String | 설명 | - |
| active | Boolean | 활성화 여부 | - |
| cronExpression | String | Cron 표현식 | - |

**응답 (200 OK)**
```json
{
  "jobId": 2,
  "jobName": "New Crawling Job",
  "targetUrl": "https://example.com",
  "selector": ".item",
  "description": "Example crawling job",
  "active": true,
  "cronExpression": "0 0 * * * ?",
  "createdAt": "2026-02-26T11:00:00",
  "updatedAt": "2026-02-26T11:00:00"
}
```

---

### 5. 크롤링 작업 수정

**요청**
```http
PUT /jobs/{jobId}
Content-Type: application/json

{
  "jobName": "Updated Job Name",
  "targetUrl": "https://updated.example.com",
  "selector": ".updated-item",
  "description": "Updated description",
  "active": true,
  "cronExpression": "0 0 * * * ?"
}
```

**응답 (200 OK)**
```json
{
  "jobId": 1,
  "jobName": "Updated Job Name",
  "targetUrl": "https://updated.example.com",
  "selector": ".updated-item",
  "description": "Updated description",
  "active": true,
  "cronExpression": "0 0 * * * ?",
  "createdAt": "2026-02-26T10:30:00",
  "updatedAt": "2026-02-26T11:15:00"
}
```

---

### 6. 크롤링 작업 삭제

**요청**
```http
DELETE /jobs/{jobId}
```

**응답 (200 OK)**
```
(빈 응답)
```

---

### 7. 크롤링 작업 실행

**요청**
```http
POST /jobs/{jobId}/execute
```

**응답 (200 OK)**
```json
{
  "resultId": 10,
  "jobId": 1,
  "content": null,
  "itemCount": 42,
  "status": "SUCCESS",
  "errorMessage": null,
  "createdAt": "2026-02-26T11:30:00",
  "updatedAt": "2026-02-26T11:30:00"
}
```

**응답 필드**
| 필드 | 타입 | 설명 |
|------|------|------|
| resultId | Long | 결과 ID |
| jobId | Long | 작업 ID |
| content | String | 크롤링된 내용 |
| itemCount | Integer | 추출된 항목 수 |
| status | String | 상태 (SUCCESS, FAILED, PARTIAL) |
| errorMessage | String | 오류 메시지 |
| createdAt | LocalDateTime | 생성 시간 |
| updatedAt | LocalDateTime | 수정 시간 |

---

## 크롤링 결과 API

### 1. 모든 크롤링 결과 조회

**요청**
```http
GET /results
```

**응답 (200 OK)**
```json
[
  {
    "resultId": 1,
    "jobId": 1,
    "content": "...",
    "itemCount": 42,
    "status": "SUCCESS",
    "errorMessage": null,
    "createdAt": "2026-02-26T11:30:00",
    "updatedAt": "2026-02-26T11:30:00"
  }
]
```

---

### 2. 특정 작업의 크롤링 결과 조회

**요청**
```http
GET /results/job/{jobId}
```

**경로 매개변수**
| 매개변수 | 타입 | 설명 |
|---------|------|------|
| jobId | Long | 크롤링 작업 ID |

**응답 (200 OK)**
```json
[
  {
    "resultId": 1,
    "jobId": 1,
    "content": "...",
    "itemCount": 42,
    "status": "SUCCESS",
    "errorMessage": null,
    "createdAt": "2026-02-26T11:30:00",
    "updatedAt": "2026-02-26T11:30:00"
  },
  {
    "resultId": 2,
    "jobId": 1,
    "content": "...",
    "itemCount": 45,
    "status": "SUCCESS",
    "errorMessage": null,
    "createdAt": "2026-02-27T11:30:00",
    "updatedAt": "2026-02-27T11:30:00"
  }
]
```

---

### 3. 특정 크롤링 결과 조회

**요청**
```http
GET /results/{resultId}
```

**경로 매개변수**
| 매개변수 | 타입 | 설명 |
|---------|------|------|
| resultId | Long | 크롤링 결과 ID |

**응답 (200 OK)**
```json
{
  "resultId": 1,
  "jobId": 1,
  "content": "...",
  "itemCount": 42,
  "status": "SUCCESS",
  "errorMessage": null,
  "createdAt": "2026-02-26T11:30:00",
  "updatedAt": "2026-02-26T11:30:00"
}
```

---

## 오류 응답

### 400 Bad Request
```json
{
  "status": 400,
  "message": "Invalid request parameter",
  "timestamp": "2026-02-26T11:30:00"
}
```

### 404 Not Found
```json
{
  "status": 404,
  "message": "Resource not found",
  "timestamp": "2026-02-26T11:30:00"
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "message": "Internal server error",
  "timestamp": "2026-02-26T11:30:00"
}
```

---

## Cron 표현식 예제

| 표현식 | 설명 |
|--------|------|
| `0 0 * * * ?` | 매일 자정 |
| `0 0 0 * * ?` | 매일 00:00 |
| `0 0 12 * * ?` | 매일 정오 |
| `0 0/30 * * * ?` | 30분마다 |
| `0 0 * * MON ?` | 매주 월요일 00:00 |
| `0 0 1 * * ?` | 매달 1일 00:00 |

---

## 사용 예제

### cURL을 사용한 작업 생성

```bash
curl -X POST http://localhost:8080/api/crawling/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "Example Job",
    "targetUrl": "https://example.com",
    "selector": ".item",
    "description": "Example crawling job",
    "active": true,
    "cronExpression": "0 0 * * * ?"
  }'
```

### 작업 실행

```bash
curl -X POST http://localhost:8080/api/crawling/jobs/1/execute
```

### 결과 조회

```bash
curl http://localhost:8080/api/crawling/results/job/1
```

---

## API 변경 이력

| 버전 | 날짜 | 변경사항 |
|------|------|---------|
| 1.0 | 2026-02-26 | 초기 API 명세 작성 |

