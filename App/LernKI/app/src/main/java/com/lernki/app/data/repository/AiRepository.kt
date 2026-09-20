package com.lernki.app.data.repository

import com.lernki.app.data.remote.ChatMessageDto
import com.lernki.app.data.remote.ImageClassifyResponse
import com.lernki.app.data.remote.ImagePayload
import com.lernki.app.data.remote.LearningMaterialResponse
import com.lernki.app.data.remote.MathSolveResponse
import com.lernki.app.data.remote.SummarizeResponse

/**
 * Trennt die UI/ViewModels vollstaendig von der konkreten KI-Anbindung
 * (Abschnitt 12: "Die KI-Schnittstelle soll so aufgebaut sein, dass das
 * verwendete KI-Modell spaeter einfach ausgetauscht werden kann").
 *
 * Aktuell implementiert durch [BackendAiRepository], das ueber Retrofit
 * das eigene Backend anspricht. Eine alternative Implementierung
 * (z.B. ein anderer Anbieter oder ein rein lokales Modell) muss nur
 * dieses Interface erfuellen - der Rest der App bleibt unveraendert.
 */
interface AiRepository {

    suspend fun summarize(
        text: String?,
        images: List<ImagePayload>,
        style: String
    ): Result<SummarizeResponse>

    suspend fun solveMath(
        text: String?,
        images: List<ImagePayload>
    ): Result<MathSolveResponse>

    suspend fun classifyImage(image: ImagePayload): Result<ImageClassifyResponse>

    suspend fun generateLearningMaterial(
        text: String?,
        images: List<ImagePayload>,
        materialType: String
    ): Result<LearningMaterialResponse>

    suspend fun chat(
        messages: List<ChatMessageDto>,
        contextText: String?,
        contextImages: List<ImagePayload>
    ): Result<String>
}
