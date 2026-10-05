# 1.3 Scripting (Pre-request Scripts)

## Why Pre-request Scripts?

Pre-request scripts run **before** the request is sent. They can dynamically compute values, generate tokens, add timestamps — anything you'd otherwise do manually before hitting Send.

---

## Example 1: Timestamp Rounded to 5 Minutes

**Use Case:** The NBA GameDay API uses CDN caching in 5-minute intervals. The `timestamp` parameter must be rounded to the nearest 5 minutes to get cache-consistent responses.

**Script:** → [`scripts/timestamp-rounding.js`](scripts/timestamp-rounding.js)

**Request:**

```
GET {{baseUrl}}/api/v1/games?timestamp={{timestamp}}
```

**Before script runs (raw):**

```
GET https://api.nbagameday.com/api/v1/games?timestamp=
    → timestamp is empty, request would fail
```

**After script runs (resolved):**

```
GET https://api.nbagameday.com/api/v1/games?timestamp=2026-04-02T15:25:00.000Z
    → timestamp is rounded to nearest 5 minutes (e.g., 15:27 → 15:25)
```

**Why it's useful:** Without this script, you'd have to manually calculate and type the rounded timestamp every time. The script does it automatically, ensuring you always hit the right cache bucket.

---

## Example 2: Correlation ID Generator

**Use Case:** Every request to NBA GameDay needs a unique `X-Correlation-Id` header for tracing through microservices. The ID should include the device type and a UUID.

**Script:** → [`scripts/correlation-id.js`](scripts/correlation-id.js)

**Request:**

```
GET {{baseUrl}}/api/v1/players/{{playerId}}/stats?season={{season}}

Headers:
  Authorization: Bearer {{apiKey}}
  X-Correlation-Id: {{correlationId}}
  X-Platform: {{platform}}
```

**Before script runs:**

```
X-Correlation-Id: (empty)
```

**After script runs:**

```
X-Correlation-Id: android-3f7a91c2-d4e5-4b89-a1c3-8f2e6d901234-1743573600
```

**Why it's useful:** Every request gets a unique, traceable ID. When debugging issues in the backend logs, you can search by this correlation ID to find the exact request path through all microservices.

---

## Example 3: Dynamic Auth Token Refresh

**Use Case:** The NBA GameDay API uses short-lived JWT tokens (15 min). This script automatically refreshes the token if it's about to expire, so you never get 401 errors during testing.

**Script:** → [`scripts/auto-token-refresh.js`](scripts/auto-token-refresh.js)

**Auth Token Response (from the internal `pm.sendRequest`):**

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "scope": "read:stats read:games read:standings"
}
```

**Before script runs:**

```
Authorization: Bearer (expired-token-abc123...)
→ Would return 401 Unauthorized
```

**After script runs:**

```
Authorization: Bearer eyJhbGciOiJSUzI1NiIs...
→ Fresh token, request succeeds with 200
```

**Why it's useful:** No more manually copying tokens from the auth endpoint. The script handles the entire refresh flow transparently. You just click Send, and the token is always valid.

> **💡 Key takeaway:** Pre-request scripts turn Postman from a simple HTTP client into a smart testing tool that automates repetitive setup tasks.
