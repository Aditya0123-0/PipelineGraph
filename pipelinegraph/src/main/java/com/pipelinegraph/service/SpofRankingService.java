package com.pipelinegraph.service;

import com.pipelinegraph.dto.SpofRankingDto;
import com.pipelinegraph.repository.GraphQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpofRankingService {
    private final GraphQueryRepository repository;

    public SpofRankingService(GraphQueryRepository repository) {
        this.repository = repository;
    }

    public List<SpofRankingDto> getRanking() {
        return repository.rankSystemsByDependents().stream()
                .map(row -> new SpofRankingDto(
                        (String) row.get("systemId"),
                        (String) row.get("systemName"),
                        ((Number) row.get("dependentCount")).longValue()
                ))
                .toList();
    }
}
