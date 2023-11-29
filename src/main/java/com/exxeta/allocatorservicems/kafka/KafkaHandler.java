package com.exxeta.allocatorservicems.kafka;

import com.exxeta.allocatorservice.entities.Category;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaHandler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(KafkaHandler.class);

    public KafkaHandler(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * publishes a Kafka message for every destination where the value for the allocation has changed
     */
    public void publishAllocationUpdate(String userId, List<Category> updatedCategories) throws JsonProcessingException {
        kafkaTemplate.send("allocation", String.valueOf(userId), objectMapper.writeValueAsString(updatedCategories));
        logger.info("Sended kafka message: " + objectMapper.writeValueAsString(updatedCategories) + " with the key " + userId);
    }
}
