package com.example.socialmeet.repository;

import com.example.socialmeet.entity.Gift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GiftRepository extends JpaRepository<Gift, Long> {
    List<Gift> findByIsActiveTrueOrderByPriceAsc();
    List<Gift> findByCategoryAndIsActiveTrueOrderByPriceAsc(String category);
    List<Gift> findByIsActiveTrueAndPriceBetweenOrderByPriceAsc(Double minPrice, Double maxPrice);
}
