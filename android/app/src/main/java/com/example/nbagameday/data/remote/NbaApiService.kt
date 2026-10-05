package com.example.nbagameday.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NbaApiService {

    @POST("/api/v1/auth/token")
    suspend fun getToken(
        @Body request: AuthRequest
    ): AuthResponse

    @GET("/api/v1/league/summary")
    suspend fun getLeagueSummary(): BaseResponse<LeagueSummaryResponse>

    @GET("/api/v1/games")
    suspend fun getGames(
        @Query("date") date: String? = null,
        @Query("status") status: String? = null
    ): BaseResponse<GamesResponse>

    @GET("/api/v2/nba/live-scores")
    suspend fun getLiveScores(): BaseResponse<GamesResponse>

    @GET("/api/v1/standings")
    suspend fun getStandings(
        @Query("conference") conference: String? = null,
        @Query("season") season: String? = null
    ): BaseResponse<StandingsResponse>

    @GET("/api/v1/players/{playerId}/stats")
    suspend fun getPlayerStats(
        @Path("playerId") playerId: String,
        @Query("season") season: String? = null
    ): BaseResponse<PlayerStatsResponse>

    @GET("/api/v2/nba/games/{gameId}/play-by-play")
    suspend fun getPlayByPlay(
        @Path("gameId") gameId: String,
        @Query("period") period: Int? = null
    ): BaseResponse<PlayByPlayResponse>
}
