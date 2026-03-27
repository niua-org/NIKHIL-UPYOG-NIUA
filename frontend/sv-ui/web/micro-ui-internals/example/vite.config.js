import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import getWorkspaceAliases from "../workspace-aliases.js";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const proxyTarget = env.VITE_PROXY_API || "https://niuatt.niua.in";

  const proxyRoutes = [
    "/access/v1/actions/mdms",
    "/egov-mdms-service",
    "/egov-location",
    "/mdms-v2",
    "/localization",
    "/egov-workflow-v2",
    "/filestore",
    "/user-otp",
    "/user",
    "/billing-service",
    "/collection-services",
    "/pdf-service",
    "/pg-service",
    "/inbox",
    "/egov-hrms",
    "/egov-user-event",
    "/sv-services",
    "/employee-dashboard",
  ];

  const proxyConfig = Object.fromEntries(
    proxyRoutes.map((route) => [route, { target: proxyTarget, changeOrigin: true }])
  );

  return {
    plugins: [react({ include: /\.(jsx|js)$/ })],

    base: "/sv-ui/",

    esbuild: {
      loader: "jsx",
      include: /.*\.(js|jsx)$/,
      exclude: [],
    },

    resolve: {
      alias: getWorkspaceAliases(path.resolve(__dirname, "..")),
    },

    server: {
      port: 3000,
      fs: { allow: [".."] },
      proxy: proxyConfig,
    },

    build: {
      sourcemap: true,
      outDir: "build",
      commonjsOptions: { transformMixedEsModules: true },
    },

    envPrefix: "VITE_",

    css: {
      preprocessorOptions: {
        scss: { silenceDeprecations: ["import"] },
      },
    },

    optimizeDeps: {
      include: [
        "pdfmake",
        "pdfmake/build/pdfmake",
        "pdfmake/build/vfs_fonts",
        "jspdf",
        "jspdf-autotable",
      ],
      esbuildOptions: {
        loader: { ".js": "jsx" },
      },
    },
  };
});
