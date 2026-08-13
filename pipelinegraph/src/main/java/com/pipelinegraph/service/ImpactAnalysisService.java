package com.pipelinegraph.service;

import com.pipelinegraph.dto.GraphEdgeDto;
import com.pipelinegraph.dto.GraphNodeDto;
import com.pipelinegraph.dto.ImpactResponse;
import com.pipelinegraph.exception.NodeNotFoundException;
import com.pipelinegraph.repository.GraphQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ImpactAnalysisService {

    private final GraphQueryRepository  graphQueryRepository;

    public ImpactAnalysisService(GraphQueryRepository graphQueryRepository) {
        this.graphQueryRepository = graphQueryRepository;
    }

    public ImpactResponse getImpact(String systemId) {

        List<Map<String, Object>> rows = graphQueryRepository.findImpact(systemId);

        if (rows.isEmpty()) {
            throw new NodeNotFoundException("System with ID " + systemId + " not found or has no impact.","Exception" );
        }

        List<GraphNodeDto> nodes = extractNodes(rows);
        List<GraphEdgeDto> edges = extractEdges(rows);

        return new ImpactResponse(systemId, nodes, edges, nodes.size());

    }

    @SuppressWarnings("unchecked")
    private List<GraphNodeDto> extractNodes(List<Map<String, Object>> rows) {
        // implementation detail: iterate "affected" collection, map each
        // Neo4j node to GraphNodeDto(id, name, label(0))
        return rows.stream()
                .flatMap(row -> ((List<Object>) row.getOrDefault("affected", List.of())).stream())
                .map(n -> {
                    var node = (org.neo4j.driver.types.Node) n;
                    String type = node.labels().iterator().hasNext() ? node.labels().iterator().next() : "Unknown";
                    return new GraphNodeDto(
                            node.get("id").asString(),
                            node.get("name").asString(node.get("id").asString()),
                            type
                    );
                })
                .distinct()
                .toList();
    }

    @SuppressWarnings("unchecked")
    private List<GraphEdgeDto> extractEdges(List<Map<String, Object>> rows) {
        return rows.stream()
                .flatMap(row -> ((List<Map<String, Object>>) row.getOrDefault("edges", List.of())).stream())
                .map(e -> new GraphEdgeDto(
                        (String) e.get("source"),
                        (String) e.get("target"),
                        (String) e.get("relationship")
                ))
                .distinct()
                .toList();
    }

}
