package com.example.translator.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TranslationResult(
    val translation: String, // Traducción exacta
    val usage: String, // Explicación de uso (formal/casual, equivalencias)
    val synonyms: List<String>, // Sinónimos y formas similares
    val notes: String // Notas adicionales (si no hay traducción directa, equivalencias)
)

