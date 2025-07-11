package com.sarasvan.billing.controller;

import com.sarasvan.billing.model.BusinessDetails;
import com.sarasvan.billing.service.BusinessDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessDetailsController {

    private final BusinessDetailsServiceImpl service;

    @PostMapping("/update")
    public BusinessDetails createOrUpdate(@RequestBody BusinessDetails details) {
        return service.createOrUpdate(details);
    }

    @GetMapping("/{id}")
    public BusinessDetails get(@PathVariable Long id) {
        return service.getById(id);
    }
}
