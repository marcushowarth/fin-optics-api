# fin-optics-api

The REST layer for **FIN OPTICS** — a thin, stateless Spring Boot service that
wraps the [`fin-model`](https://github.com/marcushowarth/fin-model) engine. It
takes a list of financial items and inflation scenarios, runs a projection, and
returns nominal and real-terms series as JSON.

No database, no session — items in, projection out. Paired with the
[Vue front end](https://github.com/marcushowarth/fin-optics-ui).

## Stack

- **Spring Boot 4.0.6** on **JDK 25**
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
(optional) seeds the cash position at `from`; it replaces the old
`bank-account` item type.

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

## Run

Requires **JDK 25** and `fin-model` available (from GitHub Packages or
`mvn install`d locally).

```bash
mvn spring-boot:run        # starts on http://localhost:8090
```

Or build and run the jar:

```bash
mvn package
java -jar target/fin-optics-api-0.0.1-SNAPSHOT.jar
```

CORS is open in dev so the Vite front end (`localhost:5173`) can call the API
directly; the front end also proxies `/api` to `:8090`.

## License

[MIT](LICENSE)
