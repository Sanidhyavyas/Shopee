import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/apiService";
import "../../styles/adminDashboard.css";

function AdminDashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);

  useEffect(() => {
    api
      .get("/admin/dashboard/stats")
      .then((res) => setStats(res.data))
      .catch(() => {});
  }, []);

  function handleLogout() {
    if (window.confirm("Log out?")) {
      localStorage.clear();
      navigate("/");
    }
  }

  return (
    <div className="dashboard">
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1.5rem" }}>
        <h2>Admin Dashboard</h2>
        <button onClick={handleLogout} style={{ padding: "0.4rem 1rem", background: "#ef4444", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" }}>
          Logout
        </button>
      </div>

      {stats && (
        <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(160px, 1fr))", gap: "1rem", marginBottom: "2rem" }}>
          <StatCard label="Total Franchises" value={stats.totalFranchises ?? 0} color="#6366f1" />
          <StatCard label="Total Orders" value={stats.totalOrders ?? 0} color="#3b82f6" />
          <StatCard label="Total Revenue" value={`₹${stats.totalRevenue ?? 0}`} color="#10b981" />
          <StatCard label="Low Stock Items" value={stats.lowStockCount ?? 0} color="#ef4444" />
        </div>
      )}

      <div style={{ display: "grid", gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))", gap: "1rem" }}>
        <NavCard title="Register Franchise" desc="Add a new franchise outlet" onClick={() => navigate("/admin/register-franchise")} color="#6366f1" />
        <NavCard title="View Franchises" desc="Manage all registered outlets" onClick={() => navigate("/admin/view-franchises")} color="#3b82f6" />
      </div>
    </div>
  );
}

function StatCard({ label, value, color }) {
  return (
    <div style={{ background: "#fff", borderRadius: "8px", padding: "1rem 1.25rem", borderLeft: `4px solid ${color}`, boxShadow: "0 1px 3px rgba(0,0,0,0.08)" }}>
      <p style={{ margin: 0, fontSize: "0.8rem", color: "#6b7280", textTransform: "uppercase", letterSpacing: "0.05em" }}>{label}</p>
      <p style={{ margin: "0.25rem 0 0", fontSize: "1.5rem", fontWeight: "700", color: "#111827" }}>{value}</p>
    </div>
  );
}

function NavCard({ title, desc, onClick, color }) {
  return (
    <button onClick={onClick} style={{ background: "#fff", border: `1px solid ${color}`, borderRadius: "8px", padding: "1.25rem", cursor: "pointer", textAlign: "left", boxShadow: "0 1px 3px rgba(0,0,0,0.06)" }}>
      <p style={{ margin: 0, fontWeight: "600", color, fontSize: "1rem" }}>{title}</p>
      <p style={{ margin: "0.25rem 0 0", color: "#6b7280", fontSize: "0.85rem" }}>{desc}</p>
    </button>
  );
}

export default AdminDashboard;

