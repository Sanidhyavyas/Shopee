import useAuthStore from "../store/authStore";
import { useNotifications } from "../hooks/useNotifications";
import NotificationBell from "./NotificationBell";

/**
 * FranchiseHeader
 *
 * Renders the top bar of any franchise page: page title on the left,
 * notification bell on the right.
 *
 * Usage in a page component:
 *   <FranchiseHeader title="Welcome to Your Store" />
 *
 * franchiseId is read from Zustand authStore — no prop needed.
 */
export default function FranchiseHeader({ title }) {
  const franchiseId = useAuthStore((s) => s.franchiseId);
  const notifications = useNotifications(franchiseId);

  return (
    <header className="content-header">
      <h1 className="content-header__title">{title}</h1>
      <NotificationBell {...notifications} />
    </header>
  );
}
