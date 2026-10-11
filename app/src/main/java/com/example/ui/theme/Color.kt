package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val BoneWhiteShadow = Color(0xFFE8E8E8)
val ComicBlack = Color(0xFF050507)
val AlertOrange = Color(0xFFFF6D00)
val OnlineGreen = Color(0xFF00E676)

enum class NxThemeOption(
    val id: String,
    val displayName: String,
    val accent: Color,
    val bg: Color,
    val cardDark: Color,
    val cardDarkText: Color = Color(0xFFF5F5F7),
    val subText: Color = Color(0xFFB0B3B8)
) {
    DEFAULT(
        id = "DEFAULT",
        displayName = "Monochrome",
        accent = Color(0xFFE0E0E0),
        bg = Color(0xFF0F0F12),
        cardDark = Color(0xFF18191E)
    ),
    CYBER_CYAN(
        id = "CYBER_CYAN",
        displayName = "Cyber Cyan",
        accent = Color(0xFF00FFFF),
        bg = Color(0xFF0A0F14),
        cardDark = Color(0xFF111A22)
    ),
    SAKURA(
        id = "SAKURA",
        displayName = "Sakura",
        accent = Color(0xFFFF8FA3),
        bg = Color(0xFF1A0F14),
        cardDark = Color(0xFF26161D)
    ),
    DARK_CRIMSON(
        id = "DARK_CRIMSON",
        displayName = "Dark Crimson",
        accent = Color(0xFFFF1744),
        bg = Color(0xFF0F0608),
        cardDark = Color(0xFF1E0D12)
    ),
    NEON_YELLOW(
        id = "NEON_YELLOW",
        displayName = "Neon Yellow",
        accent = Color(0xFFFFEA00),
        bg = Color(0xFF191605),
        cardDark = Color(0xFF242009)
    ),
    ELECTRIC_ORANGE(
        id = "ELECTRIC_ORANGE",
        displayName = "Electric Orange",
        accent = Color(0xFFFF6D00),
        bg = Color(0xFF1A0E05),
        cardDark = Color(0xFF261508)
    ),
    CYBER_LIME(
        id = "CYBER_LIME",
        displayName = "Cyber Lime",
        accent = Color(0xFF00E676),
        bg = Color(0xFF07170D),
        cardDark = Color(0xFF0E2416)
    ),
    HOT_MAGENTA(
        id = "HOT_MAGENTA",
        displayName = "Hot Magenta",
        accent = Color(0xFFFF007F),
        bg = Color(0xFF1A0612),
        cardDark = Color(0xFF280B1C)
    );

    companion object {
        fun fromId(id: String?): NxThemeOption {
            return entries.find { it.id.equals(id, ignoreCase = true) } ?: DEFAULT
        }
    }
}

@Immutable
data class NxColors(
    val themeId: String,
    val themeName: String,
    val accent: Color,
    val bg: Color,
    val cardDark: Color,
    val cardDarkText: Color,
    val cardBrightText: Color,
    val shadow: Color,
    val borderBlack: Color,
    val subText: Color,
    val online: Color,
    val warning: Color
)

val LocalNxColors = staticCompositionLocalOf {
    NxColors(
        themeId = NxThemeOption.DEFAULT.id,
        themeName = NxThemeOption.DEFAULT.displayName,
        accent = NxThemeOption.DEFAULT.accent,
        bg = NxThemeOption.DEFAULT.bg,
        cardDark = NxThemeOption.DEFAULT.cardDark,
        cardDarkText = NxThemeOption.DEFAULT.cardDarkText,
        cardBrightText = ComicBlack,
        shadow = BoneWhiteShadow,
        borderBlack = ComicBlack,
        subText = NxThemeOption.DEFAULT.subText,
        online = OnlineGreen,
        warning = AlertOrange
    )
}

fun NxThemeOption.toNxColors(): NxColors = NxColors(
    themeId = id,
    themeName = displayName,
    accent = accent,
    bg = bg,
    cardDark = cardDark,
    cardDarkText = cardDarkText,
    cardBrightText = ComicBlack,
    shadow = BoneWhiteShadow,
    borderBlack = ComicBlack,
    subText = subText,
    online = OnlineGreen,
    warning = AlertOrange
)

sealed class NxBackgroundPreset(
    val id: String,
    val name: String,
    val isGradient: Boolean
) {
    data class GradientPreset(
        val presetId: String,
        val presetName: String,
        val colors: List<Color>
    ) : NxBackgroundPreset(presetId, presetName, true) {
        val brush: Brush
            get() = Brush.verticalGradient(colors = colors)
    }

    data class SolidPreset(
        val presetId: String,
        val presetName: String,
        val color: Color
    ) : NxBackgroundPreset(presetId, presetName, false)
}

