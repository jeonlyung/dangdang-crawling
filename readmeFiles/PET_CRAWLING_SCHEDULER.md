# Pet Crawling Scheduler

animal.go.kr 강아지 분양/입양 데이터를 **주기적으로 수집 + DB 갱신(upsert)** 하도록 설정된 문서입니다.

## 1) 현재 동작 방식

- 스케줄러: `CrawlingScheduler`
- 동기화 서비스: `PetListingSyncService`
- 크롤러: `PetAdoptionCrawlerService`
- 저장 방식: `pet_listing.source_url` 기준 `INSERT ... ON DUPLICATE KEY UPDATE`

즉, 같은 공고 URL은 중복 insert하지 않고 최신 정보로 update 됩니다.

## 2) 기본 주기 (권장)

`application.yaml`

- `app.crawling.pet.cron: "0 */30 * * * *"`
  - 30분마다 수집
- `app.crawling.pet.cleanup-cron: "0 10 3 * * *"`
  - 매일 03:10 오래된 데이터 비활성화
- `app.crawling.pet.page-size: 12`
- `app.crawling.pet.max-pages: 3`
- `app.crawling.pet.stale-hours: 48`

## 3) 테이블

`schema.sql`에 아래 테이블이 추가되어 있어야 합니다.

- `pet_listing`
  - 핵심 컬럼: `breed`, `age`, `region`, `price`, `free_adoption`, `image_url`
  - 중복 기준: `source_url` (UNIQUE)
  - 상태 관리: `active`, `last_seen_at`

## 4) 운영 팁

- 초기에는 `max-pages=1~3`으로 시작 후 트래픽/데이터량 보면서 늘리기
- 너무 짧은 주기(예: 1~2분)는 차단/부하 위험이 있어서 권장하지 않음
- 사이트 구조 변경 시 selector를 먼저 점검

