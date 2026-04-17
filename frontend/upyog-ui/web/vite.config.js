// vite.config.js for micro-ui-internals/example
// Used after "yarn build" has already built all workspace packages into dist/
// Run via: yarn start (which does run-s build start:dev)
// Never run yarn start:dev directly — dist/ must exist first

import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import path from "path";
import fs from "fs";
import { fileURLToPath } from "url";

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const isProd = mode === "production";

  const proxyTarget = env.REACT_APP_PROXY_API || "https://niuatt.niua.in";
  const assetsTarget = env.REACT_APP_PROXY_ASSETS || proxyTarget;

  const apiPaths = [
    "/access/v1/actions/mdms", "/egov-mdms-service", "/egov-location",
    "/mdms-v2", "/localization", "/egov-workflow-v2", "/pgr-services",
    "/filestore", "/egov-hrms", "/user-otp", "/user", "/fsm",
    "/billing-service", "/collection-services", "/pdf-service", "/pg-service",
    "/vehicle", "/vendor", "/property-services", "/fsm-calculator",
    "/pt-calculator-v2", "/dashboard-analytics", "/echallan-services",
    "/egov-searcher", "/egov-pdf", "/egov-survey-services", "/egov-user-event",
    "/egov-document-uploader", "/egov-url-shortening", "/inbox", "/tl-services",
    "/tl-calculator", "/edcr", "/bpa-services", "/noc-services", "/ws-services",
    "/sw-services", "/ws-calculator", "/sw-calculator", "/report",
    "/service-request", "/pet-services", "/sv-services", "/ewaste-services",
    "/chb-services", "/adv-services", "/employee-dashboard",
    "/verification-service", "/asset-services", "/vendor-management",
    "/tp-services", "/pgr-ai-services", "/gis-dx-service", "/individual",
    "/bpa-calculator", "/request-service",
  ];

  const packagesRoot = path.resolve(__dirname, "../packages");

  function getAliases() {
    const aliases = {};

    function register(pkgDir) {
      const pkgJsonPath = path.join(pkgDir, "package.json");
      if (!fs.existsSync(pkgJsonPath)) return;
      const { name, main } = JSON.parse(fs.readFileSync(pkgJsonPath, "utf-8"));
      if (!name) return;
      // Point to the declared main entry, falling back to src/index.js
      const entry = main
        ? path.join(pkgDir, main)
        : path.join(pkgDir, "src", "index.js");
      if (fs.existsSync(entry)) aliases[name] = entry;
    }

    // modules/ contains subdirectories, each a separate package
    const modulesDir = path.join(packagesRoot, "modules");
    if (fs.existsSync(modulesDir)) {
      fs.readdirSync(modulesDir).forEach((pkg) => {
        const pkgDir = path.join(modulesDir, pkg);
        if (fs.statSync(pkgDir).isDirectory()) register(pkgDir);
      });
    }

    // libraries and react-components are single packages themselves
    register(path.join(packagesRoot, "libraries"));
    register(path.join(packagesRoot, "react-components"));

    return aliases;
  }

  const moduleAliases = getAliases();

  const proxy = {};
  apiPaths.forEach((path) => {
    proxy[path] = { target: proxyTarget, changeOrigin: true };
  });
  proxy["/pb-egov-assets"] = { target: assetsTarget, changeOrigin: true };

  return {
    plugins: [
      react(),
    ],

    root: __dirname,

    cacheDir: path.resolve(__dirname, "../node_modules/.vite"),

    base: isProd ? "/upyog-ui/" : "/",

    define: {
      // Keeps all process.env.REACT_APP_* working without source changes
      "process.env": JSON.stringify(env),
    },

    // No mainFields override — use Node standard resolution
    // dist/ exists because yarn build ran before this
    resolve: {
      alias: moduleAliases,
      dedupe: ["react", "react-dom"],
    },

    esbuild: {
      // All .js files treated as JSX — covers both src/ and workspace packages
      loader: "jsx",
      include: /.*\.js$/,
      exclude: /node_modules/,
    },

    server: {
      port: 3000,
      proxy,
      fs: {
        allow: [".."],
      },
      watch: {
        usePolling: true,
        interval: 300,
        include: [
          path.resolve(__dirname, "../packages/**"),
          path.resolve(__dirname, "src/**"),
        ],
        awaitWriteFinish: {
          stabilityThreshold: 100,
          pollInterval: 100,
        },
      },
      hmr: true,
    },

    build: {
      outDir: "build",
      sourcemap: false,
      rollupOptions: {
        output: {
          manualChunks: {
            vendor: ["react", "react-dom", "react-router-dom"],
          },
        },
      },
    },

    optimizeDeps: {
      include: ["react", "react-dom", "react-router-dom"],
      exclude: Object.keys(moduleAliases), // 👈 IMPORTANT
      esbuildOptions: {
        loader: { ".js": "jsx" },
      },
    },
  };
});
