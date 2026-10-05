const express = require("express");
const cors = require("cors");
const morgan = require("morgan");
const crypto = require("crypto");
const path = require("path");
const fs = require("fs");

const app = express();
const PORT = process.env.PORT || 3000;

// ---------------------------------------------------------------------------
// Data
// ---------------------------------------------------------------------------
const players = require("./data/players.json");
const teams = require("./data/teams.json");
const games = require("./data/games.json");

// ---------------------------------------------------------------------------
// Middleware
// ---------------------------------------------------------------------------
app.use(cors());
app.use(express.json());
app.use(morgan("dev"));

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------
function requestId() {
  return "req-" + crypto.randomBytes(4).toString("hex");
}

function meta(extra = {}) {
  return { requestId: requestId(), timestamp: new Date().toISOString(), ...extra };
}

function success(data, extraMeta = {}) {
  return { status: "success", data, meta: meta(extraMeta) };
}

function errorResponse(res, status, code, message, details = {}) {
  return res.status(status).json({
    status: "error",
    error: { code, message, details },
    meta: meta(),
  });
}

/**
 * Middleware: simulate errors via ?_error=403|500|429
 * This lets you test error handling in Postman without changing the endpoint.
 */
function simulateError(req, res, next) {
  const errorCode = req.query._error;
  if (!errorCode) return next();

  switch (errorCode) {
    case "403":
      return errorResponse(res, 403, "FORBIDDEN",
        "Your API subscription does not include access to this resource. Please upgrade to Premium tier.",
        { requiredTier: "premium", currentTier: "basic", upgradeUrl: "https://developer.nbagameday.com/pricing" });
    case "500":
      return errorResponse(res, 500, "INTERNAL_SERVER_ERROR",
        "An unexpected error occurred while processing your request. Our team has been notified.",
        { incidentId: `INC-${Date.now()}`, retryAfterSeconds: 30 });
    case "429":
      res.set("Retry-After", "60");
      return errorResponse(res, 429, "RATE_LIMITED",
        "You have exceeded the rate limit of 100 requests per minute. Please wait before retrying.",
        { limit: 100, windowSeconds: 60, remaining: 0, resetAt: new Date(Date.now() + 60000).toISOString() });
    case "401":
      return errorResponse(res, 401, "UNAUTHORIZED",
        "Invalid or expired authentication token. Please refresh your token.",
        { tokenExpired: true });
    default:
      return errorResponse(res, parseInt(errorCode) || 400, "SIMULATED_ERROR",
        `Simulated error with status ${errorCode}`, {});
  }
}

app.use(simulateError);

// ---------------------------------------------------------------------------
// Auth endpoint
// ---------------------------------------------------------------------------
app.post("/api/v1/auth/token", (req, res) => {
  const { clientId, clientSecret, grantType } = req.body || {};

  if (!clientId || !clientSecret) {
    return errorResponse(res, 400, "BAD_REQUEST", "Missing clientId or clientSecret");
  }

  const token = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9." +
    Buffer.from(JSON.stringify({
      sub: clientId,
      iat: Math.floor(Date.now() / 1000),
      exp: Math.floor(Date.now() / 1000) + 900,
      scope: "read:stats read:games read:standings"
    })).toString("base64") +
    ".mock-signature-" + crypto.randomBytes(16).toString("hex");

  res.json({
    accessToken: token,
    tokenType: "Bearer",
    expiresIn: 900,
    scope: "read:stats read:games read:standings",
  });
});

// ---------------------------------------------------------------------------
// v1: Player Stats
// ---------------------------------------------------------------------------
app.get("/api/v1/players/:playerId/stats", (req, res) => {
  const player = players[req.params.playerId];
  if (!player) {
    return errorResponse(res, 404, "NOT_FOUND", `Player ${req.params.playerId} not found`);
  }

  const season = req.query.season || "2025-26";
  const seasonData = player.seasons[season];
  if (!seasonData) {
    return errorResponse(res, 404, "NOT_FOUND", `No stats found for season ${season}`);
  }

  res.json(success({
    playerId: player.playerId,
    playerName: player.playerName,
    teamId: player.teamId,
    teamName: player.teamName,
    season,
    gamesPlayed: seasonData.gamesPlayed,
    stats: seasonData.stats,
  }));
});

// ---------------------------------------------------------------------------
// v1: Game Schedule
// ---------------------------------------------------------------------------
app.get("/api/v1/games", (req, res) => {
  let filtered = [...games];

  if (req.query.date) {
    // Filter games for the given date (compare date portion of startTime)
    filtered = filtered.filter((g) => g.startTime.startsWith(req.query.date));
  }
  if (req.query.teamId) {
    filtered = filtered.filter(
      (g) => g.homeTeam.teamId === req.query.teamId || g.awayTeam.teamId === req.query.teamId
    );
  }
  if (req.query.status) {
    filtered = filtered.filter((g) => g.status === req.query.status);
  }

  res.json(success(
    { date: req.query.date || new Date().toISOString().slice(0, 10), games: filtered },
    { totalGames: filtered.length }
  ));
});

