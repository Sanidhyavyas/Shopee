import { useNavigate } from "react-router-dom";
import "../../styles/franchiseDashboard.css"; 

function FranchiseDashboard() {
  const navigate = useNavigate();

  function handleLogout() {
    // Optional: Add a confirmation check
    if(window.confirm("Are you sure you want to log out?")) {
      localStorage.clear();
      navigate("/");
    }
  }

  return (
    // Changed class name to match the new CSS layout
    <div className="dashboard-layout">
      
      {/* Sidebar Section */}
      <aside className="sidebar">
        <h2>My Outlet</h2>

        <button onClick={() => navigate("/franchise/dashboard")}>
          Dashboard
        </button>

        <button onClick={() => alert("Products page coming next")}>
          Products
        </button>

        <button onClick={() => alert("Orders page coming next")}>
          Orders
        </button>

        <button onClick={() => alert("Staff page coming next")}>
          Staff
        </button>

        <button className="logout" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      {/* Main Content Section */}
      <main className="content">
        <h1>Welcome to Your Store</h1>

        <div className="stats">
          <div className="card">
            <h3>Total Products</h3>
            <p>0</p>
          </div>

          <div className="card" style={{ borderLeftColor: '#10b981' }}>
            {/* Overrode color for Sales to Green */}
            <h3>Today’s Sales</h3>
            <p>₹0</p>
          </div>

          <div className="card" style={{ borderLeftColor: '#ef4444' }}>
            {/* Overrode color for Low Stock to Red */}
            <h3>Low Stock Items</h3>
            <p>0</p>
          </div>
        </div>

        <p>This outlet is fully isolated. All data belongs only to this franchise.</p>
      </main>
    </div>
  );
}

export default FranchiseDashboard;