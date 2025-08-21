package com.safebox.back.feedback.controller;

import com.safebox.back.feedback.dto.ApiResponse;
import com.safebox.back.feedback.dto.FeedbackRequestDto;
import com.safebox.back.feedback.dto.FeedbackResponseDto;
import com.safebox.back.feedback.entity.FeedbackStatus;
import com.safebox.back.feedback.service.FeedbackService;
import com.safebox.back.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * 피드백 등록
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> createFeedback(
            @AuthenticationPrincipal UserPrincipal user,
            @Valid @RequestBody FeedbackRequestDto requestDto) {
        try {
            FeedbackResponseDto feedback = feedbackService.createFeedback(user.getUserId(), requestDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "리뷰가 성공적으로 등록되었습니다.", feedback));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "리뷰 등록에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 모든 피드백 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeedbackResponseDto>>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<FeedbackResponseDto> feedbacks = feedbackService.getAllFeedbacks(page, size);
            return ResponseEntity.ok(new ApiResponse<>(true, "리뷰 목록을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "리뷰 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * ID로 피드백 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> getFeedbackById(@PathVariable String id) {
        Optional<FeedbackResponseDto> feedback = feedbackService.getFeedbackById(id);
        return feedback.map(response -> ResponseEntity.ok(new ApiResponse<>(true, "리뷰를 성공적으로 조회했습니다.", response)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, "해당 리뷰를 찾을 수 없습니다.", null)));
    }

    /**
     * 사용자별 피드백 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByUserId(
            @PathVariable String userId) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByUserId(userId);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "사용자의 리뷰를 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "사용자별 리뷰 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 제품번호별 피드백 조회
     */
    @GetMapping("/product/{productNumber}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByProductNumber(
            @PathVariable String productNumber) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByProductNumber(productNumber);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "제품번호 " + productNumber + "의 리뷰를 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "제품번호별 리뷰 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 상태별 피드백 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByStatus(
            @PathVariable FeedbackStatus status) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    status + " 상태의 리뷰를 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "상태별 리뷰 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 전화번호별 피드백 조회
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByPhoneNumber(
            @PathVariable String phoneNumber) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByPhoneNumber(phoneNumber);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    phoneNumber + "의 리뷰를 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "전화번호별 리뷰 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 피드백 상태 업데이트
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> updateFeedbackStatus(
            @PathVariable String id,
            @RequestParam FeedbackStatus status) {
        try {
            FeedbackResponseDto updatedFeedback = feedbackService.updateFeedbackStatus(id, status);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "리뷰 상태가 성공적으로 업데이트되었습니다.", updatedFeedback));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "상태 업데이트에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 피드백 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable String id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "리뷰가 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "리뷰 삭제에 실패했습니다: " + e.getMessage(), null));
        }
    }
}