import { Routes, Route } from "react-router-dom";
import Login from "./pages/auth/Login";
import AdminDashboard from "./pages/admin/AdminDashboard";
import RegisterFranchise from "./pages/admin/RegisterFranchise";
import ViewFranchise from "./pages/admin/ViewFranchise";
import RequireAuth from "./routes/RequireAuth";
import FranchiseDashboard from "./pages/franchise/FranchiseDashboard";
import SelectFranchise from "./pages/franchise/SelectFranchise";
import Products from "./pages/franchise/Products";
import Orders from "./pages/franchise/Orders";
import Staff from "./pages/franchise/Staff";

function NotFound() {
  return (
    <div style={{ textAlign: "center", padding: "4rem" }}>
      <h1 style={{ fontSize: "4rem", margin: 0 }}>404</h1>
      <p style={{ color: "#6b7280" }}>Page not found.</p>
      <a href="/" style={{ color: "#6366f1" }}>Go to Login</a>
    </div>
  );
}

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

      <Route
        path="/franchise/products"
        element={
          <RequireAuth allowedRole="FRANCHISE_ADMIN">
            <Products />
          </RequireAuth>
        }
      />

      <Route
        path="/franchise/orders"
        element={
          <RequireAuth allowedRole="FRANCHISE_ADMIN">
            <Orders />
          </RequireAuth>
        }
      />

      <Route
        path="/franchise/staff"
        element={
          <RequireAuth allowedRole="FRANCHISE_ADMIN">
            <Staff />
          </RequireAuth>
        }
      />

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}

export default App;


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
