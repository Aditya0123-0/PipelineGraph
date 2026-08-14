package com.pipelinegraph.dto;

import jakarta.validation.constraints.NotBlank;

public record SystemRequest(
        @NotBlank(message = "id is required") String id,
        @NotBlank(message = "name is required") String name,
        @NotBlank(message = "type is required") String type,
        @NotBlank(message = "criticality is required") String criticality
)
{ }
