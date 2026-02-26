package com.dangdang.crawling.biz.service.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Jsoup 기반 HTML 크롤러 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JsoupCrawlerService {

    private static final int TIMEOUT_SECONDS = 10;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

    /**
     * URL에서 HTML 문서 파싱
     */
    public Document getDocument(String url) {
        try {
            log.info("Fetching document from URL: {}", url);
            return Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout((int) TimeUnit.SECONDS.toMillis(TIMEOUT_SECONDS))
                    .get();
        } catch (IOException e) {
            log.error("Failed to fetch document from URL: {}", url, e);
            throw new RuntimeException("Failed to fetch document: " + url, e);
        }
    }

    /**
     * CSS 선택자를 이용한 요소 추출
     */
    public Elements selectElements(Document doc, String selector) {
        try {
            log.debug("Selecting elements with selector: {}", selector);
            return doc.select(selector);
        } catch (Exception e) {
            log.error("Failed to select elements with selector: {}", selector, e);
            throw new RuntimeException("Invalid CSS selector: " + selector, e);
        }
    }

    /**
     * 요소에서 텍스트 추출
     */
    public List<String> extractText(Elements elements) {
        List<String> results = new ArrayList<>();
        for (Element element : elements) {
            results.add(element.text());
        }
        return results;
    }

    /**
     * 요소에서 속성값 추출
     */
    public List<String> extractAttribute(Elements elements, String attributeName) {
        List<String> results = new ArrayList<>();
        for (Element element : elements) {
            String attrValue = element.attr(attributeName);
            if (!attrValue.isEmpty()) {
                results.add(attrValue);
            }
        }
        return results;
    }

    /**
     * 완전한 크롤링 작업: URL -> 파싱 -> 선택 -> 추출
     */
    public List<String> crawl(String url, String selector, String attributeName) {
        Document doc = getDocument(url);
        Elements elements = selectElements(doc, selector);

        if (attributeName != null && !attributeName.isEmpty()) {
            return extractAttribute(elements, attributeName);
        } else {
            return extractText(elements);
        }
    }

}

