# Prompt — set sportsLeague to NBA

Paste this into the agent. Read the script it drafts before you enable the rule.

```
Create a Proxyman scripting rule that sets sportsLeague to NBA
in the /api/v1/league/summary response.

Match GET */api/v1/league/summary* only.
Change data.sportsLeague. Leave every other field as the server sent it.
Show me the script before you enable the rule.
```

Script the agent should produce: [`../examples/generated-script.js`](../examples/generated-script.js).
