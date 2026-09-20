package com.lernki.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history_items ORDER BY createdAtEpochMillis DESC")
    fun observeAll(): Flow<List<HistoryEntity>>

    @Query(
        """
        SELECT * FROM history_items
        WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'
        ORDER BY createdAtEpochMillis DESC
        """
    )
    fun search(query: String): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_items WHERE category = :category ORDER BY createdAtEpochMillis DESC")
    fun observeByCategory(category: HistoryCategory): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history_items WHERE id = :id")
    suspend fun getById(id: Long): HistoryEntity?

    @Insert
    suspend fun insert(item: HistoryEntity): Long

    @Update
    suspend fun update(item: HistoryEntity)

    @Delete
    suspend fun delete(item: HistoryEntity)

    @Query("DELETE FROM history_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}
