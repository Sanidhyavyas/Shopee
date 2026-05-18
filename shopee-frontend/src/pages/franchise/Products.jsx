import { useEffect, useState } from "react";
import api from "../../services/apiService";

function Products() {
  const franchiseId = localStorage.getItem("franchiseId");
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    costPrice: "",
    stockQuantity: "",
    minStockAlert: "",
    sku: "",
  });
  const [formError, setFormError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    fetchProducts();
  }, [franchiseId]);

  function fetchProducts() {
    setLoading(true);
    api
      .get(`/franchise/${franchiseId}/products`)
      .then((res) => setProducts(res.data))
      .catch(() => setError("Failed to load products"))
      .finally(() => setLoading(false));
  }

  function handleDelete(productId) {
    if (!window.confirm("Deactivate this product?")) return;
    api
      .delete(`/franchise/${franchiseId}/products/${productId}`)
      .then(() => setProducts((prev) => prev.filter((p) => p.productId !== productId)))
      .catch(() => alert("Failed to delete product"));
  }

  function handleFormChange(e) {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
  }

  function handleAddProduct(e) {
    e.preventDefault();
    setFormError("");
    if (!form.name || !form.price) {
      setFormError("Name and price are required.");
      return;
    }
    setSubmitting(true);
    api
      .post(`/franchise/${franchiseId}/products`, {
        ...form,
        price: parseFloat(form.price),
        costPrice: form.costPrice ? parseFloat(form.costPrice) : null,
        stockQuantity: form.stockQuantity ? parseInt(form.stockQuantity) : 0,
        minStockAlert: form.minStockAlert ? parseInt(form.minStockAlert) : 10,
      })
      .then((res) => {
        setProducts((prev) => [...prev, res.data]);
        setShowForm(false);
        setForm({ name: "", description: "", price: "", costPrice: "", stockQuantity: "", minStockAlert: "", sku: "" });
      })
      .catch((err) => setFormError(err.response?.data?.message || "Failed to add product"))
      .finally(() => setSubmitting(false));
  }

  const filtered = products.filter((p) =>
    p.name.toLowerCase().includes(search.toLowerCase()) ||
    (p.sku && p.sku.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div style={{ padding: "1.5rem" }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: "1rem" }}>
        <h2>Products</h2>
        <button onClick={() => setShowForm((v) => !v)} style={{ padding: "0.5rem 1rem", background: "#6366f1", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" }}>
          {showForm ? "Cancel" : "+ Add Product"}
        </button>
      </div>

      {showForm && (
        <form onSubmit={handleAddProduct} style={{ background: "#f9fafb", padding: "1rem", borderRadius: "8px", marginBottom: "1.5rem", display: "grid", gridTemplateColumns: "1fr 1fr", gap: "0.75rem" }}>
          <h3 style={{ gridColumn: "1/-1", margin: 0 }}>New Product</h3>
          {formError && <p style={{ color: "red", gridColumn: "1/-1", margin: 0 }}>{formError}</p>}
          <input name="name" placeholder="Product Name *" value={form.name} onChange={handleFormChange} required />
          <input name="sku" placeholder="SKU (optional)" value={form.sku} onChange={handleFormChange} />
          <input name="price" type="number" step="0.01" placeholder="Price *" value={form.price} onChange={handleFormChange} required />
          <input name="costPrice" type="number" step="0.01" placeholder="Cost Price" value={form.costPrice} onChange={handleFormChange} />
          <input name="stockQuantity" type="number" placeholder="Stock Quantity" value={form.stockQuantity} onChange={handleFormChange} />
          <input name="minStockAlert" type="number" placeholder="Min Stock Alert (default 10)" value={form.minStockAlert} onChange={handleFormChange} />
          <textarea name="description" placeholder="Description" value={form.description} onChange={handleFormChange} style={{ gridColumn: "1/-1", resize: "vertical", minHeight: "60px" }} />
          <button type="submit" disabled={submitting} style={{ gridColumn: "1/-1", padding: "0.6rem", background: "#10b981", color: "#fff", border: "none", borderRadius: "6px", cursor: "pointer" }}>
            {submitting ? "Adding..." : "Add Product"}
          </button>
        </form>
      )}

      <input
        placeholder="Search by name or SKU..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{ padding: "0.5rem 0.75rem", width: "100%", maxWidth: "350px", borderRadius: "6px", border: "1px solid #d1d5db", marginBottom: "1rem" }}
      />

      {loading ? (
        <p>Loading products...</p>
      ) : error ? (
        <p style={{ color: "red" }}>{error}</p>
      ) : (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr style={{ background: "#f3f4f6" }}>
              <th style={th}>Name</th>
              <th style={th}>SKU</th>
              <th style={th}>Price</th>
              <th style={th}>Stock</th>
              <th style={th}>Min Alert</th>
              <th style={th}>Status</th>
              <th style={th}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filtered.length === 0 ? (
              <tr><td colSpan={7} style={{ textAlign: "center", padding: "1rem", color: "#6b7280" }}>No products found</td></tr>
            ) : (
              filtered.map((p) => (
                <tr key={p.productId} style={{ borderBottom: "1px solid #e5e7eb" }}>
                  <td style={td}>{p.name}</td>
                  <td style={td}>{p.sku || "-"}</td>
                  <td style={td}>₹{p.price}</td>
                  <td style={{ ...td, color: p.stockQuantity <= p.minStockAlert ? "#ef4444" : "inherit", fontWeight: p.stockQuantity <= p.minStockAlert ? "bold" : "normal" }}>
                    {p.stockQuantity}
                  </td>
                  <td style={td}>{p.minStockAlert}</td>
                  <td style={td}>
                    <span style={{ color: p.active ? "#10b981" : "#ef4444" }}>{p.active ? "Active" : "Inactive"}</span>
                  </td>
                  <td style={td}>
                    <button onClick={() => handleDelete(p.productId)} style={{ padding: "0.3rem 0.7rem", background: "#ef4444", color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer" }}>
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

export default Products;
