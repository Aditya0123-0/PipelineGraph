package com.pipelinegraph.dto;

public record GraphEdgeDto (
        String source,
        String target,
        String relationship
){
}
