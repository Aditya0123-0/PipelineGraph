package com.pipelinegraph.exception;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(String type, String id) {
        super(type + " with id '" + id + "' was not found");
    }}
