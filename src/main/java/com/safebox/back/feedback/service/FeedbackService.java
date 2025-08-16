package com.safebox.back.feedback.service;

import com.safebox.back.feedback.dto.FeedbackRequestDto;
import com.safebox.back.feedback.dto.FeedbackResponseDto;
import com.safebox.back.feedback.service.FeedbackStatsDto;
import com.safebox.back.feedback.entity.Feedback;
import com.safebox.back.feedback.entity.FeedbackStatus;
import com.safebox.back.feedback.entity.FeedbackType;
import com.safebox.back.feedback.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    /**
     * 새로운 피드백 저장
     */
    public FeedbackResponseDto createFeedback(FeedbackRequestDto requestDto) {
        Feedback feedback = new Feedback(
                requestDto.getProductNumber(),
                requestDto.getPhoneNumber(),
                requestDto.getContent()
        );

        Feedback savedFeedback = feedbackRepository.save(feedback);
        return convertToResponseDto(savedFeedback);
    }

    /**
     * 피드백 ID로 조회
     */
    @Transactional(readOnly = true)
    public Optional<FeedbackResponseDto> getFeedbackById(UUID id) {
        return feedbackRepository.findById(id)
                .map(this::convertToResponseDto);
    }

    /**
     * 모든 피드백 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<FeedbackResponseDto> getAllFeedbacks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return feedbackRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::convertToResponseDto);
    }

    /**
     * 제품번호로 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByProductNumber(String productNumber) {
        return feedbackRepository.findByProductNumberOrderByCreatedAtDesc(productNumber)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 상태별 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByStatus(FeedbackStatus status) {
        return feedbackRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 키워드로 피드백 검색
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> searchFeedbacks(String keyword) {
        return feedbackRepository.findByKeyword(keyword)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 전화번호로 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByPhoneNumber(String phoneNumber) {
        return feedbackRepository.findByPhoneNumberOrderByCreatedAtDesc(phoneNumber)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 피드백 상태 업데이트
     */
    public FeedbackResponseDto updateFeedbackStatus(UUID id, FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("피드백을 찾을 수 없습니다: " + id));

        feedback.setStatus(status);
        feedback.setUpdatedAt(LocalDateTime.now());

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToResponseDto(updatedFeedback);
    }

    /**
     * 관리자 답변 추가
     */
    public FeedbackResponseDto addAdminReply(UUID id, String reply) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("피드백을 찾을 수 없습니다: " + id));

        feedback.setAdminReply(reply);
        feedback.setRepliedAt(LocalDateTime.now());
        feedback.setStatus(FeedbackStatus.RESOLVED);
        feedback.setUpdatedAt(LocalDateTime.now());

        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToResponseDto(updatedFeedback);
    }

    /**
     * 피드백 삭제
     */
    public void deleteFeedback(UUID id) {
        if (!feedbackRepository.existsById(id)) {
            throw new IllegalArgumentException("피드백을 찾을 수 없습니다: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    /**
     * 답변이 없는 피드백 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getUnansweredFeedbacks() {
        return feedbackRepository.findUnansweredFeedbacks()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 최근 N일 이내 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getRecentFeedbacks(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return feedbackRepository.findRecentFeedbacks(startDate)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 피드백 통계 조회
     */
    @Transactional(readOnly = true)
    public FeedbackStatsDto getFeedbackStats() {
        FeedbackStatsDto stats = new FeedbackStatsDto();

        stats.setTotalCount(feedbackRepository.count());
        stats.setPendingCount(feedbackRepository.countByStatus(FeedbackStatus.PENDING));
        stats.setInProgressCount(feedbackRepository.countByStatus(FeedbackStatus.IN_PROGRESS));
        stats.setResolvedCount(feedbackRepository.countByStatus(FeedbackStatus.RESOLVED));
        stats.setClosedCount(feedbackRepository.countByStatus(FeedbackStatus.CLOSED));
        stats.setTodayCount(feedbackRepository.countTodayFeedbacks());

        return stats;
    }

    /**
     * Entity를 ResponseDto로 변환
     */
    private FeedbackResponseDto convertToResponseDto(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(feedback.getId());
        dto.setProductNumber(feedback.getProductNumber());
        dto.setPhoneNumber(feedback.getPhoneNumber());
        dto.setContent(feedback.getContent());
        dto.setStatus(feedback.getStatus());
        dto.setCreatedAt(feedback.getCreatedAt());
        dto.setUpdatedAt(feedback.getUpdatedAt());
        dto.setAdminReply(feedback.getAdminReply());
        dto.setRepliedAt(feedback.getRepliedAt());
        return dto;
    }
}