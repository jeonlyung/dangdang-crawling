package com.dangdang.crawling.biz.service.crawler;

import com.dangdang.crawling.biz.dto.PetCrawlingRequestDto;
import com.dangdang.crawling.biz.dto.PetListingDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 강아지 분양/입양 정보 전용 크롤링 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetAdoptionCrawlerService {

    private final JsoupCrawlerService jsoupCrawlerService;

    private static final Pattern AGE_PATTERN = Pattern.compile("(나이|추정나이)\\s*[:：]?\\s*([^|/\\n]+)");
    private static final Pattern REGION_PATTERN = Pattern.compile("(발견장소|지역|보호장소)\\s*[:：]?\\s*([^|/\\n]+)");

    public List<PetListingDto> crawlPetListings(PetCrawlingRequestDto requestDto) {
        log.info("Crawling pet listings from URL: {}", requestDto.getTargetUrl());
        Document document = jsoupCrawlerService.getDocument(requestDto.getTargetUrl());
        return parseListings(document, requestDto);
    }

    public List<PetListingDto> parseListings(Document document, PetCrawlingRequestDto requestDto) {
        Elements items = document.select(requestDto.getItemSelector());
        List<PetListingDto> listings = new ArrayList<>();
        boolean animalGoKr = isAnimalGoKr(requestDto.getTargetUrl());

        for (Element item : items) {
            String infoText = extractText(item, requestDto.getAgeSelector());
            String breed = extractText(item, requestDto.getBreedSelector());
            String age = animalGoKr ? extractFromInfo(infoText, AGE_PATTERN, extractText(item, requestDto.getAgeSelector()))
                    : extractText(item, requestDto.getAgeSelector());
            String region = animalGoKr ? extractFromInfo(infoText, REGION_PATTERN, extractText(item, requestDto.getRegionSelector()))
                    : extractText(item, requestDto.getRegionSelector());
            String price = animalGoKr ? "무료(공공보호)" : normalizePrice(extractText(item, requestDto.getPriceSelector()));
            String imageUrl = extractAttribute(item, requestDto.getImageSelector(), requestDto.getImageAttribute());
            String sourceUrl = extractAttribute(item, requestDto.getDetailLinkSelector(), requestDto.getDetailLinkAttribute());

            listings.add(PetListingDto.builder()
                    .breed(breed)
                    .age(age)
                    .region(region)
                    .price(price)
                    .freeAdoption(animalGoKr || isFreeAdoption(price))
                    .imageUrl(imageUrl)
                    .sourceUrl(sourceUrl)
                    .build());
        }

        return listings;
    }

    private String extractText(Element item, String selector) {
        if (selector == null || selector.isBlank()) {
            return null;
        }
        Element selected = item.selectFirst(selector);
        if (selected == null) {
            return null;
        }
        String value = selected.text();
        return value.trim();
    }

    private String extractAttribute(Element item, String selector, String attribute) {
        if (selector == null || selector.isBlank() || attribute == null || attribute.isBlank()) {
            return null;
        }

        Element selected = item.selectFirst(selector);
        if (selected == null) {
            return null;
        }

        String absoluteValue = selected.absUrl(attribute);
        if (!absoluteValue.isBlank()) {
            return absoluteValue;
        }

        String value = selected.attr(attribute);
        return value.isBlank() ? null : value.trim();
    }

    private String normalizePrice(String rawPrice) {
        if (rawPrice == null) {
            return null;
        }
        return rawPrice.replaceAll("\\s+", " ").trim();
    }

    private boolean isFreeAdoption(String price) {
        if (price == null || price.isBlank()) {
            return false;
        }

        String normalized = price.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
        return normalized.contains("무료")
                || normalized.equals("0")
                || normalized.equals("0원")
                || normalized.contains("무료분양");
    }

    private boolean isAnimalGoKr(String targetUrl) {
        return targetUrl != null && targetUrl.contains("animal.go.kr");
    }

    private String extractFromInfo(String infoText, Pattern pattern, String fallback) {
        if (infoText == null || infoText.isBlank()) {
            return fallback;
        }
        Matcher matcher = pattern.matcher(infoText);
        if (matcher.find() && matcher.groupCount() >= 2) {
            return matcher.group(2).trim();
        }
        return fallback;
    }
}
