# 데이터베이스 스키마

## 개요

DangDang Crawling Server는 MySQL을 데이터베이스로 사용합니다.

## 데이터베이스 생성

```sql
CREATE DATABASE dangdang_crawling CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dangdang_crawling;
```

## 테이블 스키마

### 1. crawling_job 테이블

크롤링 작업 정보를 저장하는 테이블입니다.

```sql
CREATE TABLE crawling_job (
  job_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '작업 ID',
  job_name VARCHAR(255) NOT NULL COMMENT '작업명',
  target_url VARCHAR(2048) NOT NULL COMMENT '대상 URL',
  selector VARCHAR(1024) NOT NULL COMMENT 'CSS 선택자',
  description TEXT COMMENT '설명',
  active BOOLEAN DEFAULT true COMMENT '활성화 여부',
  cron_expression VARCHAR(100) COMMENT 'Cron 표현식',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
  
  INDEX idx_active (active),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='크롤링 작업';
```

**컬럼 설명**

| 컬럼 | 타입 | 설명 | 필수 |
|------|------|------|------|
| job_id | BIGINT | 작업 ID (자동 증가) | ✓ |
| job_name | VARCHAR(255) | 작업명 | ✓ |
| target_url | VARCHAR(2048) | 크롤링할 대상 URL | ✓ |
| selector | VARCHAR(1024) | CSS 선택자 | ✓ |
| description | TEXT | 작업 설명 | - |
| active | BOOLEAN | 활성화 여부 (기본값: true) | - |
| cron_expression | VARCHAR(100) | Quartz Cron 표현식 | - |
| created_at | TIMESTAMP | 생성 시간 | ✓ |
| updated_at | TIMESTAMP | 마지막 수정 시간 | ✓ |

---

### 2. crawling_result 테이블

크롤링 실행 결과를 저장하는 테이블입니다.

```sql
CREATE TABLE crawling_result (
  result_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '결과 ID',
  job_id BIGINT NOT NULL COMMENT '작업 ID',
  content LONGTEXT COMMENT '크롤링된 내용',
  item_count INT DEFAULT 0 COMMENT '추출된 항목 수',
  status VARCHAR(50) DEFAULT 'PENDING' COMMENT '상태 (PENDING, SUCCESS, FAILED, PARTIAL)',
  error_message VARCHAR(1024) COMMENT '오류 메시지',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
  
  CONSTRAINT fk_crawling_result_job 
    FOREIGN KEY (job_id) REFERENCES crawling_job(job_id) ON DELETE CASCADE,
  
  INDEX idx_job_id (job_id),
  INDEX idx_status (status),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='크롤링 결과';
```

**컬럼 설명**

| 컬럼 | 타입 | 설명 | 필수 |
|------|------|------|------|
| result_id | BIGINT | 결과 ID (자동 증가) | ✓ |
| job_id | BIGINT | 크롤링 작업 ID (외래키) | ✓ |
| content | LONGTEXT | 크롤링된 결과 내용 | - |
| item_count | INT | 추출된 항목 수 | - |
| status | VARCHAR(50) | 실행 상태 (PENDING, SUCCESS, FAILED, PARTIAL) | ✓ |
| error_message | VARCHAR(1024) | 오류 발생 시 오류 메시지 | - |
| created_at | TIMESTAMP | 생성 시간 | ✓ |
| updated_at | TIMESTAMP | 마지막 수정 시간 | ✓ |

---

## 인덱스 전략

### crawling_job 테이블 인덱스
- `idx_active`: 활성 작업 조회 시 빠른 검색
- `idx_created_at`: 생성 시간 기준 정렬 시 빠른 검색

### crawling_result 테이블 인덱스
- `fk_crawling_result_job`: 작업별 결과 조회
- `idx_status`: 상태 기준 검색
- `idx_created_at`: 생성 시간 기준 정렬 및 오래된 데이터 삭제

