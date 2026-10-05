# 3.1 What MCP Is

## The problem

An API client and a proxy already hold the work: collections, environments, captured flows, Map Local rules, scripts. An AI agent sitting in the editor cannot see any of that. It can only guess from the chat.

**MCP (Model Context Protocol)** is the contract between the agent and the tool. The tool publishes functions. The agent calls them the same way it calls any other function, reads the result, and calls the next one.

Spec: [modelcontextprotocol.io — Getting started](https://modelcontextprotocol.io/docs/2026-07-28/getting-started/intro)

---

## Two servers, two jobs

| | Proxyman MCP | Postman MCP |
|---|---|---|
| Role in the talk | The observer | The constructor |
| Where it runs | **Local** — on your machine, against the Proxyman app that is open | **Remote** — Postman's cloud, against your workspace |
| What the agent can touch | Flows, rules, scripts, Map Local | Collections, requests, environments, scripts |
| Config shape | `command` — a binary on disk | `url` — an HTTPS endpoint |

Server definitions: [`servers/proxyman.local.json`](servers/proxyman.local.json) and [`servers/postman.remote.json`](servers/postman.remote.json).

The functions the demos actually call are in [`tools/proxyman.json`](tools/proxyman.json) and [`tools/postman.json`](tools/postman.json).

---

## What a call looks like

The agent does not get a special UI. It gets a name and arguments.

```json
{
  "tool": "get_flows",
  "arguments": {
    "host_filter": "api.nbagameday.com",
    "limit": 200
  }
}
```

Proxyman runs that against the session on this Mac and returns flows. The agent then decides the next call — usually `filter_flows` — from that result.

> **💡 The contract is the point.** You keep using Postman and Proxyman. MCP only lets the agent press the same buttons, then hand the draft back to you.
