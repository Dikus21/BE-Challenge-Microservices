package com.example.scheduler.repository;

import com.example.scheduler.entity.ConsumerSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<ConsumerSubscription, Long> {
    Optional<ConsumerSubscription> findByTopicName(String topicName);
    @Query("select cs from ConsumerSubscription cs WHERE cs.topicName = : topic")
    public ConsumerSubscription getByTopic(@Param("topic") String topic);
}
