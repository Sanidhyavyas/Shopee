import { useRef, useEffect, useCallback } from "react";

const ALERT_TITLE = "🔔 New alert — Shopee";
const BASE_TITLE = "Shopee";
const FLASH_INTERVAL_MS = 1000;
const MAX_TICKS = 10; // 5 full on/off cycles
const SOUND_PREF_KEY = "soundEnabled";

/** Synthesise a short soft beep with the Web Audio API — no audio files needed. */
function playBeep() {
  try {
    const AudioCtx = window.AudioContext || window.webkitAudioContext;
    if (!AudioCtx) return;
    const ctx = new AudioCtx();
    const osc = ctx.createOscillator();
    const gain = ctx.createGain();

    osc.connect(gain);
    gain.connect(ctx.destination);

    osc.type = "sine";
    osc.frequency.setValueAtTime(880, ctx.currentTime); // A5 — gentle high tone
    gain.gain.setValueAtTime(0.08, ctx.currentTime);    // low volume
    gain.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + 0.35);

    osc.start(ctx.currentTime);
    osc.stop(ctx.currentTime + 0.35);
    osc.onended = () => ctx.close();
  } catch {
    // AudioContext blocked (e.g. no user gesture yet) — fail silently
  }
}

/**
 * useNotificationAlerts()
 *
 * Returns a stable `triggerAlert` callback.
 *
 * When called while the tab is hidden it:
 *   1. Plays a soft beep (skipped when localStorage "soundEnabled" === "false")
 *   2. Flashes the tab title between ALERT_TITLE and BASE_TITLE every 1 s,
 *      stopping after 5 complete flashes or as soon as the tab regains focus.
 *
 * Does nothing when the tab is already visible.
 */
export function useNotificationAlerts() {
  const flashIntervalRef = useRef(null);
  const flashTickRef = useRef(0);

  const stopFlashing = useCallback(() => {
    if (flashIntervalRef.current) {
      clearInterval(flashIntervalRef.current);
      flashIntervalRef.current = null;
    }
    document.title = BASE_TITLE;
    flashTickRef.current = 0;
  }, []);

  // Restore title and cancel interval when the tab becomes visible again
  useEffect(() => {
    const onVisibilityChange = () => {
      if (document.visibilityState === "visible") stopFlashing();
    };
    document.addEventListener("visibilitychange", onVisibilityChange);
    return () => {
      document.removeEventListener("visibilitychange", onVisibilityChange);
      stopFlashing();
    };
  }, [stopFlashing]);

  const triggerAlert = useCallback(() => {
    // Only act when the user is not looking at the tab
    if (document.visibilityState === "visible") return;

    // ── Sound ────────────────────────────────────────────────────────────────
    // Default: enabled. Opt-out by storing "soundEnabled" = "false".
    const soundEnabled = localStorage.getItem(SOUND_PREF_KEY) !== "false";
    if (soundEnabled) playBeep();

    // ── Title flash ──────────────────────────────────────────────────────────
    stopFlashing(); // cancel any in-progress flash before starting a new one

    document.title = ALERT_TITLE;

    flashIntervalRef.current = setInterval(() => {
      flashTickRef.current += 1;

      if (flashTickRef.current >= MAX_TICKS) {
        stopFlashing(); // resets title to BASE_TITLE
        return;
      }

      // Odd ticks → base title, even ticks → alert title
      document.title =
        flashTickRef.current % 2 !== 0 ? BASE_TITLE : ALERT_TITLE;
    }, FLASH_INTERVAL_MS);
  }, [stopFlashing]);

  return { triggerAlert };
}
