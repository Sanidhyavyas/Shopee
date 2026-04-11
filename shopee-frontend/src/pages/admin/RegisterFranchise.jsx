import { useState } from "react";
import api from "../../services/apiService";
import "../../styles/RegisterFranchise.css";

function RegisterFranchise() {
  const [formData, setFormData] = useState({
    validFrom: "",
    validTo: "",
    outletName: "",
    address: "",
    city: "",
    state: "",
    mobile: "",
    email: "",
  });

  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const res = await api.post("/admin/franchises", formData);
      setResult(res.data);

      setFormData({
        validFrom: "",
        validTo: "",
        outletName: "",
        address: "",
        city: "",
        state: "",
        mobile: "",
        email: "",
      });
    } catch (err) {
      alert("Failed to register franchise");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="form-container">
      <form className="outlet-form" onSubmit={handleSubmit}>
        <div className="form-header">
          <h2>Register Franchise Outlet</h2>
          <p>Enter the details for the new franchise outlet below.</p>
        </div>

        <div className="form-section">
          <h3 className="section-title">Validity Period</h3>
          <div className="form-grid">
            <div className="input-group">
              <label>Valid From</label>
              <input type="date" name="validFrom" value={formData.validFrom} onChange={handleChange} required />
            </div>
            <div className="input-group">
              <label>Valid To</label>
              <input type="date" name="validTo" value={formData.validTo} onChange={handleChange} required />
            </div>
          </div>
        </div>

        <div className="form-section">
          <h3 className="section-title">Outlet Details</h3>
          <div className="form-grid">
            <div className="input-group">
              <label>Outlet Name</label>
              <input type="text" name="outletName" value={formData.outletName} onChange={handleChange} required />
            </div>

            <div className="input-group full-width">
              <label>Outlet Address</label>
              <input type="text" name="address" value={formData.address} onChange={handleChange} required />
            </div>

            <div className="input-group">
              <label>City</label>
              <input type="text" name="city" value={formData.city} onChange={handleChange} required />
            </div>

            <div className="input-group">
              <label>State</label>
              <input type="text" name="state" value={formData.state} onChange={handleChange} required />
            </div>
          </div>
        </div>

        <div className="form-section">
          <h3 className="section-title">Contact Information</h3>
          <div className="form-grid">
            <div className="input-group">
              <label>Mobile</label>
              <input type="tel" name="mobile" value={formData.mobile} onChange={handleChange} required />
            </div>

            <div className="input-group full-width">
              <label>Email</label>
              <input type="email" name="email" value={formData.email} onChange={handleChange} required />
            </div>
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" className="btn-submit" disabled={loading}>
            {loading ? "Registering..." : "Register Franchise"}
          </button>
        </div>

        {result && (
          <div className="success-box">
            <p><b>Email:</b> {result.email}</p>
            <p><b>Temporary Password:</b> {result.temporaryPassword}</p>
          </div>
        )}
      </form>
    </div>
  );
}

export default RegisterFranchise;
