package com.dangdang.crawling.biz.api;

import com.dangdang.crawling.biz.common.dto.CrawlingJobDto;
import com.dangdang.crawling.biz.common.dto.CrawlingResultDto;
import com.dangdang.crawling.biz.crawler.JsoupCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 크롤링 작업 REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/crawling")
@RequiredArgsConstructor
public class CrawlingController {

    private final JsoupCrawlerService jsoupCrawlerService;

    /**
     * 즉시 크롤링 실행
     */
    @PostMapping("/execute")
    public ResponseEntity<?> executeCrawling(@RequestBody CrawlingJobDto jobDto) {
        try {
            log.info("Executing crawling job: {}", jobDto.getJobName());

            List<String> results = jsoupCrawlerService.crawl(
                    jobDto.getTargetUrl(),
                    jobDto.getSelector(),
                    null
            );

            CrawlingResultDto resultDto = CrawlingResultDto.builder()
                    .jobId(jobDto.getJobId())
                    .itemCount(results.size())
                    .status("SUCCESS")
                    .build();

            return ResponseEntity.ok(resultDto);
        } catch (Exception e) {
            log.error("Error executing crawling job", e);
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * 크롤링 상태 조회
     */
    @GetMapping("/status/{jobId}")
    public ResponseEntity<?> getCrawlingStatus(@PathVariable Long jobId) {
        log.info("Getting status for job: {}", jobId);
        return ResponseEntity.ok("Status: RUNNING");
    }

    /**
     * 크롤링 결과 조회
     */
    @GetMapping("/result/{jobId}")
    public ResponseEntity<?> getCrawlingResult(@PathVariable Long jobId) {
        log.info("Getting result for job: {}", jobId);
        return ResponseEntity.ok("Result not found");
    }

}

