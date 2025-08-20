package com.safebox.back.feedback.entity;

import com.safebox.back.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Getter
@Setter
@NoArgsConstructor
public class Feedback {

    // 피드백 PK (문자열, 커스텀 생성)
    @Id
    @Column(length = 36)
    private String id;

    // User FK (User.id = String UUID)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Column(name = "product_number", nullable = false)
    private String productNumber;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FeedbackStatus status = FeedbackStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // 생성자
    public Feedback(User user, String productNumber, String phoneNumber, String content) {
        this.user = user;
        this.productNumber = productNumber;
        this.phoneNumber = phoneNumber;
        this.content = content;
        this.status = FeedbackStatus.PENDING;
    }

    // 외래키 반환용 (User PK = String)
    public String getUserId() {
        return this.user != null ? this.user.getId() : null;
    }

    // 커스텀 ID 생성
    private String generateCustomId() {
        var formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        String randomSuffix = String.format("%06d", (int)(Math.random() * 1000000));
        return "FB" + timestamp + randomSuffix;
    }

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = generateCustomId();
        }
        this.createdAt = LocalDateTime.now();
    }
}