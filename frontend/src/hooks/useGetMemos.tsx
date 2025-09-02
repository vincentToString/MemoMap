import { useQuery } from "@tanstack/react-query";
import { useApiClient } from "./useApiClient";

export const useGetMemos = () => {
  const apiClient = useApiClient();
  return useQuery({
    queryKey: ["myMemos"],
    queryFn: async () => {
      const response = await apiClient.get("/api/memos");
      console.log("📥 API response", response);

      return response.data;
    },

    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};
