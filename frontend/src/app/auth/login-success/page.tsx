"use client";
import { useMutation } from "@tanstack/react-query";
import React, { createContext, useContext, useState, ReactNode } from "react";
import axios from "axios";
import { toast } from "sonner";
import { useRouter } from "next/navigation";

interface User {
  displayName: string;
  email: string;
  joinedAt: string;
}

interface UserContextType {
  user: User | null;
  token: string | null;
  setUser: (user: User) => void;
  setToken: (token: string) => void;
  clearUser: () => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export function UserProvider({ children }: { children: ReactNode }) {
  const [user, setUserState] = useState<User | null>(null);
  const [token, setTokenState] = useState<string | null>(null);

  const setUser = (user: User) => {
    setUserState(user);
  };

  const setToken = (token: string) => {
    setTokenState(token);
  };

  const clearUser = () => {
    setUserState(null);
    setTokenState(null);
  };

  return (
    <UserContext.Provider
      value={{
        user,
        token,
        setUser,
        setToken,
        clearUser,
      }}
    >
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error("useUser must be used within a UserProvider");
  }
  return context;
}

function useOAuthInit() {
  const { setToken, setUser } = useUser();
  const initMutation = useMutation({
    mutationFn: async () => {
      const response = await axios.post(
        `${process.env.NEXT_PUBLIC_SPRING_URL}/api/auth/refresh`,
        {},
        { withCredentials: true }
      );
      return response.data;
    },
    onSuccess: (data) => {
      setToken(data.access);
      setUser({
        displayName: data.displayName,
        email: data.email,
        joinedAt: data.joinedAt,
      });

      const currentHour = new Date().getHours();
      let greeting = "";
      if (currentHour < 12) {
        greeting = "Good Morning";
      } else if (currentHour < 18) {
        greeting = "Good Afternoon";
      } else {
        greeting = "Good Evening";
      }
      toast.success(`${greeting} ${data.displayName}! Welcome !`);
    },
    onError: (error: Error) => {
      console.error(error);
      toast.error("Login Faile.Please check your email and password.");
    },
  });
  return {
    initAsync: initMutation.mutateAsync,
    isLoading: initMutation.isPending,
    isError: initMutation.isError,
    isSuccess: initMutation.isSuccess,
  };
}

const Page = () => {
  const { initAsync, isLoading } = useOAuthInit();
  const router = useRouter();
  React.useEffect(() => {
    const init = async () => {
      try {
        await initAsync();
        router.push("/");
      } catch (error) {
        console.error(error);
      }
    };
    init();
  }, [initAsync, router]);
  return <>{isLoading && <div>loading</div>}</>;
};

export default Page;