// ---------------------------------------------------------------------------
// v1: Standings
// ---------------------------------------------------------------------------
app.get("/api/v1/standings", (req, res) => {
  const conference = req.query.conference || "west";
  const season = req.query.season || "2025-26";

  const teamList = Object.values(teams)
    .filter((t) => t.conference.toLowerCase() === conference.toLowerCase())
    .map((t, i) => {
      const [w, l] = t.record.split("-").map(Number);
      return {
        rank: i + 1,
        teamId: t.teamId,
        teamName: t.name,
        abbreviation: t.abbreviation,
        wins: w,
        losses: l,
        winPercentage: parseFloat((w / (w + l)).toFixed(3)),
        gamesBack: i === 0 ? 0.0 : parseFloat((i * 3.5).toFixed(1)),
        streak: ["W5", "W2", "L1", "W3"][i % 4],
        lastTen: ["8-2", "6-4", "5-5", "7-3"][i % 4],
      };
    })
    .sort((a, b) => b.winPercentage - a.winPercentage);

  res.json(success(
    { conference: conference.charAt(0).toUpperCase() + conference.slice(1), season, standings: teamList },
    { totalTeams: teamList.length }
  ));
});

// ---------------------------------------------------------------------------
// v1: Team Roster
// ---------------------------------------------------------------------------
app.get("/api/v1/teams/:teamId/roster", (req, res) => {
  const team = teams[req.params.teamId];
  if (!team) {
    return errorResponse(res, 404, "NOT_FOUND", `Team ${req.params.teamId} not found`);
  }

  res.json(success({
    teamId: team.teamId,
    teamName: team.name,
    league: "NBA",
    roster: team.roster,
  }));
});

// ---------------------------------------------------------------------------
// v1: League Summary (returns NFL data — for Proxyman transform demo)
// ---------------------------------------------------------------------------
app.get("/api/v1/league/summary", (req, res) => {
  res.json(success({
    sportsLeague: "NFL",
    leagueFullName: "National Football League",
    season: "2025",
    seasonType: "Regular Season",
    teams: [
      {
        teamId: "nfl-t-001",
        name: "Kansas City Chiefs",
        abbreviation: "KC",
        conference: "AFC",
        division: "West",
        arena: "Arrowhead Stadium",
        city: "Kansas City",
        primaryColor: "#E31837",
        secondaryColor: "#FFB81C",
      },
    ],
    featuredPlayer: {
      playerId: "nfl-p-001",
      name: "Patrick Mahomes",
      position: "QB",
      teamName: "Kansas City Chiefs",
      stats: { passingYards: 4839, touchdowns: 38, interceptions: 12 },
    },
  }));
});

// ---------------------------------------------------------------------------
// v2: Live Scores (INTENTIONALLY DISABLED FOR PROXYMAN DEMO)
// ---------------------------------------------------------------------------
/*
app.get("/api/v2/nba/live-scores", (req, res) => {
  // Simulate live games based on scheduled games
  const liveGames = games
    .filter((g) => g.status === "scheduled" || g.status === "live")
    .map((g, i) => ({
      ...g,
      status: "live",
      period: 3 - i,
      gameClock: `0${4 - i}:${15 + i * 17}`.slice(0, 5),
      homeTeam: {
        ...g.homeTeam,
        score: 60 + Math.floor(Math.random() * 30),
        timeoutsRemaining: 3 + i,
        bonusActive: i % 2 === 0,
        leaders: {
          points: { playerId: "p-2544", name: "LeBron James", value: 22 + i * 3 },
          rebounds: { playerId: "p-1629630", name: "Anthony Davis", value: 9 - i },
          assists: { playerId: "p-2544", name: "LeBron James", value: 7 + i },
        },
      },
      awayTeam: {
        ...g.awayTeam,
        score: 55 + Math.floor(Math.random() * 35),
        timeoutsRemaining: 4 - i,
        bonusActive: i % 2 === 1,
        leaders: {
          points: { playerId: "p-1628369", name: "Jayson Tatum", value: 28 - i * 2 },
          rebounds: { playerId: "p-1628369", name: "Jayson Tatum", value: 8 },
          assists: { playerId: "p-203935", name: "Jrue Holiday", value: 6 + i },
        },
      },
    }));

  res.json(success(
    { lastUpdated: new Date().toISOString(), games: liveGames },
    { pollingIntervalMs: 10000 }
  ));
});
*/

