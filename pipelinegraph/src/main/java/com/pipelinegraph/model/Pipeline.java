package com.pipelinegraph.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node("Pipeline")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pipeline {
    @Id
    private String id;
    private String name;
    private String schedule;

    @Relationship(type = "CARRIES", direction = Relationship.Direction.OUTGOING)
    private List<DataEntity> carriers;

    @Relationship(type = "FEEDS", direction = Relationship.Direction.INCOMING)
    private List<SystemNode> sourceSystem;

    @Relationship(type = "FEEDS", direction = Relationship.Direction.OUTGOING)
    private List<SystemNode> targetSystem;
}
