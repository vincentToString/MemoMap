"use client";
import mapboxgl from "mapbox-gl";
import { useEffect, useRef, useState } from "react";
import TravelMemo from "./TravelMemo";
import MemoEditor from "./MemoEditor";
import { TravelMemoFormSchema } from "@/schemas/FormSchemas";
import z from "zod";
import { useCreateMemo } from "@/hooks/useCreateMemo";
import { useGetMemos } from "@/hooks/useGetMemos";

mapboxgl.accessToken = process.env.NEXT_PUBLIC_MAPBOX_TOKEN!;

interface Location {
  placeName: string;
  city: string;
  region: string;
  country: string;
  latitude: number;
  longitude: number;
}
interface Tag {
  id: number;
  name: string;
  description: string;
}
interface TravelMemo {
  id: number;
  title: string;
  content: string;
  imageUrl: string;
  location: Location[];
  historicalWeather: string;
  rating: number;
  moodIcon: string;
  tag: Tag[];
  date: string;
  createdAt: string;
}

// const sampleMemos: TravelMemo[] = [
//   {
//     id: "1",
//     title: "Paris Adventure",
//     location: "Paris, France",
//     date: "2024-08-15",
//     preview: "Amazing day exploring the Eiffel Tower and Louvre...",
//     coordinates: { lat: 48.8566, lng: 2.3522 },
//   },
//   {
//     id: "2",
//     title: "Tokyo Streets",
//     location: "Tokyo, Japan",
//     date: "2024-07-20",
//     preview: "Incredible sushi and cherry blossoms in Shibuya...",
//     coordinates: { lat: 35.6762, lng: 139.6503 },
//   },
//   {
//     id: "3",
//     title: "New York Vibes",
//     location: "New York, USA",
//     date: "2024-06-10",
//     preview: "Central Park was beautiful, Broadway shows amazing...",
//     coordinates: { lat: 40.7128, lng: -74.006 },
//   },
// ];

