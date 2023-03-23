package com.example.kkaddak.core.repository;

import com.example.kkaddak.core.entity.Auction;
import com.example.kkaddak.core.entity.LikeAuction;
import com.example.kkaddak.core.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface LikeAuctionRepository extends JpaRepository<LikeAuction, Integer> {
    List<LikeAuction> findByLiker(Member liker);
    List<LikeAuction> findByAuction(Auction auction);
}