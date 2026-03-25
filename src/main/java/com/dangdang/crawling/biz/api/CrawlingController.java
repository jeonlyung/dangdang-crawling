package com.dangdang.crawling.biz.api;

import com.dangdang.crawling.biz.dto.CrawlingJobDto;
import com.dangdang.crawling.biz.dto.CrawlingResultDto;
import com.dangdang.crawling.biz.dto.PetCrawlingRequestDto;
import com.dangdang.crawling.biz.dto.PetListingDto;
import com.dangdang.crawling.biz.service.CrawlingJobService;
import com.dangdang.crawling.biz.service.CrawlingResultService;
import com.dangdang.crawling.biz.service.crawler.PetAdoptionCrawlerService;
import com.dangdang.crawling.biz.service.crawler.AnimalGoKrPresetService;
import com.dangdang.crawling.biz.service.crawler.PetListingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

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
    private final PetAdoptionCrawlerService petAdoptionCrawlerService;
    private final AnimalGoKrPresetService animalGoKrPresetService;
    private final PetListingSyncService petListingSyncService;

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

    /**
     * 강아지 분양/입양 목록 미리보기 크롤링
     */
    @PostMapping("/pets/preview")
    public ResponseEntity<List<PetListingDto>> previewPetCrawling(@Valid @RequestBody PetCrawlingRequestDto requestDto) {
        log.info("Preview pet crawling: {}", requestDto.getTargetUrl());
        return ResponseEntity.ok(petAdoptionCrawlerService.crawlPetListings(requestDto));
    }

    /**
     * animal.go.kr 크롤링 요청 프리셋 조회
     */
    @GetMapping("/pets/preset/animal-go-kr")
    public ResponseEntity<PetCrawlingRequestDto> getAnimalGoKrPreset(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        return ResponseEntity.ok(animalGoKrPresetService.buildRequest(page, pageSize));
    }

    /**
     * animal.go.kr 기본 설정으로 즉시 미리보기 크롤링
     */
    @GetMapping("/pets/animal-go-kr/preview")
    public ResponseEntity<List<PetListingDto>> previewAnimalGoKr(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int pageSize
    ) {
        PetCrawlingRequestDto requestDto = animalGoKrPresetService.buildRequest(page, pageSize);
        log.info("Preview animal.go.kr crawling: {}", requestDto.getTargetUrl());
        return ResponseEntity.ok(petAdoptionCrawlerService.crawlPetListings(requestDto));
    }

    /**
     * animal.go.kr 데이터를 즉시 DB 동기화
     */
    @PostMapping("/pets/animal-go-kr/sync")
    public ResponseEntity<Map<String, Object>> syncAnimalGoKrNow() {
        int upserted = petListingSyncService.syncAnimalGoKrListings();
        int deactivated = petListingSyncService.deactivateStaleListings();

        return ResponseEntity.ok(Map.of(
                "upserted", upserted,
                "deactivated", deactivated,
                "status", "OK"
        ));
    }

}
