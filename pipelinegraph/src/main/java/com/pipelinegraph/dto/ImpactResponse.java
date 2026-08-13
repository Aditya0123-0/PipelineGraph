package com.pipelinegraph.dto;

import java.util.List;

public record ImpactResponse(
        String rootSystemId,
        List<GraphNodeDto> affectedNodes,
        List<GraphEdgeDto> affectedEdges,
        int totalImpactCount
)
{
}
