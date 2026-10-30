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

import java.io.File
import java.io.FileOutputStream

class PdfViewerManager(private val context: Context) {

    private var currentPfd: ParcelFileDescriptor? = null
    private var currentRenderer: PdfRenderer? = null
    private var currentTempFile: File? = null
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
                var pfd: ParcelFileDescriptor? = null
                var renderer: PdfRenderer? = null
                var tempFile: File? = null

                // Attempt 1: Direct ContentResolver descriptor
                try {
                    val directPfd = context.contentResolver.openFileDescriptor(uri, "r")
                    if (directPfd != null) {
                        pfd = directPfd
                        renderer = PdfRenderer(directPfd)
                    }
                } catch (directException: Exception) {
                    try { renderer?.close() } catch (_: Exception) {}
                    try { pfd?.close() } catch (_: Exception) {}
                    renderer = null
                    pfd = null
                }

                // Attempt 2: If direct opening failed (non-seekable stream, pipe, cloud URI, or permission)
                if (renderer == null || pfd == null) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                        ?: return@withLock Result.failure(Exception("Cannot open document stream"))

                    val cacheFile = File(context.cacheDir, "reader_${System.currentTimeMillis()}.pdf")
                    FileOutputStream(cacheFile).use { output ->
                        inputStream.copyTo(output)
                    }

                    if (cacheFile.length() == 0L) {
                        cacheFile.delete()
                        return@withLock Result.failure(Exception("The selected PDF file is empty (0 bytes)."))
                    }

                    tempFile = cacheFile
                    val cachedPfd = ParcelFileDescriptor.open(cacheFile, ParcelFileDescriptor.MODE_READ_ONLY)
                    pfd = cachedPfd
                    renderer = PdfRenderer(cachedPfd)
                }

                currentPfd = pfd
                currentRenderer = renderer
                currentTempFile = tempFile
                currentUri = uri
                pageCount = renderer.pageCount
                Result.success(pageCount)
            } catch (e: Exception) {
                closeDocumentLocked()
                val friendlyMessage = when {
                    e.message?.contains("password", ignoreCase = true) == true ->
                        "This PDF document is password-protected and cannot be opened."
                    e.message?.contains("unsupported", ignoreCase = true) == true ||
                    e.message?.contains("cannot create document", ignoreCase = true) == true ->
                        "Cannot open this file. It may be corrupted or not a valid PDF."
                    else -> e.message ?: "Failed to open PDF document"
                }
                Result.failure(Exception(friendlyMessage, e))
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

        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null
        var tempFile: File? = null

        try {
            // Attempt direct descriptor
            try {
                val directPfd = context.contentResolver.openFileDescriptor(uri, "r")
                if (directPfd != null) {
                    pfd = directPfd
                    renderer = PdfRenderer(directPfd)
                }
            } catch (_: Exception) {
                try { renderer?.close() } catch (_: Exception) {}
                try { pfd?.close() } catch (_: Exception) {}
                renderer = null
                pfd = null
            }

            // Fallback for stream URIs
            if (renderer == null || pfd == null) {
                val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
                val cache = File(context.cacheDir, "thumb_${System.currentTimeMillis()}.pdf")
                FileOutputStream(cache).use { output -> inputStream.copyTo(output) }
                if (cache.length() > 0L) {
                    tempFile = cache
                    val cachedPfd = ParcelFileDescriptor.open(cache, ParcelFileDescriptor.MODE_READ_ONLY)
                    pfd = cachedPfd
                    renderer = PdfRenderer(cachedPfd)
                } else {
                    cache.delete()
                    return@withContext null
                }
            }

            val r = renderer ?: return@withContext null
            if (r.pageCount > 0) {
                val page = r.openPage(0)
                val scale = targetWidth.toFloat() / page.width.toFloat()
                val targetHeight = (page.height * scale).toInt().coerceAtLeast(1)
                val bitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(Color.WHITE)
                val matrix = Matrix().apply { postScale(scale, scale) }
                page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()
                bitmapCache.put(cacheKey, bitmap)
                bitmap
            } else {
                null
            }
        } catch (e: Exception) {
            null
        } finally {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            try { tempFile?.delete() } catch (_: Exception) {}
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

        try {
            currentTempFile?.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            currentTempFile = null
        }

        pageCount = 0
        currentUri = null
    }
}
