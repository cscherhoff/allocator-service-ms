package com.exxeta.allocatorservicems.kafka;

import com.exxeta.allocatorservice.entities.Allocation;
import com.exxeta.allocatorservice.repositories.AllocationRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class KafkaHandler {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AllocationRepository allocationRepository;

    public KafkaHandler(KafkaTemplate<String, String> kafkaTemplate, AllocationRepository allocationRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.allocationRepository = allocationRepository;
    }

    /**
     * publishes a Kafka message for every destination where the allocation has changed
     */
    public void sendMessages(long userId, List<Allocation> allocationsFromRequest) {
        for (Allocation allocation: allocationsFromRequest) {
            if (amountHasChanged(allocation)) {
                sendMessages(userId, allocation);
            }
        }
    }

    private boolean amountHasChanged(Allocation allocationFromRequest) {
        final Optional<Allocation> optionalAllocation = allocationRepository.findByDestination(allocationFromRequest.getDestination());
        if (optionalAllocation.isEmpty()) {
            return true;
        }
        return optionalAllocation.get().getAmount().doubleValue() == allocationFromRequest.getAmount().doubleValue();
    }

    public void sendMessages(long userId, Allocation allocation) {
        try {
            String topic = getTopic(allocation);
            allocation.setUserId(userId);
            kafkaTemplate.send(topic, mapper.writeValueAsString(allocation));
        } catch (UnknownDestinationException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String getTopic(Allocation allocation) throws UnknownDestinationException {
        switch (allocation.getDestination()) {
            case "investment":
                return "investment";
            case "categories":
                return "categories";
            case "fixcosts":
                return "fixcosts";
        }
        throw new UnknownDestinationException("The destination " + allocation.getDestination() + " is unknown");
    }
}
