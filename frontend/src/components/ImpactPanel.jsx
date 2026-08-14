import { useEffect, useState } from "react";
import { fetchSystemImpact } from "../api/client.js";
import LoadingState from "./LoadingState.jsx";
import EmptyState from "./EmptyState.jsx";
import ErrorState from "./ErrorState.jsx";

export default function ImpactPanel({ systemId }) {
  const [status, setStatus] = useState("empty"); // empty | loading | ready | error
  const [impact, setImpact] = useState(null);

  useEffect(() => {
    if (!systemId) {
      setStatus("empty");
      setImpact(null);
      return;
    }
    setStatus("loading");
    fetchSystemImpact(systemId)
      .then((data) => {
        setImpact(data);
        setStatus("ready");
      })
      .catch(() => setStatus("error"));
  }, [systemId]);

  if (status === "empty") {
    return (
      <EmptyState
        title="Pick a system to see what breaks"
        description="Select a system above to trace every workflow, agent, and downstream system that depends on it."
      />
    );
  }
  if (status === "loading") return <LoadingState label="Tracing impact…" />;
  if (status === "error") return <ErrorState message="Couldn't run impact analysis." />;

  return (
    <div className="impact-panel">
      <div className="impact-summary">
        <span className="impact-count">{impact.totalImpactCount}</span>
        <span className="impact-count-label">nodes affected</span>
      </div>

      {impact.affectedNodes.length === 0 ? (
        <EmptyState
          title="Nothing depends on this system"
          description="No workflows, agents, or systems are downstream of this one."
        />
      ) : (
        <ul className="impact-list">
          {impact.affectedNodes.map((node) => (
            <li key={node.id} className="impact-list-item">
              <span className={`type-dot type-dot-${node.type}`} />
              <span className="impact-node-label">{node.label}</span>
              <span className="impact-node-type">{node.type}</span>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}