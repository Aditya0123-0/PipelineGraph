package com.pipelinegraph.repository;

import com.pipelinegraph.model.Pipeline;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PipelineRepository extends Neo4jRepository<Pipeline,String> {
}
