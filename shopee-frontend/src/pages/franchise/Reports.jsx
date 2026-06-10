import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/apiService";

const today = new Date().toISOString().split("T")[0];
const firstOfMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1)
  .toISOString()
  .split("T")[0];

function Reports() {
  const navigate = useNavigate();
  const franchiseId = localStorage.getItem("franchiseId");

  const [from, setFrom] = useState(firstOfMonth);
  const [to, setTo] = useState(today);
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function fetchSummary() {
    if (!from || !to) {
      setError("Please select both from and to dates.");
      return;
    }
    if (from > to) {
      setError("'From' date must be before or equal to 'To' date.");
      return;
    }
    setError("");
    setLoading(true);
    api
      .get(`/franchise/${franchiseId}/reports/summary?from=${from}&to=${to}`)
      .then((res) => setSummary(res.data))
      .catch((err) => setError(err.response?.data?.message || "Failed to load report"))
      .finally(() => setLoading(false));
  }

  function handleExport(format) {
    const url = `/franchise/${franchiseId}/reports/export/${format}?from=${from}&to=${to}`;
    api
      .get(url, { responseType: "blob" })
      .then((res) => {
        const ext = format === "pdf" ? "pdf" : "xlsx";
        const blob = new Blob([res.data], { type: res.headers["content-type"] });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `sales-report-${from}-to-${to}.${ext}`;
        link.click();
        URL.revokeObjectURL(link.href);
      })
      .catch(() => alert("Export failed. Make sure a report has been loaded first."));
  }

  return (
    <div style={{ display: "flex", minHeight: "100vh", background: "#f9fafb" }}>
      {/* Sidebar */}
      <aside style={{ width: "200px", background: "#1e293b", color: "#fff", padding: "1.5rem 1rem", display: "flex", flexDirection: "column", gap: "0.5rem" }}>
        <h2 style={{ fontSize: "1rem", marginBottom: "1rem", color: "#94a3b8" }}>My Outlet</h2>
        {[
          { label: "Dashboard", path: "/franchise/dashboard" },
          { label: "Products", path: "/franchise/products" },
          { label: "Orders", path: "/franchise/orders" },
          { label: "Staff", path: "/franchise/staff" },
          { label: "Reports", path: "/franchise/reports" },
        ].map(({ label, path }) => (
          <button
            key={path}
            onClick={() => navigate(path)}
            style={{
              background: path === "/franchise/reports" ? "#6366f1" : "transparent",
              color: "#fff",
              border: "none",
              borderRadius: "6px",
              padding: "0.5rem 0.75rem",
              textAlign: "left",
              cursor: "pointer",
              fontWeight: path === "/franchise/reports" ? "600" : "400",
            }}
          >
            {label}
          </button>
        ))}
      </aside>

      {/* Main content */}
      <main style={{ flex: 1, padding: "2rem" }}>
        <h1 style={{ marginBottom: "1.5rem", fontSize: "1.5rem", fontWeight: "700", color: "#111827" }}>
          Sales Reports
        </h1>

        {/* Date range + actions */}
        <div style={{ display: "flex", gap: "1rem", alignItems: "flex-end", flexWrap: "wrap", background: "#fff", padding: "1.25rem", borderRadius: "10px", boxShadow: "0 1px 3px rgba(0,0,0,0.07)", marginBottom: "1.5rem" }}>
          <label style={{ display: "flex", flexDirection: "column", gap: "0.25rem", fontSize: "0.875rem", color: "#374151" }}>
            From
            <input type="date" value={from} max={to || today} onChange={(e) => setFrom(e.target.value)} style={inputStyle} />
          </label>
          <label style={{ display: "flex", flexDirection: "column", gap: "0.25rem", fontSize: "0.875rem", color: "#374151" }}>
            To
            <input type="date" value={to} min={from} max={today} onChange={(e) => setTo(e.target.value)} style={inputStyle} />
          </label>
          <button onClick={fetchSummary} disabled={loading} style={{ padding: "0.55rem 1.25rem", background: "#6366f1", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "600", height: "38px" }}>
            {loading ? "Loading…" : "Generate Report"}
          </button>
          {summary && (
            <>
              <button onClick={() => handleExport("pdf")} style={{ padding: "0.55rem 1rem", background: "#ef4444", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", height: "38px" }}>
                Export PDF
              </button>
              <button onClick={() => handleExport("excel")} style={{ padding: "0.55rem 1rem", background: "#10b981", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", height: "38px" }}>
                Export Excel
              </button>
            </>
          )}
        </div>

        {error && <p style={{ color: "#ef4444", marginBottom: "1rem" }}>{error}</p>}

        {summary && (
          <>
            {/* KPI cards */}
            <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(160px, 1fr))", gap: "1rem", marginBottom: "1.5rem" }}>
              <KpiCard label="Total Orders" value={summary.totalOrders} color="#6366f1" />
              <KpiCard label="Completed" value={summary.completedOrders} color="#10b981" />
              <KpiCard label="Cancelled" value={summary.cancelledOrders} color="#ef4444" />
              <KpiCard label="Total Revenue" value={`₹${Number(summary.totalRevenue).toFixed(2)}`} color="#3b82f6" />
              <KpiCard label="Avg Order Value" value={`₹${Number(summary.averageOrderValue).toFixed(2)}`} color="#f59e0b" />
            </div>

            {/* Daily revenue table */}
            {summary.dailyRevenue?.length > 0 && (
              <section style={card}>
                <h3 style={sectionTitle}>Daily Revenue</h3>
                <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.875rem" }}>
                  <thead>
                    <tr style={{ background: "#f3f4f6" }}>
                      <th style={th}>Date</th>
                      <th style={th}>Orders</th>
                      <th style={th}>Revenue</th>
                    </tr>
                  </thead>
                  <tbody>
                    {summary.dailyRevenue.map((d) => (
                      <tr key={d.date} style={{ borderBottom: "1px solid #e5e7eb" }}>
                        <td style={td}>{d.date}</td>
                        <td style={td}>{d.orderCount}</td>
                        <td style={td}>₹{Number(d.revenue).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </section>
            )}

            {/* Top products table */}
            {summary.topProducts?.length > 0 && (
              <section style={{ ...card, marginTop: "1.5rem" }}>
                <h3 style={sectionTitle}>Top Products</h3>
                <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.875rem" }}>
                  <thead>
                    <tr style={{ background: "#f3f4f6" }}>
                      <th style={th}>Product</th>
                      <th style={th}>SKU</th>
                      <th style={th}>Qty Sold</th>
                      <th style={th}>Revenue</th>
                    </tr>
                  </thead>
                  <tbody>
                    {summary.topProducts.map((p, i) => (
                      <tr key={i} style={{ borderBottom: "1px solid #e5e7eb" }}>
                        <td style={td}>{p.productName}</td>
                        <td style={td}>{p.sku || "—"}</td>
                        <td style={td}>{p.quantitySold}</td>
                        <td style={td}>₹{Number(p.revenue).toFixed(2)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </section>
            )}
          </>
        )}

        {!summary && !loading && (
          <p style={{ color: "#9ca3af", textAlign: "center", marginTop: "4rem" }}>
            Select a date range and click <strong>Generate Report</strong> to view your sales data.
          </p>
        )}
      </main>
    </div>
  );
}

function KpiCard({ label, value, color }) {
  return (
    <div style={{ background: "#fff", borderRadius: "8px", padding: "1rem 1.25rem", borderLeft: `4px solid ${color}`, boxShadow: "0 1px 3px rgba(0,0,0,0.07)" }}>
      <p style={{ margin: 0, fontSize: "0.75rem", color: "#6b7280", textTransform: "uppercase", letterSpacing: "0.05em" }}>{label}</p>
      <p style={{ margin: "0.25rem 0 0", fontSize: "1.4rem", fontWeight: "700", color: "#111827" }}>{value}</p>
    </div>
  );
}

const inputStyle = { padding: "0.4rem 0.6rem", border: "1px solid #d1d5db", borderRadius: "6px", fontSize: "0.875rem" };
const card = { background: "#fff", borderRadius: "10px", padding: "1.25rem", boxShadow: "0 1px 3px rgba(0,0,0,0.07)" };
const sectionTitle = { margin: "0 0 0.75rem", fontSize: "1rem", fontWeight: "600", color: "#111827" };
const th = { padding: "0.6rem 0.75rem", textAlign: "left", fontWeight: "600", color: "#374151" };
const td = { padding: "0.6rem 0.75rem", color: "#374151" };

export default Reports;
