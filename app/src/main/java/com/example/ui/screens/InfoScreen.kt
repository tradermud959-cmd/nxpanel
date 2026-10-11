package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.NxBadge
import com.example.ui.components.NxButton
import com.example.ui.components.NxCard
import com.example.ui.components.NxComicAppIconFrame
import com.example.ui.components.NxHalftoneTitle
import com.example.ui.components.NxIconButton
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.MonocraftFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun InfoScreen(
    onOpenDrawer: () -> Unit,
    onNavigateToChats: () -> Unit
) {
    val nxColors = LocalNxColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .testTag("info_screen")
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
                    text = "INFORMASI APLIKASI",
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
                modifier = Modifier.testTag("info_drawer_button")
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Hero Bright Card: Ikon icon_apps di tengah dengan frame 3D Comic, judul "NXPANEL", versi "v1.0.0"
        NxCard(
            isBright = true,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val frameShape = RoundedCornerShape(18.dp)
                Box(
                    modifier = Modifier.padding(end = 6.dp, bottom = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .offset(x = 6.dp, y = 6.dp)
                            .background(color = nxColors.shadow, shape = frameShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(frameShape)
                            .background(color = nxColors.cardDark, shape = frameShape)
                            .border(width = 3.dp, color = nxColors.borderBlack, shape = frameShape)
                            .padding(5.dp)
                            .border(width = 2.dp, color = nxColors.accent, shape = RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_apps),
                            contentDescription = "NXPANEL App Icon",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(11.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                NxHalftoneTitle(
                    text = "NXPANEL",
                    fontSize = 24,
                    textColor = nxColors.cardBrightText
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badges row matching NX Kalkulator screenshot: v1.0.0 | 3D COMIC STYLE | LOCAL HTTP
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NxBadge(
                        text = "v1.0.0",
                        fillColor = nxColors.shadow,
                        textColor = nxColors.borderBlack
                    )
                    NxBadge(
                        text = "3D COMIC STYLE",
                        fillColor = nxColors.warning,
                        textColor = nxColors.borderBlack
                    )
                    NxBadge(
                        text = "LOCAL HTTP",
                        fillColor = nxColors.shadow,
                        textColor = nxColors.borderBlack
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description Box inside bright card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(nxColors.accent)
                        .border(2.5.dp, nxColors.borderBlack, RoundedCornerShape(10.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Client Telegram personal bergaya Neo-Brutalist 3D Comic yang terhubung langsung ke backend Python lokal di Termux via HTTP. Dirancang untuk kecepatan, privasi penuh, dan pengalaman visual komik yang khas.",
                        color = nxColors.cardBrightText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                NxButton(
                    text = "BUKA DAFTAR CHAT",
                    icon = Icons.Default.Public,
                    onClick = onNavigateToChats,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card "INFORMASI APLIKASI" (Dark Card matching screenshot)
        NxCard(
            isBright = false,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(nxColors.accent)
                        .border(2.dp, nxColors.borderBlack, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = nxColors.cardBrightText,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                NxHalftoneTitle(
                    text = "INFORMASI APLIKASI",
                    fontSize = 17
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            InfoDetailItemBox(
                icon = Icons.Default.Code,
                label = "Application ID",
                value = "com.nxpanel.nxpanel"
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoDetailItemBox(
                icon = Icons.Default.Layers,
                label = "Build Version",
                value = "v1.0.0"
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoDetailItemBox(
                icon = Icons.Default.Info,
                label = "Pengembang",
                value = "nxkaav project"
            )

            Spacer(modifier = Modifier.height(10.dp))

            InfoDetailItemBox(
                icon = Icons.Default.Lock,
                label = "Operasional",
                value = "100% Local Server · Termux Backend · DataStore"
            )
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Footer: teks "DEVELOPED BY" + gambar R.drawable.logo (logo NXKAAV Project) + teks "NXKAAV PROJECT · ALL RIGHTS RESERVED"
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "DEVELOPED BY",
                color = nxColors.accent,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            val logoShape = RoundedCornerShape(16.dp)
            Box(
                modifier = Modifier.padding(end = 5.dp, bottom = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .offset(x = 5.dp, y = 5.dp)
                        .background(color = nxColors.shadow, shape = logoShape)
                )
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "NXKAAV Project Logo",
                    modifier = Modifier
                        .size(104.dp)
                        .clip(logoShape)
                        .background(color = nxColors.cardDark, shape = logoShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = logoShape)
                        .padding(6.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "NXKAAV PROJECT · ALL RIGHTS RESERVED",
                color = nxColors.subText,
                fontFamily = MonocraftFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoDetailItemBox(
    icon: ImageVector,
    label: String,
    value: String
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(10.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(nxColors.bg)
            .border(width = 2.dp, color = nxColors.accent.copy(alpha = 0.65f), shape = shape)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(nxColors.accent)
                .border(2.dp, nxColors.borderBlack, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = nxColors.cardBrightText,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = nxColors.subText,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = value.ifBlank { "N/A" },
                color = nxColors.cardDarkText,
                fontFamily = MonocraftFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp
            )
        }
    }
}
