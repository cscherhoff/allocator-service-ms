package com.exxeta.allocatorservicems.controllers;

import com.exxeta.allocatorservice.entities.Allocation;
import com.exxeta.allocatorservice.services.AllocationService;
import com.exxeta.allocatorservice.services.InvalidAllocationException;
import com.exxeta.allocatorservicems.kafka.KafkaHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    public String getAllocation(@PathVariable long userId) throws JsonProcessingException {
        logger.info("Getting the allocations from database");
        return mapper.writeValueAsString(allocationService.getAllocation(userId));
    }

    @PutMapping
    public void updateAllocation(@PathVariable long userId, @RequestBody Allocation allocation) {
        try {
            allocationService.updateAllocation(userId, allocation);
            kafkaHandler.publishAllocationUpdate(userId, allocation);
        } catch (InvalidAllocationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
