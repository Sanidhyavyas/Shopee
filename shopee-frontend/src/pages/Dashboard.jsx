import { useState } from "react";
import "../styles/dashboard.css";

function Dashboard() {
  const [activePage, setActivePage] = useState("register");

  return (
    <div className="dashboard">
      {/* Sidebar */}
      <div className="sidebar">
        <h3>Dashboard</h3>

        <button onClick={() => setActivePage("register")}>
          Register Outlet
        </button>

        <button onClick={() => setActivePage("view")}>
          View Outlets
        </button>
      </div>

      {/* Main Content */}
      <div className="content">
        {activePage === "register" && (
          <form className="outlet-form">
            <h2>Register Outlet</h2>

            <div className="form-row">
              <input type="date" placeholder="Valid From" />
              <input type="date" placeholder="Valid To" />
            </div>

            <input type="text" placeholder="Outlet Name" />
            <input type="text" placeholder="Owner Name" />
            <input type="text" placeholder="Outlet Address" />
            <input type="text" placeholder="City" />
            <input type="text" placeholder="State" />

            <div className="form-row">
              <input type="text" placeholder="Mobile 1" />
              <input type="text" placeholder="Mobile 2" />
            </div>

            <input type="email" placeholder="Email" />

            <button type="submit">Submit</button>
          </form>
        )}

        {activePage === "view" && <h2>View Outlets</h2>}
      </div>
    </div>
  );
}

export default Dashboard;
