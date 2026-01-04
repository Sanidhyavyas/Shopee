import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/auth.css";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  function handleLogin(e) {
    e.preventDefault();
    setError("");

    let fakeResponse = null;

    // SUPER ADMIN
    if (username === "admin" && password === "123") {
      fakeResponse = {
        token: "dev-super-admin-token",
        role: "SUPER_ADMIN",
      };
    }

    // MULTI-OUTLET FRANCHISE ADMIN
    else if (username === "store" && password === "123") {
      fakeResponse = {
        token: "dev-franchise-admin-token",
        role: "FRANCHISE_ADMIN",
        franchises: [
          { id: 101, name: "Shopee Andheri" },
          { id: 205, name: "Shopee Bandra" },
        ],
      };
    }

    // SINGLE-OUTLET FRANCHISE ADMIN
    else if (username === "single" && password === "123") {
      fakeResponse = {
        token: "dev-single-franchise-token",
        role: "FRANCHISE_ADMIN",
        franchises: [{ id: 301, name: "Shopee Pune" }],
      };
    } else {
      setError("Invalid username or password");
      return;
    }

    // Store auth data
    localStorage.setItem("token", fakeResponse.token);
    localStorage.setItem("role", fakeResponse.role);

    // SUPER ADMIN ROUTING
    if (fakeResponse.role === "SUPER_ADMIN") {
      navigate("/admin/dashboard", { replace: true });
    }

    // FRANCHISE ADMIN ROUTING
    if (fakeResponse.role === "FRANCHISE_ADMIN") {
      const franchises = fakeResponse.franchises;

      if (franchises.length === 1) {
        // Auto select single outlet
        localStorage.setItem("franchiseId", franchises[0].id);
        navigate("/franchise/dashboard", { replace: true });
      } else {
        // Save list for selection screen
        localStorage.setItem("franchises", JSON.stringify(franchises));
        navigate("/select-franchise", { replace: true });
      }
    }
  }

  return (
    <div className="page">
      <form className="card" onSubmit={handleLogin}>
        <h2>Shopee Login</h2>

        {error && <p className="error">{error}</p>}

        <input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default Login;
