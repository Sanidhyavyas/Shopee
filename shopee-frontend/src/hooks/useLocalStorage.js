import { useState } from "react";

/**
 * useState-like hook that persists the value in localStorage.
 *
 * @template T
 * @param {string} key          localStorage key
 * @param {T}      initialValue fallback when key is absent
 * @returns {[T, (value: T | ((prev: T) => T)) => void]}
 */
export function useLocalStorage(key, initialValue) {
  const [storedValue, setStoredValue] = useState(() => {
    try {
      const item = window.localStorage.getItem(key);
      return item !== null ? JSON.parse(item) : initialValue;
    } catch {
      return initialValue;
    }
  });

  const setValue = (value) => {
    try {
      const nextValue = value instanceof Function ? value(storedValue) : value;
      setStoredValue(nextValue);
      window.localStorage.setItem(key, JSON.stringify(nextValue));
    } catch (err) {
      console.warn(`[useLocalStorage] Could not write key "${key}":`, err);
    }
  };

  return [storedValue, setValue];
}
