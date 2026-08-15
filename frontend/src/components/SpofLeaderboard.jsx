import { useEffect, useState } from "react";
import { fetchSpofRanking } from "../api/client.js";
import LoadingState from "./LoadingState.jsx";
import EmptyState from "./EmptyState.jsx";
import ErrorState from "./ErrorState.jsx";

export default function SpofLeaderboard() {
  const [status, setStatus] = useState("loading");
  const [ranking, setRanking] = useState([]);

  const load = () => {
    setStatus("loading");
    fetchSpofRanking()
      .then((data) => {
        setRanking(data);
        setStatus("ready");
      })
      .catch(() => setStatus("error"));
  };

  useEffect(load, []);

  if (status === "loading") return <LoadingState label="Ranking systems…" />;
  if (status === "error") return <ErrorState message="Couldn't load ranking." onRetry={load} />;
  if (ranking.length === 0) {
    return <EmptyState title="No systems found" description="Seed some data to see the ranking." />;
  }

  const maxCount = Math.max(...ranking.map((r) => r.dependentCount), 1);

  return (
    <div className="spof-leaderboard">
      <h3 className="panel-title">Single points of failure</h3>
      <ul className="spof-list">
        {ranking.map((r, i) => (
          <li key={r.systemId} className="spof-list-item">
            <span className="spof-rank">{i + 1}</span>
            <div className="spof-bar-wrap">
              <span className="spof-name">{r.systemName}</span>
              <div className="spof-bar-track">
                <div
                  className="spof-bar-fill"
                  style={{ width: `${(r.dependentCount / maxCount) * 100}%` }}
                />
              </div>
            </div>
            <span className="spof-count">{r.dependentCount}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}