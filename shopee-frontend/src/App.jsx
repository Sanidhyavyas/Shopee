import { Routes, Route } from "react-router-dom";
import Login from "./pages/auth/Login";
import AdminDashboard from "./pages/admin/AdminDashboard";
import RegisterFranchise from "./pages/admin/RegisterFranchise";
import ViewFranchise from "./pages/admin/ViewFranchise";

function App() {
  return (
    <Routes>
      <Route path="/" element={<Login />} />
      <Route path="/admin/dashboard" element={<AdminDashboard />} />  
      <Route path="/admin/register-franchise" element={<RegisterFranchise />} />
      <Route path="/admin/view-franchises" element={<ViewFranchise />} />
    </Routes>
  );
}

export default App;
