package br.com.matheusgusmao.incometax.web.controller;

import br.com.matheusgusmao.incometax.domain.service.DependentService;
import br.com.matheusgusmao.incometax.web.dto.declaration.CreateDependentRequest;
import br.com.matheusgusmao.incometax.web.dto.declaration.DependentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/declarations/{declarationId}/dependents")
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
        return ResponseEntity.ok(response);
    }
}