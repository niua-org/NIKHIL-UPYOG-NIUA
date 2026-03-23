import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";

export default defineConfig({
  plugins: [react()],

  esbuild: {
    loader: "jsx",
    include: /.*\.js$/,
    exclude: [],
  },

  build: {
    lib: {
      entry: path.resolve(__dirname, "src/Module.js"),
      name: "digitUiModule",
      formats: ["es", "cjs"],
      fileName: (format) =>
        format === "es" ? "index.modern.js" : "index.js",
    },

    rollupOptions: {
      external: [
        "react",
        "react-dom",
        "react-router-dom",
        "@tanstack/react-query"
      ],
    },

    sourcemap: false,
    minify: false,
    outDir: "dist",
    emptyOutDir: true,
  },
});