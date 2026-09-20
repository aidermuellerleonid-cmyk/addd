package com.lernki.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Kategorien, unter denen ein Verlaufseintrag im "Verlauf" (Abschnitt 8
 * der Anforderungen) einsortiert wird.
 */
enum class HistoryCategory {
    ZUSAMMENFASSUNG,
    MATHE,
    LERNZETTEL,
    KARTEIKARTEN,
    QUIZ,
    CHAT
}

/**
 * Ein gespeichertes KI-Ergebnis. Deckt Zusammenfassungen, Mathe-Loesungen,
 * Lernzettel, Karteikarten, Quizze und Chat-Antworten ab, damit alle
 * Ergebnistypen (Abschnitt 7) einheitlich bearbeitet, geloescht,
 * erneut geoeffnet und geteilt werden koennen.
 */
@Entity(tableName = "history_items")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: HistoryCategory,
    val title: String,
    val content: String,
    val sourcePreviewPath: String? = null,
    val createdAtEpochMillis: Long = System.currentTimeMillis()
)
