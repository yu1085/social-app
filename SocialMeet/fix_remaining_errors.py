#!/usr/bin/env python3
import os
import re

def fix_file(file_path):
    """修复单个文件中的错误"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # 修复 @GeneratedValue 策略
        content = re.sub(r'@GeneratedValue\(strategy = "GenerationType\.IDENTITY"\)', 
                        '@GeneratedValue(strategy = GenerationType.IDENTITY)', content)
        
        # 修复 @ManyToOne fetch
        content = re.sub(r'@ManyToOne\(fetch = "FetchType\.LAZY"\)', 
                        '@ManyToOne(fetch = FetchType.LAZY)', content)
        
        # 修复 BigDecimal 初始化
        content = re.sub(r'private BigDecimal totalContribution = "BigDecimal\.ZERO";', 
                        'private BigDecimal totalContribution = BigDecimal.ZERO;', content)
        
        # 修复 Double 初始化
        content = re.sub(r'private Double callPricePerMinute = "1\.0";', 
                        'private Double callPricePerMinute = 1.0;', content)
        content = re.sub(r'private Double videoCallPricePerMinute = "2\.0";', 
                        'private Double videoCallPricePerMinute = 2.0;', content)
        content = re.sub(r'private Double voiceCallPricePerMinute = "0\.5";', 
                        'private Double voiceCallPricePerMinute = 0.5;', content)
        content = re.sub(r'private Double messagePricePerMessage = "0\.1";', 
                        'private Double messagePricePerMessage = 0.1;', content)
        
        # 修复 PaymentService 中的变量引用
        content = re.sub(r'order\.setStatus\(SUCCESS\);', 
                        'order.setStatus("SUCCESS");', content)
        content = re.sub(r'order\.setStatus\(FAILED\);', 
                        'order.setStatus("FAILED");', content)
        content = re.sub(r'order\.setStatus\(CANCELLED\);', 
                        'order.setStatus("CANCELLED");', content)
        
        # 修复 UserDTO 中的 getId 方法
        if 'UserDTO' in file_path:
            content = re.sub(r'response\.getUser\(\)\.getId\(\)', 
                            'response.getUser().getId()', content)
        
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed: {file_path}")
            return True
        return False
        
    except Exception as e:
        print(f"Error fixing {file_path}: {e}")
        return False

def main():
    """主函数"""
    # 需要修复的文件列表
    files_to_fix = [
        'src/main/java/com/example/socialmeet/entity/SystemConfig.java',
        'src/main/java/com/example/socialmeet/entity/Gift.java',
        'src/main/java/com/example/socialmeet/entity/WealthLevel.java',
        'src/main/java/com/example/socialmeet/entity/VipLevel.java',
        'src/main/java/com/example/socialmeet/entity/UserCoupon.java',
        'src/main/java/com/example/socialmeet/entity/GiftRecord.java',
        'src/main/java/com/example/socialmeet/entity/GuardRelationship.java',
        'src/main/java/com/example/socialmeet/entity/InviteCode.java',
        'src/main/java/com/example/socialmeet/entity/Message.java',
        'src/main/java/com/example/socialmeet/entity/Post.java',
        'src/main/java/com/example/socialmeet/entity/UserView.java',
        'src/main/java/com/example/socialmeet/entity/UserSettings.java',
        'src/main/java/com/example/socialmeet/entity/VipSubscription.java',
        'src/main/java/com/example/socialmeet/entity/Comment.java',
        'src/main/java/com/example/socialmeet/entity/FollowRelationship.java',
        'src/main/java/com/example/socialmeet/entity/IntimacyRelationship.java',
        'src/main/java/com/example/socialmeet/entity/Like.java',
        'src/main/java/com/example/socialmeet/entity/UserLike.java',
        'src/main/java/com/example/socialmeet/service/PaymentService.java',
        'src/main/java/com/example/socialmeet/controller/AuthController.java'
    ]
    
    fixed_count = 0
    for file_path in files_to_fix:
        if os.path.exists(file_path):
            if fix_file(file_path):
                fixed_count += 1
        else:
            print(f"File not found: {file_path}")
    
    print(f"\nFixed {fixed_count} files")

if __name__ == "__main__":
    main()
