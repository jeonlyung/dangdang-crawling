package com.dangdang.crawling.biz.service.impl;

import com.dangdang.crawling.biz.domain.entity.CrawlingJob;
import com.dangdang.crawling.biz.domain.entity.CrawlingResult;
import com.dangdang.crawling.biz.dto.CrawlingJobDto;
import com.dangdang.crawling.biz.dto.CrawlingResultDto;
import com.dangdang.crawling.biz.mapper.CrawlingJobMapper;
import com.dangdang.crawling.biz.service.CrawlingJobService;
import com.dangdang.crawling.biz.service.crawler.JsoupCrawlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 크롤링 작업 Service 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingJobServiceImpl implements CrawlingJobService {

    private final CrawlingJobMapper crawlingJobMapper;
    private final JsoupCrawlerService jsoupCrawlerService;

    @Override
    public List<CrawlingJobDto> getAllJobs() {
        log.debug("Fetching all crawling jobs");
        return crawlingJobMapper.selectAll()
                .stream()
                .map(CrawlingJobDto::fromEntity)
                .toList();
    }

    @Override
    public List<CrawlingJobDto> getActiveJobs() {
        log.debug("Fetching active crawling jobs");
        return crawlingJobMapper.selectActive()
                .stream()
                .map(CrawlingJobDto::fromEntity)
                .toList();
    }

    @Override
    public CrawlingJobDto getJobById(Long jobId) {
        log.debug("Fetching crawling job by id: {}", jobId);
        CrawlingJob job = crawlingJobMapper.selectById(jobId);
        return CrawlingJobDto.fromEntity(job);
    }

    @Override
    public CrawlingJobDto createJob(CrawlingJobDto jobDto) {
        log.info("Creating new crawling job: {}", jobDto.getJobName());
        CrawlingJob job = jobDto.toEntity();
        job = CrawlingJob.builder()
                .jobName(job.getJobName())
                .targetUrl(job.getTargetUrl())
                .selector(job.getSelector())
                .description(job.getDescription())
                .active(job.isActive())
                .cronExpression(job.getCronExpression())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        crawlingJobMapper.insert(job);
        return CrawlingJobDto.fromEntity(job);
    }

    @Override
    public CrawlingJobDto updateJob(Long jobId, CrawlingJobDto jobDto) {
        log.info("Updating crawling job: {}", jobId);
        CrawlingJob job = CrawlingJob.builder()
                .jobId(jobId)
                .jobName(jobDto.getJobName())
                .targetUrl(jobDto.getTargetUrl())
                .selector(jobDto.getSelector())
                .description(jobDto.getDescription())
                .active(jobDto.isActive())
                .cronExpression(jobDto.getCronExpression())
                .updatedAt(LocalDateTime.now())
                .build();
        crawlingJobMapper.update(job);
        return CrawlingJobDto.fromEntity(job);
    }

    @Override
    public void deleteJob(Long jobId) {
        log.info("Deleting crawling job: {}", jobId);
        crawlingJobMapper.delete(jobId);
    }

    @Override
    public CrawlingResultDto executeCrawling(Long jobId) {
        log.info("Executing crawling job: {}", jobId);
        CrawlingJob job = crawlingJobMapper.selectById(jobId);

        try {
            List<String> results = jsoupCrawlerService.crawl(
                    job.getTargetUrl(),
                    job.getSelector(),
                    null
            );

            CrawlingResult result = CrawlingResult.builder()
                    .jobId(jobId)
                    .itemCount(results.size())
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now())
                    .build();

            return CrawlingResultDto.fromEntity(result);
        } catch (Exception e) {
            log.error("Error executing crawling job: {}", jobId, e);
            CrawlingResult result = CrawlingResult.builder()
                    .jobId(jobId)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();

            return CrawlingResultDto.fromEntity(result);
        }
    }

}

