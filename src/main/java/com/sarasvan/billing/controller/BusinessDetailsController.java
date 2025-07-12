package com.sarasvan.billing.controller;

import com.sarasvan.billing.model.BusinessDetailsDTO;
import com.sarasvan.billing.model.OfficeAddressDTO;
import com.sarasvan.billing.service.BusinessDetailsService;
import com.sarasvan.billing.service.BusinessDetailsServiceImpl;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessDetailsController {

    private final BusinessDetailsService businessService;

    @PostMapping
    public ResponseEntity<BusinessDetailsDTO> createBusiness(@Valid @RequestBody BusinessDetailsDTO dto) {
        return ResponseEntity.ok(businessService.createOrUpdate(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessDetailsDTO> updateBusiness(@PathVariable Long id, @Valid @RequestBody BusinessDetailsDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(businessService.createOrUpdate(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessDetailsDTO> getBusiness(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getById(id));
    }

    @PostMapping("/{id}/office-address")
    public ResponseEntity<BusinessDetailsDTO> addOfficeAddress(@PathVariable Long id, @Valid @RequestBody OfficeAddressDTO officeDTO) {
        return ResponseEntity.ok(businessService.updateOfficeAddress(id, officeDTO));
    }
}
