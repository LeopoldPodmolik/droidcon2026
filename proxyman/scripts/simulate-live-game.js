/**
 * Proxyman Script: Simulate game going live
 * 
 * Rule: Match URL https://api.nbagameday.com/api/v1/games*
 * Phase: Response
 *
 * Finds the first game with status "scheduled" and changes it to "live",
 * injecting realistic live score data, period, and game clock.
 */

function onResponse(context, url, request, response) {
    // response.body is already a parsed JS object in Proxyman — no JSON.parse() needed
    var body = response.body;

    if (body.status === "success" && body.data && body.data.games) {
        var games = body.data.games;

        for (var i = 0; i < games.length; i++) {
            if (games[i].status === "scheduled") {
                // Transform from scheduled to live
                games[i].status = "live";
                games[i].period = 2;
                games[i].gameClock = "03:45";

                // Inject live scores for home team
                games[i].homeTeam.score = 48;
                games[i].homeTeam.timeoutsRemaining = 4;
                games[i].homeTeam.bonusActive = false;
                games[i].homeTeam.quarterScores = [24, 24];

                // Inject live scores for away team
                games[i].awayTeam.score = 52;
                games[i].awayTeam.timeoutsRemaining = 3;
                games[i].awayTeam.bonusActive = true;
                games[i].awayTeam.quarterScores = [28, 24];

                // Add live game metadata
                games[i].lastPlayDescription = "Jayson Tatum makes 22-foot step back jump shot";
                games[i].tvTimeout = false;
                games[i].possession = games[i].homeTeam.teamId;

                // Only transform the first scheduled game
                break;
            }
        }

        body.data.games = games;
    }

    // Assign the modified object directly — Proxyman handles serialization
    response.body = body;
    return response;
}
