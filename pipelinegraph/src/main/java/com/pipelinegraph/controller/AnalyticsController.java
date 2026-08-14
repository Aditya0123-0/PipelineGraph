package com.pipelinegraph.controller;

import com.pipelinegraph.dto.PiiTraceResponse;
import com.pipelinegraph.dto.ShortestPathResponse;
import com.pipelinegraph.dto.SpofRankingDto;
import com.pipelinegraph.service.PiiTraceService;
import com.pipelinegraph.service.ShortestPathService;
import com.pipelinegraph.service.SpofRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final ShortestPathService shortestPathService;
    private final PiiTraceService piiTraceService;
    private final SpofRankingService spofRankingService;

    public AnalyticsController(ShortestPathService shortestPathService, PiiTraceService piiTraceService, SpofRankingService spofRankingService) {
        this.shortestPathService = shortestPathService;
        this.piiTraceService = piiTraceService;
        this.spofRankingService = spofRankingService;
    }

    @GetMapping("/shortest-path")
    public ShortestPathResponse shortestPath(@RequestParam String from, @RequestParam String to) {
        return shortestPathService.getShortestPath(from, to);
    }

    @GetMapping("/pii-trace")
    public List<PiiTraceResponse> piiTrace(@RequestParam(defaultValue = "PII") String sensitivity) {
        return piiTraceService.traceSensitiveData(sensitivity);
    }

    @GetMapping("/spof-ranking")
    public List<SpofRankingDto> spofRanking() {
        return spofRankingService.getRanking();
    }
}
