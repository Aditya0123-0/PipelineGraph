package com.pipelinegraph.repository;

import com.pipelinegraph.model.Workflow;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkflowRepository extends Neo4jRepository<Workflow,String> {
}
