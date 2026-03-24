import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";
import fs from "fs";
import path from "path";

function smartResolvePlugin() {
  return {
    name: "smart-resolve",
    resolveId(id) {
      try {
        const pkgDir = path.join(process.cwd(), "node_modules", id);
        const pkgPath = path.join(pkgDir, "package.json");
        if (!fs.existsSync(pkgPath)) return null;
        const pkg = JSON.parse(fs.readFileSync(pkgPath, "utf-8"));
        if (pkg.module && fs.existsSync(path.join(pkgDir, pkg.module))) {
          return path.join(pkgDir, pkg.module);
        }
        if (pkg.main) {
          return path.join(pkgDir, pkg.main);
        }
      } catch {
        return null;
      }
    },
  };
}

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const proxyTarget = env.VITE_PROXY_URL || "https://niuatt.niua.in";

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
    proxyRoutes.map((route) => [
      route,
      { target: proxyTarget, changeOrigin: true },
    ])
  );

  return {
    plugins: [
      react(),
      smartResolvePlugin(), // keep for legacy packages
    ],

    base: "/sv-ui/",

    server: {
      port: 3000,

      fs: {
        allow: [".."],
      },

      proxy: proxyConfig,
      watch: {
        ignored: ["!**/micro-ui-internals/packages/**"],
      },
    },
    build: {
      sourcemap: true,
      outDir: "build",
      commonjsOptions: {
        transformMixedEsModules: true,
      },
    },
    envPrefix: "VITE_",
    optimizeDeps: {
      force: true,
      include: [
        "@upyog/digit-ui-module-common",
        "@upyog/digit-ui-module-core",
        "@nudmcdgnpm/digit-ui-libraries",
        "@nudmcdgnpm/upyog-ui-module-sv",
        "@upyog/digit-ui-module-engagement",
        "pdfmake",
        "pdfmake/build/pdfmake",
        "pdfmake/build/vfs_fonts",
        "jspdf",
        "jspdf-autotable",
      ],
      esbuildOptions: {
        loader: {
          ".js": "jsx",
        },
      },
    },
  };
});
