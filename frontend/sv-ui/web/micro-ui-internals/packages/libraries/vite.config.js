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
      name: "digitUiLibraries",
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
        "react-i18next",
        "i18next",
        "@tanstack/react-query",
      ],
      output: {
        manualChunks: undefined,
        inlineDynamicImports: true,
      },
    },

    outDir: "dist",
    sourcemap: false,
    minify: true,
    emptyOutDir: true,
  },
});