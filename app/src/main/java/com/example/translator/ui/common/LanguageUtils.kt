package com.example.translator.ui.common

// Utilidad simple para mapear código de idioma a bandera y nombre mostrable

data class Language(val code: String, val name: String, val flag: String)

val supportedLanguages = listOf(
    Language("en", "Inglés", "\uD83C\uDDFA\uD83C\uDDF8"), // 🇺🇸
    Language("es", "Español", "\uD83C\uDDEA\uD83C\uDDF8"), // 🇪🇸
    Language("fr", "Francés", "\uD83C\uDDEB\uD83C\uDDF7"), // 🇫🇷
    Language("de", "Alemán", "\uD83C\uDDE9\uD83C\uDDEA"), // 🇩🇪
    Language("it", "Italiano", "\uD83C\uDDEE\uD83C\uDDF9"), // 🇮🇹
    Language("pt", "Portugués", "\uD83C\uDDF5\uD83C\uDDF9"), // 🇵🇹
    Language("ja", "Japonés", "\uD83C\uDDEF\uD83C\uDDF5"), // 🇯🇵
    Language("zh", "Chino", "\uD83C\uDDE8\uD83C\uDDF3")  // 🇨🇳
)

fun getLanguageOrDefault(code: String?): Language =
    supportedLanguages.find { it.code.equals(code, ignoreCase = true) } ?: supportedLanguages.first()

