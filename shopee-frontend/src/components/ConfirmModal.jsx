import "./ConfirmModal.css";

/**
 * Generic confirmation dialog.
 *
 * Props:
 *   open        – boolean: whether the modal is visible
 *   title       – string: dialog heading
 *   message     – string: body text
 *   confirmText – string (default "Confirm")
 *   cancelText  – string (default "Cancel")
 *   onConfirm   – () => void
 *   onCancel    – () => void
 *   danger      – boolean: renders confirm button in red
 */
export default function ConfirmModal({
  open,
  title = "Are you sure?",
  message,
  confirmText = "Confirm",
  cancelText = "Cancel",
  onConfirm,
  onCancel,
  danger = false,
}) {
  if (!open) return null;

  return (
    <div className="confirm-overlay" role="dialog" aria-modal="true">
      <div className="confirm-box">
        <h3 className="confirm-title">{title}</h3>
        {message && <p className="confirm-message">{message}</p>}
        <div className="confirm-actions">
          <button className="btn-cancel" onClick={onCancel}>
            {cancelText}
          </button>
          <button
            className={`btn-confirm${danger ? " btn-danger" : ""}`}
            onClick={onConfirm}
          >
            {confirmText}
          </button>
        </div>
      </div>
    </div>
  );
}
