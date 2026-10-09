package com.pagenest.pdf.ui.screens.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.pagenest.pdf.ui.components.ReaderScrubberCapsule
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    documentUri: String,
    documentName: String,
    initialPage: Int,
    viewModel: ReaderViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpPageInput by remember { mutableStateOf("") }

    var scale by remember { mutableFloatStateOf(1f) }
    val transformState = rememberTransformableState { zoomChange, _, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 3f)
    }

    LaunchedEffect(documentUri) {
        viewModel.loadDocument(documentUri, documentName, initialPage)
    }

    // Scroll to initial page when ready
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && uiState.totalPages > 0) {
            val targetIndex = (uiState.currentPage - 1).coerceIn(0, uiState.totalPages - 1)
            listState.scrollToItem(targetIndex)
        }
    }

    // Track visible page during scrolling
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { firstIndex ->
                viewModel.onPageChanged(firstIndex + 1)
            }
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = uiState.showControls,
                enter = fadeIn() + slideInVertically { -it },
                exit = fadeOut() + slideOutVertically { -it }
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = uiState.documentName,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (uiState.totalPages > 0) {
                                Text(
                                    text = "Page ${uiState.currentPage} of ${uiState.totalPages}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            jumpPageInput = uiState.currentPage.toString()
                            showJumpDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Pin,
                                contentDescription = "Jump to page"
                            )
                        }

                        IconButton(onClick = { /* Bookmark action */ }) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        containerColor = Color(0xFFE8ECEF) // Neutral canvas contrast around paper pages
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    viewModel.toggleControls()
                }
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "Failed to open document",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                // PDF Pages List with Zoom gesture
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .transformable(transformState)
                ) {
                    items(uiState.totalPages, key = { it }) { pageIndex ->
                        PdfPageView(
                            pageIndex = pageIndex,
                            pdfViewerManager = viewModel.pdfViewerManager
                        )
                    }
                }

                // Floating Scrubber Capsule
                ReaderScrubberCapsule(
                    visible = uiState.showControls,
                    currentPage = uiState.currentPage,
                    totalPages = uiState.totalPages,
                    onPageSelected = { page ->
                        viewModel.onPageChanged(page)
                        coroutineScope.launch {
                            listState.animateScrollToItem(page - 1)
                        }
                    },
                    onZoomIn = { scale = (scale + 0.25f).coerceAtMost(3f) },
                    onZoomOut = { scale = (scale - 0.25f).coerceAtLeast(1f) },
                    onFitWidth = { scale = 1f },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }

    // Jump to Page Dialog
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text("Jump to Page") },
            text = {
                Column {
                    Text(
                        text = "Enter a page number between 1 and ${uiState.totalPages}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = jumpPageInput,
                        onValueChange = { jumpPageInput = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val target = jumpPageInput.toIntOrNull()
                        if (target != null && target in 1..uiState.totalPages) {
                            viewModel.onPageChanged(target)
                            coroutineScope.launch {
                                listState.scrollToItem(target - 1)
                            }
                            showJumpDialog = false
                        }
                    }
                ) {
                    Text("Go")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
