package com.pipelinegraph.controller;

import com.pipelinegraph.dto.ImpactResponse;
import com.pipelinegraph.service.ImpactAnalysisService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/impact")
public class ImpactController {

    private final ImpactAnalysisService impactService;

    public ImpactController(ImpactAnalysisService impactService) {
        this.impactService = impactService;
    }

    @GetMapping("/{systemId}")
    public ImpactResponse getImpact(@PathVariable String systemId){
        return impactService.getImpact(systemId);
    }

}
