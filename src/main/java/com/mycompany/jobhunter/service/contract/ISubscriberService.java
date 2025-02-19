package com.mycompany.jobhunter.service.contract;

import com.mycompany.jobhunter.domain.dto.response.email.ResEmailJob;
import com.mycompany.jobhunter.domain.entity.Job;
import com.mycompany.jobhunter.domain.entity.Subscriber;

public interface ISubscriberService {
    boolean isExistsByEmail(String email);

    Subscriber create(Subscriber subs);

    Subscriber update(Subscriber subsDB, Subscriber subsRequest);

    Subscriber findById(long id);

    ResEmailJob convertJobToSendEmail(Job job);

    void sendSubscribersEmailJobs();

    Subscriber findByEmail(String email);
}
