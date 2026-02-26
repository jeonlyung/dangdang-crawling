package com.dangdang.crawling.biz.dto;

import com.dangdang.crawling.biz.domain.entity.CrawlingJob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 크롤링 작업 DTO (Request/Response)
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
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity to DTO
     */
    public static CrawlingJobDto fromEntity(CrawlingJob entity) {
        return CrawlingJobDto.builder()
                .jobId(entity.getJobId())
                .jobName(entity.getJobName())
                .targetUrl(entity.getTargetUrl())
                .selector(entity.getSelector())
                .description(entity.getDescription())
                .active(entity.isActive())
                .cronExpression(entity.getCronExpression())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * DTO to Entity
     */
    public CrawlingJob toEntity() {
        return CrawlingJob.builder()
                .jobId(this.jobId)
                .jobName(this.jobName)
                .targetUrl(this.targetUrl)
                .selector(this.selector)
                .description(this.description)
                .active(this.active)
                .cronExpression(this.cronExpression)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

}

