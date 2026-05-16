import { useState, useEffect } from "react";
import apiService from "../services/apiService";

function BackendTest() {
  const [backendStatus, setBackendStatus] = useState("Checking...");
  const [error, setError] = useState("");

  useEffect(() => {
    testBackendConnection();
  }, []);

  const testBackendConnection = async () => {
    try {
      const response = await apiService.get("/test");

      // ✅ FIX: use response.data
      setBackendStatus(response.data);
      setError("");
    } catch (err) {
      console.error(err);
      setError("Failed to connect to backend. Make sure it's running on port 8080.");
      setBackendStatus("Connection failed");
    }
  };

  return (
    <div style={{ padding: "20px", border: "1px solid #ccc", margin: "20px" }}>
      <h3>Backend Connection Test</h3>
      <p><strong>Status:</strong> {backendStatus}</p>

      {error && (
        <p style={{ color: "red" }}>
          <strong>Error:</strong> {error}
        </p>
      )}

      <button onClick={testBackendConnection}>Test Connection Again</button>
    </div>
  );
}

export default BackendTest;
