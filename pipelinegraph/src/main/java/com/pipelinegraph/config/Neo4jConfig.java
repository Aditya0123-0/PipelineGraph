package com.pipelinegraph.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    Driver driver = GraphDatabase.driver(
            "bolt+s://db-478d50e0.databases.cognodb.com",
            AuthTokens.basic("cognodb", "faf3748fa8d76622189e19fa45d21160"));
}
