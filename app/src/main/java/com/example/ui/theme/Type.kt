package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// 1. zeroprimehalfital.otf -> Judul header & judul card
val ZeroPrimeFontFamily = FontFamily(
    Font(R.font.zeroprimehalfital, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.zeroprimehalfital, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.zeroprimehalfital, FontWeight.Normal, FontStyle.Normal)
)

// 2. SpaceGrotesk-Bold.ttf -> Body text, label, tombol, dan filter tabs
val SpaceGroteskFontFamily = FontFamily(
    Font(R.font.spacegrotesk_bold, FontWeight.Bold),
    Font(R.font.spacegrotesk_bold, FontWeight.Medium),
    Font(R.font.spacegrotesk_bold, FontWeight.Normal)
)

// 3. Monocraft-Black.otf -> Angka, badge, counter
val MonocraftFontFamily = FontFamily(
    Font(R.font.monocraft_black, FontWeight.Black),
    Font(R.font.monocraft_black, FontWeight.Bold),
    Font(R.font.monocraft_black, FontWeight.Normal)
)

// 4. JetBrainsMono-Regular.ttf -> Data teknis, preview chat
val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrainsmono_regular, FontWeight.Normal),
    Font(R.font.jetbrainsmono_regular, FontWeight.Bold)
)

// 5. PressStart2P-Regular.ttf -> Aksen retro (cadangan)
val PressStart2PFontFamily = FontFamily(
    Font(R.font.pressstart2p_regular, FontWeight.Normal),
    Font(R.font.pressstart2p_regular, FontWeight.Bold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = ZeroPrimeFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 1.sp
    ),
    displayMedium = TextStyle(
        fontFamily = ZeroPrimeFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.8.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = MonocraftFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 1.sp
    ),
    titleLarge = TextStyle(
        fontFamily = ZeroPrimeFontFamily,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.8.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SpaceGroteskFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = MonocraftFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 13.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = JetBrainsMonoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp
    )
)
