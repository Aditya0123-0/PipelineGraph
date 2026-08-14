package com.pipelinegraph.exception;
import java.time.Instant;

public record ErrorResponse(String message,
                            String errorCode,
                            Instant timestamp)
{
}
