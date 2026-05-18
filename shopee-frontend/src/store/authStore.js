import { create } from "zustand";

const useAuthStore = create((set) => ({
  token: localStorage.getItem("token") || null,
  role: localStorage.getItem("role") || null,
  franchiseId: localStorage.getItem("franchiseId") || null,
  franchises: JSON.parse(localStorage.getItem("franchises") || "null"),

  login: (data) => {
    localStorage.setItem("token", data.token);
    localStorage.setItem("role", data.role);
    if (data.franchiseId) {
      localStorage.setItem("franchiseId", String(data.franchiseId));
    }
    if (data.franchises) {
      localStorage.setItem("franchises", JSON.stringify(data.franchises));
    }
    set({
      token: data.token,
      role: data.role,
      franchiseId: data.franchiseId ? String(data.franchiseId) : null,
      franchises: data.franchises || null,
    });
  },

  selectFranchise: (franchiseId) => {
    localStorage.setItem("franchiseId", String(franchiseId));
    set({ franchiseId: String(franchiseId) });
  },

  logout: () => {
    localStorage.clear();
    set({ token: null, role: null, franchiseId: null, franchises: null });
  },

  isAuthenticated: () => !!localStorage.getItem("token"),
}));

export default useAuthStore;
