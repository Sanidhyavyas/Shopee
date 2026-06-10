/**
 * Returns a human-readable relative time string for the given ISO timestamp.
 *   < 60 s   → "just now"
 *   < 60 min → "2m ago"
 *   < 24 h   → "3h ago"
 *   older    → "Mon 4 Jun"
 */
export function timeAgo(isoString) {
  const DAYS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
  const MONTHS = [
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
  ];

  const now = new Date();
  const then = new Date(isoString);
  const diffSec = Math.floor((now - then) / 1000);

  if (diffSec < 60) return "just now";
  if (diffSec < 3600) return `${Math.floor(diffSec / 60)}m ago`;
  if (diffSec < 86400) return `${Math.floor(diffSec / 3600)}h ago`;

  return `${DAYS[then.getDay()]} ${then.getDate()} ${MONTHS[then.getMonth()]}`;
}
