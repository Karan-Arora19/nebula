"use client";

import React, { useState, useEffect, useRef, useCallback } from "react";
import PropTypes from "prop-types";

/**
 * VideoIntroEffect Component
 *
 * Plays a cinematic intro video animation on the user's FIRST visit
 * to the car configurator page. Uses localStorage to track if the
 * user has already seen the intro.
 *
 * Features a premium exit transition with:
 * - Red energy pulse expanding from center
 * - Zoom blur effect
 * - Particle dispersion
 * - Smooth fade out
 */

const STORAGE_KEY = "nebula-car-configurator-intro-seen";

// Helper to check localStorage (only on client)
const shouldShowIntro = () => {
  if (typeof window === "undefined") return false;
  return !localStorage.getItem(STORAGE_KEY);
};

export default function VideoIntroEffect({ onComplete, videoSrc }) {
  // Initialize state from localStorage to avoid useEffect setState
  const [showIntro, setShowIntro] = useState(shouldShowIntro);
  const [exitPhase, setExitPhase] = useState(0); // 0: playing, 1: pulse, 2: zoom, 3: fade, 4: done
  const [videoLoaded, setVideoLoaded] = useState(false);
  const [isMuted, setIsMuted] = useState(true); // Start muted for autoplay policy
  const videoRef = useRef(null);

  // Call onComplete if user has already seen intro
  useEffect(() => {
    if (!showIntro) {
      onComplete?.();
    }
  }, [showIntro, onComplete]);

  // Handle video loaded - just marks as loaded and ensures playback
  const handleVideoLoaded = useCallback(() => {
    if (videoLoaded) return; // Guard against multiple calls
    setVideoLoaded(true);

    const video = videoRef.current;
    if (!video) return;

    // If video isn't playing yet, try to start it (muted, so should work)
    if (video.paused) {
      video.play().catch((err) => {
        console.warn("Video autoplay failed:", err);
        // Don't skip - just show the video paused, user can interact
      });
    }
  }, [videoLoaded]);

  // Subscribe to canplay event if video is already ready when effect runs
  useEffect(() => {
    if (!showIntro || videoLoaded) return;

    const video = videoRef.current;
    if (!video) return;

    // Use event listener pattern to avoid synchronous setState
    const onCanPlay = () => handleVideoLoaded();

    // If already ready, fire immediately via microtask (not synchronous)
    if (video.readyState >= 3) {
      queueMicrotask(onCanPlay);
    }

    video.addEventListener("canplay", onCanPlay);
    return () => video.removeEventListener("canplay", onCanPlay);
  }, [showIntro, videoLoaded, handleVideoLoaded]);

  // Premium exit animation sequence - smooth timing
  const handleIntroComplete = useCallback(() => {
    localStorage.setItem(STORAGE_KEY, "true");

    // Phase 1: Red energy pulse (0ms)
    setExitPhase(1);

    // Phase 2: Zoom blur (600ms)
    setTimeout(() => setExitPhase(2), 600);

    // Phase 3: Fade out (1200ms)
    setTimeout(() => setExitPhase(3), 1200);

    // Phase 4: Complete (1800ms)
    setTimeout(() => {
      setExitPhase(4);
      setShowIntro(false);
      onComplete?.();
    }, 1800);
  }, [onComplete]);

  const handleVideoEnded = useCallback(() => {
    handleIntroComplete();
  }, [handleIntroComplete]);

  const handleVideoError = useCallback(() => {
    console.warn("Video failed to load, skipping intro");
    localStorage.setItem(STORAGE_KEY, "true");
    setShowIntro(false);
    onComplete?.();
  }, [onComplete]);

  const handleSkip = useCallback(() => {
    if (videoRef.current) {
      videoRef.current.pause();
    }
    // User skipped - stop audio and complete intro
    handleIntroComplete();
  }, [handleIntroComplete]);

  // Toggle mute/unmute
  const handleToggleMute = useCallback(() => {
    if (videoRef.current) {
      const newMuted = !videoRef.current.muted;
      videoRef.current.muted = newMuted;
      setIsMuted(newMuted);
    }
  }, []);

  if (!showIntro) {
    return null;
  }

  return (
    <div
      className={`absolute inset-0 z-[100] bg-black flex items-center justify-center overflow-hidden rounded-xl
        ${exitPhase >= 2 ? "scale-110" : "scale-100"}
        ${exitPhase >= 3 ? "opacity-0" : "opacity-100"}
      `}
      style={{
        transition:
          "transform 0.8s cubic-bezier(0.4, 0, 0.2, 1), opacity 0.6s ease-out",
        filter: exitPhase >= 2 ? "blur(12px)" : "blur(0px)",
      }}
    >
      {/* Video Container */}
      <div className="relative w-full h-full">
        {/* Video Element */}
        <video
          ref={videoRef}
          className={`absolute inset-0 w-full h-full object-cover transition-all duration-500 rounded-xl
            ${exitPhase >= 1 ? "brightness-150" : "brightness-100"}
          `}
          src={videoSrc}
          muted={isMuted}
          autoPlay
          playsInline
          preload="auto"
          onLoadedData={handleVideoLoaded}
          onCanPlay={handleVideoLoaded}
          onEnded={handleVideoEnded}
          onError={handleVideoError}
        >
          {/* Empty track for accessibility - video has no dialogue */}
          <track kind="captions" />
        </video>

        {/* Loading State */}
        {!videoLoaded && (
          <div className="absolute inset-0 flex items-center justify-center bg-black">
            <div className="text-center">
              <div className="w-16 h-16 border-4 border-red-500/30 border-t-red-500 rounded-full animate-spin mx-auto mb-4"></div>
              <p className="text-gray-400 text-sm">Loading experience...</p>
            </div>
          </div>
        )}

        {/* RED ENERGY PULSE - Expanding circle from center */}
        <div
          className={`absolute inset-0 pointer-events-none flex items-center justify-center
            ${exitPhase >= 1 ? "opacity-100" : "opacity-0"}
          `}
        >
          <div
            className={`rounded-full bg-gradient-radial from-red-500/60 via-red-600/30 to-transparent
              ${exitPhase >= 1 ? "w-[300vmax] h-[300vmax]" : "w-0 h-0"}
            `}
            style={{
              transition: "all 1s cubic-bezier(0.16, 1, 0.3, 1)",
              boxShadow:
                exitPhase >= 1
                  ? "0 0 200px 100px rgba(239, 68, 68, 0.4)"
                  : "none",
            }}
          />
        </div>

        {/* HORIZONTAL LIGHT STREAKS */}
        {exitPhase >= 1 && (
          <div className="absolute inset-0 pointer-events-none">
            <div
              className="absolute top-1/2 left-0 right-0 h-px bg-gradient-to-r from-transparent via-red-500 to-transparent"
              style={{
                transform: `scaleX(${exitPhase >= 2 ? 3 : 0})`,
                opacity: exitPhase >= 3 ? 0 : 1,
                transition: "all 0.8s cubic-bezier(0.16, 1, 0.3, 1)",
                boxShadow: "0 0 30px 10px rgba(239, 68, 68, 0.5)",
              }}
            />
            <div
              className="absolute top-1/2 left-0 right-0 h-1 bg-gradient-to-r from-transparent via-white to-transparent"
              style={{
                transform: `scaleX(${exitPhase >= 2 ? 3 : 0})`,
                opacity: exitPhase >= 3 ? 0 : 0.6,
                transition: "all 0.7s cubic-bezier(0.16, 1, 0.3, 1)",
              }}
            />
          </div>
        )}

        {/* VIGNETTE OVERLAY */}
        <div
          className="absolute inset-0 pointer-events-none"
          style={{
            background:
              exitPhase >= 1
                ? "radial-gradient(circle at center, transparent 0%, rgba(0,0,0,0.8) 100%)"
                : "none",
            opacity: exitPhase >= 3 ? 0 : 1,
            transition: "opacity 0.6s ease-out",
          }}
        />

        {/* Control Buttons */}
        {exitPhase === 0 && (
          <div className="absolute bottom-8 right-8 flex items-center gap-3 z-10">
            {/* Mute/Unmute Button */}
            <button
              onClick={handleToggleMute}
              className="p-2.5 bg-black/50 hover:bg-black/70 backdrop-blur-sm border border-white/20 rounded-lg text-white transition-all duration-200 hover:border-red-500/50"
              title={isMuted ? "Unmute" : "Mute"}
            >
              {isMuted ? (
                /* Muted Icon */
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="w-5 h-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z"
                  />
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M17 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2"
                  />
                </svg>
              ) : (
                /* Unmuted Icon */
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  className="w-5 h-5"
                  fill="none"
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    d="M15.536 8.464a5 5 0 010 7.072m2.828-9.9a9 9 0 010 12.728M5.586 15H4a1 1 0 01-1-1v-4a1 1 0 011-1h1.586l4.707-4.707C10.923 3.663 12 4.109 12 5v14c0 .891-1.077 1.337-1.707.707L5.586 15z"
                  />
                </svg>
              )}
            </button>

            {/* Skip Button */}
            <button
              onClick={handleSkip}
              className="px-6 py-2 bg-black/50 hover:bg-black/70 backdrop-blur-sm border border-white/20 rounded-lg text-white text-sm font-medium transition-all duration-200 hover:border-red-500/50"
            >
              Skip Intro
            </button>
          </div>
        )}

        {/* Progress indicator - only show when video is playing */}
        {videoLoaded && exitPhase === 0 && (
          <VideoProgress videoRef={videoRef} />
        )}
      </div>
    </div>
  );
}

/**
 * VideoProgress - Shows a subtle progress bar for the video
 */
function VideoProgress({ videoRef }) {
  const [progress, setProgress] = useState(0);

  useEffect(() => {
    const video = videoRef.current;
    if (!video) return;

    const updateProgress = () => {
      if (video.duration) {
        setProgress((video.currentTime / video.duration) * 100);
      }
    };

    video.addEventListener("timeupdate", updateProgress);
    return () => video.removeEventListener("timeupdate", updateProgress);
  }, [videoRef]);

  return (
    <div className="absolute bottom-0 left-0 right-0 h-1 bg-black/50">
      <div
        className="h-full bg-gradient-to-r from-red-500 to-red-600 transition-all duration-100"
        style={{ width: `${progress}%` }}
      />
    </div>
  );
}

VideoIntroEffect.propTypes = {
  onComplete: PropTypes.func.isRequired,
  videoSrc: PropTypes.string,
};

VideoIntroEffect.defaultProps = {
  videoSrc: "/videos/car-intro.mp4",
};

VideoProgress.propTypes = {
  videoRef: PropTypes.shape({
    current: PropTypes.instanceOf(
      typeof HTMLVideoElement === "undefined" ? Object : HTMLVideoElement,
    ),
  }).isRequired,
};
