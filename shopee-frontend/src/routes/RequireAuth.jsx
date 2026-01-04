import { Navigate } from "react-router-dom";

function RequireAuth({ allowedRole, children }) {
  const token = localStorage.getItem("token");
  const role = localStorage.getItem("role");
  const franchiseId = localStorage.getItem("franchiseId");

  // Not logged in
  if (!token || !role) {
    return <Navigate to="/" replace />;
  }

  // Wrong role
  if (allowedRole && role !== allowedRole) {
    return <Navigate to="/" replace />;
  }

  // Franchise admins must have an active franchise selected
  if (role === "FRANCHISE_ADMIN" && !franchiseId) {
    return <Navigate to="/select-franchise" replace />;
  }

  return children;
}

export default RequireAuth;
