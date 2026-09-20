package com.lernki.app.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lernki.app.data.repository.AiRepository
import com.lernki.app.util.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ImageAnalysisUiState(
    val image: Bitmap? = null,
    val isAnalyzing: Boolean = false,
    val detectedType: String? = null,
    val shortDescription: String? = null,
    val confidenceIsLow: Boolean = false,
    val errorMessage: String? = null
)

class ImageAnalysisViewModel(private val aiRepository: AiRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ImageAnalysisUiState())
    val uiState: StateFlow<ImageAnalysisUiState> = _uiState

    fun onImageSelected(bitmap: Bitmap) {
        _uiState.value = ImageAnalysisUiState(image = bitmap, isAnalyzing = true)
        viewModelScope.launch {
            val payload = ImageUtils.bitmapToPayload(bitmap)
            aiRepository.classifyImage(payload)
                .onSuccess { response ->
                    _uiState.update {
                        it.copy(
                            isAnalyzing = false,
                            detectedType = response.detectedType,
                            shortDescription = response.shortDescription,
                            confidenceIsLow = response.confidenceIsLow
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isAnalyzing = false,
                            errorMessage = "Bild konnte nicht analysiert werden: ${e.message ?: "Fehler"}"
                        )
                    }
                }
        }
    }

    fun reset() {
        _uiState.value = ImageAnalysisUiState()
    }
}
