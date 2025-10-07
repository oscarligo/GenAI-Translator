package com.example.translator.data.repository

import com.example.translator.data.TranslationResult
import com.example.translator.data.remote.OpenAiApi
import com.example.translator.data.remote.OpenAiMessage
import com.example.translator.data.remote.OpenAiRequest
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import android.util.Log

class OpenAiRepository(private val apiKey: String) {
    private val api: OpenAiApi

    init {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val req = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
                chain.proceed(req)
            }
            .addInterceptor(logging)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        api = retrofit.create(OpenAiApi::class.java)
    }

    suspend fun translateWithDetails(
        phrase: String,
        targetLang: String,
        sourceLang: String = "auto"
    ): TranslationResult? {
        Log.d("OpenAiRepository", "Iniciando traducción: '$phrase' de $sourceLang a $targetLang")
        val systemPrompt = "Eres un traductor experto. Si sourceLang=auto, detecta automáticamente el idioma de entrada. Traduce la frase o palabra de $sourceLang a $targetLang. Devuelve la traducción exacta, explica si es formal o casual, si no hay traducción directa, cómo se transmite la idea, y da sinónimos y formas similares. Responde en JSON con los campos: translation (string), usage (string), notes (string), synonyms (string[])."
        val messages = listOf(
            OpenAiMessage(role = "system", content = systemPrompt),
            OpenAiMessage(role = "user", content = phrase)
        )
        val request = OpenAiRequest(messages = messages)

        return try {
            Log.d("OpenAiRepository", "Enviando request a OpenAI...")
            val response = api.getChatCompletion(request)
            Log.d("OpenAiRepository", "Response code: ${response.code()}, isSuccessful: ${response.isSuccessful}")

            if (response.isSuccessful) {
                val aiMessage = response.body()?.choices?.firstOrNull()?.message?.content
                Log.d("OpenAiRepository", "Respuesta de OpenAI: $aiMessage")
                val result = parseTranslationResult(aiMessage)
                Log.d("OpenAiRepository", "Resultado parseado: $result")
                result
            } else {
                Log.e("OpenAiRepository", "Error en response: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("OpenAiRepository", "Excepción al llamar a OpenAI: ${e.message}", e)
            null
        }
    }

    private fun parseTranslationResult(json: String?): TranslationResult? {
        if (json == null) {
            Log.w("OpenAiRepository", "JSON es null, no se puede parsear")
            return null
        }
        return try {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
            val adapter = moshi.adapter(TranslationResult::class.java)
            adapter.fromJson(json)
        } catch (e: Exception) {
            Log.e("OpenAiRepository", "Error al parsear JSON: ${e.message}", e)
            Log.e("OpenAiRepository", "JSON recibido: $json")
            null
        }
    }
}
