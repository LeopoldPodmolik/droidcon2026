/**
 * Proxyman Script: Inject Player Efficiency Rating (PER)
 * 
 * Rule: Match URL https://api.nbagameday.com/api/v1/players/./stats.
 * Phase: Response
 *
 * Adds 'playerEfficiencyRating' and 'advancedStats' fields to the existing
 * player stats response. Uses real stats from the response to compute
 * a realistic-looking PER value.
 */

function onResponse(context, url, request, response) {
    // response.body is already a parsed JS object in Proxyman — no JSON.parse() needed
    var body = response.body;

    if (body.status === "success" && body.data && body.data.stats) {
        var stats = body.data.stats;

        // Calculate a simplified PER-like value from existing stats
        // Real PER formula is complex; this is a reasonable approximation
        var estimatedPER = (
            stats.pointsPerGame * 1.0 +
            stats.reboundsPerGame * 1.2 +
            stats.assistsPerGame * 1.5 +
            stats.stealsPerGame * 2.0 +
            stats.blocksPerGame * 2.0 -
            stats.turnoversPerGame * 1.5
        ).toFixed(1);

        // Inject PER into existing stats
        body.data.stats.playerEfficiencyRating = parseFloat(estimatedPER);

        // Inject full advanced stats block
        body.data.advancedStats = {
            "playerEfficiencyRating": parseFloat(estimatedPER),
            "trueShootingPercentage": 0.621,
            "effectiveFieldGoalPercentage": 0.567,
            "winSharesPer48": 0.198,
            "boxPlusMinus": 8.4,
            "valueOverReplacement": 5.7,
            "usageRate": 0.312,
            "assistPercentage": 0.389,
            "reboundPercentage": 0.134,
            "offensiveRating": 118.5,
            "defensiveRating": 109.2,
            "netRating": 9.3
        };
    }

    // Assign the modified object directly — Proxyman handles serialization
    response.body = body;
    return response;
}
