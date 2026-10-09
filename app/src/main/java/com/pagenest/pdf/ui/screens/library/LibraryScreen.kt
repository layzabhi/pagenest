package com.pagenest.pdf.ui.screens.library

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagenest.pdf.domain.model.PdfDocument
import com.pagenest.pdf.domain.model.SortOption
import com.pagenest.pdf.domain.model.ViewMode
import com.pagenest.pdf.ui.components.DocumentGridCard
import com.pagenest.pdf.ui.components.DocumentListItem
import com.pagenest.pdf.ui.components.EmptyStateView
import com.pagenest.pdf.ui.components.FilterChipGroup
import com.pagenest.pdf.ui.components.PageNestSearchBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    onOpenDocument: (PdfDocument, Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val thumbnails by viewModel.thumbnails.collectAsState()
    var showSortSheet by remember { mutableStateOf(false) }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { treeUri: Uri? ->
        if (treeUri != null) {
            val folderName = treeUri.lastPathSegment?.split(":")?.lastOrNull() ?: "Selected Folder"
            viewModel.onFolderSelected(treeUri, folderName)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { folderPickerLauncher.launch(null) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CreateNewFolder,
                    contentDescription = "Select Folder"
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header / Search Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                PageNestSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = viewModel::onSearchQueryChange,
                    onSortClick = { showSortSheet = true }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filter chips & view toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChipGroup(
                        selectedFilter = uiState.activeFilter,
                        onFilterSelected = viewModel::onFilterSelected,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                viewModel.setViewMode(
                                    if (uiState.viewMode == ViewMode.GRID) ViewMode.LIST else ViewMode.GRID
                                )
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.viewMode == ViewMode.GRID) Icons.Default.ViewList else Icons.Default.GridView,
                                contentDescription = "Toggle View",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (uiState.selectedFolderUri != null) {
                            IconButton(
                                onClick = viewModel::refreshFolder,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }

            // Current Folder Banner (if selected)
            if (uiState.selectedFolderName != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = uiState.selectedFolderName ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "${uiState.documents.size} files",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Content Area
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState.selectedFolderUri == null) {
                EmptyStateView(
                    icon = Icons.Default.Folder,
                    title = "No Folder Selected",
                    description = "Choose a folder on your device to discover and organize your PDF documents.",
                    actionLabel = "Select Folder",
                    onActionClick = { folderPickerLauncher.launch(null) },
                    modifier = Modifier.weight(1f)
                )
            } else if (uiState.documents.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Folder,
                    title = "No PDF Documents Found",
                    description = "The selected folder does not contain any PDF files. You can refresh or choose another folder.",
                    actionLabel = "Change Folder",
                    onActionClick = { folderPickerLauncher.launch(null) },
                    modifier = Modifier.weight(1f)
                )
            } else {
                if (uiState.viewMode == ViewMode.GRID) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(uiState.documents, key = { it.uriString }) { doc ->
                            LaunchedEffect(doc.uriString) {
                                viewModel.loadThumbnail(doc)
                            }
                            val progress = uiState.progressMap[doc.uriString]
                            DocumentGridCard(
                                document = doc,
                                progress = progress,
                                thumbnailBitmap = thumbnails[doc.uriString],
                                onClick = {
                                    val startPage = progress?.lastViewedPage ?: 1
                                    onOpenDocument(doc, startPage)
                                },
                                onMenuAction = { /* Handle action */ }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f)
                    ) {
                        items(uiState.documents, key = { it.uriString }) { doc ->
                            LaunchedEffect(doc.uriString) {
                                viewModel.loadThumbnail(doc)
                            }
                            val progress = uiState.progressMap[doc.uriString]
                            DocumentListItem(
                                document = doc,
                                progress = progress,
                                thumbnailBitmap = thumbnails[doc.uriString],
                                onClick = {
                                    val startPage = progress?.lastViewedPage ?: 1
                                    onOpenDocument(doc, startPage)
                                },
                                onMenuAction = { /* Handle action */ }
                            )
                        }
                    }
                }
            }
        }
    }

    // Sort Bottom Sheet
    if (showSortSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSortSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "Sort Documents By",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                SortOption.entries.forEach { option ->
                    val selected = option == uiState.sortOption
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.setSortOption(option)
                                showSortSheet = false
                            }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected,
                            onClick = {
                                viewModel.setSortOption(option)
                                showSortSheet = false
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
