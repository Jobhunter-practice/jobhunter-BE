package com.mycompany.jobhunter.controller;

import com.mycompany.jobhunter.domain.entity.Subscriber;
import com.mycompany.jobhunter.service.contract.ISubscriberService;
import com.mycompany.jobhunter.util.SecurityUtil;
import com.mycompany.jobhunter.util.annotation.ApiMessage;
import com.mycompany.jobhunter.util.error.IdInvalidException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final ISubscriberService subscriberService;

    public SubscriberController(ISubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> create(@Valid @RequestBody Subscriber sub) throws IdInvalidException {
        // check email
        boolean isExist = this.subscriberService.isExistsByEmail(sub.getEmail());
        if (isExist == true) {
            throw new IdInvalidException("Email " + sub.getEmail() + " has been used");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(sub));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest) throws IdInvalidException {
        // check id
        Subscriber subsDB = this.subscriberService.findById(subsRequest.getId());
        if (subsDB == null) {
            throw new IdInvalidException("Id " + subsRequest.getId() + " not found");
        }
        return ResponseEntity.ok().body(this.subscriberService.update(subsDB, subsRequest));
    }

    @GetMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUser().isPresent() == true
                ? SecurityUtil.getCurrentUser().get()
                : "";

        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }
}
