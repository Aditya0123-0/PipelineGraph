import { useEffect, useMemo, useState } from "react";
import { fetchSystems, fetchSystemImpact } from "../api/client.js";
import GraphView from "../components/GraphView.jsx";
import SystemPicker from "../components/SystemPicker.jsx";
import ImpactPanel from "../components/ImpactPanel.jsx";
import LoadingState from "../components/LoadingState.jsx";
import ErrorState from "../components/ErrorState.jsx";

export default function ExplorerPage() {
  const [selectedSystemId, setSelectedSystemId] = useState(null);
  const [allSystems, setAllSystems] = useState([]);
  const [status, setStatus] = useState("loading");
  const [impact, setImpact] = useState(null);

  useEffect(() => {
    fetchSystems()
      .then((data) => {
        setAllSystems(data);
        setStatus("ready");
      })
      .catch(() => setStatus("error"));
  }, []);

  useEffect(() => {
    if (!selectedSystemId) {
      setImpact(null);
      return;
    }
    fetchSystemImpact(selectedSystemId)
      .then(setImpact)
      .catch(() => setImpact(null));
  }, [selectedSystemId]);

  // Base graph: every system as a node, no edges — until a system is
  // selected, at which point we overlay the impact nodes/edges on top.
  const baseNodes = useMemo(
    () => allSystems.map((s) => ({ id: s.id, label: s.name, type: "System" })),
    [allSystems]
  );

  const graphNodes = impact
    ? mergeNodes(baseNodes, impact.affectedNodes, selectedSystemId, allSystems)
    : baseNodes;
  const graphEdges = impact ? impact.affectedEdges : [];
  const highlightedNodeIds = useMemo(
    () => new Set(impact ? impact.affectedNodes.map((n) => n.id) : []),
    [impact]
  );

  if (status === "loading") return <LoadingState label="Loading explorer…" />;
  if (status === "error") return <ErrorState message="Couldn't load systems." />;

  return (
    <div className="explorer-page">
      <aside className="explorer-sidebar">
        <SystemPicker selectedSystemId={selectedSystemId} onSelect={setSelectedSystemId} />
        <ImpactPanel systemId={selectedSystemId} />
      </aside>
      <section className="explorer-canvas">
        <GraphView
          nodes={graphNodes}
          edges={graphEdges}
          highlightedNodeIds={highlightedNodeIds}
          onNodeClick={(node) => {
            if (node.type === "System") setSelectedSystemId(node.id);
          }}
        />
      </section>
    </div>
  );
}

function mergeNodes(baseNodes, affectedNodes, rootId, allSystems) {
  const byId = new Map(baseNodes.map((n) => [n.id, n]));
  affectedNodes.forEach((n) => byId.set(n.id, n));
  const root = allSystems.find((s) => s.id === rootId);
  if (root) byId.set(root.id, { id: root.id, label: root.name, type: "System" });
  return Array.from(byId.values());
}