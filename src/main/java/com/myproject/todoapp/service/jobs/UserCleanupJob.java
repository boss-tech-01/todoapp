package com.myproject.todoapp.service.jobs;


import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class UserCleanupJob {
    private final MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeOldUsers() {
        Instant threshold = Instant.now().minus(90, ChronoUnit.DAYS);

        Query query = new Query(
                new Criteria().andOperator(
                        Criteria.where("softDeleted").is(true),
                        Criteria.where("softDeletedAt").lte(threshold)
                )
        );
        mongoTemplate.remove(query, User.class);
    }

}
