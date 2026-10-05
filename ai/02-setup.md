# 3.2 How to Set Up MCP

The block you paste is the same `mcpServers` object in every client. Cursor reads it from `mcp.json`. Other clients call that file `settings.json`. Drop-in copies live in [`servers/`](servers/).

Combined file for this talk: [`servers/cursor.mcp.json`](servers/cursor.mcp.json)

| Client | File |
|--------|------|
| Cursor (this machine) | `~/.cursor/mcp.json` |
| Cursor (this repo only) | `.cursor/mcp.json` |
| Claude Desktop | `~/Library/Application Support/Claude/claude_desktop_config.json` |

Restart the client after saving.

---

## Proxyman — local

Proxyman MCP is a small process on your machine. It talks to the Proxyman app over localhost. It is not a cloud API, and it does nothing if Proxyman is closed.

1. Open Proxyman → **Settings → MCP**.
2. Turn on **Enable MCP Server**.
3. Leave **Redact Sensitive Data Before Sending to AI** on. Authorization headers, cookies, and tokens are masked before the agent sees a flow.
4. If your agent is not in the list, choose **Manual** and copy the command Proxyman prints. The usual macOS path is below.

```json
{
  "mcpServers": {
    "proxyman": {
      "command": "/Applications/Proxyman.app/Contents/MacOS/mcp-server"
    }
  }
}
```

Setapp build: `/Applications/Setapp/Proxyman.app/Contents/MacOS/mcp-server`.

The HTTP server inside the app binds to `127.0.0.1` only. The handshake token stays in `~/Library/Application Support/com.proxyman.NSProxy/mcp-handshake.json`. Do not commit that file.

Docs: [docs.proxyman.com/mcp](https://docs.proxyman.com/mcp)

---

## Postman — remote

Postman MCP is hosted by Postman. The agent reaches your cloud workspace over HTTPS. Nothing runs locally.

For this talk, use **Full** mode (`/mcp`). That is the configuration that can search the workspace, scaffold a collection, and write an environment. Narrower modes:

| Mode | URL | Enough for |
|------|-----|------------|
| Minimal | `https://mcp.postman.com/minimal` | One collection or one environment |
| Code | `https://mcp.postman.com/code` | Generate client code from a spec |
| Full | `https://mcp.postman.com/mcp` | Search, collections, environments, scripts |
| Learn | `https://mcp.postman.com/learn` | Postman docs only |

OAuth, US region — no key in the file. Cursor opens the browser on the first call:

```json
{
  "mcpServers": {
    "postman": {
      "url": "https://mcp.postman.com/mcp"
    }
  }
}
```

API key instead of OAuth (and the only option on the EU server, `https://mcp.eu.postman.com/mcp`):

```json
{
  "mcpServers": {
    "postman": {
      "url": "https://mcp.postman.com/mcp",
      "headers": {
        "Authorization": "Bearer ${env:POSTMAN_API_KEY}"
      }
    }
  }
}
```

Keep the key in the environment. Do not paste it into the JSON you commit.

Docs: [Postman remote MCP server](https://learning.postman.com/docs/developer/postman-api/postman-mcp-server/postman-mcp-remote-server)

---

## Check that both are connected

| Server | Prompt that should work |
|--------|-------------------------|
| Proxyman | "How many flows are in the current Proxyman session?" — it calls `get_flows` |
| Postman | "Find the NBA GameDay collection in my workspace." — it calls `searchPostmanElements` |

If Proxyman returns no tools, the app is closed or MCP is off in Settings. If Postman asks you to sign in, finish the OAuth window and send the prompt again.
