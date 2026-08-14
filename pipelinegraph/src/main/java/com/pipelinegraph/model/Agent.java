package com.pipelinegraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Agent")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agent {
    @Id
    private String id;
    private String name;
    private String function;

    @Relationship(type = "DEPENDS_ON", direction = Relationship.Direction.OUTGOING)
    private List<SystemNode> dependsOn;
}
