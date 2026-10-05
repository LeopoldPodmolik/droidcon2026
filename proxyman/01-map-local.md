# 2.1 Map Local & Map Remote

## 🌐 How to Redirect Prod Traffic to Localhost (Map Remote)

When your Android app is configured to communicate with the production URL (`https://api.nbagameday.com`), you can use Proxyman's **Map Remote** feature to redirect all that traffic to your running `mock-server` without modifying the app's code!

**How to set up Map Remote:**
1. In Proxyman, go to **Tools** -> **Map Remote** (or `⌥⌘R`).
2. Click the `+` button at the bottom left to add a new rule.
3. Set the **Matching Rule**:
   * URL: `https://api.nbagameday.com/*`
4. Set the **Map To** destination:
   * Protocol: `http` (or `https` if your mock server uses it)
   * Host: `localhost` (or your machine's IP address if testing from a physical device, e.g., `192.168.1.50`)
   * Port: `3000` (for the Express mock server) or `4010` (for Prism)
5. Save the rule and make sure it's checked (enabled).

Now, every time your mobile app calls `https://api.nbagameday.com/...`, Proxyman will seamlessly route it to your local mock server. This allows you to test scripts and Map Local features while the app "thinks" it's talking to Prod!

---

## Why Map Local?

Map Local lets you **intercept network requests** from your app and return **custom responses from local files** instead of the real server. Perfect for:
- Testing against APIs that don't exist yet
- Simulating error responses (403, 500, 429)
- Working offline with predictable data

---

## Example 1: Mock a New Endpoint — Live Scores (v2)

**Use Case:** The backend team hasn't built the `/api/v2/nba/live-scores` endpoint yet, but the mobile team needs to start building the live scores screen.

**Proxyman Map Local Rule Configuration:**

| Setting | Value |
|---------|-------|
| **URL** | `https://api.nbagameday.com/api/v2/nba/live-scores*` |
| **Method** | `GET` |
| **Match by** | Wildcard |
| **Map to** | Local file |
| **HTTP Status** | `200 OK` |
| **Response Headers** | `Content-Type: application/json` |

**Response Body:** → [`mocks/live-scores.json`](mocks/live-scores.json)

**OpenAPI Spec:** → [`openapi/live-scores.yaml`](openapi/live-scores.yaml)

> **💡 When to use:** The backend isn't ready, but the mobile team has the API contract. Map Local lets you build and test the UI immediately.

---

## Example 2: Override Success Response with Error Codes

**Use Case:** You need to test how the NBA GameDay app handles errors. The real API returns 200, but you want to simulate various error scenarios.

### Rule A: 403 Forbidden

| Setting | Value |
|---------|-------|
| **URL** | `https://api.nbagameday.com/api/v1/players/*/stats*` |
| **Method** | `GET` |
| **HTTP Status** | `403 Forbidden` |

**Response Body:** → [`mocks/error-403.json`](mocks/error-403.json)

### Rule B: 500 Internal Server Error

| Setting | Value |
|---------|-------|
| **URL** | `https://api.nbagameday.com/api/v1/players/*/stats*` |
| **Method** | `GET` |
| **HTTP Status** | `500 Internal Server Error` |

**Response Body:** → [`mocks/error-500.json`](mocks/error-500.json)

### Rule C: 429 Rate Limited

| Setting | Value |
|---------|-------|
| **URL** | `https://api.nbagameday.com/api/v1/players/*/stats*` |
| **Method** | `GET` |
| **HTTP Status** | `429 Too Many Requests` |
| **Response Headers** | `Retry-After: 60` |

**Response Body:** → [`mocks/error-429.json`](mocks/error-429.json)

> **💡 When to use:** Testing error handling in the app is critical. Map Local lets you trigger exact error codes without needing backend changes or waiting for real failures.

---

## Example 3: Mock Play-by-Play Data

**Use Case:** A new feature displays play-by-play action during live games. The endpoint `/api/v2/nba/games/{gameId}/play-by-play` doesn't exist yet.

**Proxyman Map Local Rule Configuration:**

| Setting | Value |
|---------|-------|
| **URL** | `https://api.nbagameday.com/api/v2/nba/games/*/play-by-play*` |
| **Method** | `GET` |
| **HTTP Status** | `200 OK` |

**Response Body:** → [`mocks/play-by-play.json`](mocks/play-by-play.json)

**OpenAPI Spec:** → [`openapi/play-by-play.yaml`](openapi/play-by-play.yaml)

> **💡 Key takeaway:** Map Local decouples mobile development from backend readiness. You can build, test, and demo features weeks before the API is deployed.
