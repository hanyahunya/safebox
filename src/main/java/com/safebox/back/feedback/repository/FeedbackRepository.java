package com.safebox.back.feedback.repository;

import com.safebox.back.feedback.entity.Feedback;
import com.safebox.back.feedback.entity.FeedbackStatus;
import com.safebox.back.feedback.entity.FeedbackType;
import com.safebox.back.user.entity.User;  // 올바른 import 경로
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, String> {

    // 생성일 기준 내림차순 정렬로 모든 피드백 조회 (페이징)
    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // User 객체로 피드백 조회 (생성일 기준 내림차순)
    List<Feedback> findByUserOrderByCreatedAtDesc(User user);

    // User ID로 피드백 조회 (생성일 기준 내림차순) - 편의 메서드
    @Query("SELECT f FROM Feedback f WHERE f.user.id = :userId ORDER BY f.createdAt DESC")
    List<Feedback> findByUserIdOrderByCreatedAtDesc(@Param("userId") String userId);

    // 제품번호로 피드백 조회 (생성일 기준 내림차순)
    List<Feedback> findByProductNumberOrderByCreatedAtDesc(String productNumber);

    // 전화번호로 피드백 조회 (생성일 기준 내림차순)
    List<Feedback> findByPhoneNumberOrderByCreatedAtDesc(String phoneNumber);

    // 상태별 피드백 조회 (생성일 기준 내림차순)
    List<Feedback> findByStatusOrderByCreatedAtDesc(FeedbackStatus status);

    // 타입별 피드백 조회 (생성일 기준 내림차순)
    List<Feedback> findByTypeOrderByCreatedAtDesc(FeedbackType type);

    // 상태별 개수 조회
    long countByStatus(FeedbackStatus status);

    // 타입별 개수 조회
    long countByType(FeedbackType type);

    // 키워드로 피드백 검색 (제품번호, 전화번호, 내용에서 검색)
    @Query("SELECT f FROM Feedback f WHERE " +
            "f.productNumber LIKE %:keyword% OR " +
            "f.phoneNumber LIKE %:keyword% OR " +
            "f.content LIKE %:keyword% " +
            "ORDER BY f.createdAt DESC")
    List<Feedback> findByKeyword(@Param("keyword") String keyword);

    // 답변이 없는 피드백 조회
    @Query("SELECT f FROM Feedback f WHERE f.adminReply IS NULL ORDER BY f.createdAt DESC")
    List<Feedback> findUnansweredFeedbacks();

    // 최근 N일 이내 피드백 조회
    @Query("SELECT f FROM Feedback f WHERE f.createdAt >= :startDate ORDER BY f.createdAt DESC")
    List<Feedback> findRecentFeedbacks(@Param("startDate") LocalDateTime startDate);

    // 오늘 생성된 피드백 개수
    @Query("SELECT COUNT(f) FROM Feedback f WHERE DATE(f.createdAt) = CURRENT_DATE")
    long countTodayFeedbacks();

    // 특정 사용자의 피드백 개수
    long countByUser(User user);

    // 특정 사용자 ID의 피드백 개수
    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.user.id = :userId")
    long countByUserId(@Param("userId") String userId);
}
