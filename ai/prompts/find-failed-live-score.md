# Prompt — which live-score call failed?

Paste this into the agent after Proxyman MCP is connected and the session is recorded.

```
Which NBA live-score call failed?

Use Proxyman. Look at the current session for api.nbagameday.com.
Start with get_flows, then filter_flows to live-scores calls whose status is not 200.
Give me the one or two suspects: method, URL, status, and the error message.
Do not create a rule. Do not change anything.
```

Expected tool calls and the worked answer: [`../examples/session-summary.json`](../examples/session-summary.json).
