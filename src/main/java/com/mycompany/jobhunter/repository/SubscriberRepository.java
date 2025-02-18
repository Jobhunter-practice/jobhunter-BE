package com.mycompany.jobhunter.repository;

import com.mycompany.jobhunter.domain.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long>, JpaSpecificationExecutor<Subscriber> {

    boolean existsByEmail(String email);

    Subscriber findByEmail(String email);
}
