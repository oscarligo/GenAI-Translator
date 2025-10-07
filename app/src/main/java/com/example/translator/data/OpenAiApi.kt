package com.example.translator.data

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.Call

@JsonClass(generateAdapter = true)
data class OpenAiMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class OpenAiRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<OpenAiMessage>,
    @Json(name = "max_tokens") val maxTokens: Int = 512
)

@JsonClass(generateAdapter = true)
data class OpenAiChoice(
    val message: OpenAiMessage
)

@JsonClass(generateAdapter = true)
data class OpenAiResponse(
    val choices: List<OpenAiChoice>
)

interface OpenAiApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    fun getChatCompletion(@Body request: OpenAiRequest): Call<OpenAiResponse>
}

