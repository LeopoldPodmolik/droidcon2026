# 3.3 AI for Problem Analysis

## Why hand the session to the agent?

A live-game debug session is a few hundred calls: scores, schedule, standings, images, analytics. The failure is one or two of them. Reading that list by hand is the boring part.

The agent does not fix the bug. It narrows the session.

**Prompt:** [`prompts/find-failed-live-score.md`](prompts/find-failed-live-score.md)

> Which NBA live-score call failed?

---

## What the agent calls

1. `get_flows` — host `api.nbagameday.com`, limit 200.
2. `filter_flows` — URL contains `live-scores`, status is not 200.

Tool shapes: [`tools/proxyman.json`](tools/proxyman.json)

Worked result for the NBA GameDay session: [`examples/session-summary.json`](examples/session-summary.json)

| | Count |
|---|---|
| Flows on `api.nbagameday.com` | 214 |
| HTTP 200 | 198 |
| Other successful noise (304) | 14 |
| **Suspects** | **2** |

---

## The two suspects

| # | Call | Status | Why it matters |
|---|------|--------|----------------|
| 1 | `GET /api/v2/nba/live-scores` | **500** | The screen's call. Body says the handler is not implemented. This is the failure. |
| 2 | `GET /api/v1/nba/live-scores` | **404** | Same feature, old path. The app is still requesting v1, which was never shipped. Easy to chase if you stop at the first red row. |

Everything else in that session — `/api/v1/games`, `/api/v1/standings`, `/api/v1/players/p-2544/stats` — returned 200 and dropped out of the filter.

Authorization on both suspects is already masked (`Bearer ***REDACTED***`). That is the Proxyman redaction switch from [setup](02-setup.md), not something the model did.

> **💡 Takeaway:** 200 requests become 1–2 suspects. You still decide which of those two is the bug. The 500 is the missing backend. The 404 is a client calling a path that does not exist.
