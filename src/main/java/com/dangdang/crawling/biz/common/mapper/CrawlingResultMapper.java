package com.dangdang.crawling.biz.common.mapper;

import com.dangdang.crawling.biz.common.entity.CrawlingResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 크롤링 결과 MyBatis Mapper
 */
@Mapper
public interface CrawlingResultMapper {

    CrawlingResult selectById(Long resultId);

    List<CrawlingResult> selectByJobId(Long jobId);

    void insert(CrawlingResult crawlingResult);

    void deleteOldResults();

}

