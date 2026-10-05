/**
 * Proxyman Script: Transform NFL response to NBA
 * 
 * Rule: Match URL https://api.nbagameday.com/api/v1/league/summary*
 * Phase: Response
 *
 * Transforms NFL-specific fields to NBA equivalents:
 * - League name: NFL → NBA
 * - Teams: NFL teams → NBA teams
 * - Player positions: QB, WR, RB → PG, SF, C
 * - Scoring: touchdowns/field goals → points/rebounds/assists
 */

function onResponse(context, url, request, response) {
    // response.body is already a parsed JS object in Proxyman — no JSON.parse() needed
    var body = response.body;

    // Transform league info
    body.data.sportsLeague = "NBA";
    body.data.leagueFullName = "National Basketball Association";
    body.data.season = "2025-26";
    body.data.seasonType = "Regular Season";

    // Transform teams
    if (body.data.teams) {
        body.data.teams = [
            {
                "teamId": "t-1610612747",
                "name": "Los Angeles Lakers",
                "abbreviation": "LAL",
                "conference": "Western",
                "division": "Pacific",
                "arena": "Crypto.com Arena",
                "city": "Los Angeles",
                "primaryColor": "#552583",
                "secondaryColor": "#FDB927"
            },
            {
                "teamId": "t-1610612738",
                "name": "Boston Celtics",
                "abbreviation": "BOS",
                "conference": "Eastern",
                "division": "Atlantic",
                "arena": "TD Garden",
                "city": "Boston",
                "primaryColor": "#007A33",
                "secondaryColor": "#BA9653"
            },
            {
                "teamId": "t-1610612744",
                "name": "Golden State Warriors",
                "abbreviation": "GSW",
                "conference": "Western",
                "division": "Pacific",
                "arena": "Chase Center",
                "city": "San Francisco",
                "primaryColor": "#1D428A",
                "secondaryColor": "#FFC72C"
            }
        ];
    }

    // Transform featured player
    if (body.data.featuredPlayer) {
        body.data.featuredPlayer = {
            "playerId": "p-2544",
            "name": "LeBron James",
            "position": "SF",
            "teamName": "Los Angeles Lakers",
            "stats": {
                "pointsPerGame": 25.8,
                "reboundsPerGame": 7.2,
                "assistsPerGame": 8.1
            }
        };
    }

    // Assign the modified object directly — Proxyman handles serialization
    response.body = body;
    return response;
}
