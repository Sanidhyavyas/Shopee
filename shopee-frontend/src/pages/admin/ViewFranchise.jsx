import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../../services/apiService";
import "../../styles/viewFranchises.css";

function ViewFranchise() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [franchises, setFranchises] = useState([]);

  useEffect(() => {
    console.log("Calling backend...");
    api.get("/admin/franchises")
      .then(res => {
        console.log("Response:", res.data);
        setFranchises(res.data);
      })
      .catch(err => console.error("API error:", err));
  }, []);

  const filteredFranchises = franchises.filter((f) =>
    f.outletName.toLowerCase().includes(searchTerm.toLowerCase()) ||
    f.city.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="list-container">
      <div className="list-header">
        <div className="header-text">
          <h2>All Franchises</h2>
          <p>Manage and view all registered franchise outlets.</p>
        </div>
        <button
          className="btn-create"
          onClick={() => navigate("/admin/register-franchise")}
        >
          + Register New Franchise
        </button>
      </div>

      <div className="table-controls">
        <input
          type="text"
          placeholder="Search by Outlet Name or City..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>

      <div className="table-wrapper">
        <table className="franchise-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Outlet Name</th>
              <th>Owner Email</th>
              <th>Owner Mobile</th>
              <th>City</th>
              <th>State</th>
            </tr>
          </thead>
          <tbody>
            {filteredFranchises.length > 0 ? (
              filteredFranchises.map((f) => (
                <tr key={f.id}>
                  <td>#{f.id}</td>
                  <td className="fw-bold">{f.outletName}</td>
                  <td>{f.ownerEmail}</td>
                  <td>{f.ownerMobile}</td>
                  <td>{f.city}</td>
                  <td>{f.state}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="6" className="no-data">
                  No franchises found matching "{searchTerm}"
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ViewFranchise;
