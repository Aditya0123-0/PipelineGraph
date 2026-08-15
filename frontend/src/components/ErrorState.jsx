export default function ErrorState({ message, onRetry }) {
  return (
    <div className="state-block state-error" role="alert">
      <p className="state-title">Something went wrong</p>
      <p className="state-description">{message || "The request could not be completed."}</p>
      {onRetry && (
        <button className="btn btn-secondary" onClick={onRetry}>
          Try again
        </button>
      )}
    </div>
  );
}