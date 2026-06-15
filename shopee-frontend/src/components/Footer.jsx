import "./Footer.css";

export default function Footer() {
  const year = new Date().getFullYear();
  return (
    <footer className="app-footer">
      <span>© {year} Shopee Franchise Management System. All rights reserved.</span>
    </footer>
  );
}
