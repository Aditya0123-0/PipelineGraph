package com.pipelinegraph.service;

import com.pipelinegraph.dto.GraphNodeDto;
import com.pipelinegraph.dto.PiiTraceResponse;
import com.pipelinegraph.repository.GraphQueryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PiiTraceService {
    private final GraphQueryRepository repository;

    public PiiTraceService(GraphQueryRepository graphQueryRepository) {
        this.repository = graphQueryRepository;
    }

    @SuppressWarnings("unchecked")
    public List<PiiTraceResponse> traceSensitiveData(String sensitivity) {
        return repository.tracePiiFlow(sensitivity).stream()
                .map(row -> {
                    List<Map<String, String>> systems = (List<Map<String, String>>) row.get("systems");
                    List<Map<String, String>> pipelines = (List<Map<String, String>>) row.get("pipelines");

                    return new PiiTraceResponse(
                            (String) row.get("dataEntityId"),
                            (String) row.get("sensitivity"),
                            systems.stream().map(s -> new GraphNodeDto(s.get("id"), s.get("label"), s.get("type"))).toList(),
                            pipelines.stream().map(p -> new GraphNodeDto(p.get("id"), p.get("label"), p.get("type"))).toList()
                    );
                })
                .toList();
    }
}
