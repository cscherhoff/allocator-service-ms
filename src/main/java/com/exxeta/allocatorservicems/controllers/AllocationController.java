package com.exxeta.allocatorservicems.controllers;

import com.exxeta.allocatorservice.entities.Allocation;
import com.exxeta.allocatorservice.services.AllocationService;
import com.exxeta.allocatorservicems.kafka.KafkaHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/allocation/user/{userId}")
public class AllocationController {

    private final Logger logger = LoggerFactory.getLogger(AllocationController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final AllocationService allocationService;
    private final KafkaHandler kafkaHandler;

    public AllocationController(AllocationService allocationService, KafkaHandler kafkaHandler) {
        this.allocationService = allocationService;
        this.kafkaHandler = kafkaHandler;
    }

    @GetMapping
    public String getAllocations(@PathVariable long userId) throws JsonProcessingException {
        logger.info("Getting the allocations from database");
        return mapper.writeValueAsString(allocationService.getAllocations(userId));
    }

    @PutMapping
    public void updateAllocations(@PathVariable long userId, @RequestBody List<Allocation> allocations) {
        allocationService.updateAllocations(userId, allocations);
        kafkaHandler.sendMessages(userId, allocations);
    }
}
