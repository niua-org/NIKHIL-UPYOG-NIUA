import fs from "fs";
import path from "path";

/**
 * Generates alias mappings for local packages.
 * This function scans the `packages/` and `packages/modules/` directories
 * to find packages with a `dev:<name>` script in the root `package.json`.
 * It maps each package name to its source entry point (from `source` or `style` fields),
 * enabling tools like Vite to resolve local packages directly for HMR during development.
 *
 * @param {string} rootDir - The root directory of the monorepo.
 * @returns {Object} aliases - An object containing alias mappings.
 */

export default function getWorkspaceAliases(rootDir) {
  const rootPkg = JSON.parse(fs.readFileSync(path.join(rootDir, "package.json"), "utf-8"));
  const packagesDir = path.join(rootDir, "packages");
  const aliases = {};

  const localNames = Object.keys(rootPkg.scripts || {})
    .filter((k) => k.startsWith("dev:") && k !== "dev:example");

  for (const dir of [packagesDir, path.join(packagesDir, "modules")]) {
    if (!fs.existsSync(dir)) continue;
    for (const name of fs.readdirSync(dir)) {
      const pkgPath = path.join(dir, name, "package.json");
      if (!fs.existsSync(pkgPath)) continue;
      const pkg = JSON.parse(fs.readFileSync(pkgPath, "utf-8"));
      const entry = pkg.source || pkg.style;
      if (pkg.name && entry && localNames.includes(`dev:${name}`)) {
        aliases[pkg.name] = path.join(dir, name, entry);
      }
    }
  }

  return aliases;
}
