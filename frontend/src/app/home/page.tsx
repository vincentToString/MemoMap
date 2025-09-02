"use client";
import { useState } from "react";

export default function LocationCapture() {
  const [location, setLocation] = useState<{ lat: number; lng: number } | null>(
    null
  );

  const getLocation = () => {
    if (!navigator.geolocation) {
      alert("Geolocation not supported");
      return;
    }
    navigator.geolocation.getCurrentPosition((pos) => {
      const { latitude, longitude } = pos.coords;
      setLocation({ lat: latitude, lng: longitude });

      // send to backend
      fetch("/api/travel", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ lat: latitude, lng: longitude }),
      });
    });
  };

  return (
    <div>
      <button onClick={getLocation}>📍 Use My Location</button>
      {location && (
        <p>
          Detected: {location.lat}, {location.lng}
        </p>
      )}
    </div>
  );
}
