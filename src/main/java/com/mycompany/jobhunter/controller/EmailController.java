package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.dto.response.RestResponse;
import com.mycompany.jobhunter.service.contract.ISubscriberService;
import com.mycompany.jobhunter.util.annotation.ApiMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final ISubscriberService subscriberService;

    public EmailController(ISubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public ResponseEntity<Map<String, String>> sendSimpleEmail() {
        this.subscriberService.sendSubscribersEmailJobs();
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
