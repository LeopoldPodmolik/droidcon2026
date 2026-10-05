# Prompt — Demo 4, Map Local for live scores

Paste this into the agent. Reject a draft whose URL is wider than live-scores.

```
Create a Proxyman Map Local rule for the NBA live scores endpoint
that the backend has not built yet.

URL: */api/v2/nba/live-scores*
Method: GET
Status: 200
Content-Type: application/json
Body: the live-scores mock in proxyman/mocks/live-scores.json

Match that path only. Do not use */api/v2/nba/*.
Show me the rule and leave it disabled until I confirm.
```

Accepted tool call: [`../examples/generated-map-local.json`](../examples/generated-map-local.json).

First draft to reject: [`../examples/draft-too-wide.json`](../examples/draft-too-wide.json).
