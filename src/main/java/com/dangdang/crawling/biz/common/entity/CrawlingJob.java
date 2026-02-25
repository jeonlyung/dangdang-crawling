package com.dangdang.crawling.biz.common.entity;

import lombok.*;

/**
 * 크롤링 작업 엔티티
 */
@Getter
@Setter
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
    private String createdAt;
    private String updatedAt;

}

