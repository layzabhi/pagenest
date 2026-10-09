package com.pagenest.pdf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pagenest.pdf.data.local.entity.ReadingProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReadingProgressDao {
    @Query("SELECT * FROM reading_progress ORDER BY lastOpenedTimestamp DESC")
    fun getAllReadingProgress(): Flow<List<ReadingProgressEntity>>

    @Query("SELECT * FROM reading_progress WHERE documentUri = :documentUri LIMIT 1")
    suspend fun getProgressByUri(documentUri: String): ReadingProgressEntity?

    @Query("SELECT * FROM reading_progress WHERE documentUri = :documentUri LIMIT 1")
    fun observeProgressByUri(documentUri: String): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ReadingProgressEntity)

    @Query("DELETE FROM reading_progress WHERE documentUri = :documentUri")
    suspend fun deleteProgress(documentUri: String)

    @Query("DELETE FROM reading_progress")
    suspend fun clearAllHistory()
}
