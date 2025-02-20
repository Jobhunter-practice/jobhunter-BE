package com.mycompany.jobhunter.service.contract;

import org.springframework.scheduling.annotation.Async;

public interface IEmailService {
    void sendEmailSync(String to, String subject, String content, boolean isMultipart, boolean isHtml);

    @Async
    void sendEmailFromTemplateSync(
            String to,
            String subject,
            String templateName,
            String username,
            Object value
    );
}
