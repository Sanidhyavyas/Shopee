import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/auth.css";

function Login() {
  const [username, setUsername] = useState(""); // email
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  function handleLogin(e) {
    e.preventDefault();
    setError("");

    let fakeResponse = null;

    // SUPER ADMIN
    if (username === "admin@shopee.com" && password === "123456") {
      fakeResponse = {
        token: "dev-super-admin-token",
        role: "SUPER_ADMIN",
      };
    }

    // MULTI-OUTLET FRANCHISE ADMIN (rahul)
    else if (username === "rahul@shopee.com" && password === "123456") {
      fakeResponse = {
        token: "dev-franchise-admin-token",
        role: "FRANCHISE_ADMIN",
        franchises: [
          { id: 1, name: "Shopee Andheri" },
          { id: 2, name: "Shopee Bandra" },
        ],
      };
    }

    // SINGLE-OUTLET FRANCHISE ADMIN (amit)
    else if (username === "amit@shopee.com" && password === "123456") {
      fakeResponse = {
        token: "dev-single-franchise-token",
        role: "FRANCHISE_ADMIN",
        franchises: [{ id: 3, name: "Shopee Pune" }],
      };
    } else {
      setError("Invalid email or password");
      return;
    }

    localStorage.setItem("token", fakeResponse.token);
    localStorage.setItem("role", fakeResponse.role);

    if (fakeResponse.role === "SUPER_ADMIN") {
      navigate("/admin/dashboard", { replace: true });
    }

    if (fakeResponse.role === "FRANCHISE_ADMIN") {
      const franchises = fakeResponse.franchises;

      if (franchises.length === 1) {
        localStorage.setItem("franchiseId", franchises[0].id);
        navigate("/franchise/dashboard", { replace: true });
      } else {
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
          placeholder="Email"
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