// ---------------------------------------------------------------------------
// v2: Play-by-Play (INTENTIONALLY DISABLED FOR PROXYMAN DEMO)
// ---------------------------------------------------------------------------
/*
app.get("/api/v2/nba/games/:gameId/play-by-play", (req, res) => {
  const game = games.find((g) => g.gameId === req.params.gameId);
  if (!game) {
    return errorResponse(res, 404, "NOT_FOUND", `Game ${req.params.gameId} not found`);
  }

  const period = parseInt(req.query.period) || 3;
  const homeId = game.homeTeam.teamId;
  const awayId = game.awayTeam.teamId;

  const plays = [
    {
      playId: "play-001", clock: "04:32", period,
      teamId: homeId, playType: "MADE_SHOT",
      description: "LeBron James makes 18-foot pullup jump shot",
      player: { playerId: "p-2544", name: "LeBron James" },
      scoreHome: 78, scoreAway: 82,
      coordinates: { x: 22.5, y: 14.3 },
    },
    {
      playId: "play-002", clock: "04:10", period,
      teamId: awayId, playType: "TURNOVER",
      description: "Jayson Tatum bad pass turnover",
      player: { playerId: "p-1628369", name: "Jayson Tatum" },
      scoreHome: 78, scoreAway: 82,
      coordinates: null,
    },
    {
      playId: "play-003", clock: "03:55", period,
      teamId: homeId, playType: "MADE_SHOT",
      description: "Anthony Davis makes dunk (LeBron James assists)",
      player: { playerId: "p-1629630", name: "Anthony Davis" },
      assistPlayer: { playerId: "p-2544", name: "LeBron James" },
      scoreHome: 80, scoreAway: 82,
      coordinates: { x: 5.2, y: 25.0 },
    },
    {
      playId: "play-004", clock: "03:42", period,
      teamId: awayId, playType: "MADE_THREE",
      description: "Jayson Tatum makes 26-foot three point shot",
      player: { playerId: "p-1628369", name: "Jayson Tatum" },
      scoreHome: 80, scoreAway: 85,
      coordinates: { x: 35.1, y: 8.7 },
    },
    {
      playId: "play-005", clock: "03:20", period,
      teamId: null, playType: "TIMEOUT",
      description: "Los Angeles Lakers full timeout",
      player: null,
      scoreHome: 80, scoreAway: 85,
      coordinates: null,
    },
  ];

  res.json(success(
    { gameId: req.params.gameId, period, plays },
    { totalPlays: plays.length }
  ));
});
*/

// ---------------------------------------------------------------------------
// API Index — list all available endpoints
// ---------------------------------------------------------------------------
app.get("/", (req, res) => {
  res.json({
    name: "NBA GameDay Mock API",
    version: "1.0.0",
    description: "Mock server for Postman & Proxyman presentation demos",
    endpoints: {
      "POST /api/v1/auth/token": "Get JWT auth token (body: clientId, clientSecret)",
      "GET  /api/v1/players/:playerId/stats": "Player stats (query: season)",
      "GET  /api/v1/games": "Game schedule (query: date, teamId, status)",
      "GET  /api/v1/standings": "League standings (query: conference, season)",
      "GET  /api/v1/teams/:teamId/roster": "Team roster",
      "GET  /api/v1/league/summary": "League summary (returns NFL data for Proxyman demo)",
      "GET  /api/v2/nba/live-scores": "Live scores (v2 endpoint)",
      "GET  /api/v2/nba/games/:gameId/play-by-play": "Play-by-play (query: period)",
    },
    tips: {
      "Simulate errors": "Add ?_error=403 or ?_error=500 or ?_error=429 to any endpoint",
      "Example players": "p-2544 (LeBron), p-1628369 (Tatum), p-201939 (Curry)",
      "Example teams": "t-1610612747 (LAL), t-1610612738 (BOS), t-1610612744 (GSW)",
      "Example games": "g-0022500987, g-0022500988, g-0022500989",
    },
  });
});

// ---------------------------------------------------------------------------
// 404 catch-all
// ---------------------------------------------------------------------------
app.use((req, res) => {
  errorResponse(res, 404, "NOT_FOUND", `Endpoint ${req.method} ${req.path} not found`);
});

// ---------------------------------------------------------------------------
// Start
// ---------------------------------------------------------------------------
app.listen(PORT, () => {
  console.log(`
  🏀 NBA GameDay Mock API Server
  ═══════════════════════════════════════════════════

  Running on:  http://localhost:${PORT}
  API Index:   http://localhost:${PORT}/

  Quick test endpoints:
    curl http://localhost:${PORT}/api/v1/players/p-2544/stats?season=2025-26
    curl http://localhost:${PORT}/api/v1/games?date=2026-04-02
    curl http://localhost:${PORT}/api/v1/standings?conference=west
    curl http://localhost:${PORT}/api/v2/nba/live-scores
    curl http://localhost:${PORT}/api/v1/players/p-2544/stats?_error=403

  For Postman: set baseUrl variable to http://localhost:${PORT}
  For Proxyman: proxy your app traffic and use Map Remote to redirect to localhost:${PORT}

  ═══════════════════════════════════════════════════
  `);
});