export default function GlobalMap() {
  const { data: memos, isPending, isError, error } = useGetMemos();

  const mapContainer = useRef<HTMLDivElement | null>(null);
  const mapRef = useRef<mapboxgl.Map | null>(null);
  const [selectedMemo, setSelectedMemo] = useState<TravelMemo | null>(null);
  const [activeView, setActiveView] = useState<"list" | "editor">("list");
  useEffect(() => {
    if (mapRef.current || !mapContainer.current) return; // prevent double init
    const map = new mapboxgl.Map({
      container: mapContainer.current!,
      style: "mapbox://styles/mapbox/satellite-streets-v12", // or any style
      center: [0, 0],
      zoom: 1,
      projection: "globe",
    });

    mapRef.current = map;
  }, []);

  useEffect(() => {
    if (!mapRef.current || !selectedMemo) return;
    const { latitude, longitude } = selectedMemo.location[0];
    mapRef.current.flyTo({
      center: [longitude, latitude],
      zoom: 6,
      speed: 1.6,
      curve: 1.42,
    });
  }, [selectedMemo]);

  type TravelMemoFormValues = z.infer<typeof TravelMemoFormSchema>;

  const { uploadAsync, isLoadingNew } = useCreateMemo();

  const handleMemoSubmit = async (newMemo: TravelMemoFormValues) => {
    await uploadAsync(newMemo).then(() => {
      setActiveView("list");
    });
  };

  const handleEditorCancel = () => {
    setActiveView("list");
  };

  if (isPending) {
    return <p className="text-white">Loading memos...</p>;
  }

  if (isError) {
    return (
      <p className="text-red-400">
        Failed to load memos:{" "}
        {error instanceof Error ? error.message : "unknown error"}
      </p>
    );
  }

  return (
    <div className="relative h-screen w-screen overflow-hidden">
      <div ref={mapContainer} className="absolute inset-0 z-0" />

      {/* Desktop Layout */}
      <div className="hidden md:block absolute z-10 top-6 left-6 w-96 max-h-[calc(100vh-3rem)]">
        <div className="bg-white/10 backdrop-blur-lg rounded-2xl border border-white/20 shadow-2xl overflow-hidden">
          <div className="p-6 border-b border-white/10">
            <div className="flex items-center justify-between">
              <div>
                <h1 className="text-2xl font-bold text-white">
                  {activeView === "list" ? "Travel Journal" : "New Journey"}
                </h1>
                <p className="text-white/70 mt-1">
                  {activeView === "list"
                    ? "Your memories around the world"
                    : "Create a new travel memory"}
                </p>
              </div>
              {activeView === "list" && (
                <button
                  onClick={() => setActiveView("editor")}
                  className="p-2 bg-blue-500/20 hover:bg-blue-500/40 border border-blue-400/30 text-blue-200 rounded-xl transition-all duration-300"
                  title="Add new journey"
                >
                  ✏️
                </button>
              )}
            </div>
          </div>

          {activeView === "list" ? (
            <>
              <TravelMemo
                selectedMemos={memos}
                selectedMemo={selectedMemo}
                setSelectedMemo={setSelectedMemo}
              />
              <div className="p-4 border-t border-white/10">
                <button
                  onClick={() => setActiveView("editor")}
                  className="w-full py-3 px-4 bg-blue-500/20 hover:bg-blue-500/40 border border-blue-400/30 text-blue-200 font-medium rounded-xl transition-all duration-300 flex items-center justify-center space-x-2"
                >
                  <span>✏️</span>
                  <span>Add New Journey</span>
                </button>
              </div>
            </>
          ) : (
            <MemoEditor
              onSubmit={handleMemoSubmit}
              onCancel={handleEditorCancel}
            />
          )}
        </div>
      </div>

      {/* Mobile Layout */}
      <div className="block md:hidden absolute z-10 bottom-0 left-0 right-0">
        <div className="bg-white/10 backdrop-blur-lg rounded-t-2xl border-t border-white/20 shadow-2xl max-h-[70vh] overflow-hidden">
          <div className="flex justify-center py-2">
            <div className="w-10 h-1 bg-white/30 rounded-full"></div>
          </div>
          <div className="flex items-center justify-between px-4 pb-2">
            <h1 className="text-lg font-bold text-white">
              {activeView === "list" ? "Travel Journal" : "New Journey"}
            </h1>
            {activeView === "list" && (
              <button
                onClick={() => setActiveView("editor")}
                className="p-2 bg-blue-500/20 hover:bg-blue-500/40 border border-blue-400/30 text-blue-200 rounded-lg transition-all duration-300"
              >
                ✏️
              </button>
            )}
          </div>

          {activeView === "list" ? (
            <>
              <TravelMemo
                selectedMemos={memos}
                selectedMemo={selectedMemo}
                setSelectedMemo={setSelectedMemo}
              />
              <div className="p-3 border-t border-white/10">
                <button
                  onClick={() => setActiveView("editor")}
                  className="w-full py-3 px-4 bg-blue-500/20 hover:bg-blue-500/40 border border-blue-400/30 text-blue-200 font-medium rounded-xl transition-all duration-300 flex items-center justify-center space-x-2"
                >
                  <span>✏️</span>
                  <span>Add New Journey</span>
                </button>
              </div>
            </>
          ) : (
            <MemoEditor
              onSubmit={handleMemoSubmit}
              onCancel={handleEditorCancel}
            />
          )}
        </div>
      </div>

      {/* Selected Memo Info Card - Desktop Only */}
      {selectedMemo && (
        <div className="hidden md:block absolute top-6 right-6 bg-white/10 backdrop-blur-lg rounded-2xl border border-white/20 shadow-2xl p-6 max-w-sm z-10">
          <h2 className="font-bold text-lg text-white">{selectedMemo.title}</h2>
          <p className="text-white/80">{selectedMemo.location[0].placeName}</p>
          <p className="text-sm text-white/60 mt-1">
            {new Date(selectedMemo.date).toLocaleDateString()}
          </p>
          {/* <p className="text-sm text-white/80 mt-3">{selectedMemo.preview}</p> */}

          <div className="mt-4 p-3 bg-black/20 rounded-lg text-xs text-white/60">
            <div className="flex items-center space-x-2">
              <span>📍</span>
              <span>
                {selectedMemo.location[0].latitude.toFixed(4)},{" "}
                {selectedMemo.location[0].longitude.toFixed(4)}
              </span>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
