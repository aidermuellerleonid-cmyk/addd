package com.lernki.app.data.repository

import com.lernki.app.data.remote.AiApiService
import com.lernki.app.data.remote.ChatMessageDto
import com.lernki.app.data.remote.ChatRequest
import com.lernki.app.data.remote.ImageClassifyRequest
import com.lernki.app.data.remote.ImageClassifyResponse
import com.lernki.app.data.remote.ImagePayload
import com.lernki.app.data.remote.LearningMaterialRequest
import com.lernki.app.data.remote.LearningMaterialResponse
import com.lernki.app.data.remote.MathSolveRequest
import com.lernki.app.data.remote.MathSolveResponse
import com.lernki.app.data.remote.SummarizeRequest
import com.lernki.app.data.remote.SummarizeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackendAiRepository(
    private val api: AiApiService
) : AiRepository {

    override suspend fun summarize(
        text: String?,
        images: List<ImagePayload>,
        style: String
    ): Result<SummarizeResponse> = safeCall {
        api.summarize(SummarizeRequest(text = text, images = images, style = style))
    }

    override suspend fun solveMath(
        text: String?,
        images: List<ImagePayload>
    ): Result<MathSolveResponse> = safeCall {
        api.solveMath(MathSolveRequest(text = text, images = images))
    }

    override suspend fun classifyImage(image: ImagePayload): Result<ImageClassifyResponse> = safeCall {
        api.classifyImage(ImageClassifyRequest(image = image))
    }

    override suspend fun generateLearningMaterial(
        text: String?,
        images: List<ImagePayload>,
        materialType: String
    ): Result<LearningMaterialResponse> = safeCall {
        api.generateLearningMaterial(
            LearningMaterialRequest(text = text, images = images, materialType = materialType)
        )
    }

    override suspend fun chat(
        messages: List<ChatMessageDto>,
        contextText: String?,
        contextImages: List<ImagePayload>
    ): Result<String> = safeCall {
        api.chat(ChatRequest(messages = messages, contextText = contextText, contextImages = contextImages)).reply
    }

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
        try {
            Result.success(block())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
