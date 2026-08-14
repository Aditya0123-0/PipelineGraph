export default function LoadingState({ label = "Loading…" }) {
  return (
    <div className="state-block state-loading" role="status" aria-live="polite">
      <div className="loading-spinner" />
      <p>{label}</p>
    </div>
  );
}