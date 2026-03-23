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
      entry: path.resolve(__dirname, "src/index.js"),
      name: "upyogUiReactComponentsLts",
      formats: ["es", "cjs"],
      fileName: (format) =>
        format === "es" ? "index.modern.js" : "index.js",
    },

    rollupOptions: {
      external: [
        "react",
        "react-dom",
        "react-router-dom",
        "react-redux",
        "redux",
        "@tanstack/react-query"
      ],
    },

    sourcemap: false,
    minify: true,
    outDir: "dist",
    emptyOutDir: true,
  },
});