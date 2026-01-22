"use client";

import { useState, useEffect, useCallback } from "react";

/**
 * Hook to calculate overlay position on a video with object-cover.
 * Provides pixel positions that stay aligned with the video content
 * regardless of viewport aspect ratio.
 *
 * @param {React.RefObject} videoRef - Reference to the video element
 * @param {object} overlayConfig - Position config as percentages of the original video
 * @param {number} overlayConfig.topPercent - Top position as % of video height
 * @param {number} overlayConfig.leftPercent - Left position as % of video width
 * @param {number} overlayConfig.widthPercent - Width as % of video width
 * @param {number} overlayConfig.heightPercent - Height as % of video height
 * @param {number} videoAspectRatio - Native aspect ratio of the video (width/height)
 */
export function useVideoOverlay(
  videoRef,
  overlayConfig,
  videoAspectRatio = 16 / 9,
) {
  const [overlayStyle, setOverlayStyle] = useState({
    top: 0,
    left: 0,
    width: 0,
    height: 0,
  });

  const calculateOverlay = useCallback(() => {
    const video = videoRef.current;
    if (!video) return;

    const containerWidth = video.offsetWidth;
    const containerHeight = video.offsetHeight;
    const containerAspect = containerWidth / containerHeight;

    let renderedWidth, renderedHeight, offsetX, offsetY;

    // object-cover: video fills container, may overflow and get cropped
    if (containerAspect > videoAspectRatio) {
      // Container is wider - video height is cropped
      renderedWidth = containerWidth;
      renderedHeight = containerWidth / videoAspectRatio;
      offsetX = 0;
      offsetY = (containerHeight - renderedHeight) / 2;
    } else {
      // Container is taller - video width is cropped
      renderedHeight = containerHeight;
      renderedWidth = containerHeight * videoAspectRatio;
      offsetX = (containerWidth - renderedWidth) / 2;
      offsetY = 0;
    }

    // Calculate overlay position in pixels
    const { topPercent, leftPercent, widthPercent, heightPercent } =
      overlayConfig;

    setOverlayStyle({
      top: offsetY + (renderedHeight * topPercent) / 100,
      left: offsetX + (renderedWidth * leftPercent) / 100,
      width: (renderedWidth * widthPercent) / 100,
      height: (renderedHeight * heightPercent) / 100,
    });
  }, [videoRef, overlayConfig, videoAspectRatio]);

  useEffect(() => {
    calculateOverlay();

    window.addEventListener("resize", calculateOverlay);
    return () => window.removeEventListener("resize", calculateOverlay);
  }, [calculateOverlay]);

  // Recalculate when video loads metadata
  useEffect(() => {
    const video = videoRef.current;
    if (!video) return;

    video.addEventListener("loadedmetadata", calculateOverlay);
    return () => video.removeEventListener("loadedmetadata", calculateOverlay);
  }, [videoRef, calculateOverlay]);

  return overlayStyle;
}