object NxBackgroundCatalog {
    // 6 Gradient Presets (as specified: 6 gradient preset + 16 solid preset)
    val gradientPresets = listOf(
        NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_THEME",
            presetName = "Theme Adaptive Gradient",
            colors = listOf(Color(0xFF0F0F12), Color(0xFF1C1E26), Color(0xFF08080A))
        ),
        NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_NEON_BLAZE",
            presetName = "Neon Blaze",
            colors = listOf(Color(0xFFD4AC0D), Color(0xFF7D5A00), Color(0xFF3E2703))
        ),
        NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_CYBER_PUNK",
            presetName = "Cyber Punk",
            colors = listOf(Color(0xFF0A192F), Color(0xFF0F3156), Color(0xFF050B14))
        ),
        NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_BLOOD_MOON",
            presetName = "Blood Moon",
            colors = listOf(Color(0xFF3B0910), Color(0xFF1F0408), Color(0xFF0A0203))
        ),
        NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_EMERALD_ABYSS",
            presetName = "Emerald Abyss",
            colors = listOf(Color(0xFF062E22), Color(0xFF041B15), Color(0xFF020C09))
        ),
        NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_SAKURA_DUSK",
            presetName = "Sakura Dusk",
            colors = listOf(Color(0xFF2D1424), Color(0xFF1A0C16), Color(0xFF0D060B))
        )
    )

    // 16 Solid Presets
    val solidPresets = listOf(
        NxBackgroundPreset.SolidPreset("SOLID_OBSIDIAN", "Obsidian Default", Color(0xFF0F0F12)),
        NxBackgroundPreset.SolidPreset("SOLID_CYBER_DARK", "Cyber Trench", Color(0xFF0A0F14)),
        NxBackgroundPreset.SolidPreset("SOLID_SAKURA_NIGHT", "Sakura Night", Color(0xFF1A0F14)),
        NxBackgroundPreset.SolidPreset("SOLID_CRIMSON_VOID", "Crimson Void", Color(0xFF0F0608)),
        NxBackgroundPreset.SolidPreset("SOLID_AMBER_GOLD", "Retro Gold", Color(0xFF9A7B00)),
        NxBackgroundPreset.SolidPreset("SOLID_GUNMETAL", "Gunmetal Steel", Color(0xFF1B2028)),
        NxBackgroundPreset.SolidPreset("SOLID_DEEP_NAVY", "Deep Navy", Color(0xFF0B132B)),
        NxBackgroundPreset.SolidPreset("SOLID_FOREST_SHADOW", "Forest Shadow", Color(0xFF0B2018)),
        NxBackgroundPreset.SolidPreset("SOLID_PLUM_NOIR", "Plum Noir", Color(0xFF1F1124)),
        NxBackgroundPreset.SolidPreset("SOLID_ESPRESSO", "Dark Espresso", Color(0xFF1C130E)),
        NxBackgroundPreset.SolidPreset("SOLID_SLATE_BUNKER", "Slate Bunker", Color(0xFF222831)),
        NxBackgroundPreset.SolidPreset("SOLID_CHARCOAL", "Charcoal Ink", Color(0xFF121212)),
        NxBackgroundPreset.SolidPreset("SOLID_MIDNIGHT_TEAL", "Midnight Teal", Color(0xFF072227)),
        NxBackgroundPreset.SolidPreset("SOLID_WINE_CELLAR", "Wine Cellar", Color(0xFF240B15)),
        NxBackgroundPreset.SolidPreset("SOLID_INDIGO_CORE", "Indigo Core", Color(0xFF14102E)),
        NxBackgroundPreset.SolidPreset("SOLID_PITCH_BLACK", "Pitch Black", Color(0xFF050507))
    )

    val allPresets: List<NxBackgroundPreset> = gradientPresets + solidPresets

    fun findById(id: String?, currentTheme: NxThemeOption): NxBackgroundPreset {
        if (id.isNullOrBlank() || id == "GRAD_THEME") {
            return NxBackgroundPreset.GradientPreset(
                presetId = "GRAD_THEME",
                presetName = "Theme Adaptive Gradient",
                colors = listOf(
                    currentTheme.bg,
                    currentTheme.cardDark,
                    Color(0xFF050507)
                )
            )
        }
        return allPresets.find { it.id == id } ?: NxBackgroundPreset.GradientPreset(
            presetId = "GRAD_THEME",
            presetName = "Theme Adaptive Gradient",
            colors = listOf(currentTheme.bg, currentTheme.cardDark, Color(0xFF050507))
        )
    }
}
