import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import path from "node:path";

export default defineConfig(({ mode }) => {
  const envDir = path.resolve(process.cwd(), "..");
  const env = loadEnv(mode, envDir, "");
  const backendUrl = env.VITE_BACKEND_URL || "http://localhost:8080";

  return {
    envDir,
    plugins: [react()],
    define: {
      global: "globalThis",
    },
    server: {
      proxy: {
        "/graphql": backendUrl,
        "/ws": {
          target: backendUrl,
          ws: true,
        },
      },
    },
    build: {
      outDir: "../src/main/resources/static",
      emptyOutDir: true,
    },
  };
});
