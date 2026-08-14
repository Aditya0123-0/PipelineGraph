package com.pipelinegraph.service;

import com.pipelinegraph.dto.GraphEdgeDto;
import com.pipelinegraph.dto.GraphNodeDto;
import com.pipelinegraph.dto.ShortestPathResponse;
import com.pipelinegraph.repository.GraphQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ShortestPathService {

    private final GraphQueryRepository repository;
    public ShortestPathService(GraphQueryRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    public ShortestPathResponse getShortestPath(String fromId, String toId) {
        return repository.findShortestPath(fromId, toId)
                .map(row -> {
                    List<Map<String, String>> rawNodes = (List<Map<String, String>>) row.get("pathNodes");
                    List<String> rawRels = (List<String>) row.get("pathRels");

                    List<GraphNodeDto> nodes = rawNodes.stream()
                            .map(n -> new GraphNodeDto(n.get("id"), n.get("label"), n.get("type")))
                            .toList();

                    List<GraphEdgeDto> edges = new java.util.ArrayList<>();
                    for (int i = 0; i < rawRels.size(); i++) {
                        edges.add(new GraphEdgeDto(nodes.get(i).id(), nodes.get(i + 1).id(), rawRels.get(i)));
                    }

                    long hops = ((Number) row.get("hops")).longValue();
                    return new ShortestPathResponse(true, nodes, edges, (int) hops);
                })
                .orElse(new ShortestPathResponse(false, List.of(), List.of(), 0));
    }
}
