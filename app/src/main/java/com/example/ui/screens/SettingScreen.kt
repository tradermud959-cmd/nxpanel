package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NxAppPreferences
import com.example.ui.NxUiState
import com.example.ui.components.NxBadge
import com.example.ui.components.NxButton
import com.example.ui.components.NxCard
import com.example.ui.components.NxComicAppIconFrame
import com.example.ui.components.NxHalftoneTitle
import com.example.ui.components.NxIconButton
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.MonocraftFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun SettingScreen(
    preferences: NxAppPreferences,
    uiState: NxUiState,
    onOpenDrawer: () -> Unit,
    onSaveBaseUrl: (String) -> Unit,
    onTestConnection: (String) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onToggleSound: (Boolean) -> Unit,
    onOpenThemeModal: () -> Unit,
    onOpenBackgroundModal: () -> Unit
) {
    val nxColors = LocalNxColors.current
    var urlInput by rememberSaveable { mutableStateOf(preferences.baseUrl) }

    LaunchedEffect(preferences.baseUrl) {
        urlInput = preferences.baseUrl
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .testTag("setting_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NxComicAppIconFrame(size = 46.dp, shadowOffset = 4.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                NxHalftoneTitle(text = "NXPANEL", fontSize = 20)
                Text(
                    text = "KONFIGURASI SISTEM & SERVER",
                    color = nxColors.accent,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            NxIconButton(
                icon = Icons.Default.Menu,
                contentDescription = "Open Drawer",
                onClick = onOpenDrawer,
                modifier = Modifier.testTag("setting_drawer_button")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Card 1: Koneksi Server Lokal (Base URL + TEST KONEKSI)
        NxCard(
            isBright = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(nxColors.cardDark)
                        .border(2.dp, nxColors.borderBlack, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Dns,
                        contentDescription = "Server",
                        tint = nxColors.accent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "TERMUX PYTHON BACKEND",
                    color = nxColors.cardBrightText,
                    fontFamily = MonocraftFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Base URL Lokal (Default: http://127.0.0.1:8080)",
                color = nxColors.cardBrightText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            val inputShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(inputShape)
                    .background(color = nxColors.cardDark, shape = inputShape)
                    .border(width = 3.dp, color = nxColors.borderBlack, shape = inputShape)
                    .padding(horizontal = 14.dp, vertical = 14.dp)
            ) {
                BasicTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = nxColors.cardDarkText,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    cursorBrush = SolidColor(nxColors.accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("base_url_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NxButton(
                    text = "SIMPAN URL",
                    icon = Icons.Default.Save,
                    isDarkVariant = true,
                    onClick = { onSaveBaseUrl(urlInput) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("save_url_button")
                )

                NxButton(
                    text = if (uiState.isTestingConnection) "MENGUJI..." else "TEST KONEKSI",
                    icon = Icons.Default.WifiTethering,
                    isDarkVariant = true,
                    onClick = { onTestConnection(urlInput) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("test_connection_button")
                )
            }

            if (uiState.userProfile != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(nxColors.cardDark)
                        .border(2.dp, nxColors.borderBlack, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "AKUN TERHUBUNG (/me):",
                            color = nxColors.accent,
                            fontFamily = MonocraftFontFamily,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${uiState.userProfile.name} (@${uiState.userProfile.username}) · ID: ${uiState.userProfile.id}",
                            color = nxColors.cardDarkText,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 2: Toggle Haptic & Toggle Suara
        NxCard(
            isBright = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            NxHalftoneTitle(
                text = "PREFERENSI INTERAKSI",
                fontSize = 17
            )

            Spacer(modifier = Modifier.height(14.dp))

            NxToggleRow(
                icon = Icons.Default.Vibration,
                title = "Haptic Feedback",
                subtitle = "Getaran taktil saat menekan tombol komik",
                checked = preferences.hapticEnabled,
                onCheckedChange = onToggleHaptic,
                testTag = "toggle_haptic"
            )

            Spacer(modifier = Modifier.height(12.dp))

            NxToggleRow(
                icon = Icons.Default.VolumeUp,
                title = "Efek Suara",
                subtitle = "Suara klik retro saat berinteraksi",
                checked = preferences.soundEnabled,
                onCheckedChange = onToggleSound,
                testTag = "toggle_sound"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: Shortcut ke Ganti Tema & Ganti Background
        NxCard(
            isBright = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NxHalftoneTitle(
                    text = "KUSTOMISASI VISUAL",
                    fontSize = 17
                )
                NxBadge(text = nxColors.themeName.uppercase())
            }

            Spacer(modifier = Modifier.height(14.dp))

            NxButton(
                text = "GANTI TEMA WARNA",
                icon = Icons.Default.Palette,
                onClick = onOpenThemeModal,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shortcut_theme_button")
            )

            Spacer(modifier = Modifier.height(10.dp))

            NxButton(
                text = "GANTI BACKGROUND",
                icon = Icons.Default.Wallpaper,
                isDarkVariant = true,
                onClick = onOpenBackgroundModal,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("shortcut_background_button")
            )
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun NxToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(nxColors.bg)
            .border(2.dp, nxColors.accent.copy(alpha = 0.6f), shape)
            .clickable { onCheckedChange(!checked) }
            .padding(12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(nxColors.accent)
                .border(2.dp, nxColors.borderBlack, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = nxColors.cardBrightText,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = nxColors.cardDarkText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = subtitle,
                color = nxColors.subText,
                fontFamily = JetBrainsMonoFontFamily,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Neo-Brutalist 3D Toggle Pill
        val btnShape = RoundedCornerShape(8.dp)
        Box(
            modifier = Modifier.padding(end = 3.dp, bottom = 3.dp)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 3.dp, y = 3.dp)
                    .background(nxColors.shadow, btnShape)
                    .border(2.5.dp, nxColors.borderBlack, btnShape)
            )
            Row(
                modifier = Modifier
                    .clip(btnShape)
                    .background(if (checked) nxColors.accent else nxColors.cardDark, btnShape)
                    .border(2.5.dp, nxColors.borderBlack, btnShape)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (checked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Active",
                        tint = nxColors.cardBrightText,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = if (checked) "ON" else "OFF",
                    color = if (checked) nxColors.cardBrightText else nxColors.subText,
                    fontFamily = MonocraftFontFamily,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}
