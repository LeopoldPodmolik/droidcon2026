package com.example.nbagameday.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatsResponse(
    val playerId: String,
    val playerName: String,
    val teamId: String,
    val teamName: String,
    val season: String,
    val gamesPlayed: Int,
    val stats: Map<String, Double>,
    // This is optional and will be injected by Proxyman for the demo
    val advancedStats: Map<String, Double>? = null
)

@Serializable
data class GamesResponse(
    val date: String? = null,
    val lastUpdated: String? = null,
    val games: List<Game>
)

@Serializable
data class Game(
    val gameId: String,
    val status: String,
    val startTime: String,
    val arena: String,
    val homeTeam: TeamScore,
    val awayTeam: TeamScore,
    val period: Int? = null,
    val gameClock: String? = null,
    val lastPlayDescription: String? = null
)

@Serializable
data class TeamScore(
    val teamId: String,
    val name: String,
    val abbreviation: String,
    val score: Int? = null,
    val timeoutsRemaining: Int? = null,
    val bonusActive: Boolean? = null
)

@Serializable
data class StandingsResponse(
    val conference: String,
    val season: String,
    val standings: List<TeamStanding>
)

@Serializable
data class TeamStanding(
    val rank: Int,
    val teamId: String,
    val teamName: String,
    val abbreviation: String,
    val wins: Int,
    val losses: Int,
    val winPercentage: Double,
    val gamesBack: Double,
    val streak: String,
    val lastTen: String
)

@Serializable
data class LeagueSummaryResponse(
    val sportsLeague: String,
    val leagueFullName: String,
    val season: String,
    val seasonType: String,
    val teams: List<TeamInfo> = emptyList(),
    val featuredPlayer: FeaturedPlayer? = null
)

@Serializable
data class TeamInfo(
    val teamId: String,
    val name: String,
    val abbreviation: String,
    val conference: String,
    val division: String,
    val arena: String,
    val city: String,
    val primaryColor: String,
    val secondaryColor: String
)

@Serializable
data class FeaturedPlayer(
    val playerId: String,
    val name: String,
    val position: String,
    val teamName: String,
    val stats: Map<String, Double>
)

@Serializable
data class PlayByPlayResponse(
    val gameId: String,
    val period: Int,
    val plays: List<Play>
)

@Serializable
data class Play(
    val playId: String,
    val clock: String,
    val period: Int,
    val teamId: String?,
    val playType: String,
    val description: String,
    val player: PlayerRef?,
    val assistPlayer: PlayerRef? = null,
    val scoreHome: Int,
    val scoreAway: Int,
    val coordinates: Coordinates? = null
)

@Serializable
data class PlayerRef(
    val playerId: String,
    val name: String
)

@Serializable
data class Coordinates(
    val x: Double,
    val y: Double
)
