package com.dangdang.crawling.biz.service.crawler;

import com.dangdang.crawling.biz.dto.PetCrawlingRequestDto;
import com.dangdang.crawling.biz.dto.PetListingDto;
import com.dangdang.crawling.biz.mapper.PetListingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * animal.go.kr 목록을 DB에 동기화하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PetListingSyncService {

    private final AnimalGoKrPresetService animalGoKrPresetService;
    private final PetAdoptionCrawlerService petAdoptionCrawlerService;
    private final PetListingMapper petListingMapper;

    @Value("${app.crawling.pet.page-size:12}")
    private int pageSize;

    @Value("${app.crawling.pet.max-pages:3}")
    private int maxPages;

    @Value("${app.crawling.pet.stale-hours:48}")
    private int staleHours;

    @Transactional
    public int syncAnimalGoKrListings() {
        List<PetListingDto> allListings = new ArrayList<>();

        for (int page = 1; page <= maxPages; page++) {
            PetCrawlingRequestDto request = animalGoKrPresetService.buildRequest(page, pageSize);
            List<PetListingDto> crawled = petAdoptionCrawlerService.crawlPetListings(request);

            for (int index = 0; index < crawled.size(); index++) {
                PetListingDto normalized = normalize(crawled.get(index), request.getTargetUrl(), page, index);
                if (normalized.getSourceUrl() != null && !normalized.getSourceUrl().isBlank()) {
                    allListings.add(normalized);
                }
            }
        }

        if (allListings.isEmpty()) {
            log.warn("No pet listings crawled. Skip DB upsert.");
            return 0;
        }

        petListingMapper.upsertBatch(allListings);
        log.info("Pet listing upsert finished. count={}", allListings.size());

        return allListings.size();
    }

    @Transactional
    public int deactivateStaleListings() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(staleHours);
        int updated = petListingMapper.deactivateStale(threshold);
        log.info("Stale pet listings deactivated. threshold={}, affected={}", threshold, updated);
        return updated;
    }

    private PetListingDto normalize(PetListingDto listing, String baseUrl, int page, int index) {
        String sourceUrl = trim(listing.getSourceUrl());
        if (sourceUrl == null) {
            sourceUrl = createFallbackSourceUrl(baseUrl, listing, page, index);
        }

        return PetListingDto.builder()
                .breed(trim(listing.getBreed()))
                .age(trim(listing.getAge()))
                .region(trim(listing.getRegion()))
                .price(trim(listing.getPrice()))
                .freeAdoption(listing.isFreeAdoption())
                .imageUrl(trim(listing.getImageUrl()))
                .sourceUrl(sourceUrl)
                .build();
    }

    private String createFallbackSourceUrl(String baseUrl, PetListingDto listing, int page, int index) {
        String rawKey = String.join("|",
                defaultString(baseUrl),
                defaultString(listing.getBreed()),
                defaultString(listing.getAge()),
                defaultString(listing.getRegion()),
                defaultString(listing.getImageUrl()),
                String.valueOf(page),
                String.valueOf(index));

        UUID stableId = UUID.nameUUIDFromBytes(rawKey.getBytes(StandardCharsets.UTF_8));
        return defaultString(baseUrl) + "#generated=" + stableId;
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}

