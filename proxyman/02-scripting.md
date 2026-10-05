# 2.2 Scripting (Response Modification)

## Why Response Scripting?

Proxyman scripts let you **modify real API responses on-the-fly** as they pass through the proxy. Unlike Map Local (which replaces the entire response), scripting lets you surgically alter specific fields in a real response — perfect for testing new features against live data.

---

## Example 1: Transform NFL Response → NBA

**Use Case:** Your app's backend shares infrastructure with an NFL app. During early development, the endpoint returns NFL data. You need to transform it to NBA data to test the NBA GameDay UI.

**Script:** → [`scripts/nfl-to-nba-transform.js`](scripts/nfl-to-nba-transform.js)

**Proxyman Rule:** Match URL `https://api.nbagameday.com/api/v1/league/summary*` · Phase: Response

**Before:** → [`responses/before/league-summary.json`](responses/before/league-summary.json)
**After:** → [`responses/after/league-summary.json`](responses/after/league-summary.json)

**Diff highlights:**

| Field | Before (NFL) | After (NBA) |
|-------|-------------|-------------|
| `sportsLeague` | `"NFL"` | `"NBA"` |
| `leagueFullName` | `"National Football League"` | `"National Basketball Association"` |
| `season` | `"2025"` | `"2025-26"` |
| `teams[0].name` | `"Kansas City Chiefs"` | `"Los Angeles Lakers"` |
| `featuredPlayer.name` | `"Patrick Mahomes"` | `"LeBron James"` |
| `featuredPlayer.position` | `"QB"` | `"SF"` |
| `featuredPlayer.stats` | `passingYards, touchdowns, interceptions` | `pointsPerGame, reboundsPerGame, assistsPerGame` |

---

## Example 2: Inject New Fields into Existing Response

**Use Case:** The backend will soon add `playerEfficiencyRating` (PER) to the player stats endpoint. The mobile team wants to start building the UI before the backend ships the feature. Inject the field into the real response.

**Script:** → [`scripts/inject-advanced-stats.js`](scripts/inject-advanced-stats.js)

**Proxyman Rule:** Match URL `https://api.nbagameday.com/api/v1/players/*/stats*` · Phase: Response

**Before:** → [`responses/before/player-stats.json`](responses/before/player-stats.json)
**After:** → [`responses/after/player-stats.json`](responses/after/player-stats.json)

**Diff highlights:**

| Change | Description |
|--------|-------------|
| `stats.playerEfficiencyRating` | **NEW** — injected field: `43.1` |
| `advancedStats` | **NEW** — entire block injected with 12 advanced metrics |

> **💡 Why not Map Local?** Because you want the *real* base stats from the server — you're only *adding* new fields on top. The script preserves all original data.

---

## Example 3: Simulate Game State Transition

**Use Case:** You're building the live game view and need to test the UI transition from "scheduled" to "live" state. The real game is hours away.

**Script:** → [`scripts/simulate-live-game.js`](scripts/simulate-live-game.js)

**Proxyman Rule:** Match URL `https://api.nbagameday.com/api/v1/games*` · Phase: Response

**Before:** → [`responses/before/game-schedule.json`](responses/before/game-schedule.json)
**After:** → [`responses/after/game-schedule.json`](responses/after/game-schedule.json)

**Diff highlights:**

| Field | Before | After |
|-------|--------|-------|
| `status` | `"scheduled"` | `"live"` |
| `period` | _(missing)_ | `2` |
| `gameClock` | _(missing)_ | `"03:45"` |
| `homeTeam.score` | _(missing)_ | `48` |
| `awayTeam.score` | _(missing)_ | `52` |
| `homeTeam.quarterScores` | _(missing)_ | `[24, 24]` |
| `awayTeam.quarterScores` | _(missing)_ | `[28, 24]` |
| `awayTeam.bonusActive` | _(missing)_ | `true` |
| `lastPlayDescription` | _(missing)_ | `"Jayson Tatum makes 22-foot step back jump shot"` |
| `possession` | _(missing)_ | `"t-1610612747"` |

> **💡 Key takeaway:** Response scripting gives you surgical control over live API data. You can test any UI state — error states, edge cases, upcoming features — without touching the backend or losing real data.
