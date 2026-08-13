package com.pipelinegraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("DataEntity")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEntity {
    @Id
    private String id;
    private String name;
    private String sensitivity;
}
