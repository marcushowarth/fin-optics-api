# Deploy

FIN OPTICS uses a **dual** deploy on the existing EC2, fronted by Caddy:

- **API** (this repo) → native container, bound to `127.0.0.1:8082`, serves `/api/*` + `/q/health`.
- **UI** (`fin-optics-ui`) → static build rsync'd to `/srv/optics-ui`, served by Caddy.
- Both under `optics.howarth.eu` → same-origin, no CORS.

`deploy.yml` runs on push to `main`: native build (+ native IT) → ECR → SSH deploy.

## One-time prerequisites

1. **ECR repo** — `aws ecr create-repository --repository-name fin-optics --region eu-west-2` ✅
2. **GitHub Actions secrets/variables — required on this repo** (repo-level; no Environment gating). All added 2026-06-10. **Values are not in this repo** — they live in private ops notes.

   | Name | Kind |
   |---|---|
   | `AWS_ACCESS_KEY_ID` | secret |
   | `AWS_SECRET_ACCESS_KEY` | secret |
   | `EC2_HOST` | secret |
   | `EC2_SSH_KEY` | secret |
   | `PACKAGES_TOKEN` | secret (`read:packages` — native build pulls `fin-model` from GitHub Packages) |
   | `AWS_ACCOUNT_ID` | **variable** (read as `${{ vars.AWS_ACCOUNT_ID }}`) |

   (`fin-optics-ui` needs only `EC2_HOST` + `EC2_SSH_KEY` for its rsync deploy.)
3. **On the EC2:**
   - `sudo mkdir -p /srv/optics-ui && sudo chown ec2-user /srv/optics-ui`
   - Add `deploy/Caddyfile-optics.snippet` to the Caddyfile, reload Caddy.
4. **DNS** — `optics.howarth.eu` A record → the EC2 (done 2026-06-10).

## Ports on the EC2
`8080` kanban-mcp · `8081` mediawiki-mcp · **`8082` fin-optics-api** (this).

## Build versions
`GET /api/version` → `{version, gitSha, builtAt}`. The deploy injects `APP_GIT_SHA`
(commit) and `APP_BUILT_AT` (ISO timestamp) as env; the UI footer reads this endpoint.
