package com.dangdang.crawling.biz.api;

import com.dangdang.crawling.biz.dto.CrawlingJobDto;
import com.dangdang.crawling.biz.dto.CrawlingResultDto;
import com.dangdang.crawling.biz.service.CrawlingJobService;
import com.dangdang.crawling.biz.service.CrawlingResultService;
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

    private final CrawlingJobService crawlingJobService;
    private final CrawlingResultService crawlingResultService;

    /**
     * 모든 크롤링 작업 조회
     */
    @GetMapping("/jobs")
    public ResponseEntity<List<CrawlingJobDto>> getAllJobs() {
        log.info("Getting all crawling jobs");
        return ResponseEntity.ok(crawlingJobService.getAllJobs());
    }

    /**
     * 활성화된 크롤링 작업 조회
     */
    @GetMapping("/jobs/active")
    public ResponseEntity<List<CrawlingJobDto>> getActiveJobs() {
        log.info("Getting active crawling jobs");
        return ResponseEntity.ok(crawlingJobService.getActiveJobs());
    }

    /**
     * 특정 크롤링 작업 조회
     */
    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<CrawlingJobDto> getJob(@PathVariable Long jobId) {
        log.info("Getting crawling job: {}", jobId);
        return ResponseEntity.ok(crawlingJobService.getJobById(jobId));
    }

    /**
     * 크롤링 작업 생성
     */
    @PostMapping("/jobs")
    public ResponseEntity<CrawlingJobDto> createJob(@RequestBody CrawlingJobDto jobDto) {
        log.info("Creating crawling job: {}", jobDto.getJobName());
        return ResponseEntity.ok(crawlingJobService.createJob(jobDto));
    }

    /**
     * 크롤링 작업 수정
     */
    @PutMapping("/jobs/{jobId}")
    public ResponseEntity<CrawlingJobDto> updateJob(@PathVariable Long jobId, @RequestBody CrawlingJobDto jobDto) {
        log.info("Updating crawling job: {}", jobId);
        return ResponseEntity.ok(crawlingJobService.updateJob(jobId, jobDto));
    }

    /**
     * 크롤링 작업 삭제
     */
    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId) {
        log.info("Deleting crawling job: {}", jobId);
        crawlingJobService.deleteJob(jobId);
        return ResponseEntity.ok().build();
    }

    /**
     * 즉시 크롤링 실행
     */
    @PostMapping("/jobs/{jobId}/execute")
    public ResponseEntity<CrawlingResultDto> executeCrawling(@PathVariable Long jobId) {
        log.info("Executing crawling job: {}", jobId);
        CrawlingResultDto result = crawlingJobService.executeCrawling(jobId);
        return ResponseEntity.ok(result);
    }

    /**
     * 모든 크롤링 결과 조회
     */
    @GetMapping("/results")
    public ResponseEntity<List<CrawlingResultDto>> getAllResults() {
        log.info("Getting all crawling results");
        return ResponseEntity.ok(crawlingResultService.getAllResults());
    }

    /**
     * 특정 작업의 크롤링 결과 조회
     */
    @GetMapping("/results/job/{jobId}")
    public ResponseEntity<List<CrawlingResultDto>> getResultsByJobId(@PathVariable Long jobId) {
        log.info("Getting crawling results for job: {}", jobId);
        return ResponseEntity.ok(crawlingResultService.getResultsByJobId(jobId));
    }

    /**
     * 특정 크롤링 결과 조회
     */
    @GetMapping("/results/{resultId}")
    public ResponseEntity<CrawlingResultDto> getResult(@PathVariable Long resultId) {
        log.info("Getting crawling result: {}", resultId);
        return ResponseEntity.ok(crawlingResultService.getResultById(resultId));
    }

}
