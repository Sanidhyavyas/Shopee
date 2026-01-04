import { useState } from "react";
import "../../styles/registerFranchise.css";

function RegisterFranchise() {
  // 1. State to hold form data
  const [formData, setFormData] = useState({
    validFrom: "",
    validTo: "",
    outletName: "",
    ownerName: "",
    address: "",
    city: "",
    state: "",
    mobile1: "",
    mobile2: "",
    email: "",
  });

  // 2. Handle input changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  // 3. Handle Submit
  const handleSubmit = (e) => {
    e.preventDefault();
    console.log("Form Submitted:", formData);
    // Add your API call here
  };

  return (
    <div className="form-container">
      <form className="outlet-form" onSubmit={handleSubmit}>
        <div className="form-header">
          <h2>Register Franchise Outlet</h2>
          <p>Enter the details for the new franchise outlet below.</p>
        </div>

        {/* SECTION 1: Validity */}
        <div className="form-section">
          <h3 className="section-title">Validity Period</h3>
          <div className="form-grid">
            <div className="input-group">
              <label>Valid From</label>
              <input 
                type="date" 
                name="validFrom" 
                value={formData.validFrom} 
                onChange={handleChange} 
                required 
              />
            </div>
            <div className="input-group">
              <label>Valid To</label>
              <input 
                type="date" 
                name="validTo" 
                value={formData.validTo} 
                onChange={handleChange} 
                required 
              />
            </div>
          </div>
        </div>

        {/* SECTION 2: Outlet Details */}
        <div className="form-section">
          <h3 className="section-title">Outlet Details</h3>
          <div className="form-grid">
            <div className="input-group">
              <label>Outlet Name</label>
              <input 
                type="text" 
                name="outletName" 
                placeholder="e.g. Downtown Plaza Store" 
                value={formData.outletName} 
                onChange={handleChange} 
              />
            </div>
            <div className="input-group">
              <label>Owner Name</label>
              <input 
                type="text" 
                name="ownerName" 
                placeholder="Full Name" 
                value={formData.ownerName} 
                onChange={handleChange} 
              />
            </div>
            {/* Address Spans 2 Columns */}
            <div className="input-group full-width">
              <label>Outlet Address</label>
              <input 
                type="text" 
                name="address" 
                placeholder="Street, Building, Area" 
                value={formData.address} 
                onChange={handleChange} 
              />
            </div>
            <div className="input-group">
              <label>City</label>
              <input 
                type="text" 
                name="city" 
                value={formData.city} 
                onChange={handleChange} 
              />
            </div>
            <div className="input-group">
              <label>State</label>
              <input 
                type="text" 
                name="state" 
                value={formData.state} 
                onChange={handleChange} 
              />
            </div>
          </div>
        </div>

        {/* SECTION 3: Contact Info */}
        <div className="form-section">
          <h3 className="section-title">Contact Information</h3>
          <div className="form-grid">
            <div className="input-group">
              <label>Primary Mobile</label>
              <input 
                type="tel" 
                name="mobile1" 
                placeholder="10-digit number" 
                value={formData.mobile1} 
                onChange={handleChange} 
              />
            </div>
            <div className="input-group">
              <label>Secondary Mobile (Optional)</label>
              <input 
                type="tel" 
                name="mobile2" 
                placeholder="10-digit number" 
                value={formData.mobile2} 
                onChange={handleChange} 
              />
            </div>
            <div className="input-group full-width">
              <label>Email Address</label>
              <input 
                type="email" 
                name="email" 
                placeholder="franchise@example.com" 
                value={formData.email} 
                onChange={handleChange} 
              />
            </div>
          </div>
        </div>

        <div className="form-actions">
          <button type="button" className="btn-cancel">Cancel</button>
          <button type="submit" className="btn-submit">Register Franchise</button>
        </div>
      </form>
    </div>
  );
}

export default RegisterFranchise;