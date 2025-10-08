package com.example.translator.ui.common

// Utilidad simple para mapear código de idioma a bandera y nombre mostrable

data class Language(val code: String, val name: String, val flag: String)

data class UiLabels(
    val translation: String,
    val usage: String,
    val notes: String,
    val synonyms: String,
    val inputPlaceholder: String
)

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

fun getUiLabelsFor(code: String?): UiLabels {
    return when (code?.lowercase()) {
        "es" -> UiLabels("Traducción", "Uso", "Notas", "Sinónimos", "Escribe un mensaje…")
        "fr" -> UiLabels("Traduction", "Usage", "Notes", "Synonymes", "Écris un message…")
        "de" -> UiLabels("Übersetzung", "Gebrauch", "Hinweise", "Synonyme", "Nachricht schreiben…")
        "it" -> UiLabels("Traduzione", "Uso", "Note", "Sinonimi", "Scrivi un messaggio…")
        "pt" -> UiLabels("Tradução", "Uso", "Notas", "Sinônimos", "Escreva uma mensagem…")
        "ja" -> UiLabels("翻訳", "使い方", "注意", "類義語", "メッセージを入力…")
        "zh" -> UiLabels("翻译", "用法", "备注", "同义词", "输入消息…")
        else -> UiLabels("Translation", "Usage", "Notes", "Synonyms", "Type a message…")
    }
}
