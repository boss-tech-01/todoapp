package com.myproject.todoapp.service.jobs;

import com.myproject.todoapp.entity.business.Todo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class TodoCleanupJob {

    private final MongoTemplate mongoTemplate;

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeTodosAfter3Months(){
        Instant threshold = Instant.now().minus(90, ChronoUnit.DAYS);

        Query q = new Query(new Criteria()
                .andOperator(
                        Criteria.where("softDeleted").is(true),
                        Criteria.where("softDeletedAt").lte(threshold)
                ));

        mongoTemplate.remove(q, Todo.class);
    }
}
