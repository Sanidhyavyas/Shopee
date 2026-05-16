import { Routes, Route } from "react-router-dom";
import Login from "./pages/auth/Login";
import AdminDashboard from "./pages/admin/AdminDashboard";
import RegisterFranchise from "./pages/admin/RegisterFranchise";
import ViewFranchise from "./pages/admin/ViewFranchise";
import RequireAuth from "./routes/RequireAuth";
import FranchiseDashboard from "./pages/franchise/FranchiseDashboard";
import SelectFranchise from "./pages/franchise/SelectFranchise";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />

      {/* SUPER ADMIN ROUTES */}
      <Route
        path="/admin/dashboard"
        element={
          <RequireAuth allowedRole="SUPER_ADMIN">
            <AdminDashboard />
          </RequireAuth>
        }
      />

      <Route
        path="/admin/register-franchise"
        element={
          <RequireAuth allowedRole="SUPER_ADMIN">
            <RegisterFranchise />
          </RequireAuth>
        }
      />

      <Route
        path="/admin/view-franchises"
        element={
          <RequireAuth allowedRole="SUPER_ADMIN">
            <ViewFranchise />
          </RequireAuth>
        }
      />

      {/* FRANCHISE ADMIN ROUTES */}
      <Route
        path="/select-franchise"
        element={
          <RequireAuth allowedRole="FRANCHISE_ADMIN">
            <SelectFranchise />
          </RequireAuth>
        }
      />

      <Route
        path="/franchise/dashboard"
        element={
          <RequireAuth allowedRole="FRANCHISE_ADMIN">
            <FranchiseDashboard />
          </RequireAuth>
        }
      />
    </Routes>
  );
}

export default App;
