package com.socialmeet.backend.config;

import com.socialmeet.backend.entity.LuckyNumber;
import com.socialmeet.backend.repository.LuckyNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化器
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final LuckyNumberRepository luckyNumberRepository;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化靓号数据...");
        
        // 检查是否已有数据
        if (luckyNumberRepository.count() > 0) {
            log.info("靓号数据已存在，跳过初始化");
            return;
        }
        
        // 创建靓号数据
        List<LuckyNumber> luckyNumbers = createLuckyNumbers();
        luckyNumberRepository.saveAll(luckyNumbers);
        
        log.info("成功初始化 {} 个靓号数据", luckyNumbers.size());
    }
    
    private List<LuckyNumber> createLuckyNumbers() {
        return Arrays.asList(
            // 限量靓号
            createLuckyNumber("10000001", new BigDecimal("58800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000002", new BigDecimal("88800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000003", new BigDecimal("58800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000004", new BigDecimal("58800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000005", new BigDecimal("88800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000006", new BigDecimal("8880"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000007", new BigDecimal("88800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000008", new BigDecimal("88800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000009", new BigDecimal("88800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            createLuckyNumber("10000010", new BigDecimal("58800"), LuckyNumber.LuckyNumberTier.LIMITED, "限量靓号"),
            
            // 超级靓号
            createLuckyNumber("12345671", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("23456780", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("23456786", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("34567890", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("12355555", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("12345888", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("98765430", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            
            // 顶级靓号
            createLuckyNumber("66666666", new BigDecimal("10800"), LuckyNumber.LuckyNumberTier.TOP_TIER, "顶级靓号", true),
            createLuckyNumber("88888888", new BigDecimal("10800"), LuckyNumber.LuckyNumberTier.TOP_TIER, "顶级靓号", true),
            createLuckyNumber("33333333", new BigDecimal("108000"), LuckyNumber.LuckyNumberTier.TOP_TIER, "顶级靓号", true),
            createLuckyNumber("77777777", new BigDecimal("10800"), LuckyNumber.LuckyNumberTier.TOP_TIER, "顶级靓号", true),
            createLuckyNumber("55555555", new BigDecimal("10800"), LuckyNumber.LuckyNumberTier.TOP_TIER, "顶级靓号", true),
            createLuckyNumber("96699669", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.TOP_TIER, "顶级靓号", true),
            
            // 其他靓号
            createLuckyNumber("90909090", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("12341234", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("16881688", new BigDecimal("2880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("28002800", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("58005800", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("62226222", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("66006600", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("66806680", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("88808880", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("12211221", new BigDecimal("5880"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("16666616", new BigDecimal("8800"), LuckyNumber.LuckyNumberTier.SUPER, "超级靓号", true),
            createLuckyNumber("16666626", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("16666686", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("18888188", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("18888818", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("18888828", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("18888868", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("18888878", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("19188888", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("19188818", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("88881688", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号"),
            createLuckyNumber("88801888", new BigDecimal("3800"), LuckyNumber.LuckyNumberTier.LIMITED, "靓号")
        );
    }
    
    private LuckyNumber createLuckyNumber(String number, BigDecimal price, LuckyNumber.LuckyNumberTier tier, String description) {
        return createLuckyNumber(number, price, tier, description, false);
    }
    
    private LuckyNumber createLuckyNumber(String number, BigDecimal price, LuckyNumber.LuckyNumberTier tier, String description, boolean isSpecial) {
        LuckyNumber luckyNumber = new LuckyNumber();
        luckyNumber.setNumber(number);
        luckyNumber.setPrice(price);
        luckyNumber.setTier(tier);
        luckyNumber.setStatus(LuckyNumber.LuckyNumberStatus.AVAILABLE);
        luckyNumber.setDescription(description);
        luckyNumber.setIsSpecial(isSpecial);
        luckyNumber.setCreatedAt(LocalDateTime.now());
        luckyNumber.setUpdatedAt(LocalDateTime.now());
        return luckyNumber;
    }
}
