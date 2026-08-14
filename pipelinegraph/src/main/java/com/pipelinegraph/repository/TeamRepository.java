package com.pipelinegraph.repository;

import com.pipelinegraph.model.Team;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends Neo4jRepository<Team,String> {
}
