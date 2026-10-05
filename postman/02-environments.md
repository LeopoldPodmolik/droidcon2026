# 1.2 Environments

## Why Environments?

Environments let you maintain **separate sets of variables** for different server configurations. Switch between Production, Alpha, and Beta with a single dropdown — the same request works everywhere.

---

## Example 1: Three-Environment Setup

Import these directly into Postman:

- **Production:** → [`environments/production.json`](environments/production.json)
- **Alpha:** → [`environments/alpha.json`](environments/alpha.json)
- **Beta:** → [`environments/beta.json`](environments/beta.json)

**Comparison Table:**

| Variable | Production | Alpha | Beta |
|----------|-----------|-------|------|
| `baseUrl` | `https://api.nbagameday.com` | `https://alpha-api.nbagameday.com` | `https://beta-api.nbagameday.com` |
| `apiKey` | `prod-key-Xk9m...` | `alpha-key-Yt3n...` | `beta-key-Hj7p...` |
| `apiVersion` | `v1` | `v2` | `v1` |
| `environment` | `production` | `alpha` | `beta` |
| `timeout` | `5000` | `15000` | `10000` |

**The same request works across all environments:**

```
GET {{baseUrl}}/api/{{apiVersion}}/players/{{playerId}}/stats?season={{season}}

Headers:
  Authorization: Bearer {{apiKey}}
  X-Environment: {{environment}}
```

> **💡 Demo flow:** Select "Production" in the dropdown → Send → get real data. Switch to "Alpha" → Send → same request hits alpha server with v2 API. Zero changes to the request.

---

## Example 2: Multi-Brand Scenario (NBA vs. WNBA)

**Use Case:** Your company also builds a WNBA companion app. The API structure is identical, but the base URL and brand differ.

**Environment: NBA GameDay**

| Variable | Value |
|----------|-------|
| `baseUrl` | `https://api.nbagameday.com` |
| `brand` | `nba` |
| `leagueName` | `NBA` |
| `apiKey` | `nba-prod-key-Xk9mP2qR7wL4` |
| `featuredTeamId` | `t-1610612747` |

**Environment: WNBA GameDay**

| Variable | Value |
|----------|-------|
| `baseUrl` | `https://api.wnbagameday.com` |
| `brand` | `wnba` |
| `leagueName` | `WNBA` |
| `apiKey` | `wnba-prod-key-Rm5kL8vN3jQ1` |
| `featuredTeamId` | `t-1611661317` |

**Universal request:**

```
GET {{baseUrl}}/api/v1/teams/{{featuredTeamId}}/roster

Headers:
  Authorization: Bearer {{apiKey}}
  X-Brand: {{brand}}
```

**Responses:**
- NBA → [`responses/team-roster-nba.json`](responses/team-roster-nba.json)
- WNBA → [`responses/team-roster-wnba.json`](responses/team-roster-wnba.json)

> **💡 Key takeaway:** Environments make it trivial to test the same API contract across different servers, brands, or configurations. One collection — many targets.
