package com.dangdang.crawling.biz.common.entity;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 크롤링 결과 엔티티
 */
@Getter
@Setter
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

}

