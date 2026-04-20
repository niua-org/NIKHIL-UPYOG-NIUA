import { useMemo } from "react";
import { useLocation } from "react-router-dom";

function collectRouteKeys(cfg) {
  if (!cfg) return [];
  if (cfg.routes && typeof cfg.routes === "object" && !Array.isArray(cfg.routes)) {
    return Object.keys(cfg.routes);
  }
  if (Array.isArray(cfg)) {
    return cfg.map((r) => r?.route).filter(Boolean);
  }
  if (Array.isArray(cfg.routes)) {
    return cfg.routes.map((r) => r?.route).filter(Boolean);
  }
  return [];
}

/**
 * Wizard flows: derive a v5-style `match.path` for `${path}/step` navigation.
 * When the last URL segment is a known step (or terminal screen), strips it to get the wizard base.
 */
export default function useWizardPath(config) {
  const { pathname } = useLocation();
  return useMemo(() => {
    const routeKeys = collectRouteKeys(config);
    const terminal = [...routeKeys, "response", "check", "acknowledgement"];
    const indexRoute = config?.indexRoute ?? config?.configs?.indexRoute;
    if (indexRoute) terminal.push(indexRoute);
    const last = pathname.split("/").filter(Boolean).pop() || "";
    const path = terminal.includes(last) ? pathname.replace(/\/[^/]+$/, "") : pathname;
    return { path, url: path, isExact: false, params: {} };
  }, [pathname, config]);
}
