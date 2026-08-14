package com.pipelinegraph.repository;

import org.neo4j.driver.Record;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class GraphQueryRepository {

    private final Neo4jClient neo4jClient;

    public GraphQueryRepository(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    // --- 1. Impact analysis: multi-hop, variable-depth traversal ---
    // "If this System fails, what breaks?" Walks DEPENDS_ON and TRIGGERS
    // backwards up to 5 hops. This is the query a relational DB would need
    // a recursive CTE (and a fixed max depth) to even approximate.
    
    public List<Map<String, Object>> findImpact(String systemId) {
        String cypher = """
            MATCH (s:System {id: $systemId})
            MATCH path = (n)-[:DEPENDS_ON|TRIGGERS*1..5]->(s)
            WITH collect(DISTINCT n) AS affected, collect(path) AS paths
            UNWIND paths AS p
            UNWIND relationships(p) AS r
            RETURN affected,
                   collect(DISTINCT {
                       source: startNode(r).id,
                       target: endNode(r).id,
                       relationship: type(r)
                   }) AS edges
            """;
        return neo4jClient.query(cypher)
                .bind(systemId).to("systemId")
                .fetch()
                .all()
                .stream()
                .map(this::toRecordLike) // see note below
                .toList();
    }

    // --- 2. Shortest path between any two named nodes ---
    public Optional<Map<String, Object>> findShortestPath(String fromId, String toId) {
        String cypher = """
            MATCH (a {id: $fromId}), (b {id: $toId})
            MATCH p = shortestPath((a)-[*..10]-(b))
            RETURN [n IN nodes(p) | {id: n.id, label: n.name, type: labels(n)[0]}] AS pathNodes,
                   [r IN relationships(p) | type(r)] AS pathRels,
                   length(p) AS hops
            """;
        return neo4jClient.query(cypher)
                .bind(fromId).to("fromId")
                .bind(toId).to("toId")
                .fetch()
                .one();
    }

    // --- 3. PII / sensitive data trace ---
    // DataEntity -> Pipeline -> System, filtered by sensitivity.
    public List<Map<String, Object>> tracePiiFlow(String sensitivity) {
        String cypher = """
            MATCH (d:DataEntity {sensitivity: $sensitivity})<-[:CARRIES]-(p:Pipeline)
            MATCH (p)-[:FEEDS]-(s:System)
            RETURN d.id AS dataEntityId, d.sensitivity AS sensitivity,
                   collect(DISTINCT {id: s.id, label: s.name, type: 'System'}) AS systems,
                   collect(DISTINCT {id: p.id, label: p.name, type: 'Pipeline'}) AS pipelines
            """;
        return neo4jClient.query(cypher)
                .bind(sensitivity).to("sensitivity")
                .fetch()
                .all()
                .stream()
                .toList();
    }

    // --- 4. Single-point-of-failure ranking ---
    // For every System, count distinct downstream nodes reachable via
    // DEPENDS_ON/TRIGGERS. This aggregation-over-variable-depth-reachability
    // is the other classic "painful in SQL" case.
    public List<Map<String, Object>> rankSystemsByDependents() {
        String cypher = """
            MATCH (s:System)
            OPTIONAL MATCH (n)-[:DEPENDS_ON|TRIGGERS*1..5]->(s)
            WITH s, count(DISTINCT n) AS dependentCount
            RETURN s.id AS systemId, s.name AS systemName, dependentCount
            ORDER BY dependentCount DESC
            """;
        return neo4jClient.query(cypher)
                .fetch()
                .all()
                .stream()
                .toList();
    }

    private Map<String, Object> toRecordLike(Map<String, Object> row) {
        return row; // placeholder — mapping detail handled in service layer below
    }
}