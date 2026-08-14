package com.pipelinegraph.dto;

public record GraphNodeDto(
        String id,
        String label,      // display name
        String type
) { }
