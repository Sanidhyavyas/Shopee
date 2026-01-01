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

    // Temporary development users (simulate backend)
    let fakeResponse = null;

    if (username === "admin" && password === "123") {
      fakeResponse = {
        token: "dev-super-admin-token",
        role: "SUPER_ADMIN",
      };
    } else if (username === "store" && password === "123") {
      fakeResponse = {
        token: "dev-franchise-admin-token",
        role: "FRANCHISE_ADMIN",
      };
    } else {
      setError("Invalid username or password");
      return;
    }

    // Store exactly like a real JWT-based backend
    localStorage.setItem("token", fakeResponse.token);
    localStorage.setItem("role", fakeResponse.role);

    // Role-based redirection
    if (fakeResponse.role === "SUPER_ADMIN") {
      navigate("/admin/dashboard");
    } else if (fakeResponse.role === "FRANCHISE_ADMIN") {
      navigate("/franchise/dashboard");
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
