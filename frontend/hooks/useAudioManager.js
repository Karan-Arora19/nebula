"use client";

import { useRef, useEffect, useCallback } from "react";

/**
 * useAudioManager Hook
 *
 * Manages sound effects for the car configurator.
 * Currently handles paint job SFX only.
 */

// Audio paths
const PAINT_SFX_PATH = "/audio/Paint-job.mp3";

// Volume levels
const PAINT_SFX_VOLUME = 0.4;

export function useAudioManager() {
  const paintSfxRef = useRef(null);

  // Initialize paint SFX lazily
  const initializePaintSfx = useCallback(() => {
    if (typeof window === "undefined") return;

    if (!paintSfxRef.current) {
      paintSfxRef.current = new Audio(PAINT_SFX_PATH);
      paintSfxRef.current.volume = PAINT_SFX_VOLUME;
      paintSfxRef.current.preload = "auto";
    }
  }, []);

  // Play paint job sound effect with callback when complete
  const playPaintSfx = useCallback(
    (onComplete) => {
      initializePaintSfx();

      if (!paintSfxRef.current) return onComplete?.();

      paintSfxRef.current.currentTime = 0;

      const handleEnded = () => {
        onComplete?.();
        paintSfxRef.current.removeEventListener("ended", handleEnded);
      };

      paintSfxRef.current.addEventListener("ended", handleEnded);

      paintSfxRef.current.play().catch((err) => {
        console.log("Paint SFX play failed:", err.message);
        onComplete?.();
      });
    },
    [initializePaintSfx],
  );

  // Cleanup paint SFX on unmount
  useEffect(() => {
    return () => {
      if (paintSfxRef.current) {
        paintSfxRef.current.pause();
        paintSfxRef.current = null;
      }
    };
  }, []);

  return {
    playPaintSfx,
  };
}