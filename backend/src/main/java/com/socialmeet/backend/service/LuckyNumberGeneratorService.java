package com.socialmeet.backend.service;

import com.socialmeet.backend.entity.LuckyNumber;
import com.socialmeet.backend.repository.LuckyNumberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 靓号生成服务
 * 参考市面上主流应用的靓号生成策略
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LuckyNumberGeneratorService {
    
    private final LuckyNumberRepository luckyNumberRepository;
    
    /**
     * 靓号生成策略枚举
     */
    public enum GenerationStrategy {
        SEQUENTIAL,     // 顺序号：10000001, 10000002...
        REPEATING,      // 重复数字：11111111, 22222222...
        PATTERN,        // 规律模式：12345678, 87654321...
        SPECIAL,        // 特殊数字：5201314, 88888888...
        RANDOM,         // 随机生成
        CUSTOM          // 自定义规则
    }
    
    /**
     * 生成靓号
     */
    public List<LuckyNumber> generateLuckyNumbers(int count, GenerationStrategy strategy) {
        log.info("生成靓号 - count: {}, strategy: {}", count, strategy);
        
        List<LuckyNumber> luckyNumbers = new ArrayList<>();
        Set<String> generatedNumbers = new HashSet<>();
        
        for (int i = 0; i < count; i++) {
            String number = generateSingleNumber(strategy, generatedNumbers);
            if (number != null && !generatedNumbers.contains(number)) {
                LuckyNumber luckyNumber = createLuckyNumber(number);
                luckyNumbers.add(luckyNumber);
                generatedNumbers.add(number);
            }
        }
        
        return luckyNumbers;
    }
    
    /**
     * 生成单个靓号
     */
    private String generateSingleNumber(GenerationStrategy strategy, Set<String> existingNumbers) {
        int maxAttempts = 100;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            String number = switch (strategy) {
                case SEQUENTIAL -> generateSequentialNumber();
                case REPEATING -> generateRepeatingNumber();
                case PATTERN -> generatePatternNumber();
                case SPECIAL -> generateSpecialNumber();
                case RANDOM -> generateRandomNumber();
                case CUSTOM -> generateCustomNumber();
            };
            
            if (number != null && !existingNumbers.contains(number) && isNumberAvailable(number)) {
                return number;
            }
            attempts++;
        }
        
        return null;
    }
    
    /**
     * 生成顺序号
     */
    private String generateSequentialNumber() {
        // 从10000001开始
        long baseNumber = 10000001L;
        long randomOffset = ThreadLocalRandom.current().nextLong(0, 90000000L);
        return String.valueOf(baseNumber + randomOffset);
    }
    
    /**
     * 生成重复数字靓号
     */
    private String generateRepeatingNumber() {
        int[] repeatingDigits = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int digit = repeatingDigits[ThreadLocalRandom.current().nextInt(repeatingDigits.length)];
        int length = ThreadLocalRandom.current().nextInt(6, 9); // 6-8位
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(digit);
        }
        return sb.toString();
    }
    
    /**
     * 生成规律模式靓号
     */
    private String generatePatternNumber() {
        String[] patterns = {
            "12345678", "87654321", "11223344", "44332211",
            "13579246", "24681357", "11112222", "22221111",
            "12344321", "43211234", "11223355", "55332211"
        };
        return patterns[ThreadLocalRandom.current().nextInt(patterns.length)];
    }
    
    /**
     * 生成特殊数字靓号
     */
    private String generateSpecialNumber() {
        String[] specialNumbers = {
            "5201314", "1314520", "88888888", "66666666",
            "99999999", "11111111", "22222222", "33333333",
            "44444444", "55555555", "77777777", "00000000",
            "12345678", "87654321", "11223344", "44332211",
            "16881688", "18888188", "28888888", "21212121"
        };
        return specialNumbers[ThreadLocalRandom.current().nextInt(specialNumbers.length)];
    }
    
    /**
     * 生成随机靓号
     */
    private String generateRandomNumber() {
        int length = ThreadLocalRandom.current().nextInt(6, 9);
        StringBuilder sb = new StringBuilder();
        
        // 第一位不能是0
        sb.append(ThreadLocalRandom.current().nextInt(1, 10));
        
        for (int i = 1; i < length; i++) {
            sb.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成自定义规则靓号
     */
    private String generateCustomNumber() {
        // 自定义规则：包含特殊数字组合
        String[] prefixes = {"88", "66", "99", "11", "22", "33", "44", "55", "77"};
        String[] suffixes = {"88", "66", "99", "11", "22", "33", "44", "55", "77", "00"};
        
        String prefix = prefixes[ThreadLocalRandom.current().nextInt(prefixes.length)];
        String suffix = suffixes[ThreadLocalRandom.current().nextInt(suffixes.length)];
        
        // 中间填充随机数字
        int middleLength = ThreadLocalRandom.current().nextInt(2, 5);
        StringBuilder middle = new StringBuilder();
        for (int i = 0; i < middleLength; i++) {
            middle.append(ThreadLocalRandom.current().nextInt(0, 10));
        }
        
        return prefix + middle.toString() + suffix;
    }
    
    /**
     * 创建靓号实体
     */
    private LuckyNumber createLuckyNumber(String number) {
        LuckyNumber luckyNumber = new LuckyNumber();
        luckyNumber.setNumber(number);
        luckyNumber.setStatus(LuckyNumber.LuckyNumberStatus.AVAILABLE);
        luckyNumber.setIsSpecial(false);
        
        // 根据数字特征确定等级和价格
        LuckyNumber.LuckyNumberTier tier = determineTier(number);
        BigDecimal price = determinePrice(number, tier);
        
        luckyNumber.setTier(tier);
        luckyNumber.setPrice(price);
        luckyNumber.setDescription(generateDescription(number, tier));
        
        return luckyNumber;
    }
    
    /**
     * 确定靓号等级
     */
    private LuckyNumber.LuckyNumberTier determineTier(String number) {
        if (isTopTierNumber(number)) {
            return LuckyNumber.LuckyNumberTier.TOP_TIER;
        } else if (isSuperNumber(number)) {
            return LuckyNumber.LuckyNumberTier.SUPER;
        } else {
            return LuckyNumber.LuckyNumberTier.LIMITED;
        }
    }
    
    /**
     * 判断是否为顶级靓号
     */
    private boolean isTopTierNumber(String number) {
        // 全相同数字且长度>=6
        if (number.length() >= 6 && number.chars().allMatch(c -> c == number.charAt(0))) {
            return true;
        }
        
        // 特殊组合
        String[] topTierPatterns = {
            "88888888", "66666666", "99999999", "11111111",
            "22222222", "33333333", "44444444", "55555555",
            "77777777", "00000000", "12345678", "87654321"
        };
        
        return Arrays.asList(topTierPatterns).contains(number);
    }
    
    /**
     * 判断是否为超级靓号
     */
    private boolean isSuperNumber(String number) {
        // 重复模式
        if (number.matches("(\\d)\\1{3,}")) {
            return true;
        }
        
        // 特殊数字组合
        String[] superPatterns = {
            "5201314", "1314520", "16881688", "18888188",
            "28888888", "21212121", "12344321", "43211234"
        };
        
        return Arrays.asList(superPatterns).contains(number);
    }
    
    /**
     * 确定价格
     */
    private BigDecimal determinePrice(String number, LuckyNumber.LuckyNumberTier tier) {
        return switch (tier) {
            case TOP_TIER -> {
                if (number.length() >= 8) {
                    yield new BigDecimal("108000");
                } else {
                    yield new BigDecimal("10800");
                }
            }
            case SUPER -> new BigDecimal("8800");
            case LIMITED -> {
                if (number.startsWith("10000")) {
                    yield new BigDecimal("58800");
                } else {
                    yield new BigDecimal("3800");
                }
            }
        };
    }
    
    /**
     * 生成描述
     */
    private String generateDescription(String number, LuckyNumber.LuckyNumberTier tier) {
        return switch (tier) {
            case TOP_TIER -> "顶级靓号 - " + number;
            case SUPER -> "超级靓号 - " + number;
            case LIMITED -> "限量靓号 - " + number;
        };
    }
    
    /**
     * 检查靓号是否可用
     */
    private boolean isNumberAvailable(String number) {
        return luckyNumberRepository.findByNumber(number).isEmpty();
    }
    
    /**
     * 批量生成并保存靓号
     */
    public void generateAndSaveLuckyNumbers(int count, GenerationStrategy strategy) {
        log.info("批量生成并保存靓号 - count: {}, strategy: {}", count, strategy);
        
        List<LuckyNumber> luckyNumbers = generateLuckyNumbers(count, strategy);
        luckyNumberRepository.saveAll(luckyNumbers);
        
        log.info("成功生成并保存 {} 个靓号", luckyNumbers.size());
    }
}
