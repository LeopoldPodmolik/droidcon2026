# 1.1 Variables

## Why Variables?

Variables let you **parameterize** requests so you can reuse the same request with different data every day — no need to manually edit URLs or parameters.

---

## Example 1: Fetching Player Stats

**Use Case:** Every morning you check stats for a different player and season. Instead of editing the URL each time, use variables.

**Request:**

```
GET {{baseUrl}}/api/v1/players/{{playerId}}/stats?season={{season}}
```

**Variable Definitions:**

| Variable | Initial Value | Current Value | Scope |
|----------|--------------|---------------|-------|
| `baseUrl` | `https://api.nbagameday.com` | `https://api.nbagameday.com` | Collection |
| `playerId` | `p-2544` | `p-2544` | Collection |
| `season` | `2025-26` | `2025-26` | Collection |

**Headers:**

```
Authorization: Bearer {{apiKey}}
Content-Type: application/json
```

**Screenshot-ready layout:**
- Method badge: `GET` (green)
- URL bar: `{{baseUrl}}/api/v1/players/{{playerId}}/stats?season={{season}}`
- Variables tab open showing the three variables above
- Send button → Response panel below

**Sample Response:** → [`responses/player-stats.json`](responses/player-stats.json)

> **💡 Tip:** Change `playerId` to `p-201939` and you instantly get Stephen Curry's stats — no URL editing needed!

---

## Example 2: Fetching Game Schedule

**Use Case:** Check the schedule for a specific team on a specific date. Both `date` and `teamId` change daily.

**Request:**

```
GET {{baseUrl}}/api/v1/games?date={{gameDate}}&teamId={{teamId}}
```

**Variable Definitions:**

| Variable | Initial Value | Current Value | Scope |
|----------|--------------|---------------|-------|
| `gameDate` | `2026-04-02` | `2026-04-02` | Collection |
| `teamId` | `t-1610612747` | `t-1610612747` | Collection |

**Screenshot-ready layout:**
- Method badge: `GET` (green)
- URL bar with highlighted `{{gameDate}}` and `{{teamId}}` in orange
- Params tab showing the key-value pairs resolved

**Sample Response:** → [`responses/game-schedule.json`](responses/game-schedule.json)

---

## Example 3: Fetching League Standings

**Use Case:** Display current standings filtered by conference. The `conference` and `season` are parameterized.

**Request:**

```
GET {{baseUrl}}/api/v1/standings?conference={{conference}}&season={{season}}
```

**Variable Definitions:**

| Variable | Initial Value | Current Value | Scope |
|----------|--------------|---------------|-------|
| `conference` | `west` | `west` | Collection |
| `season` | `2025-26` | `2025-26` | Collection |

**Sample Response:** → [`responses/standings.json`](responses/standings.json)

> **💡 Key takeaway:** With variables you define data once and reuse it across dozens of requests. Change one variable — all requests update automatically.
