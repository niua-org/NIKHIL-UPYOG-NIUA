import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";

export default defineConfig({
  plugins: [react({ include: /\.(jsx|js)$/ })],
  esbuild: {
    loader: "jsx",
    include: /.*\.js$/,
    exclude: [],
    logOverride: { "duplicate-object-key": "silent", "duplicate-case": "silent" },
  },
  build: {
    lib: {
      entry: path.resolve(__dirname, "src/Module.js"),
      name: "digitUiModule",
      formats: ["es", "cjs"],
      fileName: (format) => format === "es" ? "index.modern.js" : "index.js",
    },
    rollupOptions: {
      external: ["react", "react-dom", "react-router-dom"],
      output: {
        globals: {
          react: "React",
          "react-dom": "ReactDOM",
          "react-router-dom": "ReactRouterDOM",
        },
      },
    },
    sourcemap: false,
    minify: false,
    outDir: "dist",
  },
});
