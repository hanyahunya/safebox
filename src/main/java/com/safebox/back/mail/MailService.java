package com.safebox.back.mail;

import com.safebox.back.util.ResponseDto;

import java.util.Map;

public interface MailService {
    boolean sendArrivedMail(String toEmail, String subject, String templateName, Map<String, Object> variables);
}
