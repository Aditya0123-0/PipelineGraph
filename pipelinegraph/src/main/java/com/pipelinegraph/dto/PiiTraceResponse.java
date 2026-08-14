package com.pipelinegraph.dto;

import java.util.List;

public record PiiTraceResponse(
        String dataEntityId,
        String sensitivity,
        List<GraphNodeDto> touchingSystems,
        List<GraphNodeDto> touchingPipelines)
{ }
