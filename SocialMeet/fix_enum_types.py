#!/usr/bin/env python3
import os
import re

def fix_enum_types(file_path):
    """修复文件中的枚举类型错误"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        original_content = content
        
        # 修复 Coupon 类
        if 'Coupon.java' in file_path:
            # 修复构造函数参数类型
            content = re.sub(r'public Coupon\(String name, String description, CouponType type,', 
                            'public Coupon(String name, String description, String type,', content)
            # 修复 getter/setter 方法
            content = re.sub(r'public CouponType getType\(\)', 
                            'public String getType()', content)
            content = re.sub(r'public void setType\(CouponType type\)', 
                            'public void setType(String type)', content)
        
        # 修复 Gift 类
        elif 'Gift.java' in file_path:
            content = re.sub(r'public Gift\(String name, String description, BigDecimal price, GiftCategory category\)', 
                            'public Gift(String name, String description, BigDecimal price, String category)', content)
            content = re.sub(r'public GiftCategory getCategory\(\)', 
                            'public String getCategory()', content)
            content = re.sub(r'public void setCategory\(GiftCategory category\)', 
                            'public void setCategory(String category)', content)
        
        # 修复 UserCoupon 类
        elif 'UserCoupon.java' in file_path:
            content = re.sub(r'private CouponStatus status = "UNUSED";', 
                            'private String status = "UNUSED";', content)
            content = re.sub(r'public CouponStatus getStatus\(\)', 
                            'public String getStatus()', content)
            content = re.sub(r'public void setStatus\(CouponStatus status\)', 
                            'public void setStatus(String status)', content)
        
        # 修复 GuardRelationship 类
        elif 'GuardRelationship.java' in file_path:
            content = re.sub(r'private GuardStatus status = "ACTIVE";', 
                            'private String status = "ACTIVE";', content)
            content = re.sub(r'public GuardStatus getStatus\(\)', 
                            'public String getStatus()', content)
            content = re.sub(r'public void setStatus\(GuardStatus status\)', 
                            'public void setStatus(String status)', content)
        
        # 修复 UserView 类
        elif 'UserView.java' in file_path:
            content = re.sub(r'private ViewType viewType = "PROFILE";', 
                            'private String viewType = "PROFILE";', content)
            content = re.sub(r'public UserView\(Long viewerId, Long viewedId, ViewType viewType, Long relatedId\)', 
                            'public UserView(Long viewerId, Long viewedId, String viewType, Long relatedId)', content)
            content = re.sub(r'public ViewType getViewType\(\)', 
                            'public String getViewType()', content)
            content = re.sub(r'public void setViewType\(ViewType viewType\)', 
                            'public void setViewType(String viewType)', content)
        
        # 修复 VipSubscription 类
        elif 'VipSubscription.java' in file_path:
            content = re.sub(r'private SubscriptionStatus status = "ACTIVE";', 
                            'private String status = "ACTIVE";', content)
            content = re.sub(r'public SubscriptionStatus getStatus\(\)', 
                            'public String getStatus()', content)
            content = re.sub(r'public void setStatus\(SubscriptionStatus status\)', 
                            'public void setStatus(String status)', content)
        
        # 修复 IntimacyRelationship 类
        elif 'IntimacyRelationship.java' in file_path:
            content = re.sub(r'private IntimacyLevel level = "STRANGER";', 
                            'private String level = "STRANGER";', content)
            content = re.sub(r'public IntimacyLevel getLevel\(\)', 
                            'public String getLevel()', content)
            content = re.sub(r'public void setLevel\(IntimacyLevel level\)', 
                            'public void setLevel(String level)', content)
        
        if content != original_content:
            with open(file_path, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"Fixed enum types in: {file_path}")
            return True
        return False
        
    except Exception as e:
        print(f"Error fixing {file_path}: {e}")
        return False

def main():
    """主函数"""
    files_to_fix = [
        'src/main/java/com/example/socialmeet/entity/Coupon.java',
        'src/main/java/com/example/socialmeet/entity/Gift.java',
        'src/main/java/com/example/socialmeet/entity/UserCoupon.java',
        'src/main/java/com/example/socialmeet/entity/GuardRelationship.java',
        'src/main/java/com/example/socialmeet/entity/UserView.java',
        'src/main/java/com/example/socialmeet/entity/VipSubscription.java',
        'src/main/java/com/example/socialmeet/entity/IntimacyRelationship.java'
    ]
    
    fixed_count = 0
    for file_path in files_to_fix:
        if os.path.exists(file_path):
            if fix_enum_types(file_path):
                fixed_count += 1
        else:
            print(f"File not found: {file_path}")
    
    print(f"\nFixed enum types in {fixed_count} files")

if __name__ == "__main__":
    main()
