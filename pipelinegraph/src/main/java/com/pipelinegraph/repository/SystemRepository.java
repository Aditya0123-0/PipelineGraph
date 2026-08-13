package com.pipelinegraph.repository;

import com.pipelinegraph.model.DataEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface SystemRepository extends Neo4jRepository<DataEntity,String> {
}
