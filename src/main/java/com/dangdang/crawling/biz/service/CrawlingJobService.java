package com.dangdang.crawling.biz.service;

import com.dangdang.crawling.biz.dto.CrawlingJobDto;
import com.dangdang.crawling.biz.dto.CrawlingResultDto;

import java.util.List;

/**
 * 크롤링 작업 Service Interface
 */
public interface CrawlingJobService {

    /**
     * 모든 크롤링 작업 조회
     */
    List<CrawlingJobDto> getAllJobs();

    /**
     * 활성화된 크롤링 작업 조회
     */
    List<CrawlingJobDto> getActiveJobs();

    /**
     * 특정 작업 조회
     */
    CrawlingJobDto getJobById(Long jobId);

    /**
     * 크롤링 작업 생성
     */
    CrawlingJobDto createJob(CrawlingJobDto jobDto);

    /**
     * 크롤링 작업 수정
     */
    CrawlingJobDto updateJob(Long jobId, CrawlingJobDto jobDto);

    /**
     * 크롤링 작업 삭제
     */
    void deleteJob(Long jobId);

    /**
     * 크롤링 작업 실행
     */
    CrawlingResultDto executeCrawling(Long jobId);

}

