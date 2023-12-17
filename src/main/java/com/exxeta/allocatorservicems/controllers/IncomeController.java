package com.exxeta.allocatorservicems.controllers;

import com.exxeta.allocatorservice.services.IncomeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/income")
public class IncomeController {

    private final Logger logger = LoggerFactory.getLogger(AllocationController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public String getIncome(@RequestParam String userId) throws JsonProcessingException {
        return mapper.writeValueAsString(incomeService.getIncome(userId));
    }

    @PutMapping
    public void updateIncome(@RequestParam String userId, @RequestBody BigDecimal income) {
        incomeService.updateIncome(userId, income);
    }
}
