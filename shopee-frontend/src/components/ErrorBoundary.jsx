import { Component } from "react";

/**
 * React Error Boundary — catches render-time errors in the subtree.
 * Wrap around route components or any section that may throw.
 *
 * Usage:
 *   <ErrorBoundary>
 *     <SomePage />
 *   </ErrorBoundary>
 */
export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { hasError: false, error: null };
  }

  static getDerivedStateFromError(error) {
    return { hasError: true, error };
  }

  componentDidCatch(error, info) {
    console.error("[ErrorBoundary] Uncaught error:", error, info);
  }

  handleReset = () => {
    this.setState({ hasError: false, error: null });
  };

  render() {
    if (this.state.hasError) {
      return (
        <div style={{ textAlign: "center", padding: "4rem" }}>
          <h2 style={{ color: "#ef4444" }}>Something went wrong.</h2>
          <p style={{ color: "#6b7280", marginBottom: "1.5rem" }}>
            {this.state.error?.message || "An unexpected error occurred."}
          </p>
          <button
            onClick={this.handleReset}
            style={{
              padding: "0.5rem 1.5rem",
              background: "#6366f1",
              color: "#fff",
              border: "none",
              borderRadius: "8px",
              cursor: "pointer",
            }}
          >
            Try again
          </button>
        </div>
      );
    }

    return this.props.children;
  }
}
