package com.dangdang.crawling.biz.mapper;

import com.dangdang.crawling.biz.dto.CrawlingJobDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 크롤링 작업 MyBatis Mapper
 */
@Mapper
public interface CrawlingJobMapper {

    List<CrawlingJobDto> selectAll();

    CrawlingJobDto selectById(Long jobId);

    List<CrawlingJobDto> selectActive();

    void insert(CrawlingJobDto crawlingJobDto);

    void update(CrawlingJobDto crawlingJobDto);

    void delete(Long jobId);

}

