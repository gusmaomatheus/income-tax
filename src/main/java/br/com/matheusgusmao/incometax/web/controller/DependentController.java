package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.service.DependentService;
import br.com.matheusgusmao.incometax.web.dto.dependent.CreateDependentRequest;
import br.com.matheusgusmao.incometax.web.dto.dependent.DependentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/declarations/{declarationId}/dependents") 
public class DependentController {

    private final DependentService dependentService;

    public DependentController(DependentService dependentService) {
        this.dependentService = dependentService;
    }

    @PostMapping
    public ResponseEntity<DependentResponse> addDependent(
            @PathVariable Long declarationId,
            @RequestBody CreateDependentRequest request) {
        DependentResponse response = dependentService.addDependent(declarationId, request);
        return ResponseEntity.status(201).body(response);
    }
}