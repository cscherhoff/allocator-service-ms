package com.exxeta.allocatorservicems.controllers;

import com.exxeta.allocatorservice.entities.Allocation;
import com.exxeta.allocatorservice.services.AllocationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/allocation")
public class AllocationController {

    private final Logger logger = LoggerFactory.getLogger(AllocationController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final AllocationService allocationService;

    public AllocationController(AllocationService allocationService) {
        this.allocationService = allocationService;
    }

    @GetMapping
    public String getAllocations() throws JsonProcessingException {
        logger.info("Getting the allocations from database");
        return mapper.writeValueAsString(allocationService.getAllocations());
    }

    @PutMapping
    public void updateAllocations(@RequestBody List<Allocation> allocations) {
        allocationService.updateAllocations(allocations);
    }
}
