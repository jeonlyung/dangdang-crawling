# PET CRAWLING GUIDE

강아지 분양/입양 목록에서 아래 항목을 수집하는 방법입니다.

- 품종 (`breed`)
- 나이 (`age`)
- 지역 (`region`)
- 가격/무료분양 여부 (`price`, `freeAdoption`)
- 사진 (`imageUrl`)

## API

- `POST /api/crawling/pets/preview`

요청 바디에서 사이트 구조(CSS selector)를 전달하면, 서버가 목록을 파싱해서 JSON으로 반환합니다.

## Request Example

```json
{
  "targetUrl": "https://example.com/pets",
  "itemSelector": ".pet-item",
  "breedSelector": ".breed",
  "ageSelector": ".age",
  "regionSelector": ".region",
  "priceSelector": ".price",
  "imageSelector": "img.photo",
  "imageAttribute": "src",
  "detailLinkSelector": "a.detail",
  "detailLinkAttribute": "href"
}
```

## cURL Example

```bash
curl -X POST 'http://localhost:8081/api/crawling/pets/preview' \
  -H 'Content-Type: application/json' \
  -d '{
    "targetUrl": "https://example.com/pets",
    "itemSelector": ".pet-item",
    "breedSelector": ".breed",
    "ageSelector": ".age",
    "regionSelector": ".region",
    "priceSelector": ".price",
    "imageSelector": "img.photo",
    "imageAttribute": "src",
    "detailLinkSelector": "a.detail",
    "detailLinkAttribute": "href"
  }'
```

## Response Example

```json
[
  {
    "breed": "푸들",
    "age": "2살",
    "region": "서울 강남",
    "price": "무료 분양",
    "freeAdoption": true,
    "imageUrl": "https://example.com/images/poodle.jpg",
    "sourceUrl": "https://example.com/pets/1"
  }
]
```

## Notes

- 사이트마다 CSS selector가 다르므로 브라우저 개발자도구(F12)로 먼저 구조를 확인해야 합니다.
- 동적 렌더링 사이트라면 Selenium 크롤러를 별도로 연결해야 할 수 있습니다.
- 대상 사이트의 이용약관/robots.txt를 먼저 확인하고 요청 간격을 조절하세요.

## Animal.go.kr Quick Start

- 프리셋 조회: `GET /api/crawling/pets/preset/animal-go-kr?page=1&pageSize=12`
- 바로 실행: `GET /api/crawling/pets/animal-go-kr/preview?page=1&pageSize=12`

```bash
curl 'http://localhost:8081/api/crawling/pets/animal-go-kr/preview?page=1&pageSize=12'
```
