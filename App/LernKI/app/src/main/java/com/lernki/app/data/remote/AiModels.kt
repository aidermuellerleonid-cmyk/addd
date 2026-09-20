package com.lernki.app.data.remote

import kotlinx.serialization.Serializable

/**
 * Diese Modelle bilden die Schnittstelle zwischen App und Backend
 * (siehe /server). Das Backend uebersetzt sie in Anfragen an das
 * jeweils konfigurierte KI-Modell. Damit ist das KI-Modell austauschbar,
 * ohne dass sich an der App etwas aendern muss (Abschnitt 12).
 */

enum class SummaryStyle { KURZ, NORMAL, AUSFUEHRLICH, STICHPUNKTE, LERNZETTEL }

@Serializable
data class ImagePayload(
    val base64: String,
    val mimeType: String
)

@Serializable
data class SummarizeRequest(
    val text: String? = null,
    val images: List<ImagePayload> = emptyList(),
    val style: String
)

@Serializable
data class SummarizeResponse(
    val title: String,
    val markdown: String,
    val wasTextUnclear: Boolean = false
)

@Serializable
data class MathSolveRequest(
    val text: String? = null,
    val images: List<ImagePayload> = emptyList()
)

@Serializable
data class MathTask(
    val taskNumber: Int,
    val recognizedProblem: String,
    val steps: List<String>,
    val finalAnswer: String,
    val confidenceIsLow: Boolean = false,
    val note: String? = null
)

@Serializable
data class MathSolveResponse(
    val tasks: List<MathTask>
)

@Serializable
data class ImageClassifyRequest(
    val image: ImagePayload
)

enum class ImageContentType {
    TEXT, MATH_TASK, DIAGRAM, TABLE, WORKSHEET, DRAWING, FORMULA, UNKNOWN
}

@Serializable
data class ImageClassifyResponse(
    val detectedType: String,
    val confidenceIsLow: Boolean,
    val shortDescription: String
)

@Serializable
data class LearningMaterialRequest(
    val text: String? = null,
    val images: List<ImagePayload> = emptyList(),
    val materialType: String // SUMMARY, LERNZETTEL, FLASHCARDS, QUIZ, KEY_TERMS, QUESTIONS, EXAM_SIM
)

@Serializable
data class Flashcard(val front: String, val back: String)

@Serializable
data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val explanation: String
)

@Serializable
data class LearningMaterialResponse(
    val title: String,
    val markdown: String? = null,
    val flashcards: List<Flashcard>? = null,
    val quiz: List<QuizQuestion>? = null
)

@Serializable
data class ChatMessageDto(
    val role: String, // "user" oder "assistant"
    val content: String
)

@Serializable
data class ChatRequest(
    val messages: List<ChatMessageDto>,
    val contextText: String? = null,
    val contextImages: List<ImagePayload> = emptyList()
)

@Serializable
data class ChatResponse(
    val reply: String
)
