package com.exxeta.allocatorservicems.controllers;

import com.exxeta.allocatorservice.entities.Allocation;
import com.exxeta.allocatorservice.entities.Category;
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
    public String getAllocation(@PathVariable String userId) throws JsonProcessingException {
        logger.info("Getting the allocations from database");
        return mapper.writeValueAsString(allocationService.getAllocation(userId));
    }

    @PutMapping
    public void updateAllocation(@PathVariable String userId, @RequestBody Allocation allocation) {
        try {
            allocation.setUserId(userId);
            allocationService.validateAllocation(allocation);
            List<Category> updatedCategories = allocationService.getUpdatedCategories(allocation);
            allocationService.updateAllocation(userId, allocation); //TODO: check if userId is still needed
//            kafkaHandler.publishAllocationUpdate(userId, updatedCategories);
        } catch (InvalidAllocationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
