package com.dangdang.crawling.biz.crawler;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Selenium 기반 동적 페이지 크롤러 서비스
 */
@Slf4j
@Service
public class SeleniumCrawlerService {

    private static final int TIMEOUT_SECONDS = 10;

    /**
     * Selenium WebDriver 초기화
     */
    private WebDriver initWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        return driver;
    }

    /**
     * URL에 접속하여 페이지 로드 완료 대기
     */
    public WebDriver getPage(String url) {
        try {
            log.info("Loading page from URL: {}", url);
            WebDriver driver = initWebDriver();
            driver.get(url);

            // 페이지 로드 대기
            Thread.sleep(2000);

            return driver;
        } catch (InterruptedException e) {
            log.error("Interrupted while loading page: {}", url, e);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Page loading interrupted", e);
        } catch (Exception e) {
            log.error("Failed to load page from URL: {}", url, e);
            throw new RuntimeException("Failed to load page: " + url, e);
        }
    }

    /**
     * CSS 선택자를 이용한 요소 추출
     */
    public List<WebElement> selectElements(WebDriver driver, String selector) {
        try {
            log.debug("Selecting elements with selector: {}", selector);
            return driver.findElements(By.cssSelector(selector));
        } catch (Exception e) {
            log.error("Failed to select elements with selector: {}", selector, e);
            throw new RuntimeException("Invalid CSS selector: " + selector, e);
        }
    }

    /**
     * 요소에서 텍스트 추출
     */
    public List<String> extractText(List<WebElement> elements) {
        List<String> results = new ArrayList<>();
        for (WebElement element : elements) {
            String text = element.getText();
            if (!text.isEmpty()) {
                results.add(text);
            }
        }
        return results;
    }

    /**
     * 요소에서 속성값 추출
     */
    public List<String> extractAttribute(List<WebElement> elements, String attributeName) {
        List<String> results = new ArrayList<>();
        for (WebElement element : elements) {
            String attrValue = element.getAttribute(attributeName);
            if (attrValue != null && !attrValue.isEmpty()) {
                results.add(attrValue);
            }
        }
        return results;
    }

    /**
     * 완전한 크롤링 작업: URL -> 페이지 로드 -> 선택 -> 추출
     */
    public List<String> crawl(String url, String selector, String attributeName) {
        WebDriver driver = null;
        try {
            driver = getPage(url);
            List<WebElement> elements = selectElements(driver, selector);

            if (attributeName != null && !attributeName.isEmpty()) {
                return extractAttribute(elements, attributeName);
            } else {
                return extractText(elements);
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
    }

}

