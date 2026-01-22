import { useState, useEffect, useRef } from "react";

/**
 * Syncs an element's position to a video's playback time using keyframes.
 * Interpolates between keyframes for smooth motion tracking.
 *
 * @param {React.MutableRefObject} videoRef - Ref to the video element
 * @param {Array} keyframes - Array of { time: number, x: number, y: number, scale: number }
 * @returns {Object} - Style object with transform
 */
export function useVideoMotion(videoRef, keyframes = []) {
  const [transform, setTransform] = useState("translate(0px, 0px) scale(1)");
  const requestRef = useRef();

  useEffect(() => {
    const video = videoRef.current;
    if (!video || keyframes.length === 0) return;

    const animate = () => {
      const time = video.currentTime;

      // Find the active keyframes (prev and next)
      // We assume keyframes are sorted by time
      let prevKey = keyframes[0];
      let nextKey = keyframes[keyframes.length - 1];

      for (let i = 0; i < keyframes.length - 1; i++) {
        if (time >= keyframes[i].time && time < keyframes[i + 1].time) {
          prevKey = keyframes[i];
          nextKey = keyframes[i + 1];
          break;
        }
      }

      // Calculate progress between frames (0 to 1)
      const duration = nextKey.time - prevKey.time;
      const progress = duration > 0 ? (time - prevKey.time) / duration : 0;

      // Linear interpolation
      const x = prevKey.x + (nextKey.x - prevKey.x) * progress;
      const y = prevKey.y + (nextKey.y - prevKey.y) * progress;
      const s = prevKey.scale + (nextKey.scale - prevKey.scale) * progress;

      setTransform(`translate(${x}px, ${y}px) scale(${s})`);

      requestRef.current = requestAnimationFrame(animate);
    };

    requestRef.current = requestAnimationFrame(animate);

    return () => {
      if (requestRef.current) cancelAnimationFrame(requestRef.current);
    };
  }, [videoRef, keyframes]);

  return { transform };
}
