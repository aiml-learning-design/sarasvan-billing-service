package sarasvan.billing.main.controller;

import sarasvan.billing.main.model.BusinessDetails;
import sarasvan.billing.main.service.BusinessDetailsService;
import sarasvan.billing.main.service.BusinessDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessDetailsController {

    private final BusinessDetailsService service;

    @PostMapping("/update")
    public BusinessDetails createOrUpdate(@RequestBody BusinessDetails details) {
        return service.createOrUpdate(details);
    }

    @GetMapping("/{id}")
    public BusinessDetails get(@PathVariable Long id) {
        return service.getById(id).orElse(null);
    }
}
