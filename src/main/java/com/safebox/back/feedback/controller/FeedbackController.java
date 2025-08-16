// controller/FeedbackController.java
package com.safebox.back.feedback.controller;

import com.safebox.back.feedback.dto.AdminReplyDto;
import com.safebox.back.feedback.dto.ApiResponse;
import com.safebox.back.feedback.dto.FeedbackRequestDto;
import com.safebox.back.feedback.dto.FeedbackResponseDto;
import com.safebox.back.feedback.entity.FeedbackStatus;
import com.safebox.back.feedback.entity.FeedbackType;
import com.safebox.back.feedback.service.FeedbackService;
import com.safebox.back.feedback.service.FeedbackStatsDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController // 이 클래스가 REST API 컨트롤러임을 선언. 반환값은 JSON/XML 등의 바디로 직렬화됨.
@RequestMapping("/api/feedback") // 컨트롤러의 공통 URL prefix
@CrossOrigin(origins = "*") // CORS 허용(모든 오리진 허용). 운영 환경에서는 도메인 제한 권장.
public class FeedbackController {

    // 비즈니스 로직을 담당하는 서비스 계층 의존성
    private final FeedbackService feedbackService;

    // 생성자 주입. 스프링이 FeedbackService 빈을 주입.
    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    /**
     * 새로운 피드백 생성
     * HTTP: POST /api/feedback
     * 바디: FeedbackRequestDto (유효성 검사 @Valid)
     * 성공 시: 201(CREATED) + 생성된 피드백 DTO 반환
     * 실패 시: 400(BAD_REQUEST) 등 에러 메시지
     *
     * TIP) 이미 GlobalExceptionHandler가 있으므로, 여기 try-catch를 제거하고
     *     서비스에서 던진 예외를 전역 핸들러로 일괄 처리해도 좋음.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> createFeedback(
            @Valid @RequestBody FeedbackRequestDto requestDto) { // @Valid: DTO 필드 제약조건(@NotBlank 등) 검증
        try {
            FeedbackResponseDto feedback = feedbackService.createFeedback(requestDto);
            return ResponseEntity.status(HttpStatus.CREATED) // 201 Created
                    .body(new ApiResponse<>(true, "피드백이 성공적으로 등록되었습니다.", feedback));
        } catch (Exception e) {
            // 이 컨트롤러 내부에서 에러 메시지를 내려주지만,
            // GlobalExceptionHandler로 위임하면 중복이 줄고 응답 형식이 더 일관적이 됨.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "피드백 등록에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 모든 피드백 조회 (페이징)
     * HTTP: GET /api/feedback?page=0&size=10
     * 성공 시: 200(OK) + Page<FeedbackResponseDto>
     *
     * NOTE) 전역 Pageable 설정(DatabaseConfig)을 사용하는 방식(파라미터로 Pageable 받기)도 가능.
     *       지금은 page/size를 직접 파라미터로 받아 서비스로 전달하는 구조.
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<FeedbackResponseDto>>> getAllFeedbacks(
            @RequestParam(defaultValue = "0") int page, // 기본값 0페이지 (0-based)
            @RequestParam(defaultValue = "10") int size) { // 기본 페이지 크기 10
        try {
            Page<FeedbackResponseDto> feedbacks = feedbackService.getAllFeedbacks(page, size);
            return ResponseEntity.ok(new ApiResponse<>(true, "피드백 목록을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "피드백 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 특정 피드백 단건 조회
     * HTTP: GET /api/feedback/{id}
     * 성공 시: 200(OK)
     * 없으면: 404(NOT_FOUND)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> getFeedbackById(@PathVariable Long id) {
        Optional<FeedbackResponseDto> feedback = feedbackService.getFeedbackById(id);

        if (feedback.isPresent()) {
            return ResponseEntity.ok(new ApiResponse<>(true, "피드백을 성공적으로 조회했습니다.", feedback.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, "해당 피드백을 찾을 수 없습니다.", null));
        }
    }

    /**
     * 타입별 피드백 조회
     * HTTP: GET /api/feedback/type/{type}
     * PathVariable이 enum(FeedbackType)으로 바인딩됨.
     * - 유효하지 않은 값이면 MethodArgumentTypeMismatchException 발생 → 전역 예외 핸들러에서 400 처리 가능.
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByType(
            @PathVariable FeedbackType type) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByType(type);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    type.getDescription() + " 피드백을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "타입별 피드백 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 상태별 피드백 조회
     * HTTP: GET /api/feedback/status/{status}
     * enum(FeedbackStatus) 바인딩. 유효하지 않은 값은 전역 예외 핸들러로 400 처리.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByStatus(
            @PathVariable FeedbackStatus status) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    status.getDescription() + " 피드백을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, "상태별 피드백 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 키워드 검색
     * HTTP: GET /api/feedback/search?keyword=버그
     * 성공 시: 200(OK) + List<FeedbackResponseDto>
     * 검색 대상(제목/내용/작성자 등)은 서비스/레포지토리에서 정의.
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> searchFeedbacks(
            @RequestParam String keyword) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.searchFeedbacks(keyword);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "키워드 '" + keyword + "'로 검색한 피드백을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "피드백 검색에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 이메일로 피드백 조회
     * HTTP: GET /api/feedback/email/{email}
     * NOTE) 이메일에는 '.' 문자가 포함되므로 pathVariable 처리 시 URL 인코딩/패턴 주의.
     *       (Spring Boot 최신 PathPattern 사용 시 대부분 문제없지만, 구버전은 설정 필요할 수 있음)
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getFeedbacksByEmail(
            @PathVariable String email) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getFeedbacksByEmail(email);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    email + "의 피드백을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "이메일별 피드백 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 피드백 상태 업데이트 (관리자용)
     * HTTP: PUT /api/feedback/{id}/status?status=IN_PROGRESS
     * - status는 enum 바인딩. 유효하지 않으면 전역 예외 핸들러에서 400 처리 가능.
     * - 존재하지 않는 id인 경우 IllegalArgumentException 등으로 404 처리(현재 컨트롤러에서 캐치).
     *
     * REST 관점 팁) PATCH /{id} + JSON 바디로 부분 업데이트를 받는 방식도 흔함.
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> updateFeedbackStatus(
            @PathVariable Long id,
            @RequestParam FeedbackStatus status) {
        try {
            FeedbackResponseDto updatedFeedback = feedbackService.updateFeedbackStatus(id, status);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "피드백 상태가 성공적으로 업데이트되었습니다.", updatedFeedback));
        } catch (IllegalArgumentException e) {
            // 서비스에서 "존재하지 않음" 등 도메인 오류 메시지를 담아 던졌다고 가정 → 404로 매핑
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "상태 업데이트에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 관리자 답변 추가 (관리자용)
     * HTTP: POST /api/feedback/{id}/reply
     * 바디: AdminReplyDto (유효성 검사 @Valid)
     * - 존재하지 않는 id: 404
     * - 유효성 실패: 전역 예외 핸들러에서 400으로 맵핑 가능(MethodArgumentNotValidException)
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<FeedbackResponseDto>> addAdminReply(
            @PathVariable Long id,
            @Valid @RequestBody AdminReplyDto replyDto) {
        try {
            FeedbackResponseDto updatedFeedback = feedbackService.addAdminReply(id, replyDto.getReply());
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "관리자 답변이 성공적으로 등록되었습니다.", updatedFeedback));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "답변 등록에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 피드백 삭제 (관리자용)
     * HTTP: DELETE /api/feedback/{id}
     * 성공 시: 200(OK) + 메시지
     * REST 관점 팁) 본문 없이 204(NO_CONTENT)로 응답하는 것도 일반적.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(@PathVariable Long id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "피드백이 성공적으로 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "피드백 삭제에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 답변이 없는(관리자 미답변) 피드백 조회 (관리자용)
     * HTTP: GET /api/feedback/unanswered
     * 권한 제어가 필요하다면 Security(메서드 보안)로 접근 제한 권장.
     */
    @GetMapping("/unanswered")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getUnansweredFeedbacks() {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getUnansweredFeedbacks();
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "답변이 없는 피드백을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "미답변 피드백 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 최근 N일간 피드백 조회
     * HTTP: GET /api/feedback/recent?days=7
     * - days 기본값 7일. 음수/과도한 값 방어 로직은 서비스/전역 검증에서 처리 권장.
     */
    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<FeedbackResponseDto>>> getRecentFeedbacks(
            @RequestParam(defaultValue = "7") int days) {
        try {
            List<FeedbackResponseDto> feedbacks = feedbackService.getRecentFeedbacks(days);
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "최근 " + days + "일간의 피드백을 성공적으로 조회했습니다.", feedbacks));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "최근 피드백 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 피드백 통계 조회 (관리자용)
     * HTTP: GET /api/feedback/stats
     * 예: 총 건수, 타입/상태별 분포, 최근 7일 추이 등(정의는 FeedbackStatsDto에 따름)
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<FeedbackStatsDto>> getFeedbackStats() {
        try {
            FeedbackStatsDto stats = feedbackService.getFeedbackStats();
            return ResponseEntity.ok(new ApiResponse<>(true,
                    "피드백 통계를 성공적으로 조회했습니다.", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "통계 조회에 실패했습니다: " + e.getMessage(), null));
        }
    }
}
