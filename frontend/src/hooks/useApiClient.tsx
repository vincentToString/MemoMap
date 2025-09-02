import { useMemo } from "react";
import axios from "axios";
import { toast } from "sonner";
import { useUser } from "@/app/auth/login-success/page";

export const useApiClient = () => {
  const { user, token } = useUser();

  const apiClient = useMemo(() => {
    const client = axios.create({
      baseURL: process.env.NEXT_PUBLIC_SPRING_URL,
      withCredentials: true,
    });

    client.interceptors.request.use((config) => {
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    });

    return client;
  }, [token]);

  apiClient.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      const { setToken } = useUser();
      if (
        error.response &&
        error.response.status === 401 &&
        !originalRequest._retry
      ) {
        originalRequest._retry = true;
        try {
          const refreshResponse = await axios.post(
            `${process.env.NEXT_PUBLIC_SPRING_URL}/api/auth/refresh`,
            {},
            { withCredentials: true }
          );
          if (refreshResponse.data.access) {
            setToken(refreshResponse.data.access);
            originalRequest.headers.Authorization = `Bearer ${refreshResponse.data.access}`;
            return apiClient(originalRequest);
          }
        } catch (refreshError) {
          setToken("");
          console.error("Failed to refresh token:", refreshError);
          return Promise.reject(error);
        }
      }

      return Promise.reject(error);
    }
  );

  return apiClient;
};
