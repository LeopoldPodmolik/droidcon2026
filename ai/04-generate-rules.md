# 3.4 Generate Rules and Scripts from a Prompt

Natural language goes in. A Proxyman rule comes back. You can read the script and you run it. Same idea on the Postman side: the agent scaffolds the collection and the environment instead of you clicking through it.

---

## Example 1: Rewrite one field — NFL to NBA

**Use case:** `/api/v1/league/summary` still returns NFL data. You want `sportsLeague` set to `NBA` without replacing the rest of the body. Same rule as [Proxyman scripting, example 1](../proxyman/02-scripting.md).

**Prompt:** [`prompts/rewrite-sports-league.md`](prompts/rewrite-sports-league.md)

> Create a Proxyman rule to set sportsLeague to NBA in the /league/summary response.

**Agent calls** `create_scripting_rule` with:

| Argument | Value |
|----------|-------|
| `url` | `*/api/v1/league/summary*` |
| `method` | `GET` |
| `enable_response` | `true` |
| `script_content` | [`examples/generated-script.js`](examples/generated-script.js) |

The script is the short version from the slide. The fuller NFL → NBA transform (teams, players, stats) is already in [`proxyman/scripts/nfl-to-nba-transform.js`](../proxyman/scripts/nfl-to-nba-transform.js). Ask for that only when you want the whole payload rewritten.

**Before / after bodies:** [`proxyman/responses/before/league-summary.json`](../proxyman/responses/before/league-summary.json) → [`proxyman/responses/after/league-summary.json`](../proxyman/responses/after/league-summary.json)

---

## Example 2: Demo 4 — Map Local for a route that does not exist

**Use case:** `/api/v2/nba/live-scores` 500s because the backend has not built it. Mock it the same way as [Map Local, example 1](../proxyman/01-map-local.md).

**Prompt:** [`prompts/map-local-live-scores.md`](prompts/map-local-live-scores.md)

> Create a Proxyman Map Local rule for GET */api/v2/nba/live-scores*. Return 200 with the live scores mock.

**What you should see in Proxyman after you accept the draft:**

| Setting | Value |
|---------|-------|
| **URL** | `*/api/v2/nba/live-scores*` |
| **Method** | `GET` |
| **Match by** | Wildcard |
| **Map to** | Local file [`proxyman/mocks/live-scores.json`](../proxyman/mocks/live-scores.json) |
| **HTTP Status** | `200 OK` |
| **Response Headers** | `Content-Type: application/json` |

The tool call the agent makes is [`examples/generated-map-local.json`](examples/generated-map-local.json). `create_map_local` sends the body inline. In the app you can point the same rule at the file instead.

A first draft that matches `*/api/v2/nba/*` is in [`examples/draft-too-wide.json`](examples/draft-too-wide.json). That one also swallows play-by-play. Reject it. The review pass is [3.5](05-review.md).

---

## Example 3: Scaffold the Postman side

**Use case:** A teammate needs the Production environment and a live-scores request without importing by hand.

**Prompt:**

> In the NBA GameDay workspace, create a Production environment with baseUrl https://api.nbagameday.com and a secret apiKey, then add a GET live-scores request that uses {{baseUrl}}.

**Agent calls:**

| Tool | Produces |
|------|----------|
| `searchPostmanElements` | Finds the workspace (`q`: `NBA GameDay`, `entityType`: `workspaces`) |
| `createEnvironment` | [`examples/generated-environment.json`](examples/generated-environment.json) — same values as [`postman/environments/production.json`](../postman/environments/production.json) |
| `createCollection` | A collection whose request URL is `{{baseUrl}}/api/v2/nba/live-scores` |

Pre-request scripts (timestamp rounding, correlation id) stay the files in [`postman/scripts/`](../postman/scripts/). The agent can attach them as a `prerequest` event. You still read the script before it is saved into the shared workspace.
