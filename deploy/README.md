# Deploy

FIN OPTICS uses a **dual** deploy on a shared Hetzner host, fronted by Caddy:

- **API** (this repo) → native container, bound to `127.0.0.1:8082`, serves `/api/*` + `/q/health`.
- **UI** (`fin-optics-ui`) → static build rsync'd to `/srv/optics-ui`, served by Caddy.
- Both under `optics.howarth.eu` → same-origin, no CORS.

`deploy.yml` runs on push to `main`: native build (+ native IT) → GHCR → SSH deploy.

**History:** originally deployed to AWS (EC2 + ECR). Migrated to Hetzner Cloud on 2026-07-24 — same host already running KanbanMCP, MediaWikiMCP, Vaultwarden, and `real-retro-api`.

## One-time prerequisites

1. **GHCR package** — created automatically on first push; needs an explicit `permissions: contents: read / packages: write` block in `deploy.yml` (the default `GITHUB_TOKEN` can't create a new package without it) ✅
2. **GitHub Actions secrets — required on this repo** (repo-level; no Environment gating). **Values are not in this repo** — they live in private ops notes.

   | Name | Kind |
   |---|---|
   | `HETZNER_HOST` | secret |
   | `HETZNER_SSH_KEY` | secret |
   | `PACKAGES_TOKEN` | secret (`read:packages` — native build pulls `fin-model` from GitHub Packages) |

   (`fin-optics-ui` needs only `HETZNER_HOST` + `HETZNER_SSH_KEY` for its rsync deploy.)
3. **On the host:**
   - `sudo mkdir -p /srv/optics-ui && sudo chown deploy /srv/optics-ui`
   - Add `deploy/Caddyfile-optics.snippet` to the Caddyfile, reload Caddy.
4. **DNS** — `optics.howarth.eu` A record → the host (migrated 2026-07-24).

## Ports on the host
`8080` kanban-mcp · `8081` mediawiki-mcp · `8083` vaultwarden · `8084` real-retro-api · **`8082` fin-optics-api** (this).

## Build versions
`GET /api/version` → `{version, gitSha, builtAt}`. The deploy injects `APP_GIT_SHA`
(commit) and `APP_BUILT_AT` (ISO timestamp) as env; the UI footer reads this endpoint.
