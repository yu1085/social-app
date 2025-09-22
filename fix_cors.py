import os
import re

def fix_cors_annotations(directory):
    """Fix all @CrossOrigin annotations to use originPatterns instead of origins"""
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.java'):
                file_path = os.path.join(root, file)
                try:
                    with open(file_path, 'r', encoding='utf-8') as f:
                        content = f.read()
                    
                    # Replace @CrossOrigin(origins = "*") with @CrossOrigin(originPatterns = "*")
                    original_content = content
                    content = re.sub(
                        r'@CrossOrigin\(origins\s*=\s*"\*"\)',
                        '@CrossOrigin(originPatterns = "*")',
                        content
                    )
                    
                    # Only write if content changed
                    if content != original_content:
                        with open(file_path, 'w', encoding='utf-8') as f:
                            f.write(content)
                        print(f"Fixed: {file_path}")
                        
                except Exception as e:
                    print(f"Error processing {file_path}: {e}")

if __name__ == "__main__":
    fix_cors_annotations("SocialMeet/src")
    print("CORS annotation fixes completed!")
