package com.dangdang.crawling.biz.service.scheduler;

import com.dangdang.crawling.biz.service.crawler.PetListingSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 정기적 크롤링 스케줄러
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingScheduler {

    private final PetListingSyncService petListingSyncService;

    @Value("${app.crawling.pet.enabled:true}")
    private boolean petSyncEnabled;

    /**
     * animal.go.kr 목록을 주기적으로 수집해서 DB를 갱신
     */
    @Scheduled(cron = "${app.crawling.pet.cron:0 */30 * * * *}")
    public void syncAnimalGoKr() {
        if (!petSyncEnabled) {
            log.info("Pet listing sync is disabled by config.");
            return;
        }

        log.info("Starting scheduled animal.go.kr sync");
        try {
            int count = petListingSyncService.syncAnimalGoKrListings();
            log.info("Scheduled animal.go.kr sync completed. upserted={}", count);
        } catch (Exception e) {
            log.error("Error in scheduled animal.go.kr sync", e);
        }
    }

    /**
     * 장시간 보이지 않는 목록을 비활성화
     */
    @Scheduled(cron = "${app.crawling.pet.cleanup-cron:0 10 3 * * *}")
    public void deactivateStaleListings() {
        if (!petSyncEnabled) {
            return;
        }

        try {
            int updated = petListingSyncService.deactivateStaleListings();
            log.info("Stale pet listing cleanup completed. deactivated={}", updated);
        } catch (Exception e) {
            log.error("Error in stale pet listing cleanup", e);
        }
    }
}
