import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  async rewrites() {
    return [
      {
        source: "/oauth2/:path*",
        destination: "http://localhost:8080/api/oauth2/:path*",
      },
    ];
  },
};

export default nextConfig;
