package com.dangdang.crawling.biz.dto;

import com.dangdang.crawling.biz.domain.entity.CrawlingResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 크롤링 결과 DTO (Request/Response)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlingResultDto {

    private Long resultId;
    private Long jobId;
    private String content;
    private int itemCount;
    private String status;      // SUCCESS, FAILED, PARTIAL
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Entity to DTO
     */
    public static CrawlingResultDto fromEntity(CrawlingResult entity) {
        return CrawlingResultDto.builder()
                .resultId(entity.getResultId())
                .jobId(entity.getJobId())
                .content(entity.getContent())
                .itemCount(entity.getItemCount())
                .status(entity.getStatus())
                .errorMessage(entity.getErrorMessage())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * DTO to Entity
     */
    public CrawlingResult toEntity() {
        return CrawlingResult.builder()
                .resultId(this.resultId)
                .jobId(this.jobId)
                .content(this.content)
                .itemCount(this.itemCount)
                .status(this.status)
                .errorMessage(this.errorMessage)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

}

