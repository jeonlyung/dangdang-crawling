package com.dangdang.crawling.biz.mapper;

import com.dangdang.crawling.biz.domain.entity.CrawlingResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 크롤링 결과 MyBatis Mapper
 */
@Mapper
public interface CrawlingResultMapper {

    List<CrawlingResult> selectAll();

    CrawlingResult selectById(Long resultId);

    List<CrawlingResult> selectByJobId(Long jobId);

    void insert(CrawlingResult crawlingResult);

    void delete(Long resultId);

    void deleteOldResults();

}

