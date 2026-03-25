package com.dangdang.crawling.biz.service.crawler;

import com.dangdang.crawling.biz.dto.PetCrawlingRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * animal.go.kr 전용 기본 크롤링 설정 제공 서비스
 */
@Slf4j
@Service
public class AnimalGoKrPresetService {

    private static final String BASE_URL = "https://www.animal.go.kr/front/awtis/public/publicAllList.do";

    public PetCrawlingRequestDto buildRequest(int page, int pageSize) {
        int safePage = Math.max(page, 1);
        int safePageSize = Math.max(pageSize, 1);

        String targetUrl = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("menuNo", "1000000064")
                .queryParam("page", safePage)
                .queryParam("pageSize", safePageSize)
                .build()
                .toUriString();

        log.info("Prepared animal.go.kr preset URL: {}", targetUrl);

        return PetCrawlingRequestDto.builder()
                .targetUrl(targetUrl)
                .itemSelector("ul.animals-list > li")
                .breedSelector("li.subject")
                .ageSelector("li.info")
                .regionSelector("li.info")
                .priceSelector("li.info")
                .imageSelector(".thum-img img")
                .imageAttribute("src")
                .detailLinkSelector("a[href]")
                .detailLinkAttribute("href")
                .build();
    }
}

