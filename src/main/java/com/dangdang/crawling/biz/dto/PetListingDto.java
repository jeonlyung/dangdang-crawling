package com.dangdang.crawling.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 강아지 분양/입양 목록 단건 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetListingDto {

    private String breed;
    private String age;
    private String region;
    private String price;
    private boolean freeAdoption;
    private String imageUrl;
    private String sourceUrl;
}

