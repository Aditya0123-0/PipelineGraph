package com.pipelinegraph.dto;

import java.util.List;

public record ShortestPathResponse(
        boolean pathFound,
        List<GraphNodeDto> pathNodes,
        List<GraphEdgeDto> pathEdges,
        int hopCount
) {
}
