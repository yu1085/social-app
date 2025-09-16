package com.example.socialmeet.repository;

import com.example.socialmeet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByPhone(String phone);
    
    Optional<User> findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.gender = :gender AND u.isActive = true")
    List<User> findByGender(@Param("gender") User.Gender gender);
    
    @Query("SELECT u FROM User u WHERE u.nickname LIKE %:keyword% AND u.isActive = true")
    List<User> findByNicknameContaining(@Param("keyword") String keyword);
    
    @Query("SELECT u FROM User u WHERE u.location LIKE %:location% AND u.isActive = true")
    List<User> findByLocationContaining(@Param("location") String location);
}
