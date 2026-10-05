package com.example.nbagameday.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import kotlinx.serialization.json.Json

class AuthInterceptor(
    private val clientId: String,
    private val clientSecret: String
) : Interceptor {

    private var accessToken: String? = null
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // Skip auth for the token endpoint itself
        if (request.url.encodedPath.endsWith("/auth/token")) {
            return chain.proceed(request)
        }

        // Try with current token if we have one
        var tokenToUse = accessToken
        var response: Response? = null

        if (tokenToUse != null) {
            val authenticatedRequest = addTokenToRequest(request, tokenToUse)
            response = chain.proceed(authenticatedRequest)

            // If the token is still valid, return
            if (response.code != 401) {
                return response
            }
            // If 401, we'll try to refresh
            response.close()
        }

        // Need new token (either we didn't have one, or the one we had was expired/rejected)
        synchronized(this) {
            // Check if another thread already refreshed while we were waiting
            if (tokenToUse != accessToken && accessToken != null) {
                val authenticatedRequest = addTokenToRequest(request, accessToken!!)
                return chain.proceed(authenticatedRequest)
            }

            // Fetch new token blocking
            val newToken = fetchNewToken(request)
            if (newToken != null) {
                accessToken = newToken
                val authenticatedRequest = addTokenToRequest(request, newToken)
                return chain.proceed(authenticatedRequest)
            }
        }

        // If all else fails, just proceed without a token or return the last failure
        return response ?: chain.proceed(request)
    }

    private fun addTokenToRequest(request: Request, token: String): Request {
        return request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }

    private fun fetchNewToken(originalRequest: Request): String? {
        try {
            val authRequest = AuthRequest(clientId, clientSecret)
            val requestBody = json.encodeToString(AuthRequest.serializer(), authRequest)
                .toRequestBody("application/json".toMediaType())

            // Base URL is usually the same host but without the path, so just build manually
            val baseUrl = originalRequest.url.newBuilder()
                .encodedPath("/api/v1/auth/token")
                .query(null)
                .build()

            val authCall = Request.Builder()
                .url(baseUrl)
                .post(requestBody)
                .build()

            val client = OkHttpClient()
            val response = client.newCall(authCall).execute()

            if (response.isSuccessful) {
                val bodyStr = response.body?.string()
                if (bodyStr != null) {
                    val authResponse = json.decodeFromString(AuthResponse.serializer(), bodyStr)
                    return authResponse.accessToken
                }
            } else {
                Log.e("AuthInterceptor", "Failed to fetch token: ${response.code}")
            }
        } catch (e: Exception) {
            Log.e("AuthInterceptor", "Error fetching token", e)
        }
        return null
    }
}

