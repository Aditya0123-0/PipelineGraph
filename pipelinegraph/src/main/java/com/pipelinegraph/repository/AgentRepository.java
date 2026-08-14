package com.pipelinegraph.repository;

import com.pipelinegraph.model.Agent;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgentRepository extends Neo4jRepository<Agent,String> {
}
