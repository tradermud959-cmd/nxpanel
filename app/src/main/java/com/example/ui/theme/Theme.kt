package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember

@Composable
fun NxPanelTheme(
    themeOption: NxThemeOption = NxThemeOption.DEFAULT,
    content: @Composable () -> Unit
) {
    val nxColors = remember(themeOption) { themeOption.toNxColors() }

    val colorScheme = remember(nxColors) {
        darkColorScheme(
            primary = nxColors.accent,
            onPrimary = nxColors.cardBrightText,
            secondary = nxColors.accent,
            onSecondary = nxColors.cardBrightText,
            background = nxColors.bg,
            onBackground = nxColors.cardDarkText,
            surface = nxColors.cardDark,
            onSurface = nxColors.cardDarkText
        )
    }

    CompositionLocalProvider(LocalNxColors provides nxColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
