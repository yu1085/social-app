package com.example.socialmeet.controller;

import com.example.socialmeet.dto.ApiResponse;
import com.example.socialmeet.dto.SearchRequest;
import com.example.socialmeet.dto.SearchResultDTO;
import com.example.socialmeet.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索控制器
 * 
 * @author SocialMeet Team
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(originPatterns = "*")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    /**
     * 综合搜索
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SearchResultDTO>> search(
            @Valid @RequestBody SearchRequest request,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<SearchResultDTO> response = searchService.search(request, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 高级搜索
     */
    @PostMapping("/advanced")
    public ResponseEntity<ApiResponse<SearchResultDTO>> advancedSearch(
            @Valid @RequestBody SearchRequest request,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<SearchResultDTO> response = searchService.advancedSearch(request, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @RequestParam String keyword,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<List<String>> response = searchService.getSearchSuggestions(keyword, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 获取搜索历史
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<String>>> getSearchHistory(
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<List<String>> response = searchService.getSearchHistory(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 保存搜索历史
     */
    @PostMapping("/history")
    public ResponseEntity<ApiResponse<String>> saveSearchHistory(
            @RequestParam String keyword,
            @RequestHeader("Authorization") String token) {
        
        ApiResponse<String> response = searchService.saveSearchHistory(keyword, token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
}
