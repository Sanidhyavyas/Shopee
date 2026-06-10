import { useState, useEffect, useRef, useCallback } from "react";

// Derived from apiService.js base URL — strip /api suffix for WebSocket origin
const API_BASE = "http://localhost:8081/api";
const WS_ORIGIN = API_BASE.replace(/^http/, "ws").replace(/\/api\/?$/, "");
const POLL_MS = 30_000;
const WS_RECONNECT_MS = 5_000;

function getHeaders() {
  const token = localStorage.getItem("token");
  return {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };
}

/**
 * useNotifications(franchiseId)
 *
 * Data strategy:
 *   Layer 1 — polls GET /api/notifications every 30 s (always starts on mount)
 *   Layer 2 — upgrades to WebSocket when available; pauses the poll while
 *              connected, resumes automatically on disconnect.
 *              Attempts a single reconnect after 5 s on unexpected close.
 *
 * Returns:
 *   { notifications, unreadCount, isOpen, setIsOpen, markAsRead, markAllRead, loading }
 */
export function useNotifications(franchiseId) {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  // Stable refs — never trigger re-renders
  const wsRef = useRef(null);
  const pollRef = useRef(null);
  const wsConnectedRef = useRef(false);
  const unmountedRef = useRef(false);
  const reconnectTimerRef = useRef(null);
  // Always up-to-date franchiseId without making callbacks depend on state
  const franchiseIdRef = useRef(franchiseId);
  franchiseIdRef.current = franchiseId;

  // ── Fetch ─────────────────────────────────────────────────────────────────
  // Stable — reads franchiseId from ref so the dep array can stay empty
  const fetchNotifications = useCallback(async () => {
    const fid = franchiseIdRef.current;
    if (!fid) return;

    setLoading(true);
    try {
      const res = await fetch(
        `${API_BASE}/notifications?franchiseId=${fid}&unreadOnly=false`,
        { headers: getHeaders() }
      );
      if (res.status === 401) {
        localStorage.clear();
        window.location.href = "/";
        return;
      }
      if (!res.ok) return;
      const data = await res.json();
      if (!unmountedRef.current) {
        setNotifications(data);
        setUnreadCount(data.filter((n) => !n.read).length);
      }
    } catch {
      // Network down — stay silent, keep existing cached data
    } finally {
      if (!unmountedRef.current) setLoading(false);
    }
  }, []); // stable — all mutable values accessed via refs

  // ── Polling ───────────────────────────────────────────────────────────────
  const stopPolling = useCallback(() => {
    if (pollRef.current) {
      clearInterval(pollRef.current);
      pollRef.current = null;
    }
  }, []);

  const startPolling = useCallback(() => {
    if (pollRef.current) return; // already running
    fetchNotifications();
    pollRef.current = setInterval(fetchNotifications, POLL_MS);
  }, [fetchNotifications]);

  // ── WebSocket ─────────────────────────────────────────────────────────────
  const connectWebSocket = useCallback(() => {
    const token = localStorage.getItem("token");
    if (!franchiseIdRef.current || !token) return;

    let ws;
    try {
      ws = new WebSocket(
        `${WS_ORIGIN}/ws/notifications?token=${encodeURIComponent(token)}`
      );
    } catch {
      // Browser blocked or URL invalid — stay on polling
      return;
    }

    wsRef.current = ws;

    ws.onopen = () => {
      if (unmountedRef.current) {
        ws.close();
        return;
      }
      wsConnectedRef.current = true;
      stopPolling(); // WebSocket is live — pause the poll interval
    };

    ws.onmessage = ({ data }) => {
      if (unmountedRef.current) return;
      try {
        const n = JSON.parse(data);
        setNotifications((prev) =>
          // De-duplicate: ignore if already in the list
          prev.some((x) => x.id === n.id) ? prev : [n, ...prev]
        );
        if (!n.read) setUnreadCount((c) => c + 1);
      } catch {
        // Malformed frame — ignore
      }
    };

    ws.onerror = () => {
      // onclose fires right after with wasClean=false; handle everything there
    };

    ws.onclose = (event) => {
      if (unmountedRef.current) return;
      wsConnectedRef.current = false;
      wsRef.current = null;

      // Resume polling immediately so no data is missed
      startPolling();

      // Attempt one reconnect after 5 s only for unexpected closes
      if (!event.wasClean) {
        reconnectTimerRef.current = setTimeout(() => {
          if (!unmountedRef.current && !wsConnectedRef.current) {
            connectWebSocket();
          }
        }, WS_RECONNECT_MS);
      }
    };
  }, [startPolling, stopPolling]); // both stable → connectWebSocket is stable

  // ── Lifecycle ─────────────────────────────────────────────────────────────
  useEffect(() => {
    if (!franchiseId) return;

    unmountedRef.current = false;
    startPolling();      // Layer 1: polling always starts
    connectWebSocket();  // Layer 2: WS upgrade attempt (silently falls back)

    return () => {
      unmountedRef.current = true;
      stopPolling();

      if (reconnectTimerRef.current) {
        clearTimeout(reconnectTimerRef.current);
        reconnectTimerRef.current = null;
      }
      if (wsRef.current) {
        // Null out onclose so the handler doesn't try to reconnect on teardown
        wsRef.current.onclose = null;
        wsRef.current.close();
        wsRef.current = null;
      }
    };
  }, [franchiseId, startPolling, connectWebSocket, stopPolling]);

  // ── Optimistic mark-as-read ───────────────────────────────────────────────
  const markAsRead = useCallback(async (id) => {
    // Apply immediately in the UI
    setNotifications((prev) =>
      prev.map((n) => (n.id === id ? { ...n, read: true } : n))
    );
    setUnreadCount((c) => Math.max(0, c - 1));

    try {
      const res = await fetch(`${API_BASE}/notifications/${id}/read`, {
        method: "PATCH",
        headers: getHeaders(),
      });
      if (!res.ok) throw new Error("API error");
    } catch {
      // Revert on failure
      setNotifications((prev) =>
        prev.map((n) => (n.id === id ? { ...n, read: false } : n))
      );
      setUnreadCount((c) => c + 1);
    }
  }, []);

  const markAllRead = useCallback(async () => {
    const fid = franchiseIdRef.current;
    if (!fid) return;

    setNotifications((prev) => prev.map((n) => ({ ...n, read: true })));
    setUnreadCount(0);

    try {
      const res = await fetch(
        `${API_BASE}/notifications/read-all?franchiseId=${fid}`,
        { method: "PATCH", headers: getHeaders() }
      );
      if (!res.ok) throw new Error("API error");
    } catch {
      // Re-fetch to restore accurate server state
      fetchNotifications();
    }
  }, [fetchNotifications]);

  return {
    notifications,
    unreadCount,
    isOpen,
    setIsOpen,
    markAsRead,
    markAllRead,
    loading,
  };
}
