package com.dangdang.crawling.biz.service.impl;

import com.dangdang.crawling.biz.dto.CrawlingResultDto;
import com.dangdang.crawling.biz.mapper.CrawlingResultMapper;
import com.dangdang.crawling.biz.service.CrawlingResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 크롤링 결과 Service 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingResultServiceImpl implements CrawlingResultService {

    private final CrawlingResultMapper crawlingResultMapper;

    @Override
    public List<CrawlingResultDto> getAllResults() {
        log.debug("Fetching all crawling results");
        return crawlingResultMapper.selectAll();
    }

    @Override
    public List<CrawlingResultDto> getResultsByJobId(Long jobId) {
        log.debug("Fetching crawling results for job: {}", jobId);
        return crawlingResultMapper.selectByJobId(jobId);
    }

    @Override
    public CrawlingResultDto getResultById(Long resultId) {
        log.debug("Fetching crawling result by id: {}", resultId);
        return crawlingResultMapper.selectById(resultId);
    }

    @Override
    public CrawlingResultDto saveResult(CrawlingResultDto resultDto) {
        log.info("Saving crawling result for job: {}", resultDto.getJobId());
        CrawlingResultDto result = CrawlingResultDto.builder()
                .jobId(resultDto.getJobId())
                .content(resultDto.getContent())
                .itemCount(resultDto.getItemCount())
                .status(resultDto.getStatus())
                .errorMessage(resultDto.getErrorMessage())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        crawlingResultMapper.insert(result);
        return result;
    }

    @Override
    public void deleteResult(Long resultId) {
        log.info("Deleting crawling result: {}", resultId);
        crawlingResultMapper.delete(resultId);
    }

}

