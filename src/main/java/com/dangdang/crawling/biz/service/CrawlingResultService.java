package com.dangdang.crawling.biz.service;

import com.dangdang.crawling.biz.dto.CrawlingResultDto;

import java.util.List;

/**
 * 크롤링 결과 Service Interface
 */
public interface CrawlingResultService {

    /**
     * 모든 크롤링 결과 조회
     */
    List<CrawlingResultDto> getAllResults();

    /**
     * 특정 작업의 크롤링 결과 조회
     */
    List<CrawlingResultDto> getResultsByJobId(Long jobId);

    /**
     * 특정 결과 조회
     */
    CrawlingResultDto getResultById(Long resultId);

    /**
     * 크롤링 결과 저장
     */
    CrawlingResultDto saveResult(CrawlingResultDto resultDto);

    /**
     * 크롤링 결과 삭제
     */
    void deleteResult(Long resultId);

}

