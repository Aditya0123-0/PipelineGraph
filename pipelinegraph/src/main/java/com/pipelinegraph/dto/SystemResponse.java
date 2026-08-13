package com.pipelinegraph.dto;

public record SystemResponse(
        String id,
        String name,
        String type,
        String criticality
) { }
