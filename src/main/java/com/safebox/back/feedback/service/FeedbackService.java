package com.safebox.back.feedback.service;

import com.safebox.back.feedback.dto.FeedbackRequestDto;
import com.safebox.back.feedback.dto.FeedbackResponseDto;
import com.safebox.back.feedback.entity.Feedback;
import com.safebox.back.feedback.entity.FeedbackStatus;
import com.safebox.back.feedback.repository.FeedbackRepository;
import com.safebox.back.user.entity.User;
import com.safebox.back.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository) {
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
    }

    /**
     * 새로운 피드백 저장
     */
    public FeedbackResponseDto createFeedback(String userId, FeedbackRequestDto requestDto) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("유효하지 않은 사용자 ID입니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다: " + userId));

        Feedback feedback = new Feedback(
                user,
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
    public Optional<FeedbackResponseDto> getFeedbackById(String id) {
        return feedbackRepository.findById(id)
                .map(this::convertToResponseDto);
    }

    /**
     * 사용자별 피드백 조회
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponseDto> getFeedbacksByUserId(String userId) {
        return feedbackRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
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
    public FeedbackResponseDto updateFeedbackStatus(String id, FeedbackStatus status) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("피드백을 찾을 수 없습니다: " + id));

        feedback.setStatus(status);
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        return convertToResponseDto(updatedFeedback);
    }

    /**
     * 피드백 삭제
     */
    public void deleteFeedback(String id) {
        if (!feedbackRepository.existsById(id)) {
            throw new IllegalArgumentException("피드백을 찾을 수 없습니다: " + id);
        }
        feedbackRepository.deleteById(id);
    }

    /**
     * Entity → ResponseDto 변환
     */
    private FeedbackResponseDto convertToResponseDto(Feedback feedback) {
        FeedbackResponseDto dto = new FeedbackResponseDto();
        dto.setId(feedback.getId());
        dto.setUserId(feedback.getUserId());
        dto.setProductNumber(feedback.getProductNumber());
        dto.setPhoneNumber(feedback.getPhoneNumber());
        dto.setContent(feedback.getContent());
        dto.setStatus(feedback.getStatus());
        dto.setCreatedAt(feedback.getCreatedAt());
        return dto;
    }
}