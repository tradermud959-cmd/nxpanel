package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

// 1. ZeroPrime Halftone Italic (Judul / Display Comic Header)
val ZeroPrimeFontFamily = FontFamily(
    Font(R.font.zeroprime_halftone_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.zeroprime_halftone_italic, FontWeight.Normal, FontStyle.Normal)
)

// 2. Space Grotesk Bold (Body / Tombol)
val SpaceGroteskFontFamily = FontFamily(
    Font(R.font.space_grotesk, FontWeight.Bold),
    Font(R.font.space_grotesk, FontWeight.Medium),
    Font(R.font.space_grotesk, FontWeight.Normal)
)

// 3. Monocraft Black (Angka / Badge / Splash Title)
val MonocraftFontFamily = FontFamily(
    Font(R.font.monocraft_black, FontWeight.Black),
    Font(R.font.monocraft_black, FontWeight.Bold),
    Font(R.font.monocraft_black, FontWeight.Normal)
)

// 4. JetBrains Mono (Data Teknis / Preview Chat)
val JetBrainsMonoFontFamily = FontFamily(
    Font(R.font.jetbrains_mono, FontWeight.Normal),
    Font(R.font.jetbrains_mono, FontWeight.Bold)
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
