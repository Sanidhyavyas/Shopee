import { useNavigate } from "react-router-dom";
import { useState } from "react";
import "../styles/auth.css";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  function handleLogin(e) {
    e.preventDefault();

    if (username === "admin" && password === "123") {
      navigate("/dashboard");
    } else {
      alert("Invalid credentials");
    }
  }

  return (
    <div className="page">
      <form className="card" onSubmit={handleLogin}>
        <h2>Shopee Login</h2>

        <input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button type="submit">Login</button>
      </form>
    </div>
  );
}

export default Login;
