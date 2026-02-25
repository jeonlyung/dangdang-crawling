package com.dangdang.crawling.biz.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 크롤링 작업 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingJobDto {

    private Long jobId;
    private String jobName;
    private String targetUrl;
    private String selector;
    private String description;
    private boolean active;
    private String cronExpression;

}

