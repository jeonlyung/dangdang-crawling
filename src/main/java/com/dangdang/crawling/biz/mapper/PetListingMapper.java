package com.dangdang.crawling.biz.mapper;

import com.dangdang.crawling.biz.dto.PetListingDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 반려동물 분양/입양 목록 저장 Mapper
 */
@Mapper
public interface PetListingMapper {

    void upsertBatch(@Param("listings") List<PetListingDto> listings);

    int deactivateStale(@Param("threshold") LocalDateTime threshold);
}

