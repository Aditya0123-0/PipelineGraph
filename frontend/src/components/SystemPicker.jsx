import { useEffect, useState } from "react";
import { fetchSystems } from "../api/client.js";
import LoadingState from "./LoadingState.jsx";
import ErrorState from "./ErrorState.jsx";

export default function SystemPicker({ selectedSystemId, onSelect }) {
  const [systems, setSystems] = useState([]);
  const [status, setStatus] = useState("loading"); // loading | ready | error

  const loadSystems = () => {
    setStatus("loading");
    fetchSystems()
      .then((data) => {
        setSystems(data);
        setStatus("ready");
      })
      .catch(() => setStatus("error"));
  };

  useEffect(loadSystems, []);

  if (status === "loading") return <LoadingState label="Loading systems…" />;
  if (status === "error") return <ErrorState message="Couldn't load systems." onRetry={loadSystems} />;

  return (
    <div className="system-picker">
      <label htmlFor="system-select" className="field-label">
        Pick a system
      </label>
      <select
        id="system-select"
        className="select-input"
        value={selectedSystemId || ""}
        onChange={(e) => onSelect(e.target.value)}
      >
        <option value="" disabled>
          Select a system…
        </option>
        {systems.map((s) => (
          <option key={s.id} value={s.id}>
            {s.name} — {s.criticality}
          </option>
        ))}
      </select>
    </div>
  );
}