package com.dangdang.crawling.biz.mapper;

import com.dangdang.crawling.biz.dto.CrawlingResultDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 크롤링 결과 MyBatis Mapper
 */
@Mapper
public interface CrawlingResultMapper {

    List<CrawlingResultDto> selectAll();

    CrawlingResultDto selectById(Long resultId);

    List<CrawlingResultDto> selectByJobId(Long jobId);

    void insert(CrawlingResultDto crawlingResultDto);

    void delete(Long resultId);

    void deleteOldResults();

}

