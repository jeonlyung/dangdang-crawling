package com.dangdang.crawling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DangdangCrawlingApplication {

    public static void main(String[] args) {
        SpringApplication.run(DangdangCrawlingApplication.class, args);
    }

}

