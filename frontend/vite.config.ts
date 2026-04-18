import { reactRouter } from "@react-router/dev/vite";
import tsconfigPaths from "vite-tsconfig-paths";
import { defineConfig } from "vite";

export default defineConfig(({ mode }) => ({
  base: mode === "production" ? "/new/" : "/",
  plugins: [reactRouter(), tsconfigPaths()],
  server: {
    proxy: {
      "/api": {
        target: "https://localhost:8443/api",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
        secure: false
      },
    },
  },
}));