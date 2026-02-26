package com.dangdang.crawling.biz.service.impl;

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
        return crawlingJobMapper.selectAll();
    }

    @Override
    public List<CrawlingJobDto> getActiveJobs() {
        log.debug("Fetching active crawling jobs");
        return crawlingJobMapper.selectActive();
    }

    @Override
    public CrawlingJobDto getJobById(Long jobId) {
        log.debug("Fetching crawling job by id: {}", jobId);
        return crawlingJobMapper.selectById(jobId);
    }

    @Override
    public CrawlingJobDto createJob(CrawlingJobDto jobDto) {
        log.info("Creating new crawling job: {}", jobDto.getJobName());
        CrawlingJobDto job = CrawlingJobDto.builder()
                .jobName(jobDto.getJobName())
                .targetUrl(jobDto.getTargetUrl())
                .selector(jobDto.getSelector())
                .description(jobDto.getDescription())
                .active(jobDto.isActive())
                .cronExpression(jobDto.getCronExpression())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        crawlingJobMapper.insert(job);
        return job;
    }

    @Override
    public CrawlingJobDto updateJob(Long jobId, CrawlingJobDto jobDto) {
        log.info("Updating crawling job: {}", jobId);
        CrawlingJobDto job = CrawlingJobDto.builder()
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
        return job;
    }

    @Override
    public void deleteJob(Long jobId) {
        log.info("Deleting crawling job: {}", jobId);
        crawlingJobMapper.delete(jobId);
    }

    @Override
    public CrawlingResultDto executeCrawling(Long jobId) {
        log.info("Executing crawling job: {}", jobId);
        CrawlingJobDto job = crawlingJobMapper.selectById(jobId);

        try {
            List<String> results = jsoupCrawlerService.crawl(
                    job.getTargetUrl(),
                    job.getSelector(),
                    null
            );

            CrawlingResultDto result = CrawlingResultDto.builder()
                    .jobId(jobId)
                    .itemCount(results.size())
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now())
                    .build();

            return result;
        } catch (Exception e) {
            log.error("Error executing crawling job: {}", jobId, e);
            CrawlingResultDto result = CrawlingResultDto.builder()
                    .jobId(jobId)
                    .status("FAILED")
                    .errorMessage(e.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();

            return result;
        }
    }

}

