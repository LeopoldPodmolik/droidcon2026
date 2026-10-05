# 3.5 Review the Draft

The first draft is a starting point. It is not the rule you enable.

AI writes the rule. You own the review.

---

## Checklist before you enable anything

| Check | What goes wrong |
|-------|-----------------|
| Read the URL pattern | A `*` in the wrong place matches routes you did not mean to mock. |
| Read the script | Confirm it changes the field you named and returns `response`. |
| One rule, one route | A wide rule silently replaces responses other tests still need. |
| Status and method | A 200 on `ANY` method hides a POST the app uses to submit state. |
| Secrets in the session | Flows carry tokens. Redaction on, and do not paste raw logs into a chat that leaves the machine. |
| Turn it off when the demo ends | A Map Local left enabled is the next "why is prod data fake?" |

---

## The wildcard from Demo 4

Same prompt, two drafts. Only the second one should be enabled.

| | First draft | After review |
|---|---|---|
| File | [`examples/draft-too-wide.json`](examples/draft-too-wide.json) | [`examples/generated-map-local.json`](examples/generated-map-local.json) |
| URL | `*/api/v2/nba/*` | `*/api/v2/nba/live-scores*` |
| Also matches | `/api/v2/nba/play-by-play`, any later v2 NBA route | The live-scores call only |

`*/api/v2/nba/*` is a valid pattern. Proxyman will apply it. The app will render play-by-play from the live-scores JSON and the failure will look like a UI bug.

---

## What you still decide

- Which of the two live-score failures is actually the bug — the 500 or the 404. See [3.3](03-analyse-traffic.md).
- Whether this rule collides with one a teammate already imported.
- Whether the captured log is safe to send. A session is tokens and account identifiers as well as JSON.

> **💡 Line to close on:** If you repeat it, automate it. If you wait for it, mock it. If it is tedious, let AI draft it. You review it.
