/**
 * Format a number as Philippine Peso currency.
 * formatCurrency(1234.5)  → "₱1,234.50"
 * formatCurrency(0)       → "₱0.00"
 *
 * @param {number} amount
 * @param {string} [currency="PHP"]
 * @param {string} [locale="en-PH"]
 * @returns {string}
 */
export function formatCurrency(amount, currency = "PHP", locale = "en-PH") {
  if (amount === null || amount === undefined || Number.isNaN(Number(amount))) {
    return "—";
  }
  return new Intl.NumberFormat(locale, {
    style: "currency",
    currency,
    minimumFractionDigits: 2,
  }).format(Number(amount));
}

/**
 * Parse a currency string back to a number.
 * parseCurrency("₱1,234.50") → 1234.5
 *
 * @param {string} value
 * @returns {number}
 */
export function parseCurrency(value) {
  if (!value) return 0;
  return parseFloat(String(value).replace(/[^0-9.-]/g, "")) || 0;
}
