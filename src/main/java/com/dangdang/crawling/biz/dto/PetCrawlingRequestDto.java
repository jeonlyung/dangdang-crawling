package com.dangdang.crawling.biz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 반려동물 분양/입양 목록 크롤링 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetCrawlingRequestDto {

    @NotBlank
    private String targetUrl;

    @NotBlank
    private String itemSelector;

    @NotBlank
    private String breedSelector;

    @NotBlank
    private String ageSelector;

    @NotBlank
    private String regionSelector;

    @NotBlank
    private String priceSelector;

    @NotBlank
    private String imageSelector;

    @Builder.Default
    private String imageAttribute = "src";

    private String detailLinkSelector;

    @Builder.Default
    private String detailLinkAttribute = "href";
}

