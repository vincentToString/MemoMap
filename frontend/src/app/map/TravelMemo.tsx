"use client";
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
export default function TravelMemo({
  selectedMemos,
  setSelectedMemo,
  selectedMemo,
}: {
  selectedMemos: TravelMemo[];
  selectedMemo: TravelMemo | null;
  setSelectedMemo: (memo: TravelMemo) => void;
}) {
  const handleMemoSelect = (memo: TravelMemo) => {
    setSelectedMemo(memo);
  };

  return (
    <div className="overflow-y-auto max-h-96 md:max-h-[calc(100vh-12rem)] scrollbar-thin scrollbar-thumb-white/30 scrollbar-track-transparent">
      <div className="p-2 md:p-4 space-y-3">
        {selectedMemos.map((memo, index) => (
          <div
            key={memo.id}
            onClick={() => handleMemoSelect(memo)}
            className={`group relative p-4 rounded-xl cursor-pointer transition-all duration-300 transform hover:scale-[1.02] ${
              selectedMemo?.id === memo.id
                ? "bg-white/20 border-2 border-white/40 shadow-lg"
                : "bg-white/5 border border-white/10 hover:bg-white/15 hover:border-white/30"
            }`}
            style={{
              animationDelay: `${index * 0.1}s`,
              animation: "fadeInUp 0.6s ease-out forwards",
            }}
          >
            {/* Selection indicator */}
            <div
              className={`absolute left-0 top-0 bottom-0 w-1 rounded-r-full transition-all duration-300 ${
                selectedMemo?.id === memo.id
                  ? "bg-blue-400 opacity-100"
                  : "bg-white/30 opacity-0 group-hover:opacity-50"
              }`}
            />

            <div className="flex items-start space-x-4">
              {/* Location pin icon */}
              <div
                className={`flex-shrink-0 w-8 h-8 rounded-full flex items-center justify-center transition-all duration-300 ${
                  selectedMemo?.id === memo.id
                    ? "bg-blue-400/20 text-blue-200"
                    : "bg-white/10 text-white/60 group-hover:bg-white/20 group-hover:text-white/80"
                }`}
              >
                <span className="text-sm">📍</span>
              </div>

              <div className="flex-1 min-w-0">
                <h3
                  className={`font-semibold truncate transition-colors duration-300 ${
                    selectedMemo?.id === memo.id
                      ? "text-white"
                      : "text-white/90 group-hover:text-white"
                  }`}
                >
                  {memo.title}
                </h3>

                <p
                  className={`text-sm mt-1 transition-colors duration-300 ${
                    selectedMemo?.id === memo.id
                      ? "text-white/80"
                      : "text-white/60 group-hover:text-white/75"
                  }`}
                >
                  {memo.location[0].city}
                </p>

                <div className="flex items-center justify-between mt-2">
                  <p
                    className={`text-xs transition-colors duration-300 ${
                      selectedMemo?.id === memo.id
                        ? "text-white/70"
                        : "text-white/50 group-hover:text-white/65"
                    }`}
                  >
                    {new Date(memo.date).toLocaleDateString("en-US", {
                      month: "short",
                      day: "numeric",
                      year: "numeric",
                    })}
                  </p>

                  {selectedMemo?.id === memo.id && (
                    <div className="flex items-center text-xs text-blue-300">
                      <span className="mr-1">✈️</span>
                      <span>Active</span>
                    </div>
                  )}
                </div>
                {/* 
                <p
                  className={`text-sm mt-2 line-clamp-2 transition-colors duration-300 ${
                    selectedMemo?.id === memo.id
                      ? "text-white/75"
                      : "text-white/60 group-hover:text-white/70"
                  }`}
                >
                  {memo.preview}
                </p> */}

                {/* Mobile-only expanded info */}
                <div
                  className={`md:hidden mt-3 pt-3 border-t border-white/10 transition-all duration-300 ${
                    selectedMemo?.id === memo.id
                      ? "opacity-100 max-h-20"
                      : "opacity-0 max-h-0 overflow-hidden"
                  }`}
                >
                  <div className="flex items-center text-xs text-white/60 space-x-4">
                    <span className="flex items-center">
                      🌍 {memo.location[0].latitude.toFixed(2)},{" "}
                      {memo.location[0].longitude.toFixed(2)}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>

      <style jsx>{`
        @keyframes fadeInUp {
          from {
            opacity: 0;
            transform: translateY(20px);
          }
          to {
            opacity: 1;
            transform: translateY(0);
          }
        }

        .scrollbar-thin {
          scrollbar-width: thin;
        }

        .scrollbar-thumb-white\/30::-webkit-scrollbar {
          width: 6px;
        }

        .scrollbar-thumb-white\/30::-webkit-scrollbar-track {
          background: transparent;
        }

        .scrollbar-thumb-white\/30::-webkit-scrollbar-thumb {
          background-color: rgba(255, 255, 255, 0.3);
          border-radius: 3px;
        }

        .scrollbar-thumb-white\/30::-webkit-scrollbar-thumb:hover {
          background-color: rgba(255, 255, 255, 0.5);
        }
      `}</style>
    </div>
  );
}
