import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../../styles/viewFranchises.css";

// 1. Dummy Data (Replace this with your API call later)
const MOCK_FRANCHISES = [
  { id: 101, name: "Downtown Plaza", owner: "Rahul Sharma", city: "Mumbai", validTo: "2026-12-31", status: "Active" },
  { id: 102, name: "Tech Park Outlet", owner: "Anita Roy", city: "Bangalore", validTo: "2025-10-15", status: "Active" },
  { id: 103, name: "Westside Mall", owner: "Vikram Singh", city: "Delhi", validTo: "2024-05-20", status: "Expired" },
  { id: 104, name: "Lakeside View", owner: "John Doe", city: "Pune", validTo: "2026-01-01", status: "Inactive" },
];

function ViewFranchise() {
  const navigate = useNavigate();
  const [searchTerm, setSearchTerm] = useState("");
  const [franchises] = useState(MOCK_FRANCHISES);

  // Filter logic for the search bar
  const filteredFranchises = franchises.filter((f) =>
    f.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
    f.city.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="list-container">
      {/* Header Section */}
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

      {/* Search & Filter Section */}
      <div className="table-controls">
        <input 
          type="text" 
          placeholder="Search by Outlet Name or City..." 
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="search-input"
        />
      </div>

      {/* The Table */}
      <div className="table-wrapper">
        <table className="franchise-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Outlet Name</th>
              <th>Owner</th>
              <th>City</th>
              <th>Valid Until</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredFranchises.length > 0 ? (
              filteredFranchises.map((franchise) => (
                <tr key={franchise.id}>
                  <td>#{franchise.id}</td>
                  <td className="fw-bold">{franchise.name}</td>
                  <td>{franchise.owner}</td>
                  <td>{franchise.city}</td>
                  <td>{franchise.validTo}</td>
                  <td>
                    {/* Dynamic Badge Class based on status */}
                    <span className={`status-badge status-${franchise.status.toLowerCase()}`}>
                      {franchise.status}
                    </span>
                  </td>
                  <td>
                    <button className="action-btn btn-edit">Edit</button>
                    <button className="action-btn btn-delete">Delete</button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan="7" className="no-data">No franchises found matching "{searchTerm}"</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default ViewFranchise;