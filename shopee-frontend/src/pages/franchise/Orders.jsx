import { useEffect, useState } from "react";
import api from "../../services/apiService";

const STATUS_COLORS = {
  PENDING: "#f59e0b",
  PROCESSING: "#6366f1",
  SHIPPED: "#3b82f6",
  DELIVERED: "#10b981",
  CANCELLED: "#ef4444",
};

function Orders() {
  const franchiseId = localStorage.getItem("franchiseId");
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [newStatus, setNewStatus] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  useEffect(() => {
    api
      .get(`/franchise/${franchiseId}/orders`)
      .then((res) => setOrders(res.data))
      .catch(() => setError("Failed to load orders"))
      .finally(() => setLoading(false));
  }, [franchiseId]);

  function handleStatusUpdate(orderId) {
    if (!newStatus) return;
    setUpdatingId(orderId);
    api
      .patch(`/franchise/${franchiseId}/orders/${orderId}/status`, { status: newStatus })
      .then((res) => {
        setOrders((prev) => prev.map((o) => (o.orderId === orderId ? res.data : o)));
        setSelectedOrder(null);
        setNewStatus("");
      })
      .catch((err) => alert(err.response?.data?.message || "Failed to update status"))
      .finally(() => setUpdatingId(null));
  }

  function handleCancel(orderId) {
    if (!window.confirm("Cancel this order?")) return;
    api
      .post(`/franchise/${franchiseId}/orders/${orderId}/cancel`)
      .then((res) => setOrders((prev) => prev.map((o) => (o.orderId === orderId ? res.data : o))))
      .catch((err) => alert(err.response?.data?.message || "Failed to cancel order"));
  }

  return (
    <div style={{ padding: "1.5rem" }}>
      <h2 style={{ marginBottom: "1rem" }}>Orders</h2>

      {loading ? (
        <p>Loading orders...</p>
      ) : error ? (
        <p style={{ color: "red" }}>{error}</p>
      ) : orders.length === 0 ? (
        <p style={{ color: "#6b7280" }}>No orders yet.</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ background: "#f3f4f6" }}>
              <th style={th}>Order #</th>
              <th style={th}>Customer</th>
              <th style={th}>Items</th>
              <th style={th}>Total</th>
              <th style={th}>Payment</th>
              <th style={th}>Status</th>
              <th style={th}>Date</th>
              <th style={th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map((o) => (
              <tr key={o.orderId} style={{ borderBottom: "1px solid #e5e7eb" }}>
                <td style={td}>#{o.orderId}</td>
                <td style={td}>{o.customerName || "Walk-in"}</td>
                <td style={td}>{o.items?.length ?? 0}</td>
                <td style={td}>₹{o.totalAmount}</td>
                <td style={td}>{o.paymentMethod}</td>
                <td style={td}>
                  <span style={{ color: STATUS_COLORS[o.status] ?? "#374151", fontWeight: "600" }}>
                    {o.status}
                  </span>
                </td>
                <td style={td}>{new Date(o.createdAt).toLocaleDateString("en-IN")}</td>
                <td style={td}>
                  {o.status !== "CANCELLED" && o.status !== "DELIVERED" && (
                    <>
                      {selectedOrder === o.orderId ? (
                        <div style={{ display: "flex", gap: "0.4rem" }}>
                          <select value={newStatus} onChange={(e) => setNewStatus(e.target.value)} style={{ fontSize: "0.8rem", padding: "0.2rem 0.4rem", borderRadius: "4px" }}>
                            <option value="">Select</option>
                            {["PROCESSING", "SHIPPED", "DELIVERED"].map((s) => (
                              <option key={s} value={s}>{s}</option>
                            ))}
                          </select>
                          <button onClick={() => handleStatusUpdate(o.orderId)} disabled={updatingId === o.orderId || !newStatus} style={btnSmall("#10b981")}>
                            {updatingId === o.orderId ? "..." : "Save"}
                          </button>
                          <button onClick={() => { setSelectedOrder(null); setNewStatus(""); }} style={btnSmall("#6b7280")}>✕</button>
                        </div>
                      ) : (
                        <div style={{ display: "flex", gap: "0.4rem" }}>
                          <button onClick={() => setSelectedOrder(o.orderId)} style={btnSmall("#6366f1")}>Update</button>
                          <button onClick={() => handleCancel(o.orderId)} style={btnSmall("#ef4444")}>Cancel</button>
                        </div>
                      )}
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

const th = { padding: "0.6rem 1rem", textAlign: "left", fontWeight: "600", fontSize: "0.85rem", color: "#374151" };
const td = { padding: "0.6rem 1rem", fontSize: "0.9rem" };
const btnSmall = (bg) => ({ padding: "0.25rem 0.6rem", background: bg, color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer", fontSize: "0.8rem" });

export default Orders;
