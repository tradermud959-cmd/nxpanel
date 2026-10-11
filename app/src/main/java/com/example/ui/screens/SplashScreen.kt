package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.MonocraftFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit
) {
    val nxColors = LocalNxColors.current
    var startProgress by remember { mutableStateOf(false) }

    val progress by animateFloatAsState(
        targetValue = if (startProgress) 1f else 0.08f,
        animationSpec = tween(durationMillis = 1850, easing = FastOutSlowInEasing),
        label = "splash_progress"
    )

    LaunchedEffect(Unit) {
        startProgress = true
        delay(2000L)
        onNavigateToHome()
    }

    val splashGradient = Brush.verticalGradient(
        colors = listOf(
            nxColors.bg,
            nxColors.cardDark,
            Color(0xFF050507)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = splashGradient)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(28.dp)
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ikon R.drawable.icon_apps di tengah, dibungkus frame 3D Comic (border hitam 3dp, hard shadow #E8E8E8 6dp)
            val iconShape = RoundedCornerShape(22.dp)
            Box(
                modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .offset(x = 6.dp, y = 6.dp)
                        .background(color = nxColors.shadow, shape = iconShape)
                )
                Box(
                    modifier = Modifier
                        .size(124.dp)
                        .clip(iconShape)
                        .background(color = nxColors.cardDark, shape = iconShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = iconShape)
                        .padding(6.dp)
                        .border(width = 2.dp, color = nxColors.accent, shape = RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.icon_apps),
                        contentDescription = "NXPANEL App Icon",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Judul "NXPANEL" pakai font Monocraft, background accent, border hitam, hard shadow
            val titleShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 6.dp, y = 6.dp)
                        .background(color = nxColors.shadow, shape = titleShape)
                )
                Box(
                    modifier = Modifier
                        .clip(titleShape)
                        .background(color = nxColors.accent, shape = titleShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = titleShape)
                        .padding(horizontal = 28.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NXPANEL",
                        color = nxColors.cardBrightText,
                        fontFamily = MonocraftFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 28.sp,
                        letterSpacing = 2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Subtitle: "BACA CHAT, BIAR GAK KETINGGALAN" pakai Space Grotesk
            Text(
                text = "BACA CHAT, BIAR GAK KETINGGALAN",
                color = nxColors.cardDarkText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
        }

        // Progress bar horizontal di bawah, border hitam tebal, fill accent tema
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "MENGHUBUNGKAN KE LOCAL ENGINE...",
                color = nxColors.subText,
                fontFamily = MonocraftFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            val barShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 5.dp, bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .offset(x = 5.dp, y = 5.dp)
                        .background(color = nxColors.shadow, shape = barShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(barShape)
                        .background(color = nxColors.cardDark, shape = barShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = barShape)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress.coerceIn(0.05f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(color = nxColors.accent)
                            .border(
                                width = 1.5.dp,
                                color = nxColors.borderBlack,
                                shape = RoundedCornerShape(6.dp)
                            )
                    )
                }
            }
        }
    }
}
