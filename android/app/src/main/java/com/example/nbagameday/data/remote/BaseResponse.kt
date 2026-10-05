package com.example.nbagameday.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
    val status: String,
    val data: T?,
    val error: ErrorDetails? = null,
    val meta: Meta? = null
)

@Serializable
data class ErrorDetails(
    val code: String,
    val message: String,
    val details: Map<String, String>? = null
)

@Serializable
data class Meta(
    val requestId: String,
    val timestamp: String
)
