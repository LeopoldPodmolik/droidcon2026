# 🏀 NBA GameDay Mock Server

Local mock API server for the Postman & Proxyman presentation demos.

## Quick Start

### Option A: Express Server (recommended)

```bash
cd mock-server
npm install
npm start
```

Server starts on **http://localhost:3000**

For auto-reload during development:
```bash
npm run dev
```

### Option B: Prism (OpenAPI mock — zero code)

```bash
# Install Prism globally (one-time)
npm install -g @stoplight/prism-cli

# Run mock server from OpenAPI spec
cd mock-server
prism mock openapi.yaml --port 4010

# To make the mock server accessible from external devices on your network:
prism mock openapi.yaml --host 0.0.0.0 --port 4010
```

Server starts on **http://localhost:4010** (or your local IP address, e.g., **http://192.168.x.x:4010**)

> Prism generates responses from OpenAPI examples automatically. The Express server provides more dynamic behavior (filtering, error simulation, random scores).

---

## Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/auth/token` | Get JWT token |
| `GET` | `/api/v1/players/:playerId/stats` | Player statistics |
| `GET` | `/api/v1/games` | Game schedule |
| `GET` | `/api/v1/standings` | League standings |
| `GET` | `/api/v1/teams/:teamId/roster` | Team roster |
| `GET` | `/api/v1/league/summary` | League summary (NFL data for Proxyman demo) |
| `GET` | `/api/v2/nba/live-scores` | Live scores |
| `GET` | `/api/v2/nba/games/:gameId/play-by-play` | Play-by-play |

---

## Quick Test Commands

```bash
# Player stats
curl http://localhost:3000/api/v1/players/p-2544/stats?season=2025-26

# Stephen Curry stats
curl http://localhost:3000/api/v1/players/p-201939/stats

# Game schedule for a date
curl http://localhost:3000/api/v1/games?date=2026-04-02

# Games for Lakers
curl http://localhost:3000/api/v1/games?teamId=t-1610612747

# Western Conference standings
curl http://localhost:3000/api/v1/standings?conference=west

# Lakers roster
curl http://localhost:3000/api/v1/teams/t-1610612747/roster

# Live scores (v2)
curl http://localhost:3000/api/v2/nba/live-scores

# Play-by-play
curl http://localhost:3000/api/v2/nba/games/g-0022500987/play-by-play?period=3

# Auth token
curl -X POST http://localhost:3000/api/v1/auth/token \
  -H "Content-Type: application/json" \
  -d '{"clientId":"demo","clientSecret":"secret","grantType":"client_credentials"}'

# League summary (NFL → for Proxyman transform demo)
curl http://localhost:3000/api/v1/league/summary
```

---

## 🔴 Error Simulation

Add `?_error=<code>` to **any endpoint** to simulate error responses:

```bash
# 403 Forbidden
curl http://localhost:3000/api/v1/players/p-2544/stats?_error=403

# 500 Internal Server Error
curl http://localhost:3000/api/v1/games?_error=500

# 429 Rate Limited (includes Retry-After header)
curl -i http://localhost:3000/api/v1/standings?_error=429

# 401 Unauthorized
curl http://localhost:3000/api/v1/players/p-2544/stats?_error=401
```

---

## Using with Postman

1. Start the mock server: `npm start`
2. In Postman, set the environment variable:
   - `baseUrl` = `http://localhost:3000`
3. All requests from the presentation examples will work immediately
4. Use `?_error=403` etc. to test error handling

### Postman Environment (localhost)

```json
{
  "name": "NBA GameDay — Localhost",
  "values": [
    { "key": "baseUrl", "value": "http://localhost:3000" },
    { "key": "apiKey", "value": "demo-token" },
    { "key": "apiVersion", "value": "v1" },
    { "key": "environment", "value": "local" },
    { "key": "playerId", "value": "p-2544" },
    { "key": "season", "value": "2025-26" },
    { "key": "teamId", "value": "t-1610612747" },
    { "key": "gameDate", "value": "2026-04-02" },
    { "key": "conference", "value": "west" },
    { "key": "platform", "value": "android" }
  ]
}
```

---

## Using with Proxyman

### Map Local demos
The mock server already returns real responses, so Map Local demos work by:
1. Start the server
2. In Proxyman, use **Map Remote** to redirect `api.nbagameday.com` → `localhost:3000`
3. Or use **Map Local** with the JSON files in `../proxyman/mocks/` directory

### Scripting demos
1. Start the server
2. In Proxyman, point scripts at `localhost:3000`
3. The `/api/v1/league/summary` endpoint returns **NFL data** — perfect for the NFL→NBA transform script
4. The `/api/v1/players/:id/stats` returns data **without** `advancedStats` — perfect for the inject script
5. The `/api/v1/games` returns **scheduled** games — perfect for the scheduled→live script

---

## Reference Data

| Type | ID | Name |
|------|----|------|
| Player | `p-2544` | LeBron James |
| Player | `p-1628369` | Jayson Tatum |
| Player | `p-201939` | Stephen Curry |
| Team | `t-1610612747` | Los Angeles Lakers |
| Team | `t-1610612738` | Boston Celtics |
| Team | `t-1610612744` | Golden State Warriors |
| Game | `g-0022500987` | LAL vs BOS (scheduled) |
| Game | `g-0022500988` | GSW vs MIA (scheduled) |
| Game | `g-0022500989` | BOS vs GSW (final) |
