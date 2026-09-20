package com.lernki.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lernki.app.data.local.HistoryCategory
import com.lernki.app.data.remote.ImagePayload
import com.lernki.app.data.repository.AiRepository
import com.lernki.app.data.repository.HistoryRepository
import com.lernki.app.util.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SummarizeUiState(
    val inputText: String = "",
    val images: List<Bitmap> = emptyList(),
    val style: String = "Normal",
    val isLoading: Boolean = false,
    val resultTitle: String? = null,
    val resultMarkdown: String? = null,
    val wasUnclear: Boolean = false,
    val errorMessage: String? = null
)

class SummarizeViewModel(
    private val aiRepository: AiRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val availableStyles = listOf("Kurz", "Normal", "Ausführlich", "Stichpunkte", "Lernzettel")

    private val _uiState = MutableStateFlow(SummarizeUiState())
    val uiState: StateFlow<SummarizeUiState> = _uiState

    fun onTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun onStyleSelected(style: String) {
        _uiState.update { it.copy(style = style) }
    }

    fun addImages(bitmaps: List<Bitmap>) {
        _uiState.update { it.copy(images = it.images + bitmaps) }
    }

    fun removeImage(bitmap: Bitmap) {
        _uiState.update { it.copy(images = it.images - bitmap) }
    }

    fun summarize() {
        val state = _uiState.value
        if (state.inputText.isBlank() && state.images.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Bitte Text eingeben oder ein Bild hochladen.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val payloads: List<ImagePayload> = state.images.map { ImageUtils.bitmapToPayload(it) }
            val result = aiRepository.summarize(
                text = state.inputText.ifBlank { null },
                images = payloads,
                style = state.style.uppercase()
            )
            result.onSuccess { response ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        resultTitle = response.title,
                        resultMarkdown = response.markdown,
                        wasUnclear = response.wasTextUnclear
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Die Zusammenfassung ist fehlgeschlagen: ${e.message ?: "Unbekannter Fehler"}"
                    )
                }
            }
        }
    }

    fun saveToHistory() {
        val state = _uiState.value
        val title = state.resultTitle ?: return
        val content = state.resultMarkdown ?: return
        viewModelScope.launch {
            historyRepository.save(HistoryCategory.ZUSAMMENFASSUNG, title, content)
        }
    }

    fun updateResultText(newText: String) {
        _uiState.update { it.copy(resultMarkdown = newText) }
    }

    fun reset() {
        _uiState.value = SummarizeUiState()
    }
}
