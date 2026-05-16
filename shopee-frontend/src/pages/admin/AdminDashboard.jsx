import { useNavigate } from "react-router-dom";
import "../../styles/adminDashboard.css";

function AdminDashboard() {
  const navigate = useNavigate();

  return (
    <div className="dashboard">
      <h2>Admin Dashboard</h2>

      <button onClick={() => navigate("/admin/register-franchise")}>
        Register Franchise
      </button>

      <button onClick={() => navigate("/admin/view-franchises")}>
        View Franchises
      </button>
    </div>
  );
}

export default AdminDashboard;
