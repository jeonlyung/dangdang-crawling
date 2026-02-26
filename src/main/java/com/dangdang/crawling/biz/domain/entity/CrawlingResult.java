package com.dangdang.crawling.biz.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 크롤링 결과 Entity
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingResult {

    private Long resultId;
    private Long jobId;
    private String content;
    private int itemCount;
    private String status;      // SUCCESS, FAILED, PARTIAL
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

