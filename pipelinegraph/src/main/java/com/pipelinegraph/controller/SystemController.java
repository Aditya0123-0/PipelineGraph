package com.pipelinegraph.controller;

import com.pipelinegraph.dto.SystemRequest;
import com.pipelinegraph.dto.SystemResponse;
import com.pipelinegraph.service.SystemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    private final SystemService systemService;

    @Autowired
    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping
    public List<SystemResponse> getAllSystems(){
        return systemService.getAllSystems();
    }

    @GetMapping("/{id}")
    public SystemResponse getSystemById(@PathVariable String id){
        return systemService.getSystemById(id);
    }

    @PostMapping
    public ResponseEntity<SystemResponse> createSystem(@Valid @RequestBody SystemRequest systemRequest) {
        SystemResponse createdSystem = systemService.createSystem(systemRequest);
        return ResponseEntity.ok(createdSystem);
    }

}
