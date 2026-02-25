package com.dangdang.crawling.biz.common.mapper;

import com.dangdang.crawling.biz.common.entity.CrawlingJob;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 크롤링 작업 MyBatis Mapper
 */
@Mapper
public interface CrawlingJobMapper {

    List<CrawlingJob> selectAll();

    CrawlingJob selectById(Long jobId);

    List<CrawlingJob> selectActive();

    void insert(CrawlingJob crawlingJob);

    void update(CrawlingJob crawlingJob);

    void delete(Long jobId);

}

