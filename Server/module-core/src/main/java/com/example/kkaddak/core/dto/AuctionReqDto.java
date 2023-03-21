package com.example.kkaddak.core.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionReqDto {

    private String nftId;
    private String creatorName;
    private String nftImagePath;
    private String songTitle;
    private LocalDate expiredDate;
    private Double bidStartPrice;

    @Builder
    public AuctionReqDto(String nftId, String nftImagePath, String creatorName, String songTitle, String expiredDate, Double bidStartPrice) {
        this.nftId = nftId;
        this.nftImagePath = nftImagePath;
        this.creatorName = creatorName;
        this.songTitle = songTitle;
        this.expiredDate = LocalDate.parse(expiredDate, DateTimeFormatter.ISO_DATE);
        this.bidStartPrice = bidStartPrice;
    }
}
