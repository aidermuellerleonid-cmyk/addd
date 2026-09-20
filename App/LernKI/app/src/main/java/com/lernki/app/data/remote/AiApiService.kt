package com.lernki.app.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Schnittstelle zum eigenen Backend (siehe /server/index.js).
 * Die App spricht NIE direkt mit einem KI-Anbieter, sondern immer nur
 * mit diesem Backend. So bleibt der API-Key serverseitig, und das
 * eingesetzte KI-Modell kann rein im Backend ausgetauscht werden,
 * ohne die App neu bauen zu muessen (siehe Abschnitt 12).
 */
interface AiApiService {

    @POST("api/summarize")
    suspend fun summarize(@Body request: SummarizeRequest): SummarizeResponse

    @POST("api/solve-math")
    suspend fun solveMath(@Body request: MathSolveRequest): MathSolveResponse

    @POST("api/classify-image")
    suspend fun classifyImage(@Body request: ImageClassifyRequest): ImageClassifyResponse

    @POST("api/learning-material")
    suspend fun generateLearningMaterial(@Body request: LearningMaterialRequest): LearningMaterialResponse

    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
