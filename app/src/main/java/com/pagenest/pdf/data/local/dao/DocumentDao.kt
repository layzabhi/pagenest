package com.pagenest.pdf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pagenest.pdf.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE folderUriString = :folderUri")
    fun getDocumentsByFolder(folderUri: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE uriString = :uri LIMIT 1")
    suspend fun getDocumentByUri(uri: String): DocumentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>)

    @Query("DELETE FROM documents WHERE folderUriString = :folderUri")
    suspend fun deleteByFolder(folderUri: String)

    @Query("DELETE FROM documents")
    suspend fun clearAll()
}
