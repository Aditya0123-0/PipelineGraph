# PipelineGraph

**A tool that shows what breaks when one system in your company's automation stack goes down.**

Built for the Wexa AI take-home assignment, using CognoDB (a managed graph database) as the data layer.

---

## What is this, in simple words?

Imagine a company that uses a bunch of different software tools — Salesforce for sales, SAP for finance, Zendesk for support, and so on. Behind the scenes, these tools are wired together: data pipelines move information between them, AI agents automate parts of the work, and business workflows (like "turn a lead into a paying customer") depend on several of these systems working at once.

**PipelineGraph lets you click on any one of these systems and instantly see everything that depends on it** — which workflows would stall, which AI agents would stop working, and which other systems would be affected, if that one system went down.

It also answers a few other useful questions:
- *Which systems handle sensitive customer data (PII), and where does that data flow?*
- *What's the shortest chain of connections between two systems?*
- *Which system is the biggest single point of failure — the one the most things depend on?*

Think of it as a small, focused version of the kind of "what's connected to what" map that a platform like Wexa needs internally to understand how automated workflows ripple across a company's tools.

---

## Why a graph database?

The core questions this tool answers are all about **connections and chains**, not rows of data:

- "What depends on this system?" means following relationships outward, possibly through several hops (a system → an agent that uses it → a workflow that uses that agent → another workflow it triggers).
- "What's the shortest path between two systems?" is a pathfinding problem.
- "How many things ultimately depend on this system?" means counting everything reachable through a chain of relationships, of unknown length.

In a relational (SQL) database, each of these would need a **recursive query with a fixed maximum depth**, and get messier the deeper the chain goes. In a graph database, they're natural, direct queries — Cypher's `[:RELATIONSHIP*1..5]` syntax and `shortestPath()` express "follow this connection up to 5 hops" or "find the shortest route" in a single line, because the database is built around relationships instead of tables.

This project has one relationship in particular — a workflow that can trigger another workflow, which can trigger another — that's a great example of something painful in SQL (an unknown-depth chain) and simple in Cypher.

---

## Data model

Six types of things (nodes), connected by five types of relationships:

| Node | What it represents |
|---|---|
| **System** | A piece of software — Salesforce, SAP, Zendesk, etc. |
| **Pipeline** | A scheduled job that moves data between systems |
| **Workflow** | A business process, like "Lead to Cash" |
| **Agent** | An automation step that does one job inside a workflow |
| **DataEntity** | A category of data, like "Customer PII" or "Invoice Data" |
| **Team** | The business unit that owns a workflow |

**How they connect:**

- A **Workflow** is `OWNED_BY` a **Team**
- A **Workflow** `USES_AGENT` — one or more **Agents**
- An **Agent** `DEPENDS_ON` a **System**
- A **System** `FEEDS` a **Pipeline**, and a **Pipeline** `FEEDS` back out to other **Systems**
- A **Pipeline** `CARRIES` a **DataEntity**
- A **Workflow** can `TRIGGERS` another **Workflow** — this is the one that chains, sometimes 3–4 steps deep, and it's what makes the "what breaks downstream" question interesting

*(See `docs/data-model-diagram.png` for the visual version.)*

---

## Tech stack

- **Database:** CognoDB Cloud (graph database, speaks openCypher over Bolt)
- **Backend:** Java 17 + Spring Boot 3, using Spring Data Neo4j and the official Neo4j driver
- **Seed script:** Python, using the official `neo4j` driver — loads sample data into CognoDB
- **Frontend:** React (Vite) with `react-force-graph-2d` for the interactive graph visualization

---

## How to run this yourself

### 1. Create your own CognoDB instance

1. Go to [console.cognodb.com/signup](https://console.cognodb.com/signup) and sign up (no credit card needed for the free tier).
2. Create a free (**c0**) instance and pick a region — it provisions in under a minute.
3. Copy the connection URI (looks like `bolt+s://<instance-id>.databases.cognodb.cloud`) and the generated password for the `cognodb` user. **The password is only shown once** — save it immediately.

### 2. Seed the database

```bash
cd seed
python -m venv venv
source venv/bin/activate      # Windows: venv\Scripts\activate
pip install -r requirements.txt

cp .env.example .env          # then fill in your real CognoDB URI + password
python seed_data.py
```

You should see a summary line confirming how many teams, systems, pipelines, agents, workflows, and data entities were created.

### 3. Run the backend

```bash
cd backend
# fill in your CognoDB URI + password as environment variables, or in application.yml
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8080` by default.

### 4. Run the frontend

```bash
cd frontend
npm install
cp .env.example .env          # set VITE_API_BASE_URL to your backend URL
npm run dev
```

Open the printed local URL in your browser.

---

## The main things you can do in the app

**Explorer page** — pick a system from the dropdown, and the graph highlights every workflow, agent, and system that depends on it. Click any node in the graph to explore from there.

**Analytics page** —
- **Single points of failure**: a ranked list of which systems the most things depend on
- **PII trace**: shows exactly which pipelines and systems touch sensitive customer or employee data

---

## The four core queries, explained simply

1. **Impact analysis** (`GET /api/impact/{systemId}`) — "If this system fails, what breaks?" Follows dependency and trigger chains outward, up to 5 steps, and returns everything reachable.
2. **Shortest path** (`GET /api/analytics/shortest-path`) — the fewest number of hops connecting any two systems or workflows in the graph.
3. **PII trace** (`GET /api/analytics/pii-trace`) — starts from data marked as sensitive, and walks backward to every pipeline and system that touches it.
4. **Single-point-of-failure ranking** (`GET /api/analytics/spof-ranking`) — for every system, counts how many other things would be affected if it went down, and ranks them highest-impact first.

All four are written as parameterized Cypher queries (no string concatenation) using the official Neo4j driver.

---

## Screenshots

*(Add screenshots here before submitting — Explorer page with a system selected and impact highlighted, and the Analytics page showing the SPOF ranking and PII trace.)*

---

## Project structure

```
pipelinegraph/
├── backend/     # Spring Boot API
├── seed/        # Python seed script + sample data
├── frontend/    # React app
└── docs/        # Data model diagram, screenshots
```
