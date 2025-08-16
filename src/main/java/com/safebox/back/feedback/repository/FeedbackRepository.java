package com.safebox.back.feedback.repository;

import com.safebox.back.feedback.entity.Feedback;
import com.safebox.back.feedback.entity.FeedbackType;
import com.safebox.back.feedback.entity.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    // 타입별로 피드백 조회 (최신순)
    List<Feedback> findByTypeOrderByCreatedAtDesc(FeedbackType type);

    // 상태별로 피드백 조회 (최신순)
    List<Feedback> findByStatusOrderByCreatedAtDesc(FeedbackStatus status);

    // 타입과 상태로 피드백 조회
    List<Feedback> findByTypeAndStatusOrderByCreatedAtDesc(FeedbackType type, FeedbackStatus status);

    // 페이징을 지원하는 전체 피드백 조회 (최신순)
    Page<Feedback> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // 페이징을 지원하는 타입별 피드백 조회
    Page<Feedback> findByTypeOrderByCreatedAtDesc(FeedbackType type, Pageable pageable);

    // 페이징을 지원하는 상태별 피드백 조회
    Page<Feedback> findByStatusOrderByCreatedAtDesc(FeedbackStatus status, Pageable pageable);

    // 특정 기간 내 피드백 조회
    List<Feedback> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    // 이메일로 피드백 조회
    List<Feedback> findByEmailOrderByCreatedAtDesc(String email);

    // 제목이나 내용에 특정 키워드가 포함된 피드백 검색
    @Query("SELECT f FROM Feedback f WHERE f.subject LIKE %:keyword% OR f.content LIKE %:keyword% ORDER BY f.createdAt DESC")
    List<Feedback> findByKeyword(@Param("keyword") String keyword);

    // 페이징을 지원하는 키워드 검색
    @Query("SELECT f FROM Feedback f WHERE f.subject LIKE %:keyword% OR f.content LIKE %:keyword% ORDER BY f.createdAt DESC")
    Page<Feedback> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 상태별 개수 조회
    long countByStatus(FeedbackStatus status);

    // 타입별 개수 조회
    long countByType(FeedbackType type);

    // 오늘 등록된 피드백 개수
    @Query("SELECT COUNT(f) FROM Feedback f WHERE DATE(f.createdAt) = CURRENT_DATE")
    long countTodayFeedbacks();

    // 특정 기간 내 피드백 개수
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    // 답변이 없는 피드백 조회 (관리자용)
    @Query("SELECT f FROM Feedback f WHERE f.adminReply IS NULL OR f.adminReply = '' ORDER BY f.createdAt ASC")
    List<Feedback> findUnansweredFeedbacks();

    // 페이징을 지원하는 답변이 없는 피드백 조회
    @Query("SELECT f FROM Feedback f WHERE f.adminReply IS NULL OR f.adminReply = '' ORDER BY f.createdAt ASC")
    Page<Feedback> findUnansweredFeedbacks(Pageable pageable);

    // 최근 N일 이내의 피드백 조회
    @Query("SELECT f FROM Feedback f WHERE f.createdAt >= :startDate ORDER BY f.createdAt DESC")
    List<Feedback> findRecentFeedbacks(@Param("startDate") LocalDateTime startDate);

    // 이름으로 피드백 검색
    List<Feedback> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

    // 복합 검색 (제목, 내용, 이름)
    @Query("SELECT f FROM Feedback f WHERE " +
            "f.subject LIKE %:keyword% OR " +
            "f.content LIKE %:keyword% OR " +
            "f.name LIKE %:keyword% " +
            "ORDER BY f.createdAt DESC")
    List<Feedback> findByAllFields(@Param("keyword") String keyword);
}