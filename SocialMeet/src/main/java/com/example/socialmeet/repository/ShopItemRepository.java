package com.example.socialmeet.repository;

import com.example.socialmeet.entity.ShopItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopItemRepository extends JpaRepository<ShopItem, Long> {
    List<ShopItem> findByIsActiveTrueOrderBySortOrderAsc();
    List<ShopItem> findByCategoryAndIsActiveTrueOrderBySortOrderAsc(String category);
    List<ShopItem> findByIsLimitedTrueAndIsActiveTrueOrderBySortOrderAsc();
}
