import { useState, useEffect } from "react";

/**
 * Returns a debounced version of `value` that only updates
 * after `delay` ms of inactivity.  Useful for search inputs.
 *
 * @param {*}      value – value to debounce
 * @param {number} delay – debounce delay in ms (default 400)
 * @returns debounced value
 */
export function useDebounce(value, delay = 400) {
  const [debouncedValue, setDebouncedValue] = useState(value);

  useEffect(() => {
    const timer = setTimeout(() => setDebouncedValue(value), delay);
    return () => clearTimeout(timer);
  }, [value, delay]);

  return debouncedValue;
}
