package com.dangdang.crawling.biz.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 크롤링 작업 Entity
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingJob {

    private Long jobId;
    private String jobName;
    private String targetUrl;
    private String selector;
    private String description;
    private boolean active;
    private String cronExpression;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}

