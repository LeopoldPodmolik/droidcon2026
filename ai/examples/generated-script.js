/**
 * Proxyman Script — drafted from the prompt in prompts/rewrite-sports-league.md
 *
 * Rule: GET */api/v1/league/summary*
 * Phase: Response
 *
 * Sets data.sportsLeague to "NBA" and leaves the rest of the payload alone.
 * The full NFL → NBA rewrite (teams, players, stats) is
 * proxyman/scripts/nfl-to-nba-transform.js.
 */

async function onResponse(context, url, request, response) {
    var body = response.body;
    body.data.sportsLeague = "NBA";
    response.body = body;
    return response;
}
