package com.pipelinegraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Team")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Team {
    @Id
    private String id;
    private String name;
}
