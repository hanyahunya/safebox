    package com.safebox.back.feedback.dto;

    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;

    public class AdminReplyDto {

        @NotBlank(message = "답변 내용은 필수입니다") // 공백으로 내면 문구 출력
        @Size(max = 500, message = "답변은 500자 이내여야 합니다") // 500자 이상으로 넘어가면 나오는 문구 설정
        private String reply;

        public AdminReplyDto() {} // 빈 객체 생성

        public AdminReplyDto(String reply) {
            this.reply = reply;
        }

        public String getReply() { return reply; }
        public void setReply(String reply) { this.reply = reply; }
    }