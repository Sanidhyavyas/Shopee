import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { timeAgo } from "../utlis/timeUtils";
import "../styles/notificationBell.css";

const TYPE_ROUTES = {
  NEW_ORDER: "/franchise/orders",
  LOW_STOCK: "/franchise/products",
  ORDER_STATUS_UPDATE: "/franchise/orders",
};

const MAX_VISIBLE = 5;

/**
 * Purely presentational — all state and logic come from useNotifications via props.
 * Import useNotifications in a parent (FranchiseHeader) and spread its return value.
 */
export default function NotificationBell({
  notifications,
  unreadCount,
  isOpen,
  setIsOpen,
  markAsRead,
  markAllRead,
  loading,
}) {
  const navigate = useNavigate();
  const wrapperRef = useRef(null);

  // Close the dropdown when the user clicks anywhere outside the component
  useEffect(() => {
    if (!isOpen) return;

    function handleMouseDown(e) {
      if (wrapperRef.current && !wrapperRef.current.contains(e.target)) {
        setIsOpen(false);
      }
    }

    document.addEventListener("mousedown", handleMouseDown);
    return () => document.removeEventListener("mousedown", handleMouseDown);
  }, [isOpen, setIsOpen]);

  function handleItemClick(notification) {
    if (!notification.read) markAsRead(notification.id);
    setIsOpen(false);
    navigate(TYPE_ROUTES[notification.type] ?? "/franchise/dashboard");
  }

  const visible = notifications.slice(0, MAX_VISIBLE);
  const hasMore = notifications.length > MAX_VISIBLE;

  return (
    <div className="nb-wrapper" ref={wrapperRef}>
      {/* Bell trigger button */}
      <button
        className="nb-button"
        onClick={() => setIsOpen((v) => !v)}
        aria-label={`Notifications${unreadCount > 0 ? `, ${unreadCount} unread` : ""}`}
        aria-haspopup="dialog"
        aria-expanded={isOpen}
      >
        {/* Feather-style bell — no icon library */}
        <svg
          className="nb-icon"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
          aria-hidden="true"
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>

        {unreadCount > 0 && (
          <span className="nb-badge" aria-hidden="true">
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {/* Dropdown */}
      {isOpen && (
        <div className="nb-dropdown" role="dialog" aria-label="Notifications panel">
          {/* Header */}
          <div className="nb-header">
            <p className="nb-header-title">
              Notifications
              {loading && (
                <span style={{ fontWeight: 400, color: "#9ca3af", marginLeft: 6, fontSize: 13 }}>
                  loading…
                </span>
              )}
            </p>
            {unreadCount > 0 && (
              <button className="nb-mark-all-btn" onClick={markAllRead}>
                Mark all read
              </button>
            )}
          </div>

          {/* Notification list */}
          <ul className="nb-list">
            {visible.length === 0 ? (
              <li className="nb-empty">
                <span className="nb-empty-icon" aria-hidden="true">🔔</span>
                No notifications yet
              </li>
            ) : (
              visible.map((n) => (
                <li
                  key={n.id}
                  className={`nb-item nb-item--${n.type}${!n.read ? " unread" : ""}`}
                  onClick={() => handleItemClick(n)}
                  role="button"
                  tabIndex={0}
                  onKeyDown={(e) => e.key === "Enter" && handleItemClick(n)}
                >
                  <div className="nb-item-body">
                    <p className="nb-item-title">{n.title}</p>
                    <p className="nb-item-message">{n.message}</p>
                    <p className="nb-item-time">{timeAgo(n.timestamp)}</p>
                  </div>
                  {!n.read && <span className="nb-unread-dot" aria-hidden="true" />}
                </li>
              ))
            )}
          </ul>

          {/* View all — only appears when list is truncated */}
          {hasMore && (
            <button
              className="nb-view-all"
              onClick={() => {
                setIsOpen(false);
                navigate("/franchise/orders");
              }}
            >
              View all {notifications.length} notifications
            </button>
          )}
        </div>
      )}
    </div>
  );
}