---

## 초기 데이터 삽입

### 테스트 작업 추가

```sql
INSERT INTO crawling_job (job_name, target_url, selector, description, active, cron_expression)
VALUES 
  ('당당 도서 크롤링', 'https://search.dangdang.com/', '.search-item', '당당 도서 검색 결과 크롤링', true, '0 0 * * * ?'),
  ('네이버 뉴스 크롤링', 'https://news.naver.com/', '.newslist li', '네이버 뉴스 목록 크롤링', true, '0 0/6 * * * ?');
```

---

## 데이터 관리

### 오래된 결과 삭제 쿼리

7일 이상 된 결과 데이터 자동 삭제 (Quartz 스케줄러에서 실행):

```sql
DELETE FROM crawling_result
WHERE created_at < DATE_SUB(NOW(), INTERVAL 7 DAY);
```

### 작업별 결과 통계 조회

```sql
SELECT 
  j.job_id,
  j.job_name,
  COUNT(r.result_id) as total_results,
  SUM(CASE WHEN r.status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count,
  SUM(CASE WHEN r.status = 'FAILED' THEN 1 ELSE 0 END) as failed_count,
  AVG(r.item_count) as avg_items,
  MAX(r.created_at) as last_execution
FROM crawling_job j
LEFT JOIN crawling_result r ON j.job_id = r.job_id
GROUP BY j.job_id, j.job_name;
```

### 실패한 크롤링 조회

```sql
SELECT 
  r.result_id,
  r.job_id,
  j.job_name,
  r.status,
  r.error_message,
  r.created_at
FROM crawling_result r
JOIN crawling_job j ON r.job_id = j.job_id
WHERE r.status = 'FAILED'
ORDER BY r.created_at DESC
LIMIT 10;
```

---

## 백업 및 복구

### 데이터베이스 백업

```bash
mysqldump -u root -p dangdang_crawling > backup_$(date +%Y%m%d_%H%M%S).sql
```

### 데이터베이스 복구

```bash
mysql -u root -p dangdang_crawling < backup_20260226_120000.sql
```

---

## 성능 최적화

### 테이블 최적화

```sql
-- 테이블 최적화
OPTIMIZE TABLE crawling_job;
OPTIMIZE TABLE crawling_result;

-- 통계 업데이트
ANALYZE TABLE crawling_job;
ANALYZE TABLE crawling_result;
```

### 파티셀링 (선택사항)

대량의 크롤링 결과가 누적될 경우 월 단위 파티셀링 고려:

```sql
ALTER TABLE crawling_result
PARTITION BY RANGE (YEAR_MONTH(created_at)) (
  PARTITION p202601 VALUES LESS THAN (202602),
  PARTITION p202602 VALUES LESS THAN (202603),
  PARTITION p202603 VALUES LESS THAN (202604)
);
```

---

## MySQL 설정 권장사항

### my.cnf 설정 예제

```ini
[mysqld]
# 문자셋
character-set-server=utf8mb4
collation-server=utf8mb4_unicode_ci

# 연결 풀
max_connections=1000
max_allowed_packet=256M

# 로그
log_error=/var/log/mysql/error.log
slow_query_log=1
slow_query_log_file=/var/log/mysql/slow.log
long_query_time=2

# InnoDB 설정
innodb_buffer_pool_size=2G
innodb_log_file_size=512M
```

---

## 버전 정보

| 항목 | 권장값 |
|------|--------|
| MySQL | 8.0+ |
| Character Set | utf8mb4 |
| Collation | utf8mb4_unicode_ci |
| Engine | InnoDB |

---

## 마이그레이션

데이터베이스 스키마 변경 시 Spring Boot의 `schema.sql` 또는 `schema-${platform}.sql` 파일을 사용하여 자동 마이그레이션 가능합니다.

자세한 내용은 `src/main/resources/mapper/ddl/` 디렉토리를 참고하세요.

