package com.pagenest.pdf.ui.screens.themes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pagenest.pdf.domain.model.AppTheme
import java.util.UUID

val PalettePresets = listOf(
    0xFF4969D8, 0xFF2C4FBE, 0xFF1E88E5, 0xFF00897B,
    0xFF43A047, 0xFFE53935, 0xFF8E24AA, 0xFFD81B60,
    0xFFFB8C00, 0xFFFDD835, 0xFF3949AB, 0xFF00ACC1
)

val BackgroundPresets = listOf(
    0xFFF8F9FB, 0xFFFFFFFF, 0xFFF5EFEB, 0xFF111318,
    0xFF10141C, 0xFF000000, 0xFF121A15, 0xFF0E1A24
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditorScreen(
    initialTheme: AppTheme?,
    onSaveTheme: (AppTheme) -> Unit,
    onBack: () -> Unit
) {
    var themeName by remember { mutableStateOf(initialTheme?.name ?: "Custom Theme") }
    var isDark by remember { mutableStateOf(initialTheme?.isDark ?: false) }
    var primaryColor by remember { mutableLongStateOf(initialTheme?.primaryColor ?: 0xFF4969D8) }
    var backgroundColor by remember { mutableLongStateOf(initialTheme?.backgroundColor ?: 0xFFF8F9FB) }
    var surfaceColor by remember { mutableLongStateOf(initialTheme?.surfaceColor ?: 0xFFFFFFFF) }
    var textColor by remember { mutableLongStateOf(initialTheme?.primaryTextColor ?: 0xFF191C1E) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme Editor") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Live Interactive Preview Card
            Text(
                text = "Live Preview",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(backgroundColor))
                    .border(1.dp, Color(primaryColor).copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(surfaceColor))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(primaryColor).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = null,
                                    tint = Color(primaryColor),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Machine Learning.pdf",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color(textColor),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "420 Pages  •  42% Progress",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(textColor).copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(40.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(primaryColor),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Active Action Button", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Theme Name Field
            OutlinedTextField(
                value = themeName,
                onValueChange = { themeName = it },
                label = { Text("Theme Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Dark Mode Flag Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Dark Palette Variant",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Optimizes contrast for nighttime reading",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = isDark,
                    onCheckedChange = { checked ->
                        isDark = checked
                        if (checked) {
                            backgroundColor = 0xFF111318
                            surfaceColor = 0xFF1B1E25
                            textColor = 0xFFE2E2EA
                        } else {
                            backgroundColor = 0xFFF8F9FB
                            surfaceColor = 0xFFFFFFFF
                            textColor = 0xFF191C1E
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(primaryColor)
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Color Swatches
            ColorPickerSection(
                title = "Primary Accent Color",
                currentColor = primaryColor,
                presets = PalettePresets,
                onColorSelected = { primaryColor = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Background Color Swatches
            ColorPickerSection(
                title = "Canvas Background Color",
                currentColor = backgroundColor,
                presets = BackgroundPresets,
                onColorSelected = { backgroundColor = it }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Save Action
            Button(
                onClick = {
                    val themeId = initialTheme?.id ?: ("custom_" + UUID.randomUUID().toString().take(8))
                    val newTheme = AppTheme(
                        id = themeId,
                        name = themeName.ifBlank { "Custom Theme" },
                        isBuiltIn = false,
                        isDark = isDark,
                        backgroundColor = backgroundColor,
                        surfaceColor = surfaceColor,
                        primaryColor = primaryColor,
                        secondaryColor = primaryColor,
                        primaryTextColor = textColor,
                        secondaryTextColor = textColor,
                        toolbarColor = surfaceColor,
                        navigationColor = surfaceColor,
                        borderColor = primaryColor
                    )
                    onSaveTheme(newTheme)
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Save and Apply Theme", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerSection(
    title: String,
    currentColor: Long,
    presets: List<Long>,
    onColorSelected: (Long) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            presets.forEach { colorValue ->
                val selected = colorValue == currentColor
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(colorValue))
                        .border(
                            width = if (selected) 2.5.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(colorValue) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = if (colorValue == 0xFFFFFFFFL || colorValue == 0xFFF8F9FBL) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
