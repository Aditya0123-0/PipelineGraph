import { useEffect, useState } from "react";
import { fetchPiiTrace } from "../api/client.js";
import LoadingState from "./LoadingState.jsx";
import EmptyState from "./EmptyState.jsx";
import ErrorState from "./ErrorState.jsx";

export default function PiiTracePanel() {
  const [status, setStatus] = useState("loading");
  const [traces, setTraces] = useState([]);

  const load = () => {
    setStatus("loading");
    fetchPiiTrace("PII")
      .then((data) => {
        setTraces(data);
        setStatus("ready");
      })
      .catch(() => setStatus("error"));
  };

  useEffect(load, []);

  if (status === "loading") return <LoadingState label="Tracing sensitive data flow…" />;
  if (status === "error") return <ErrorState message="Couldn't load PII trace." onRetry={load} />;
  if (traces.length === 0) {
    return <EmptyState title="No PII flows found" description="No data entities are tagged as PII in this dataset." />;
  }

  return (
    <div className="pii-trace-panel">
      <h3 className="panel-title">Where PII flows</h3>
      {traces.map((trace) => (
        <div key={trace.dataEntityId} className="pii-trace-card">
          <div className="pii-trace-header">
            <span className="type-dot type-dot-DataEntity" />
            <span>{trace.dataEntityId}</span>
            <span className="pii-sensitivity-badge">{trace.sensitivity}</span>
          </div>
          <div className="pii-trace-body">
            <div className="pii-trace-group">
              <span className="field-label">Pipelines</span>
              <ul>
                {trace.touchingPipelines.map((p) => (
                  <li key={p.id}>{p.label}</li>
                ))}
              </ul>
            </div>
            <div className="pii-trace-group">
              <span className="field-label">Systems</span>
              <ul>
                {trace.touchingSystems.map((s) => (
                  <li key={s.id}>{s.label}</li>
                ))}
              </ul>
            </div>
          </div>
        </div>
      ))}
    </div>
  );
}