package com.lernki.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lernki.app.data.remote.ChatMessageDto
import com.lernki.app.data.repository.AiRepository
import com.lernki.app.util.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiMessage(val isUser: Boolean, val text: String)

data class ChatUiState(
    val contextImage: Bitmap? = null,
    val contextText: String? = null,
    val messages: List<ChatUiMessage> = emptyList(),
    val currentInput: String = "",
    val isSending: Boolean = false,
    val errorMessage: String? = null
)

class ChatViewModel(private val aiRepository: AiRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState

    fun setContextImage(bitmap: Bitmap?) = _uiState.update { it.copy(contextImage = bitmap) }
    fun setContextText(text: String?) = _uiState.update { it.copy(contextText = text) }
    fun onInputChanged(text: String) = _uiState.update { it.copy(currentInput = text) }

    fun sendMessage() {
        val state = _uiState.value
        val question = state.currentInput.trim()
        if (question.isEmpty()) return

        val updatedMessages = state.messages + ChatUiMessage(isUser = true, text = question)
        _uiState.update {
            it.copy(messages = updatedMessages, currentInput = "", isSending = true, errorMessage = null)
        }

        viewModelScope.launch {
            val history = updatedMessages.map {
                ChatMessageDto(role = if (it.isUser) "user" else "assistant", content = it.text)
            }
            val images = state.contextImage?.let { listOf(ImageUtils.bitmapToPayload(it)) } ?: emptyList()

            val result = aiRepository.chat(
                messages = history,
                contextText = state.contextText,
                contextImages = images
            )
            result.onSuccess { reply ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        messages = it.messages + ChatUiMessage(isUser = false, text = reply)
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isSending = false, errorMessage = "Antwort fehlgeschlagen: ${e.message ?: "Fehler"}")
                }
            }
        }
    }
}
