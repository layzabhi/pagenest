package com.pagenest.pdf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pagenest.pdf.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks WHERE documentUri = :documentUri ORDER BY pageNumber ASC")
    fun getBookmarksForDocument(documentUri: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE documentUri = :documentUri AND pageNumber = :page LIMIT 1")
    suspend fun getBookmarkForPage(documentUri: String, page: Int): BookmarkEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE documentUri = :documentUri AND pageNumber = :page")
    suspend fun deleteBookmarkForPage(documentUri: String, page: Int)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
}
