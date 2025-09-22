import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class GenerateRSAKeys {
    public static void main(String[] args) {
        try {
            // 生成RSA密钥对
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            
            // 转换为Base64格式
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            
            System.out.println("=== RSA密钥对生成完成 ===");
            System.out.println();
            System.out.println("应用私钥（PKCS8格式）:");
            System.out.println("-----BEGIN PRIVATE KEY-----");
            System.out.println(formatKey(privateKeyBase64));
            System.out.println("-----END PRIVATE KEY-----");
            System.out.println();
            System.out.println("应用公钥:");
            System.out.println("-----BEGIN PUBLIC KEY-----");
            System.out.println(formatKey(publicKeyBase64));
            System.out.println("-----END PUBLIC KEY-----");
            System.out.println();
            System.out.println("请将应用公钥上传到支付宝开放平台");
            System.out.println("然后从开放平台获取支付宝公钥");
            
        } catch (Exception e) {
            System.err.println("生成密钥失败: " + e.getMessage());
        }
    }
    
    private static String formatKey(String key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < key.length(); i += 64) {
            int end = Math.min(i + 64, key.length());
            sb.append(key.substring(i, end));
            if (end < key.length()) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}
