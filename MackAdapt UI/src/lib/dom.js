/**
 * Query helpers that avoid repetitive null checks across pages.
 */
export const qs = (selector, scope = document) => scope.querySelector(selector);
export const qsa = (selector, scope = document) =>
  Array.from(scope.querySelectorAll(selector));

/**
 * Update the text content of an element, accepting either an element or selector.
 */
export function setText(target, value) {
  const el = typeof target === 'string' ? qs(target) : target;
  if (!el) return;
  el.textContent = value;
}

/**
 * Inject HTML while guarding against missing nodes.
 */
export function setHTML(target, html) {
  const el = typeof target === 'string' ? qs(target) : target;
  if (!el) return;
  el.innerHTML = html;
}

/**
 * Convenience hook for DOMContentLoaded.
 */
export function onReady(cb) {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', cb, { once: true });
  } else {
    cb();
  }
}
