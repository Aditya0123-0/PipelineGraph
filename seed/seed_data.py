"""
Seed script for PipelineGraph — loads teams, systems, pipelines, agents,
workflows, and data entities into CognoDB, then wires up the relationships
between them.

Run with: python seed_data.py
Requires .env with NEO4J_URI, NEO4J_USER, NEO4J_PASSWORD set.
"""

import json
import os
from pathlib import Path

from dotenv import load_dotenv
from neo4j import GraphDatabase

# Always resolve .env relative to this file's own location, regardless of
# which directory the script is launched from.
load_dotenv(dotenv_path=Path(__file__).parent / ".env")

NEO4J_URI = os.environ["NEO4J_URI"]
NEO4J_USER = os.environ["NEO4J_USER"]
NEO4J_PASSWORD = os.environ["NEO4J_PASSWORD"]

DATA_DIR = Path(__file__).parent / "data"


def load_json(filename: str):
    with open(DATA_DIR / filename, "r", encoding="utf-8") as f:
        return json.load(f)


# ---------- Node creation (MERGE = idempotent: safe to re-run) ----------

def seed_teams(tx, teams):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (t:Team {id: row.id})
        SET t.name = row.name
        """,
        rows=teams,
    )


def seed_systems(tx, systems):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (s:System {id: row.id})
        SET s.name = row.name,
            s.type = row.type,
            s.criticality = row.criticality
        """,
        rows=systems,
    )


def seed_data_entities(tx, entities):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (d:DataEntity {id: row.id})
        SET d.name = row.name,
            d.sensitivity = row.sensitivity
        """,
        rows=entities,
    )


def seed_pipelines(tx, pipelines):
    # Node properties only here; source/target/carries wired separately below
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (p:Pipeline {id: row.id})
        SET p.name = row.name,
            p.schedule = row.schedule
        """,
        rows=pipelines,
    )


def seed_agents(tx, agents):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (a:Agent {id: row.id})
        SET a.name = row.name,
            a.function = row.function
        """,
        rows=agents,
    )


def seed_workflows(tx, workflows):
    tx.run(
        """
        UNWIND $rows AS row
        MERGE (w:Workflow {id: row.id})
        SET w.name = row.name
        """,
        rows=workflows,
    )


# ---------- Relationship wiring ----------

def link_pipeline_sources_targets_carries(tx, pipelines):
    for pipeline in pipelines:
        tx.run(
            """
            MATCH (p:Pipeline {id: $pipelineId})
            UNWIND $sourceIds AS srcId
            MATCH (src:System {id: srcId})
            MERGE (src)-[:FEEDS]->(p)
            """,
            pipelineId=pipeline["id"],
            sourceIds=pipeline["sources"],
        )
        tx.run(
            """
            MATCH (p:Pipeline {id: $pipelineId})
            UNWIND $targetIds AS tgtId
            MATCH (tgt:System {id: tgtId})
            MERGE (p)-[:FEEDS]->(tgt)
            """,
            pipelineId=pipeline["id"],
            targetIds=pipeline["targets"],
        )
        tx.run(
            """
            MATCH (p:Pipeline {id: $pipelineId})
            UNWIND $carriesIds AS deId
            MATCH (d:DataEntity {id: deId})
            MERGE (p)-[:CARRIES]->(d)
            """,
            pipelineId=pipeline["id"],
            carriesIds=pipeline["carries"],
        )


def link_agent_dependencies(tx, agents):
    for agent in agents:
        tx.run(
            """
            MATCH (a:Agent {id: $agentId})
            UNWIND $systemIds AS sysId
            MATCH (s:System {id: sysId})
            MERGE (a)-[:DEPENDS_ON]->(s)
            """,
            agentId=agent["id"],
            systemIds=agent["depends_on"],
        )


def link_workflows(tx, workflows):
    for workflow in workflows:
        tx.run(
            """
            MATCH (w:Workflow {id: $workflowId})
            MATCH (t:Team {id: $teamId})
            MERGE (w)-[:OWNED_BY]->(t)
            """,
            workflowId=workflow["id"],
            teamId=workflow["owned_by"],
        )
        tx.run(
            """
            MATCH (w:Workflow {id: $workflowId})
            UNWIND $agentIds AS agentId
            MATCH (a:Agent {id: agentId})
            MERGE (w)-[:USES_AGENT]->(a)
            """,
            workflowId=workflow["id"],
            agentIds=workflow["uses_agents"],
        )
        tx.run(
            """
            MATCH (w:Workflow {id: $workflowId})
            UNWIND $triggerIds AS triggerId
            MATCH (next:Workflow {id: triggerId})
            MERGE (w)-[:TRIGGERS]->(next)
            """,
            workflowId=workflow["id"],
            triggerIds=workflow["triggers"],
        )


def clear_database(tx):
    tx.run("MATCH (n) DETACH DELETE n")


def main():
    driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
    driver.verify_connectivity()

    teams = load_json("teams.json")
    systems = load_json("systems.json")
    data_entities = load_json("data_entities.json")
    pipelines = load_json("pipelines.json")
    agents = load_json("agents.json")
    workflows = load_json("workflows.json")

    try:
        with driver.session() as session:
            print("Clearing existing data...")
            session.execute_write(clear_database)

            print("Seeding nodes...")
            session.execute_write(seed_teams, teams)
            session.execute_write(seed_systems, systems)
            session.execute_write(seed_data_entities, data_entities)
            session.execute_write(seed_pipelines, pipelines)
            session.execute_write(seed_agents, agents)
            session.execute_write(seed_workflows, workflows)

            print("Wiring relationships...")
            session.execute_write(link_pipeline_sources_targets_carries, pipelines)
            session.execute_write(link_agent_dependencies, agents)
            session.execute_write(link_workflows, workflows)

        print(
            f"Seed complete: {len(teams)} teams, {len(systems)} systems, "
            f"{len(pipelines)} pipelines, {len(agents)} agents, "
            f"{len(workflows)} workflows, {len(data_entities)} data entities."
        )
    finally:
        driver.close()


if __name__ == "__main__":
    main()