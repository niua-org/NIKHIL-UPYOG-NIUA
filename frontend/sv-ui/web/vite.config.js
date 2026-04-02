import { defineConfig, loadEnv } from "vite";
import react from "@vitejs/plugin-react";

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const proxyTarget = env.VITE_PROXY_API;

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
      react({ include: /\.(jsx|js)$/ }),
    ],

    base: "/sv-ui/",

    esbuild: {
      loader: "jsx",
      include: /.*\.(js|jsx)$/,
      exclude: [],
      logOverride: {
        "duplicate-case": "silent",
      },
    },

    server: {
      port: 3000,
      fs: { allow: [".."] },
      proxy: proxyConfig,
    },

    build: {
      sourcemap: false,
      outDir: "build",
      commonjsOptions: {
        transformMixedEsModules: true,
      },
      rollupOptions: {
        output: {
          manualChunks(id) {
            if (id.includes("node_modules/react-dom") || id.includes("node_modules/react/") || id.includes("node_modules/scheduler")) {
              return "vendor-react";
            }
            if (id.includes("node_modules/react-router-dom") || id.includes("node_modules/@remix-run") || id.includes("node_modules/react-router/")) {
              return "vendor-router";
            }
            if (id.includes("node_modules/pdfmake") || id.includes("node_modules/jspdf")) {
              return "vendor-pdf";
            }
            if (id.includes("node_modules/xlsx")) {
              return "vendor-xlsx";
            }
            if (id.includes("node_modules/@tanstack")) {
              return "vendor-query";
            }
            if (id.includes("node_modules/i18next") || id.includes("node_modules/react-i18next")) {
              return "vendor-i18n";
            }
            if (id.includes("node_modules/redux") || id.includes("node_modules/react-redux")) {
              return "vendor-redux";
            }
            if (id.includes("node_modules/")) {
              return "vendor-misc";
            }
            if (id.includes("digit-ui-libraries") || id.includes("packages/libraries")) {
              return "internal-libraries";
            }
            if (id.includes("upyog-ui-react-components") || id.includes("packages/react-components")) {
              return "internal-components";
            }
            if (id.includes("digit-ui-module-common") || id.includes("packages/modules/common")) {
              return "internal-common";
            }
            if (id.includes("digit-ui-module-core") || id.includes("packages/modules/core")) {
              return "internal-core";
            }
            if (id.includes("upyog-ui-module-sv") || id.includes("packages/modules/sv")) {
              return "internal-sv";
            }
            if (id.includes("digit-ui-module-engagement") || id.includes("packages/modules/engagement")) {
              return "internal-engagement";
            }
          },
        },
      },
    },

    envPrefix: "VITE_",

    optimizeDeps: {
      include: [
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
