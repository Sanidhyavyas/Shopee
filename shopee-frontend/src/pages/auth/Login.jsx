import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/apiService";
import "../../styles/auth.css";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleLogin(e) {
    e.preventDefault();
    setError("");
    setLoading(true);

    try {
      const res = await api.post("/auth/login", { email: username, password });
      const { token, role, franchises } = res.data;

      localStorage.setItem("token", token);
      localStorage.setItem("role", role);

      if (role === "SUPER_ADMIN") {
        navigate("/admin/dashboard", { replace: true });
        return;
      }

      if (role === "FRANCHISE_ADMIN") {
        if (franchises && franchises.length === 1) {
          localStorage.setItem("franchiseId", String(franchises[0].franchiseId));
          navigate("/franchise/dashboard", { replace: true });
        } else if (franchises && franchises.length > 1) {
          localStorage.setItem("franchises", JSON.stringify(franchises));
          navigate("/select-franchise", { replace: true });
        } else {
          navigate("/franchise/dashboard", { replace: true });
        }
        return;
      }

      // STAFF role
      if (franchises && franchises.length > 0) {
        localStorage.setItem("franchiseId", String(franchises[0].franchiseId));
      }
      navigate("/franchise/dashboard", { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "Invalid email or password");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="page">
      <form className="card" onSubmit={handleLogin}>
        <h2>Shopee Login</h2>

        {error && <p className="error">{error}</p>}

        <input
          placeholder="Email"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
          autoComplete="email"
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          autoComplete="current-password"
        />

        <button type="submit" disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>
      </form>
    </div>
  );
}

export default Login;

