import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/selectFranchise.css";

function SelectFranchise() {
  const navigate = useNavigate();
  const [franchises, setFranchises] = useState([]);

  useEffect(() => {
    // Load from login session
    const stored = localStorage.getItem("franchises");

    if (!stored) {
      // No franchises → force re-login
      navigate("/", { replace: true });
      return;
    }

    setFranchises(JSON.parse(stored));
  }, [navigate]);

  function selectFranchise(id) {
    localStorage.setItem("franchiseId", id);
    localStorage.removeItem("franchises"); // no longer needed
    navigate("/franchise/dashboard");
  }

  return (
    <div className="select-page-container">
      <div className="selection-card">
        <h2>Select Outlet</h2>
        <p>Please choose which store you want to manage.</p>

        <div className="franchise-list">
          {franchises.map((f) => (
            <button
              key={f.id}
              className="outlet-button"
              onClick={() => selectFranchise(f.id)}
            >
              🏪 {f.name}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

export default SelectFranchise;
