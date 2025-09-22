import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户充值脚本
 * 给指定用户ID充值10000
 */
public class RechargeScript {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/socialmeet?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&autoReconnect=true";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";
    
    public static void main(String[] args) {
        Long[] userIds = {65899032L, 44479883L};
        BigDecimal rechargeAmount = new BigDecimal("10000.00");
        
        System.out.println("开始执行用户充值操作...");
        System.out.println("充值金额: " + rechargeAmount);
        System.out.println("目标用户ID: " + java.util.Arrays.toString(userIds));
        System.out.println("=====================================");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            conn.setAutoCommit(false); // 开启事务
            
            for (Long userId : userIds) {
                rechargeUser(conn, userId, rechargeAmount);
            }
            
            conn.commit(); // 提交事务
            System.out.println("=====================================");
            System.out.println("所有用户充值操作完成！");
            
            // 查询充值后的余额
            queryUserBalances(conn, userIds);
            
        } catch (SQLException e) {
            System.err.println("充值操作失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void rechargeUser(Connection conn, Long userId, BigDecimal amount) throws SQLException {
        System.out.println("正在为用户 " + userId + " 充值 " + amount + "...");
        
        // 1. 确保用户存在
        ensureUserExists(conn, userId);
        
        // 2. 确保钱包存在
        ensureWalletExists(conn, userId);
        
        // 3. 更新钱包余额
        String updateWalletSql = "UPDATE wallets SET balance = balance + ?, updated_at = NOW() WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateWalletSql)) {
            stmt.setBigDecimal(1, amount);
            stmt.setLong(2, userId);
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                throw new SQLException("更新钱包失败，用户ID: " + userId);
            }
        }
        
        // 4. 记录交易
        String insertTransactionSql = "INSERT INTO transactions (user_id, type, amount, balance_after, description, status, created_at) " +
                                    "SELECT ?, 'RECHARGE', ?, balance, '管理员充值', 'SUCCESS', NOW() FROM wallets WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(insertTransactionSql)) {
            stmt.setLong(1, userId);
            stmt.setBigDecimal(2, amount);
            stmt.setLong(3, userId);
            stmt.executeUpdate();
        }
        
        System.out.println("用户 " + userId + " 充值成功！");
    }
    
    private static void ensureUserExists(Connection conn, Long userId) throws SQLException {
        String checkUserSql = "SELECT COUNT(*) FROM users WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkUserSql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // 用户不存在，创建用户
                    String createUserSql = "INSERT INTO users (id, username, password, nickname, is_active, created_at, updated_at) VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
                    try (PreparedStatement createStmt = conn.prepareStatement(createUserSql)) {
                        createStmt.setLong(1, userId);
                        createStmt.setString(2, "user_" + userId);
                        createStmt.setString(3, "default_password");
                        createStmt.setString(4, "用户" + userId);
                        createStmt.setBoolean(5, true);
                        createStmt.executeUpdate();
                        System.out.println("创建用户: " + userId);
                    }
                }
            }
        }
    }
    
    private static void ensureWalletExists(Connection conn, Long userId) throws SQLException {
        String checkWalletSql = "SELECT COUNT(*) FROM wallets WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkWalletSql)) {
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // 钱包不存在，创建钱包
                    String createWalletSql = "INSERT INTO wallets (user_id, balance, frozen_amount, currency, created_at, updated_at) VALUES (?, 0.00, 0.00, 'CNY', NOW(), NOW())";
                    try (PreparedStatement createStmt = conn.prepareStatement(createWalletSql)) {
                        createStmt.setLong(1, userId);
                        createStmt.executeUpdate();
                        System.out.println("创建钱包: " + userId);
                    }
                }
            }
        }
    }
    
    private static void queryUserBalances(Connection conn, Long[] userIds) throws SQLException {
        System.out.println("\n查询充值后的用户余额:");
        System.out.println("用户ID\t\t用户名\t\t\t余额\t\t货币\t\t更新时间");
        System.out.println("----------------------------------------------------------------");
        
        String querySql = "SELECT u.id, u.username, u.nickname, w.balance, w.currency, w.updated_at " +
                         "FROM users u JOIN wallets w ON u.id = w.user_id " +
                         "WHERE u.id IN (?, ?) ORDER BY u.id";
        
        try (PreparedStatement stmt = conn.prepareStatement(querySql)) {
            stmt.setLong(1, userIds[0]);
            stmt.setLong(2, userIds[1]);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("%-10d\t%-20s\t%.2f\t\t%s\t\t%s%n",
                        rs.getLong("id"),
                        rs.getString("username"),
                        rs.getBigDecimal("balance"),
                        rs.getString("currency"),
                        rs.getTimestamp("updated_at")
                    );
                }
            }
        }
    }
}
