package com.pagenest.pdf.data.repository

import com.pagenest.pdf.data.local.dao.BookmarkDao
import com.pagenest.pdf.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getBookmarks(documentUri: String): Flow<List<BookmarkEntity>> =
        bookmarkDao.getBookmarksForDocument(documentUri)

    suspend fun isBookmarked(documentUri: String, page: Int): Boolean =
        bookmarkDao.getBookmarkForPage(documentUri, page) != null

    suspend fun toggleBookmark(documentUri: String, page: Int, title: String): Boolean {
        val existing = bookmarkDao.getBookmarkForPage(documentUri, page)
        return if (existing != null) {
            bookmarkDao.deleteBookmarkForPage(documentUri, page)
            false
        } else {
            bookmarkDao.insertBookmark(
                BookmarkEntity(
                    documentUri = documentUri,
                    pageNumber = page,
                    title = title
                )
            )
            true
        }
    }

    suspend fun deleteBookmark(id: Long) {
        bookmarkDao.deleteBookmarkById(id)
    }
}
