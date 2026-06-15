import { useState, useMemo } from "react";
import { DEFAULT_PAGE_SIZE } from "../utlis/constants";

/**
 * Client-side pagination helper.
 *
 * @param {Array}  items       – full data array
 * @param {number} [pageSize]  – rows per page (default from constants)
 * @returns {{ page, totalPages, paginatedItems, goTo, next, prev, setPageSize, pageSize }}
 */
export function usePagination(items = [], pageSize: initialPageSize = DEFAULT_PAGE_SIZE) {
  const [page, setPage] = useState(1);
  const [pageSize, setPageSize] = useState(initialPageSize);

  const totalPages = Math.max(1, Math.ceil(items.length / pageSize));

  const paginatedItems = useMemo(() => {
    const start = (page - 1) * pageSize;
    return items.slice(start, start + pageSize);
  }, [items, page, pageSize]);

  const goTo = (n) => setPage(Math.min(Math.max(1, n), totalPages));
  const next = () => goTo(page + 1);
  const prev = () => goTo(page - 1);

  return { page, totalPages, paginatedItems, goTo, next, prev, pageSize, setPageSize };
}
