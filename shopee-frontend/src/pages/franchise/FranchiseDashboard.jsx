import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/apiService";
import "../../styles/franchiseDashboard.css";

function FranchiseDashboard() {
  const navigate = useNavigate();
  const franchiseId = localStorage.getItem("franchiseId");
  const [stats, setStats] = useState({ totalProducts: 0, todaySales: 0, lowStockItems: 0 });

  useEffect(() => {
    const today = new Date().toISOString().split("T")[0];

    Promise.allSettled([
      api.get(`/franchise/${franchiseId}/products`),
      api.get(`/franchise/${franchiseId}/products/low-stock`),
      api.get(`/franchise/${franchiseId}/reports/sales-summary?from=${today}&to=${today}`),
    ]).then(([productsRes, lowStockRes, salesRes]) => {
      setStats({
        totalProducts: productsRes.status === "fulfilled" ? productsRes.value.data.length : 0,
        lowStockItems: lowStockRes.status === "fulfilled" ? lowStockRes.value.data.length : 0,
        todaySales: salesRes.status === "fulfilled" ? salesRes.value.data.totalRevenue ?? 0 : 0,
      });
    });
  }, [franchiseId]);

  function handleLogout() {
    if (window.confirm("Are you sure you want to log out?")) {
      localStorage.clear();
      navigate("/");
    }
  }

  return (
    <div className="dashboard-layout">
      <aside className="sidebar">
        <h2>My Outlet</h2>

        <button onClick={() => navigate("/franchise/dashboard")}>Dashboard</button>
        <button onClick={() => navigate("/franchise/products")}>Products</button>
        <button onClick={() => navigate("/franchise/orders")}>Orders</button>
        <button onClick={() => navigate("/franchise/staff")}>Staff</button>

        <button className="logout" onClick={handleLogout}>Logout</button>
      </aside>

      <main className="content">
        <h1>Welcome to Your Store</h1>

        <div className="stats">
          <div className="card">
            <h3>Total Products</h3>
            <p>{stats.totalProducts}</p>
          </div>

          <div className="card" style={{ borderLeftColor: "#10b981" }}>
            <h3>Today's Sales</h3>
            <p>Rs.{Number(stats.todaySales).toFixed(2)}</p>
          </div>

          <div className="card" style={{ borderLeftColor: "#ef4444" }}>
            <h3>Low Stock Items</h3>
            <p>{stats.lowStockItems}</p>
          </div>
        </div>

        <p>This outlet is fully isolated. All data belongs only to this franchise.</p>
      </main>
    </div>
  );
}

export default FranchiseDashboard;
