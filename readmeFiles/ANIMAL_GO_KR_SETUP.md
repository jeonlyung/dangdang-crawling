# ANIMAL.GO.KR SETUP

`https://www.animal.go.kr` 도메인을 바로 수집할 수 있도록 전용 프리셋 API를 추가했습니다.

## 추가된 엔드포인트

- `GET /api/crawling/pets/preset/animal-go-kr?page=1&pageSize=12`
  - animal.go.kr 전용 `PetCrawlingRequestDto`를 반환합니다.
- `GET /api/crawling/pets/animal-go-kr/preview?page=1&pageSize=12`
  - 프리셋으로 즉시 크롤링하여 목록(JSON)을 반환합니다.

## 수집 필드 매핑

- `breed`: 카드 내 품종 텍스트
- `age`: info 영역 텍스트에서 `나이` 항목 파싱
- `region`: info 영역 텍스트에서 `발견장소/지역/보호장소` 항목 파싱
- `price`: `무료(공공보호)`로 고정
- `freeAdoption`: `true`
- `imageUrl`: 카드 이미지 URL

## 예시 실행

```bash
curl 'http://localhost:8081/api/crawling/pets/animal-go-kr/preview?page=1&pageSize=12'
```

## 참고

- 공공 사이트 UI/HTML은 바뀔 수 있어, 셀렉터가 바뀌면 프리셋 셀렉터도 같이 수정해야 합니다.
- 이 설정은 `보호종료 동물` 목록 경로를 기준으로 맞춰져 있습니다.

