package com.dangdang.crawling.biz.service.crawler;

import com.dangdang.crawling.biz.dto.PetCrawlingRequestDto;
import com.dangdang.crawling.biz.dto.PetListingDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PetAdoptionCrawlerServiceTest {

    private final PetAdoptionCrawlerService service = new PetAdoptionCrawlerService(new JsoupCrawlerService());

    @Test
    void parseListings_extractsRequiredFields() {
        String html = """
                <html>
                  <body>
                    <div class='pet-item'>
                      <span class='breed'>푸들</span>
                      <span class='age'>2살</span>
                      <span class='region'>서울 강남</span>
                      <span class='price'>무료 분양</span>
                      <img class='photo' src='/images/poodle.jpg' />
                      <a class='detail' href='/pets/1'>상세</a>
                    </div>
                  </body>
                </html>
                """;

        Document document = Jsoup.parse(html, "https://example.com");
        PetCrawlingRequestDto request = PetCrawlingRequestDto.builder()
                .targetUrl("https://example.com")
                .itemSelector(".pet-item")
                .breedSelector(".breed")
                .ageSelector(".age")
                .regionSelector(".region")
                .priceSelector(".price")
                .imageSelector("img.photo")
                .imageAttribute("src")
                .detailLinkSelector("a.detail")
                .detailLinkAttribute("href")
                .build();

        List<PetListingDto> listings = service.parseListings(document, request);

        assertThat(listings).hasSize(1);
        PetListingDto listing = listings.get(0);
        assertThat(listing.getBreed()).isEqualTo("푸들");
        assertThat(listing.getAge()).isEqualTo("2살");
        assertThat(listing.getRegion()).isEqualTo("서울 강남");
        assertThat(listing.getPrice()).isEqualTo("무료 분양");
        assertThat(listing.isFreeAdoption()).isTrue();
        assertThat(listing.getImageUrl()).isEqualTo("https://example.com/images/poodle.jpg");
        assertThat(listing.getSourceUrl()).isEqualTo("https://example.com/pets/1");
    }

    @Test
    void parseListings_animalGoKrPresetParsesAgeRegionAndFreeFlag() {
        String html = """
                <html>
                  <body>
                    <ul class='animals-list'>
                      <li>
                        <a href='/front/awtis/public/publicAllDtl.do?desertionNo=111'>
                          <div class='thum-img'><img src='/front/fileMng/imageView.do?f=/files/shelter/sample.jpg' /></div>
                          <ul>
                            <li class='subject'>믹스견</li>
                            <li class='info'>나이 : 2(년생) / 발견장소 : 서울 강동구 / 성별 : M</li>
                          </ul>
                        </a>
                      </li>
                    </ul>
                  </body>
                </html>
                """;

        Document document = Jsoup.parse(html, "https://www.animal.go.kr");
        PetCrawlingRequestDto request = PetCrawlingRequestDto.builder()
                .targetUrl("https://www.animal.go.kr/front/awtis/public/publicAllList.do?menuNo=1000000064")
                .itemSelector("ul.animals-list > li")
                .breedSelector("li.subject")
                .ageSelector("li.info")
                .regionSelector("li.info")
                .priceSelector("li.info")
                .imageSelector(".thum-img img")
                .imageAttribute("src")
                .detailLinkSelector("a[href]")
                .detailLinkAttribute("href")
                .build();

        List<PetListingDto> listings = service.parseListings(document, request);

        assertThat(listings).hasSize(1);
        PetListingDto listing = listings.get(0);
        assertThat(listing.getBreed()).isEqualTo("믹스견");
        assertThat(listing.getAge()).contains("2(년생)");
        assertThat(listing.getRegion()).contains("서울 강동구");
        assertThat(listing.getPrice()).isEqualTo("무료(공공보호)");
        assertThat(listing.isFreeAdoption()).isTrue();
        assertThat(listing.getImageUrl()).contains("/front/fileMng/imageView.do");
        assertThat(listing.getSourceUrl()).contains("publicAllDtl.do");
    }
}
