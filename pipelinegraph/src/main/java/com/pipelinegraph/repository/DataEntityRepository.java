package com.pipelinegraph.repository;

import com.pipelinegraph.model.DataEntity;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataEntityRepository extends Neo4jRepository<DataEntity,String> {
}
