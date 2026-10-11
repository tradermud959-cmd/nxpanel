package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.NxBackgroundCatalog
import com.example.ui.theme.NxBackgroundPreset
import com.example.ui.theme.NxThemeOption
import com.example.ui.theme.SpaceGroteskFontFamily
import com.example.ui.theme.ZeroPrimeFontFamily

/**
 * Drawer Navigation:
 * Header: ikon icon_apps + tulisan "NXPANEL" + "MENU UTAMA".
 * Menu: Daftar Chat, Setting, Ganti Tema, Ganti Background, Info Aplikasi, Keluar.
 * SEMUA tombol menu pakai double stacked sticker.
 * Tombol aktif = fill accent + text hitam. Tombol tidak aktif = fill CardDark + text terang.
 */
@Composable
fun NxDrawerContent(
    currentRoute: String,
    onNavigateChats: () -> Unit,
    onNavigateSetting: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenBackgroundPicker: () -> Unit,
    onNavigateInfo: () -> Unit,
    onExitApp: () -> Unit
) {
    val nxColors = LocalNxColors.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp)
            .background(color = nxColors.bg)
            .border(width = 3.dp, color = nxColors.borderBlack)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 20.dp)
            .testTag("nx_drawer_content")
    ) {
        // Header: ikon icon_apps + tulisan "NXPANEL" + "MENU UTAMA"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NxComicAppIconFrame(
                size = 54.dp,
                shadowOffset = 4.dp
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                NxHalftoneTitle(
                    text = "NXPANEL",
                    fontSize = 21
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "MENU UTAMA",
                    color = nxColors.accent,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = nxColors.accent.copy(alpha = 0.25f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Daftar Chat
        NxDrawerMenuButton(
            title = "Daftar Chat",
            icon = Icons.Default.Forum,
            isSelected = currentRoute == "home" || currentRoute.startsWith("chat"),
            onClick = onNavigateChats,
            testTag = "drawer_item_chats"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Setting
        NxDrawerMenuButton(
            title = "Setting",
            icon = Icons.Default.Settings,
            isSelected = currentRoute == "setting",
            onClick = onNavigateSetting,
            testTag = "drawer_item_setting"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Info Aplikasi
        NxDrawerMenuButton(
            title = "Info Aplikasi",
            icon = Icons.Default.Info,
            isSelected = currentRoute == "info",
            onClick = onNavigateInfo,
            testTag = "drawer_item_info"
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = nxColors.accent.copy(alpha = 0.25f)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // 4. Ganti Tema
        NxDrawerMenuButton(
            title = "Ganti Tema",
            icon = Icons.Default.Palette,
            isSelected = false,
            onClick = onOpenThemePicker,
            testTag = "drawer_item_theme"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Ganti Background
        NxDrawerMenuButton(
            title = "Ganti Background",
            icon = Icons.Default.Wallpaper,
            isSelected = false,
            onClick = onOpenBackgroundPicker,
            testTag = "drawer_item_background"
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Keluar
        NxDrawerMenuButton(
            title = "Keluar",
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            isSelected = false,
            onClick = onExitApp,
            testTag = "drawer_item_exit"
        )
    }
}

@Composable
private fun NxDrawerMenuButton(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(12.dp)
    val shadowOffset = 5.dp

    // Tombol aktif = fill accent + text hitam. Tombol tidak aktif = fill CardDark + text terang.
    val fillColor = if (isSelected) nxColors.accent else nxColors.cardDark
    val textColor = if (isSelected) nxColors.cardBrightText else nxColors.cardDarkText
    val iconColor = if (isSelected) nxColors.cardBrightText else nxColors.accent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        // Layer BELAKANG: Double Stacked Sticker Shadow (#E8E8E8 + border hitam 2.5dp, offset 5.dp)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = shape)
        )
        // Layer DEPAN: Elemen utama dengan border hitam 3dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(color = fillColor, shape = shape)
                .border(width = 3.dp, color = nxColors.borderBlack, shape = shape)
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag(testTag),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                color = textColor,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Dialog Pilih Tema Warna (dengan fix judul "PILIH TEMA WARNA" tidak ketiban tombol X)
 */
@Composable
fun NxThemePickerDialog(
    currentThemeId: String,
    onSelectTheme: (NxThemeOption) -> Unit,
    onDismiss: () -> Unit
) {
    val nxColors = LocalNxColors.current
    val themes = NxThemeOption.entries

    Dialog(onDismissRequest = onDismiss) {
        val dialogShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 6.dp, bottom = 6.dp)
        ) {
            // Double Stacked Sticker Shadow
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 6.dp, y = 6.dp)
                    .background(color = nxColors.shadow, shape = dialogShape)
                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = dialogShape)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(dialogShape)
                    .background(color = nxColors.bg, shape = dialogShape)
                    .border(width = 3.dp, color = nxColors.borderBlack, shape = dialogShape)
                    .padding(18.dp)
            ) {
                // TUGAS 1 FIX: Bungkus judul dan tombol X dalam satu Row,
                // Modifier.weight(1f) + padding(end = 8.dp), maxLines = 1, TextOverflow.Ellipsis
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PILIH TEMA WARNA",
                        color = nxColors.cardDarkText,
                        fontFamily = ZeroPrimeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    NxDialogCloseButton(onClick = onDismiss)
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(themes, key = { it.id }) { theme ->
                        val isSelected = theme.id.equals(currentThemeId, ignoreCase = true)
                        val rowShape = RoundedCornerShape(12.dp)
                        val rowFill = if (isSelected) nxColors.accent else nxColors.cardDark
                        val rowText = if (isSelected) nxColors.cardBrightText else nxColors.cardDarkText

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 5.dp, bottom = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .offset(x = 5.dp, y = 5.dp)
                                    .background(nxColors.shadow, rowShape)
                                    .border(2.5.dp, nxColors.borderBlack, rowShape)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(rowShape)
                                    .background(rowFill, rowShape)
                                    .border(3.dp, nxColors.borderBlack, rowShape)
                                    .clickable { onSelectTheme(theme) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                                    .testTag("theme_option_${theme.id}"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Accent circle preview
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(theme.accent)
                                        .border(2.dp, nxColors.borderBlack, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Dark bg circle preview
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(theme.bg)
                                        .border(2.dp, nxColors.borderBlack, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = theme.displayName,
                                    color = rowText,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = rowText,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    NxButton(
                        text = "Tutup",
                        onClick = onDismiss,
                        modifier = Modifier.width(110.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dialog Pilih Background: 6 Gradient Presets + 16 Solid Presets (realtime)
 */
@Composable
fun NxBackgroundPickerDialog(
    currentBackgroundId: String,
    onSelectBackground: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val nxColors = LocalNxColors.current
    val gradients = NxBackgroundCatalog.gradientPresets
    val solids = NxBackgroundCatalog.solidPresets

    Dialog(onDismissRequest = onDismiss) {
        val dialogShape = RoundedCornerShape(16.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 6.dp, bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 6.dp, y = 6.dp)
                    .background(color = nxColors.shadow, shape = dialogShape)
                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = dialogShape)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(dialogShape)
                    .background(color = nxColors.bg, shape = dialogShape)
                    .border(width = 3.dp, color = nxColors.borderBlack, shape = dialogShape)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PILIH BACKGROUND",
                        color = nxColors.cardDarkText,
                        fontFamily = ZeroPrimeFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 17.sp,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )

                    NxDialogCloseButton(onClick = onDismiss)
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "GRADIENT PRESETS",
                                color = nxColors.accent,
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            NxBadge(text = "${gradients.size}")
                        }
                    }

                    items(gradients, key = { it.id }) { preset ->
                        BackgroundPresetRow(
                            preset = preset,
                            isSelected = preset.id == currentBackgroundId,
                            onClick = { onSelectBackground(preset.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SOLID PRESETS",
                                color = nxColors.accent,
                                fontFamily = SpaceGroteskFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            NxBadge(text = "${solids.size}")
                        }
                    }

                    items(solids, key = { it.id }) { preset ->
                        BackgroundPresetRow(
                            preset = preset,
                            isSelected = preset.id == currentBackgroundId,
                            onClick = { onSelectBackground(preset.id) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    NxButton(
                        text = "Tutup",
                        onClick = onDismiss,
                        modifier = Modifier.width(110.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NxDialogCloseButton(onClick: () -> Unit) {
    val nxColors = LocalNxColors.current
    val closeShape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier.padding(end = 3.dp, bottom = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .offset(x = 3.dp, y = 3.dp)
                .background(color = nxColors.shadow, shape = closeShape)
                .border(width = 2.dp, color = nxColors.borderBlack, shape = closeShape)
        )
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(closeShape)
                .background(color = nxColors.accent, shape = closeShape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = closeShape)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Tutup",
                tint = nxColors.cardBrightText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun BackgroundPresetRow(
    preset: NxBackgroundPreset,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val nxColors = LocalNxColors.current
    val rowShape = RoundedCornerShape(12.dp)
    val rowFill = if (isSelected) nxColors.accent else nxColors.cardDark
    val rowText = if (isSelected) nxColors.cardBrightText else nxColors.cardDarkText

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 5.dp, bottom = 5.dp)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 5.dp, y = 5.dp)
                .background(nxColors.shadow, rowShape)
                .border(2.5.dp, nxColors.borderBlack, rowShape)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(rowShape)
                .background(rowFill, rowShape)
                .border(3.dp, nxColors.borderBlack, rowShape)
                .clickable { onClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("bg_preset_${preset.id}"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle preview with double stacked comic shadow
            Box(
                modifier = Modifier.padding(end = 2.dp, bottom = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .offset(x = 2.dp, y = 2.dp)
                        .background(nxColors.shadow, CircleShape)
                        .border(1.5.dp, nxColors.borderBlack, CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .then(
                            when (preset) {
                                is NxBackgroundPreset.GradientPreset -> Modifier.background(preset.brush, CircleShape)
                                is NxBackgroundPreset.SolidPreset -> Modifier.background(preset.color, CircleShape)
                            }
                        )
                        .border(2.dp, nxColors.borderBlack, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = preset.name,
                color = rowText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = rowText,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
