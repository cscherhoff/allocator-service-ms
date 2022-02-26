package com.exxeta.allocatorservicems.kafka;

import com.exxeta.allocatorservice.entities.Allocation;
import com.exxeta.allocatorservice.repositories.AllocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class KafkaHandler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AllocationRepository allocationRepository;

    public KafkaHandler(KafkaTemplate<String, String> kafkaTemplate, AllocationRepository allocationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.allocationRepository = allocationRepository;
    }

    /**
     * publishes a Kafka message for every destination where the value for the allocation has changed
     */
    public void publishAllocationUpdate(long userId, Allocation allocationFromRequest) {
        final Allocation allocationFromDb = allocationRepository.findById(userId).orElseGet(() -> new Allocation(userId));
        if (valueHasChanged(allocationFromRequest.getInvestment(), allocationFromDb.getInvestment())) {
            sendMessage(userId, "investment", allocationFromRequest.getInvestment());
        }
        if (valueHasChanged(allocationFromRequest.getFixCosts(), allocationFromDb.getFixCosts())) {
            sendMessage(userId, "fixCosts", allocationFromRequest.getFixCosts());
        }
        if (valueHasChanged(allocationFromRequest.getCategories(), allocationFromDb.getCategories())) {
            sendMessage(userId, "categories", allocationFromRequest.getCategories());
        }
    }

    private void sendMessage(long userId, String topic, BigDecimal newValue) {
        kafkaTemplate.send(topic, "The new value for the user " + userId + " is " + newValue);
    }

    private boolean valueHasChanged(BigDecimal valueFromRequest, BigDecimal valueFromDb) {
        return valueFromRequest.doubleValue() != valueFromDb.doubleValue();
    }
}
