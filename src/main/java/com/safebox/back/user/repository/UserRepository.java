package com.safebox.back.user.repository;

import com.safebox.back.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 로그인 ID로 사용자 조회
     */
    Optional<User> findByLoginId(String loginId);

    /**
     * 로그인 ID 존재 여부 확인
     */
    boolean existsByLoginId(String loginId);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * 로그인 ID 또는 이메일로 사용자 조회
     */
    @Query("SELECT u FROM User u WHERE u.loginId = :identifier OR u.email = :identifier")
    Optional<User> findByLoginIdOrEmail(@Param("identifier") String identifier);
}