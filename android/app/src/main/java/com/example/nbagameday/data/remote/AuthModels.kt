package com.example.nbagameday.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class AuthRequest(
    val clientId: String,
    val clientSecret: String,
    val grantType: String = "client_credentials"
)

@Serializable
data class AuthResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Int,
    val scope: String
)
