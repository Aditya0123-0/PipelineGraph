package com.pipelinegraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("System")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemNode {
    @Id
    private String id;
    private String name;
    private String type;
    private String criticality;
}
