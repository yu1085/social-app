package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.ShopItemDTO;
import com.example.socialmeet.entity.ShopItem;
import com.example.socialmeet.repository.ShopItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shop")
@CrossOrigin(originPatterns = "*")
public class ShopController {

    @Autowired
    private ShopItemRepository shopItemRepository;

    // 获取商品列表
    @GetMapping("/items")
    public ResponseEntity<ApiResponse<List<ShopItemDTO>>> getShopItems(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isLimited) {
        try {
            List<ShopItem> items;
            
            if (category != null && !category.isEmpty()) {
                String itemCategory = String.valueOf(category.toUpperCase());
                items = shopItemRepository.findByCategoryAndIsActiveTrueOrderBySortOrderAsc(itemCategory);
            } else if (isLimited != null && isLimited) {
                items = shopItemRepository.findByIsLimitedTrueAndIsActiveTrueOrderBySortOrderAsc();
            } else {
                items = shopItemRepository.findByIsActiveTrueOrderBySortOrderAsc();
            }

            List<ShopItemDTO> dtos = items.stream()
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取商品列表失败: " + e.getMessage()));
        }
    }

    // 获取商品详情
    @GetMapping("/items/{id}")
    public ResponseEntity<ApiResponse<ShopItemDTO>> getShopItem(@PathVariable Long id) {
        try {
            ShopItem item = shopItemRepository.findById(id).orElse(null);
            if (item == null) {
                return ResponseEntity.badRequest().body(ApiResponse.error("商品不存在"));
            }

            ShopItemDTO dto = convertToDTO(item);
            return ResponseEntity.ok(ApiResponse.success(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取商品详情失败: " + e.getMessage()));
        }
    }

    // 获取商品分类
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getCategories() {
        try {
            List<String> categories = List.of(
                    "VIP", "COINS", "GIFT", "GUARD", "COUPON", 
                    "DECORATION", "EFFECT", "OTHER"
            );
            return ResponseEntity.ok(ApiResponse.success(categories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("获取商品分类失败: " + e.getMessage()));
        }
    }

    // 搜索商品
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ShopItemDTO>>> searchItems(@RequestParam String keyword) {
        try {
            List<ShopItem> items = shopItemRepository.findByIsActiveTrueOrderBySortOrderAsc();
            List<ShopItemDTO> dtos = items.stream()
                    .filter(item -> item.getName().contains(keyword) || 
                                   (item.getDescription() != null && item.getDescription().contains(keyword)))
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(ApiResponse.success(dtos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("搜索商品失败: " + e.getMessage()));
        }
    }

    private ShopItemDTO convertToDTO(ShopItem item) {
        ShopItemDTO dto = new ShopItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setCategory(item.getCategory());
        dto.setPrice(item.getPrice());
        dto.setOriginalPrice(item.getOriginalPrice());
        dto.setCurrency(item.getCurrency());
        dto.setImageUrl(item.getImageUrl());
        dto.setIconUrl(item.getIconUrl());
        dto.setEffectType(item.getEffectType());
        dto.setEffectValue(item.getEffectValue());
        dto.setDurationDays(item.getDurationDays());
        dto.setIsLimited(item.getIsLimited());
        dto.setStockQuantity(item.getStockQuantity());
        dto.setSoldQuantity(item.getSoldQuantity());
        dto.setIsActive(item.getIsActive());
        dto.setSortOrder(item.getSortOrder());
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }
}
