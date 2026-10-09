package com.pagenest.pdf.domain.manager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class PdfViewerManager(private val context: Context) {

    private var currentPfd: ParcelFileDescriptor? = null
    private var currentRenderer: PdfRenderer? = null
    private var currentUri: Uri? = null
    private val renderMutex = Mutex()

    // 24MB bitmap memory cache
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = (maxMemory / 8).coerceAtMost(24 * 1024) // in KB
    private val bitmapCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    var pageCount: Int = 0
        private set

    suspend fun openDocument(uri: Uri): Result<Int> = withContext(Dispatchers.IO) {
        renderMutex.withLock {
            closeDocumentLocked()
            try {
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                    ?: return@withLock Result.failure(Exception("Cannot open file descriptor"))
                val renderer = PdfRenderer(pfd)
                currentPfd = pfd
                currentRenderer = renderer
                currentUri = uri
                pageCount = renderer.pageCount
                Result.success(pageCount)
            } catch (e: Exception) {
                closeDocumentLocked()
                Result.failure(e)
            }
        }
    }

    suspend fun renderPage(
        pageIndex: Int,
        targetWidth: Int = 1080
    ): Bitmap? = withContext(Dispatchers.IO) {
        val uriKey = currentUri?.toString() ?: return@withContext null
        val cacheKey = "${uriKey}_${pageIndex}_$targetWidth"

        bitmapCache.get(cacheKey)?.let {
            return@withContext it
        }

        renderMutex.withLock {
            val renderer = currentRenderer ?: return@withContext null
            if (pageIndex < 0 || pageIndex >= renderer.pageCount) return@withContext null

            var page: PdfRenderer.Page? = null
            try {
                page = renderer.openPage(pageIndex)
                val pageWidth = page.width
                val pageHeight = page.height

                val scale = targetWidth.toFloat() / pageWidth.toFloat()
                val targetHeight = (pageHeight * scale).toInt().coerceAtLeast(1)

                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                // White background to preserve document paper integrity
                bitmap.eraseColor(Color.WHITE)

                val matrix = Matrix().apply {
                    postScale(scale, scale)
                }

                page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                bitmapCache.put(cacheKey, bitmap)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                try {
                    page?.close()
                } catch (e: Exception) {
                    // Ignore page close exceptions
                }
            }
        }
    }

    suspend fun renderThumbnail(uri: Uri, targetWidth: Int = 300): Bitmap? = withContext(Dispatchers.IO) {
        val cacheKey = "thumb_${uri}_$targetWidth"
        bitmapCache.get(cacheKey)?.let { return@withContext it }

        try {
            val pfd = context.contentResolver.openFileDescriptor(uri, "r") ?: return@withContext null
            pfd.use {
                val renderer = PdfRenderer(it)
                if (renderer.pageCount > 0) {
                    val page = renderer.openPage(0)
                    val scale = targetWidth.toFloat() / page.width.toFloat()
                    val targetHeight = (page.height * scale).toInt().coerceAtLeast(1)
                    val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                    bitmap.eraseColor(Color.WHITE)
                    val matrix = Matrix().apply { postScale(scale, scale) }
                    page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    page.close()
                    renderer.close()
                    bitmapCache.put(cacheKey, bitmap)
                    bitmap
                } else {
                    renderer.close()
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun closeDocument() {
        closeDocumentLocked()
    }

    private fun closeDocumentLocked() {
        try {
            currentRenderer?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentRenderer = null
        }

        try {
            currentPfd?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentPfd = null
        }
        pageCount = 0
        currentUri = null
    }
}
