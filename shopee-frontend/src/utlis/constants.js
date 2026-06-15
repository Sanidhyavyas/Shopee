// ─── API ──────────────────────────────────────────────────────────────────
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8081/api";

// ─── Roles ────────────────────────────────────────────────────────────────
export const ROLES = Object.freeze({
  SUPER_ADMIN: "SUPER_ADMIN",
  FRANCHISE_ADMIN: "FRANCHISE_ADMIN",
  STAFF: "STAFF",
});

// ─── Order statuses ───────────────────────────────────────────────────────
export const ORDER_STATUS = Object.freeze({
  PENDING: "PENDING",
  CONFIRMED: "CONFIRMED",
  PREPARING: "PREPARING",
  READY: "READY",
  DELIVERED: "DELIVERED",
  CANCELLED: "CANCELLED",
});

// ─── Pagination defaults ──────────────────────────────────────────────────
export const DEFAULT_PAGE_SIZE = 10;
export const PAGE_SIZE_OPTIONS = [5, 10, 20, 50];

// ─── Low-stock threshold ──────────────────────────────────────────────────
export const LOW_STOCK_THRESHOLD = 10;

// ─── Date formats ─────────────────────────────────────────────────────────
export const DATE_FORMAT = "YYYY-MM-DD";
export const DATETIME_FORMAT = "YYYY-MM-DD HH:mm";
