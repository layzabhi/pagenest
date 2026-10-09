package com.pagenest.pdf.ui.screens.reader

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.pagenest.pdf.domain.manager.PdfViewerManager

@Composable
fun PdfPageView(
    pageIndex: Int,
    pdfViewerManager: PdfViewerManager,
    modifier: Modifier = Modifier
) {
    var pageBitmap by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember(pageIndex) { mutableStateOf(true) }

    LaunchedEffect(pageIndex) {
        isLoading = true
        val bitmap = pdfViewerManager.renderPage(pageIndex)
        pageBitmap = bitmap
        isLoading = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(4.dp))
            .clip(RoundedCornerShape(4.dp))
            // Paper White background is preserved regardless of theme!
            .background(Color.White)
            .border(0.5.dp, Color(0x33000000), RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (pageBitmap != null) {
            Image(
                bitmap = pageBitmap!!.asImageBitmap(),
                contentDescription = "Page ${pageIndex + 1}",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        } else if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.5.dp
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Page ${pageIndex + 1} unavailable",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}
