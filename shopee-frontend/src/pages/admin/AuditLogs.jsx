import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/apiService";

const PAGE_SIZE = 20;

const today = new Date();
const iso = (d) => d.toISOString().slice(0, 19);

const defaultFrom = iso(new Date(today.getFullYear(), today.getMonth(), 1, 0, 0, 0));
const defaultTo = iso(new Date(today.getFullYear(), today.getMonth(), today.getDate(), 23, 59, 59));

function AuditLogs() {
  const navigate = useNavigate();

  const [franchises, setFranchises] = useState([]);
  const [franchiseId, setFranchiseId] = useState("");
  const [from, setFrom] = useState(defaultFrom.slice(0, 10));
  const [to, setTo] = useState(defaultTo.slice(0, 10));
  const [logs, setLogs] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    api
      .get("/admin/franchise?size=100")
      .then((res) => setFranchises(res.data.content ?? []))
      .catch(() => {});
  }, []);

  function fetchLogs(p = 0) {
    if (!franchiseId) { setError("Please select a franchise."); return; }
    setError("");
    setLoading(true);
    const fromDt = encodeURIComponent(`${from}T00:00:00`);
    const toDt = encodeURIComponent(`${to}T23:59:59`);
    api
      .get(`/api/audit-logs/franchise/${franchiseId}?from=${fromDt}&to=${toDt}&page=${p}&size=${PAGE_SIZE}&sort=timestamp,desc`)
      .then((res) => {
        setLogs(res.data.content ?? []);
        setTotalPages(res.data.totalPages ?? 1);
        setPage(p);
      })
      .catch((err) => setError(err.response?.data?.message || "Failed to load audit logs"))
      .finally(() => setLoading(false));
  }

  function handleExportCsv() {
    if (!franchiseId) return;
    const fromDt = encodeURIComponent(`${from}T00:00:00`);
    const toDt = encodeURIComponent(`${to}T23:59:59`);
    const url = `/api/audit-logs/export?franchiseId=${franchiseId}&from=${fromDt}&to=${toDt}`;
    api
      .get(url, { responseType: "blob" })
      .then((res) => {
        const blob = new Blob([res.data], { type: "text/csv;charset=utf-8;" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `audit-logs-${franchiseId}-${from}-to-${to}.csv`;
        link.click();
        URL.revokeObjectURL(link.href);
      })
      .catch(() => alert("CSV export failed."));
  }

  function handleLogout() {
    if (window.confirm("Log out?")) {
      localStorage.clear();
      navigate("/");
    }
  }

  return (
    <div style={{ display: "flex", minHeight: "100vh", background: "#f9fafb" }}>
      {/* Sidebar */}
      <aside style={{ width: "200px", background: "#1e293b", color: "#fff", padding: "1.5rem 1rem", display: "flex", flexDirection: "column", gap: "0.5rem" }}>
        <h2 style={{ fontSize: "1rem", marginBottom: "1rem", color: "#94a3b8" }}>Admin</h2>
        {[
          { label: "Dashboard", path: "/admin/dashboard" },
          { label: "Register Franchise", path: "/admin/register-franchise" },
          { label: "View Franchises", path: "/admin/view-franchises" },
          { label: "Audit Logs", path: "/admin/audit-logs" },
        ].map(({ label, path }) => (
          <button
            key={path}
            onClick={() => navigate(path)}
            style={{
              background: path === "/admin/audit-logs" ? "#6366f1" : "transparent",
              color: "#fff",
              border: "none",
              borderRadius: "6px",
              padding: "0.5rem 0.75rem",
              textAlign: "left",
              cursor: "pointer",
              fontWeight: path === "/admin/audit-logs" ? "600" : "400",
            }}
          >
            {label}
          </button>
        ))}
        <button onClick={handleLogout} style={{ marginTop: "auto", background: "#ef4444", color: "#fff", border: "none", borderRadius: "6px", padding: "0.5rem 0.75rem", cursor: "pointer" }}>
          Logout
        </button>
      </aside>

      {/* Main */}
      <main style={{ flex: 1, padding: "2rem" }}>
        <h1 style={{ marginBottom: "1.5rem", fontSize: "1.5rem", fontWeight: "700", color: "#111827" }}>
          Audit Logs
        </h1>

        {/* Filters */}
        <div style={{ display: "flex", gap: "1rem", alignItems: "flex-end", flexWrap: "wrap", background: "#fff", padding: "1.25rem", borderRadius: "10px", boxShadow: "0 1px 3px rgba(0,0,0,0.07)", marginBottom: "1.5rem" }}>
          <label style={labelStyle}>
            Franchise
            <select value={franchiseId} onChange={(e) => setFranchiseId(e.target.value)} style={inputStyle}>
              <option value="">— Select —</option>
              {franchises.map((f) => (
                <option key={f.franchiseId} value={f.franchiseId}>{f.name}</option>
              ))}
            </select>
          </label>
          <label style={labelStyle}>
            From
            <input type="date" value={from} max={to} onChange={(e) => setFrom(e.target.value)} style={inputStyle} />
          </label>
          <label style={labelStyle}>
            To
            <input type="date" value={to} min={from} onChange={(e) => setTo(e.target.value)} style={inputStyle} />
          </label>
          <button onClick={() => fetchLogs(0)} disabled={loading} style={{ padding: "0.55rem 1.25rem", background: "#6366f1", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "600", alignSelf: "flex-end" }}>
            {loading ? "Loading…" : "Search"}
          </button>
          {logs.length > 0 && (
            <button onClick={handleExportCsv} style={{ padding: "0.55rem 1rem", background: "#10b981", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", alignSelf: "flex-end" }}>
              Export CSV
            </button>
          )}
        </div>

        {error && <p style={{ color: "#ef4444", marginBottom: "1rem" }}>{error}</p>}

        {logs.length > 0 ? (
          <>
            <div style={{ overflowX: "auto", background: "#fff", borderRadius: "10px", boxShadow: "0 1px 3px rgba(0,0,0,0.07)" }}>
              <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.85rem" }}>
                <thead>
                  <tr style={{ background: "#f3f4f6" }}>
                    <th style={th}>Timestamp</th>
                    <th style={th}>Actor</th>
                    <th style={th}>Role</th>
                    <th style={th}>Action</th>
                    <th style={th}>Entity</th>
                    <th style={th}>Entity ID</th>
                    <th style={th}>IP</th>
                  </tr>
                </thead>
                <tbody>
                  {logs.map((log) => (
                    <tr key={log.id} style={{ borderBottom: "1px solid #e5e7eb" }}>
                      <td style={td}>{new Date(log.timestamp).toLocaleString("en-IN")}</td>
                      <td style={td}>{log.actorEmail}</td>
                      <td style={td}>
                        <span style={{ background: "#e0e7ff", color: "#4338ca", padding: "0.15rem 0.5rem", borderRadius: "9999px", fontSize: "0.75rem", fontWeight: "600" }}>
                          {log.actorRole}
                        </span>
                      </td>
                      <td style={td}>
                        <span style={{ fontFamily: "monospace", background: "#f3f4f6", padding: "0.1rem 0.4rem", borderRadius: "4px" }}>{log.action}</span>
                      </td>
                      <td style={td}>{log.entityType}</td>
                      <td style={td}>{log.entityId}</td>
                      <td style={{ ...td, color: "#9ca3af" }}>{log.ipAddress}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div style={{ display: "flex", gap: "0.5rem", justifyContent: "center", marginTop: "1rem" }}>
                <button onClick={() => fetchLogs(page - 1)} disabled={page === 0} style={pageBtnStyle(false)}>← Prev</button>
                <span style={{ padding: "0.4rem 0.75rem", fontSize: "0.875rem", color: "#374151" }}>
                  Page {page + 1} / {totalPages}
                </span>
                <button onClick={() => fetchLogs(page + 1)} disabled={page >= totalPages - 1} style={pageBtnStyle(false)}>Next →</button>
              </div>
            )}
          </>
        ) : !loading && (
          <p style={{ color: "#9ca3af", textAlign: "center", marginTop: "4rem" }}>
            Select a franchise and date range, then click <strong>Search</strong>.
          </p>
        )}
      </main>
    </div>
  );
}

const labelStyle = { display: "flex", flexDirection: "column", gap: "0.25rem", fontSize: "0.875rem", color: "#374151", fontWeight: "500" };
const inputStyle = { padding: "0.4rem 0.6rem", border: "1px solid #d1d5db", borderRadius: "6px", fontSize: "0.875rem" };
const th = { padding: "0.6rem 0.75rem", textAlign: "left", fontWeight: "600", color: "#374151", whiteSpace: "nowrap" };
const td = { padding: "0.6rem 0.75rem", color: "#374151", whiteSpace: "nowrap" };
const pageBtnStyle = () => ({ padding: "0.4rem 0.75rem", background: "#fff", border: "1px solid #d1d5db", borderRadius: "6px", cursor: "pointer", fontSize: "0.875rem" });

export default AuditLogs;
