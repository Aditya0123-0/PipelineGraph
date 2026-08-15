import PiiTracePanel from "../components/PiiTracePanel.jsx";
import SpofLeaderboard from "../components/SpofLeaderboard.jsx";

export default function AnalyticsPage() {
  return (
    <div className="analytics-page">
      <section className="analytics-column">
        <SpofLeaderboard />
      </section>
      <section className="analytics-column">
        <PiiTracePanel />
      </section>
    </div>
  );
}