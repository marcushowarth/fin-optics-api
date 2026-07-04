# fin-optics-api

The REST layer for **FIN OPTICS** — a thin, stateless Quarkus-native service
that wraps the [`fin-model`](https://github.com/marcushowarth/fin-model) engine. It
takes a list of financial items and inflation scenarios, runs a projection, and
returns nominal and real-terms series as JSON.

No database, no session — items in, projection out. Paired with the
[Vue front end](https://github.com/marcushowarth/fin-optics-ui).

## Stack

- **Quarkus 3.33.1 LTS** on **JDK 25**, compiled to a **native image** (GraalVM/Mandrel, ~20 MiB)
- `fin-model-planning` (consumed from GitHub Packages) for the calculation
- Jackson polymorphic deserialisation over a sealed `FinancialItemDto`, mapped to
  the engine's domain via an exhaustive pattern-matching `switch`

## Endpoint

### `POST /api/projection`

**Request**

```json
{
  "from": "2026-01",
  "to":   "2055-12",
  "base": "2026-01",
  "startingCash": 5000,
  "items": [
    { "type": "income", "name": "Salary", "start": "2026-01",
      "monthlyAmount": 4000, "annualGrowthRate": 0.03 },
    { "type": "expenditure", "name": "Rent", "start": "2026-01",
      "monthlyAmount": 1500, "annualGrowthRate": 0.02 },
    { "type": "event", "name": "Inheritance", "date": "2030-06", "amount": 50000 }
  ],
  "scenarios": [
    { "name": "low",  "annualRate": 0.02 },
    { "name": "base", "annualRate": 0.035 },
    { "name": "high", "annualRate": 0.06 }
  ]
}
```

`type` is one of `asset`, `investment`, `income`, `expenditure`, `liability`,
`event`; each carries its own fields. `income`/`expenditure` with no `end` run
to the horizon; `event` is a one-off dated cash movement with a signed `amount`
(positive in, negative out). All months are `"YYYY-MM"` strings. `startingCash`
(optional) seeds the cash position at `from`.

**Response**

```json
{
  "nominal": {
    "netWorth":      { "2026-01": 5000, "...": "..." },
    "cashPosition":  { "2026-01": 5000, "...": "..." },
    "itemPositions": { "Starting cash": { "2026-01": 5000 } },
    "warnings":      [ { "month": "2031-04", "cashPosition": -1200 } ]
  },
  "realTerms": {
    "base": "2026-01",
    "netWorth":     { "low": { "2026-01": 5000 }, "base": { }, "high": { } },
    "cashPosition": { "low": { }, "base": { }, "high": { } },
    "itemPositions": { "low": { } }
  }
}
```

`warnings` lists every month the cash position is negative — the projection still
runs to completion, it just flags the breaches.

### `GET /api/version`

Returns `{ "version", "gitSha", "builtAt" }` — the UI footer reads this.

### `GET /q/health`

Quarkus's built-in liveness/readiness check.

## Run

Requires **JDK 25** and `fin-model` available (from GitHub Packages or
`mvn install`d locally).

```bash
mvn quarkus:dev -Dquarkus.http.port=8090    # dev mode, live reload, http://localhost:8090
```

Native build:

```bash
mvn package -Dnative -Dquarkus.native.container-build=true \
  -Dquarkus.native.builder-image=quay.io/quarkus/ubi9-quarkus-mandrel-builder-image:jdk-25
./target/fin-optics-api-*-runner
```

CORS is open in dev so the Vite front end (`localhost:5173`) can call the API
directly; the front end also proxies `/api` to `:8090`.

## Deploy

GitHub Actions builds the native image, pushes it to ECR, and restarts the
container on EC2 (`127.0.0.1:8082`). Caddy fronts it at
[optics.howarth.eu](https://optics.howarth.eu), serving the UI as static files
and reverse-proxying `/api` to this service — same origin, no CORS needed in
production.

## License

[MIT](LICENSE)
