package com.dangdang.crawling.biz.scheduler;

import com.dangdang.crawling.biz.crawler.JsoupCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 정기적 크롤링 스케줄러
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingScheduler {

    private final JsoupCrawlerService jsoupCrawlerService;

    /**
     * 매일 자정에 실행되는 크롤링 작업
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyCrawling() {
        log.info("Starting daily crawling task");
        try {
            // 예제 크롤링 작업
            // TODO: 실제 크롤링 로직 구현
            log.info("Daily crawling task completed");
        } catch (Exception e) {
            log.error("Error in daily crawling task", e);
        }
    }

    /**
     * 매시간 실행되는 크롤링 작업
     */
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyCheck() {
        log.info("Starting hourly check");
        try {
            // 예제 크롤링 작업
            // TODO: 실제 크롤링 로직 구현
            log.info("Hourly check completed");
        } catch (Exception e) {
            log.error("Error in hourly check", e);
        }
    }

}

