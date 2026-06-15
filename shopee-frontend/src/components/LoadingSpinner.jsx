import "./LoadingSpinner.css";

/**
 * Full-page centered loading spinner.
 * Usage: <LoadingSpinner />
 *        <LoadingSpinner size={48} color="#6366f1" />
 */
export default function LoadingSpinner({ size = 40, color = "#6366f1" }) {
  return (
    <div className="spinner-overlay" role="status" aria-label="Loading">
      <div
        className="spinner"
        style={{
          width: size,
          height: size,
          borderTopColor: color,
          borderRightColor: color,
        }}
      />
    </div>
  );
}
