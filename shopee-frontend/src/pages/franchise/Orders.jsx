import { useEffect, useState } from "react";
import api from "../../services/apiService";

const STATUS_COLORS = {
  PENDING: "#f59e0b",
  PROCESSING: "#6366f1",
  SHIPPED: "#3b82f6",
  DELIVERED: "#10b981",
  CANCELLED: "#ef4444",
};

const EMPTY_ORDER_FORM = {
  customerName: "",
  customerMobile: "",
  notes: "",
  paymentMethod: "CASH",
  discountAmount: "",
  paid: false,
};

function Orders() {
  const franchiseId = localStorage.getItem("franchiseId");
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [newStatus, setNewStatus] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  // Create-order state
  const [showCreate, setShowCreate] = useState(false);
  const [orderForm, setOrderForm] = useState(EMPTY_ORDER_FORM);
  const [cart, setCart] = useState([]); // [{ product, quantity }]
  const [productSearch, setProductSearch] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [searching, setSearching] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [createError, setCreateError] = useState("");

  useEffect(() => {
    fetchOrders();
  }, [franchiseId]);

  function fetchOrders() {
    setLoading(true);
    api
      .get(`/franchise/${franchiseId}/orders`)
      .then((res) => setOrders(res.data.content ?? res.data))
      .catch(() => setError("Failed to load orders"))
      .finally(() => setLoading(false));
  }

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

  // --- Create Order helpers ---
  function handleSearchProducts(e) {
    const kw = e.target.value;
    setProductSearch(kw);
    if (!kw.trim()) { setSearchResults([]); return; }
    setSearching(true);
    api
      .get(`/franchise/${franchiseId}/products/search?keyword=${encodeURIComponent(kw)}`)
      .then((res) => setSearchResults(res.data))
      .catch(() => setSearchResults([]))
      .finally(() => setSearching(false));
  }

  function addToCart(product) {
    setCart((prev) => {
      const existing = prev.find((c) => c.product.productId === product.productId);
      if (existing) {
        return prev.map((c) =>
          c.product.productId === product.productId ? { ...c, quantity: c.quantity + 1 } : c
        );
      }
      return [...prev, { product, quantity: 1 }];
    });
    setProductSearch("");
    setSearchResults([]);
  }

  function updateCartQty(productId, qty) {
    const q = parseInt(qty);
    if (isNaN(q) || q < 1) return;
    setCart((prev) =>
      prev.map((c) => (c.product.productId === productId ? { ...c, quantity: q } : c))
    );
  }

  function removeFromCart(productId) {
    setCart((prev) => prev.filter((c) => c.product.productId !== productId));
  }

  const cartTotal = cart.reduce((sum, c) => sum + Number(c.product.price) * c.quantity, 0);
  const discount = parseFloat(orderForm.discountAmount) || 0;
  const grandTotal = Math.max(0, cartTotal - discount);

  function handleSubmitOrder(e) {
    e.preventDefault();
    setCreateError("");
    if (cart.length === 0) { setCreateError("Add at least one product to the cart."); return; }
    setSubmitting(true);
    const payload = {
      ...orderForm,
      discountAmount: discount,
      items: cart.map((c) => ({ productId: c.product.productId, quantity: c.quantity })),
    };
    api
      .post(`/franchise/${franchiseId}/orders`, payload)
      .then((res) => {
        setOrders((prev) => [res.data, ...prev]);
        setShowCreate(false);
        setOrderForm(EMPTY_ORDER_FORM);
        setCart([]);
      })
      .catch((err) => setCreateError(err.response?.data?.message || "Failed to create order"))
      .finally(() => setSubmitting(false));
  }

  function handleCancelCreate() {
    setShowCreate(false);
    setOrderForm(EMPTY_ORDER_FORM);
    setCart([]);
    setProductSearch("");
    setSearchResults([]);
    setCreateError("");
  }

  return (
    <div style={{ padding: "1.5rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem" }}>
        <h2 style={{ margin: 0 }}>Orders</h2>
        <button
          onClick={() => setShowCreate((v) => !v)}
          style={{ padding: "0.5rem 1rem", background: showCreate ? "#6b7280" : "#6366f1", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "600" }}
        >
          {showCreate ? "Cancel" : "+ New Order"}
        </button>
      </div>

      {/* ── Create Order Panel ── */}
      {showCreate && (
        <form onSubmit={handleSubmitOrder} style={{ background: "#f9fafb", border: "1px solid #e5e7eb", borderRadius: "10px", padding: "1.25rem", marginBottom: "1.5rem" }}>
          <h3 style={{ margin: "0 0 1rem", fontSize: "1rem", fontWeight: "700" }}>Create New Order</h3>

          {/* Product search */}
          <div style={{ position: "relative", marginBottom: "1rem" }}>
            <label style={labelStyle}>Search Product</label>
            <input
              type="text"
              value={productSearch}
              onChange={handleSearchProducts}
              placeholder="Type product name or SKU…"
              style={{ ...inputStyle, width: "100%" }}
            />
            {searching && <p style={{ margin: "0.25rem 0", fontSize: "0.8rem", color: "#9ca3af" }}>Searching…</p>}
            {searchResults.length > 0 && (
              <ul style={{ position: "absolute", zIndex: 10, background: "#fff", border: "1px solid #d1d5db", borderRadius: "6px", margin: 0, padding: 0, listStyle: "none", width: "100%", maxHeight: "180px", overflowY: "auto", boxShadow: "0 4px 12px rgba(0,0,0,0.1)" }}>
                {searchResults.map((p) => (
                  <li
                    key={p.productId}
                    onClick={() => addToCart(p)}
                    style={{ padding: "0.5rem 0.75rem", cursor: "pointer", fontSize: "0.875rem", borderBottom: "1px solid #f3f4f6" }}
                    onMouseEnter={(e) => (e.currentTarget.style.background = "#f3f4f6")}
                    onMouseLeave={(e) => (e.currentTarget.style.background = "")}
                  >
                    <strong>{p.name}</strong>{p.sku ? <span style={{ color: "#9ca3af" }}> · {p.sku}</span> : null}
                    <span style={{ float: "right", color: "#6366f1" }}>₹{p.price}</span>
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* Cart */}
          {cart.length > 0 && (
            <div style={{ marginBottom: "1rem" }}>
              <table style={{ width: "100%", borderCollapse: "collapse", fontSize: "0.875rem" }}>
                <thead>
                  <tr style={{ background: "#f3f4f6" }}>
                    <th style={th}>Product</th>
                    <th style={th}>Price</th>
                    <th style={th}>Qty</th>
                    <th style={th}>Subtotal</th>
                    <th style={th}></th>
                  </tr>
                </thead>
                <tbody>
                  {cart.map((c) => (
                    <tr key={c.product.productId} style={{ borderBottom: "1px solid #e5e7eb" }}>
                      <td style={td}>{c.product.name}</td>
                      <td style={td}>₹{c.product.price}</td>
                      <td style={td}>
                        <input
                          type="number"
                          min="1"
                          value={c.quantity}
                          onChange={(e) => updateCartQty(c.product.productId, e.target.value)}
                          style={{ width: "60px", padding: "0.2rem 0.4rem", border: "1px solid #d1d5db", borderRadius: "4px" }}
                        />
                      </td>
                      <td style={td}>₹{(Number(c.product.price) * c.quantity).toFixed(2)}</td>
                      <td style={td}>
                        <button type="button" onClick={() => removeFromCart(c.product.productId)} style={{ background: "none", border: "none", color: "#ef4444", cursor: "pointer", fontWeight: "700" }}>✕</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <div style={{ textAlign: "right", marginTop: "0.5rem", fontSize: "0.875rem", color: "#374151" }}>
                Subtotal: <strong>₹{cartTotal.toFixed(2)}</strong>
                {discount > 0 && <>&nbsp;— Discount: <strong style={{ color: "#ef4444" }}>₹{discount.toFixed(2)}</strong>&nbsp;→ Total: <strong style={{ color: "#10b981" }}>₹{grandTotal.toFixed(2)}</strong></>}
              </div>
            </div>
          )}

          {/* Customer & payment details */}
          <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.75rem", marginBottom: "0.75rem" }}>
            <div>
              <label style={labelStyle}>Customer Name</label>
              <input value={orderForm.customerName} onChange={(e) => setOrderForm((p) => ({ ...p, customerName: e.target.value }))} placeholder="Walk-in" style={inputStyle} />
            </div>
            <div>
              <label style={labelStyle}>Mobile</label>
              <input value={orderForm.customerMobile} onChange={(e) => setOrderForm((p) => ({ ...p, customerMobile: e.target.value }))} placeholder="+91 …" style={inputStyle} />
            </div>
            <div>
              <label style={labelStyle}>Payment Method</label>
              <select value={orderForm.paymentMethod} onChange={(e) => setOrderForm((p) => ({ ...p, paymentMethod: e.target.value }))} style={inputStyle}>
                <option value="CASH">Cash</option>
                <option value="CARD">Card</option>
                <option value="UPI">UPI</option>
              </select>
            </div>
            <div>
              <label style={labelStyle}>Discount (₹)</label>
              <input type="number" min="0" step="0.01" value={orderForm.discountAmount} onChange={(e) => setOrderForm((p) => ({ ...p, discountAmount: e.target.value }))} placeholder="0" style={inputStyle} />
            </div>
            <div style={{ display: "flex", alignItems: "center", gap: "0.5rem", paddingTop: "1.25rem" }}>
              <input type="checkbox" id="paid" checked={orderForm.paid} onChange={(e) => setOrderForm((p) => ({ ...p, paid: e.target.checked }))} />
              <label htmlFor="paid" style={{ fontSize: "0.875rem", color: "#374151" }}>Mark as Paid</label>
            </div>
            <div>
              <label style={labelStyle}>Notes</label>
              <input value={orderForm.notes} onChange={(e) => setOrderForm((p) => ({ ...p, notes: e.target.value }))} placeholder="Optional notes" style={inputStyle} />
            </div>
          </div>

          {createError && <p style={{ color: "#ef4444", margin: "0 0 0.5rem", fontSize: "0.875rem" }}>{createError}</p>}

          <div style={{ display: "flex", gap: "0.75rem" }}>
            <button type="submit" disabled={submitting} style={{ padding: "0.55rem 1.5rem", background: "#10b981", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer", fontWeight: "600" }}>
              {submitting ? "Placing…" : `Place Order${cart.length > 0 ? ` — ₹${grandTotal.toFixed(2)}` : ""}`}
            </button>
            <button type="button" onClick={handleCancelCreate} style={{ padding: "0.55rem 1rem", background: "#f3f4f6", color: "#374151", border: "none", borderRadius: "6px", cursor: "pointer" }}>
              Discard
            </button>
          </div>
        </form>
      )}

      {/* ── Orders list ── */}
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

const labelStyle = { display: "block", fontSize: "0.75rem", fontWeight: "600", color: "#6b7280", marginBottom: "0.25rem", textTransform: "uppercase", letterSpacing: "0.05em" };
const inputStyle = { padding: "0.4rem 0.6rem", border: "1px solid #d1d5db", borderRadius: "6px", fontSize: "0.875rem", width: "100%", boxSizing: "border-box" };
const th = { padding: "0.6rem 1rem", textAlign: "left", fontWeight: "600", fontSize: "0.85rem", color: "#374151" };
const td = { padding: "0.6rem 1rem", fontSize: "0.9rem" };
const btnSmall = (bg) => ({ padding: "0.25rem 0.6rem", background: bg, color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer", fontSize: "0.8rem" });

export default Orders;

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
