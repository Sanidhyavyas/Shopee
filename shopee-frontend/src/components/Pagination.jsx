import "./Pagination.css";

/**
 * Page navigation bar.
 *
 * Props:
 *   page       – current 1-based page number
 *   totalPages – total number of pages
 *   onPage     – (n: number) => void
 */
export default function Pagination({ page, totalPages, onPage }) {
  if (totalPages <= 1) return null;

  const pages = Array.from({ length: totalPages }, (_, i) => i + 1);

  return (
    <nav className="pagination" aria-label="Pagination">
      <button
        className="pg-btn"
        onClick={() => onPage(page - 1)}
        disabled={page === 1}
        aria-label="Previous page"
      >
        ‹
      </button>

      {pages.map((p) => (
        <button
          key={p}
          className={`pg-btn${p === page ? " pg-active" : ""}`}
          onClick={() => onPage(p)}
          aria-current={p === page ? "page" : undefined}
        >
          {p}
        </button>
      ))}

      <button
        className="pg-btn"
        onClick={() => onPage(page + 1)}
        disabled={page === totalPages}
        aria-label="Next page"
      >
        ›
      </button>
    </nav>
  );
}
