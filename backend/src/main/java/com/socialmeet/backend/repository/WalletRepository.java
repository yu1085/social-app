package com.socialmeet.backend.repository;

import com.socialmeet.backend.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 钱包Repository
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * 根据用户ID查找钱包
     */
    Optional<Wallet> findByUserId(Long userId);

    /**
     * 根据用户ID删除钱包
     */
    void deleteByUserId(Long userId);
}
