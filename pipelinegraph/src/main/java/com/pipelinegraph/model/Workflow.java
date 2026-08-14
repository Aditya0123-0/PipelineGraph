package com.pipelinegraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Workflow")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Workflow {
    @Id
    private String id;
    private String name;

    @Relationship(type = "OWNED_BY", direction = Relationship.Direction.OUTGOING)
    private Team ownedBy;

    @Relationship(type = "USES_AGENT", direction = Relationship.Direction.OUTGOING)
    private List<Agent> usesAgents;

    @Relationship(type = "TRIGGERS", direction = Relationship.Direction.OUTGOING)
    private List<Workflow> triggers;


}
