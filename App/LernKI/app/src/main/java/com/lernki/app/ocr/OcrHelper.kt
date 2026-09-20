package com.lernki.app.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * On-Device Texterkennung (OCR). Wird primaer genutzt, um die Bildqualitaet
 * grob einzuschaetzen (Abschnitt 11: "keine Informationen erfinden" bei
 * unscharfen Bildern) - die eigentliche inhaltliche Erkennung/Loesung
 * uebernimmt danach das KI-Modell im Backend, das das Bild ebenfalls
 * erhaelt und so auch Diagramme, Tabellen und Zeichnungen versteht.
 */
object OcrHelper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    data class OcrResult(
        val recognizedText: String,
        val isLikelyReadable: Boolean
    )

    suspend fun recognize(bitmap: Bitmap): OcrResult = suspendCancellableCoroutine { cont ->
        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { result ->
                val text = result.text
                // Sehr kurze/leere Erkennung deutet auf ein unscharfes oder
                // ungeeignetes Bild hin.
                val readable = text.trim().length >= 6
                cont.resume(OcrResult(recognizedText = text, isLikelyReadable = readable))
            }
            .addOnFailureListener { e ->
                if (cont.isActive) cont.resumeWithException(e)
            }
    }
}
