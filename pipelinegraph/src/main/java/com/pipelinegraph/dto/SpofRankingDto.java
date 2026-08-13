package com.pipelinegraph.dto;

public record SpofRankingDto(
        String systemId,
        String systemName,
        long dependentCount
) { }
