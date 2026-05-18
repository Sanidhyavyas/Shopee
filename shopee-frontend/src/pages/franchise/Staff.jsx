import { useEffect, useState } from "react";
import api from "../../services/apiService";

function Staff() {
  const franchiseId = localStorage.getItem("franchiseId");
  const [staff, setStaff] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: "", email: "", mobile: "", role: "STAFF" });
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    api
      .get(`/franchise/${franchiseId}/staff`)
      .then((res) => setStaff(res.data))
      .catch(() => setError("Failed to load staff"))
      .finally(() => setLoading(false));
  }, [franchiseId]);

  function handleFormChange(e) {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  }

  function handleAddStaff(e) {
    e.preventDefault();
    setFormError("");
    if (!form.name || !form.email) {
      setFormError("Name and email are required.");
      return;
    }
    setSubmitting(true);
    api
      .post(`/franchise/${franchiseId}/staff`, form)
      .then((res) => {
        setStaff((prev) => [...prev, res.data]);
        setShowForm(false);
        setForm({ name: "", email: "", mobile: "", role: "STAFF" });
      })
      .catch((err) => setFormError(err.response?.data?.message || "Failed to add staff"))
      .finally(() => setSubmitting(false));
  }

  function handleRemove(staffId) {
    if (!window.confirm("Remove this staff member?")) return;
    api
      .delete(`/franchise/${franchiseId}/staff/${staffId}`)
      .then(() => setStaff((prev) => prev.filter((s) => s.userId !== staffId)))
      .catch((err) => alert(err.response?.data?.message || "Failed to remove staff"));
  }

  return (
    <div style={{ padding: "1.5rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem" }}>
        <h2>Staff Management</h2>
        <button
          onClick={() => setShowForm((v) => !v)}
          style={{ padding: "0.5rem 1rem", background: "#6366f1", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" }}
        >
          {showForm ? "Cancel" : "+ Add Staff"}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleAddStaff} style={{ background: "#f9fafb", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem", display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.75rem" }}>
          <h3 style={{ gridColumn: "1/-1", margin: 0 }}>New Staff Member</h3>
          {formError && <p style={{ color: "red", gridColumn: "1/-1", margin: 0 }}>{formError}</p>}
          <input name="name" placeholder="Full Name *" value={form.name} onChange={handleFormChange} required />
          <input name="email" type="email" placeholder="Email *" value={form.email} onChange={handleFormChange} required />
          <input name="mobile" placeholder="Mobile Number" value={form.mobile} onChange={handleFormChange} />
          <select name="role" value={form.role} onChange={handleFormChange} style={{ padding: "0.4rem", borderRadius: "6px", border: "1px solid #d1d5db" }}>
            <option value="STAFF">Staff</option>
            <option value="FRANCHISE_ADMIN">Franchise Admin</option>
          </select>
          <button type="submit" disabled={submitting} style={{ gridColumn: "1/-1", padding: "0.6rem", background: "#10b981", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" }}>
            {submitting ? "Adding..." : "Add Staff Member"}
          </button>
        </form>
      )}

      {loading ? (
        <p>Loading staff...</p>
      ) : error ? (
        <p style={{ color: "red" }}>{error}</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ background: "#f3f4f6" }}>
              <th style={th}>Name</th>
              <th style={th}>Email</th>
              <th style={th}>Mobile</th>
              <th style={th}>Role</th>
              <th style={th}>Status</th>
              <th style={th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {staff.length === 0 ? (
              <tr><td colSpan={6} style={{ textAlign: "center", padding: "1rem", color: "#6b7280" }}>No staff members found</td></tr>
            ) : (
              staff.map((s) => (
                <tr key={s.userId} style={{ borderBottom: "1px solid #e5e7eb" }}>
                  <td style={td}>{s.name}</td>
                  <td style={td}>{s.email}</td>
                  <td style={td}>{s.mobile || "-"}</td>
                  <td style={td}>{s.role}</td>
                  <td style={td}>
                    <span style={{ color: s.active ? "#10b981" : "#ef4444" }}>{s.active ? "Active" : "Inactive"}</span>
                  </td>
                  <td style={td}>
                    <button onClick={() => handleRemove(s.userId)} style={{ padding: "0.3rem 0.7rem", background: "#ef4444", color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer" }}>
                      Remove
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}

const th = { padding: "0.6rem 1rem", textAlign: "left", fontWeight: "600", fontSize: "0.85rem", color: "#374151" };
const td = { padding: "0.6rem 1rem", fontSize: "0.9rem" };

export default Staff;
