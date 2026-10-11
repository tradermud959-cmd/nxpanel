package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.network.ChatItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: NxUiState,
    filteredChats: List<ChatItem>,
    baseUrl: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectFilter: (String) -> Unit,
    onRefresh: () -> Unit,
    onOpenDrawer: () -> Unit,
    onChatClick: (ChatItem) -> Unit,
    onNavigateToSetting: () -> Unit
) {
    val nxColors = LocalNxColors.current
    val totalUnread = try {
        uiState.chats.sumOf { it.unread.coerceAtLeast(0) }
    } catch (_: Exception) {
        0
    }

    LaunchedEffect(Unit) {
        if (uiState.chats.isEmpty()) {
            onRefresh()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("home_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top Bar: ikon icon_apps (kiri), judul "NXPANEL", badge unread counter, tombol refresh, tombol drawer (kanan)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NxComicAppIconFrame(
                size = 46.dp,
                shadowOffset = 4.dp
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NxHalftoneTitle(
                        text = "NXPANEL",
                        fontSize = 20
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    NxBadge(
                        text = totalUnread.toString(),
                        fillColor = nxColors.accent,
                        textColor = nxColors.cardBrightText
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (uiState.isServerOnline) "NXSERVER · ONLINE" else "NXSERVER · OFFLINE",
                    color = if (uiState.isServerOnline) nxColors.online else nxColors.accent,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            NxIconButton(
                icon = Icons.Default.Refresh,
                contentDescription = "Refresh Chats",
                onClick = onRefresh,
                modifier = Modifier.testTag("refresh_chats_button")
            )

            Spacer(modifier = Modifier.width(8.dp))

            NxIconButton(
                icon = Icons.Default.Menu,
                contentDescription = "Open Drawer Menu",
                onClick = onOpenDrawer,
                modifier = Modifier.testTag("open_drawer_button")
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar (filter chat by nama, realtime)
        NxSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Tabs di bawah Search Bar: [ USER ] | [ GROUP ] | [ CHANNEL ]
        NxFilterTabsRow(
            selectedFilter = uiState.selectedFilter,
            onSelectFilter = onSelectFilter
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Pull-to-refresh + LazyColumn list chat
        PullToRefreshBox(
            isRefreshing = uiState.isLoadingChats,
            onRefresh = onRefresh,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (filteredChats.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        NxCard(
                            isBright = false,
                            cornerRadius = 12.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Ikon Neo-Brutalist tebal dengan hard shadow
                                val iconShape = RoundedCornerShape(14.dp)
                                Box(
                                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .offset(x = 4.dp, y = 4.dp)
                                            .background(color = nxColors.shadow, shape = iconShape)
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(iconShape)
                                            .background(nxColors.accent, shape = iconShape)
                                            .border(3.dp, nxColors.borderBlack, iconShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (!uiState.isServerOnline || uiState.chats.isEmpty()) {
                                                Icons.Default.CloudOff
                                            } else {
                                                Icons.Default.Forum
                                            },
                                            contentDescription = "Server Offline / Empty State",
                                            tint = nxColors.cardBrightText,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                val titleText = if (!uiState.isServerOnline || uiState.chats.isEmpty()) {
                                    "SERVER OFFLINE"
                                } else {
                                    "TIDAK ADA CHAT [${uiState.selectedFilter}]"
                                }

                                NxHalftoneTitle(
                                    text = titleText,
                                    fontSize = 24,
                                    textColor = nxColors.accent
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                val subtitleText = if (!uiState.isServerOnline || uiState.chats.isEmpty()) {
                                    "Pastikan NXServer lu sudah menyala di Termux."
                                } else if (uiState.searchQuery.isNotBlank()) {
                                    "Tidak ada chat kategori ${uiState.selectedFilter} yang cocok dengan \"${uiState.searchQuery}\"."
                                } else {
                                    "Belum ada daftar chat untuk kategori ${uiState.selectedFilter}."
                                }

                                Text(
                                    text = subtitleText,
                                    color = nxColors.cardDarkText,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    NxButton(
                                        text = "MUAT ULANG",
                                        icon = Icons.Default.Refresh,
                                        isDarkVariant = false,
                                        onClick = onRefresh,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NxButton(
                                        text = "SETTING",
                                        icon = Icons.Default.Settings,
                                        isDarkVariant = false,
                                        onClick = onNavigateToSetting,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("chat_list"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredChats,
                        key = { it.id }
                    ) { chat ->
                        ChatItemRowCard(
                            chat = chat,
                            baseUrl = baseUrl,
                            onClick = { onChatClick(chat) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ketentuan UI Filter Tabs (Gaya Neo-Brutalist 3D Comic):
 * 1. Baris horizontal (Row) dengan 3 tombol tab: [ USER ] | [ GROUP ] | [ CHANNEL ].
 * 2. Default tab aktif adalah "USER".
 * 3. Desain Tab Aktif: Fill warna Accent dari tema (LocalNxColors.current), text HITAM, border hitam 3dp, hard shadow putih #E8E8E8 offset 4dp.
 * 4. Desain Tab Tidak Aktif: Fill CardDark dari tema, text terang, border Accent 2.5dp, hard shadow putih #E8E8E8 offset 5dp.
 * 5. Gunakan font SpaceGrotesk-Bold untuk teks tab.
 * 6. Jangan hardcode warna. Gunakan LocalNxColors.current.
 */
@Composable
private fun NxFilterTabsRow(
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    val tabs = listOf("USER", "GROUP", "CHANNEL")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("filter_tabs_row"),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEach { tab ->
            val isActive = selectedFilter.equals(tab, ignoreCase = true)
            NxFilterTabButton(
                label = tab,
                isActive = isActive,
                onClick = { onSelectFilter(tab) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("filter_tab_${tab.lowercase()}")
            )
        }
    }
}

@Composable
private fun NxFilterTabButton(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(10.dp)

    val shadowOffset = if (isActive) 4.dp else 5.dp
    val borderWidth = if (isActive) 3.dp else 2.5.dp
    val fillColor = if (isActive) nxColors.accent else nxColors.cardDark
    val textColor = if (isActive) nxColors.cardBrightText else nxColors.cardDarkText
    val borderColor = if (isActive) nxColors.borderBlack else nxColors.accent

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(color = fillColor, shape = shape)
                .border(width = borderWidth, color = borderColor, shape = shape)
                .clickable { onClick() }
                .padding(vertical = 11.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = textColor,
                fontFamily = SpaceGroteskFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 0.8.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NxSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val nxColors = LocalNxColors.current
    val shape = RoundedCornerShape(12.dp)
    val shadowOffset = 5.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(color = nxColors.cardDark, shape = shape)
                .border(width = 2.5.dp, color = nxColors.accent, shape = shape)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = nxColors.accent,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text(
                        text = "Cari nama chat...",
                        color = nxColors.subText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = nxColors.cardDarkText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    cursorBrush = SolidColor(nxColors.accent),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_chat_input")
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Search",
                    tint = nxColors.accent,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onQueryChange("") }
                )
            }
        }
    }
}

@Composable
private fun ChatItemRowCard(
    chat: ChatItem,
    baseUrl: String,
    onClick: () -> Unit
) {
    val nxColors = LocalNxColors.current
    val safeName = chat.name.ifBlank { "N/A" }
    val safeLastMsg = chat.last_message.ifBlank { "N/A" }
    val initials = try {
        safeName
            .trim()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }
            .take(2)
            .joinToString("") { it.first().uppercaseChar().toString() }
            .ifEmpty { "NX" }
    } catch (_: Exception) {
        "NX"
    }

    val resolvedPhotoUrl = remember(chat.photo_url, baseUrl) {
        val raw = chat.photo_url?.trim()
        if (raw.isNullOrEmpty() || raw.equals("null", ignoreCase = true)) {
            null
        } else if (raw.startsWith("http://") || raw.startsWith("https://")) {
            raw
        } else {
            val cleanBase = baseUrl.trim().removeSuffix("/")
            val cleanPath = if (raw.startsWith("/")) raw else "/$raw"
            "$cleanBase$cleanPath"
        }
    }

    var imageLoadFailed by remember(resolvedPhotoUrl) { mutableStateOf(false) }

    val typeTag = when {
        chat.is_channel -> "CHANNEL"
        chat.is_group -> "GROUP"
        chat.is_user -> "USER"
        else -> "CHAT"
    }

    NxCard(
        isBright = false,
        contentPadding = 14.dp,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_item_${chat.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar bulat (CircleShape) dengan border hitam tebal 2.5dp dan hard shadow putih #E8E8E8 3dp
            Box(
                modifier = Modifier.padding(end = 3.dp, bottom = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(x = 3.dp, y = 3.dp)
                        .background(color = nxColors.shadow, shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(color = nxColors.accent, shape = CircleShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = nxColors.cardBrightText,
                        fontFamily = MonocraftFontFamily,
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp
                    )
                    if (resolvedPhotoUrl != null && !imageLoadFailed) {
                        AsyncImage(
                            model = resolvedPhotoUrl,
                            contentDescription = safeName,
                            contentScale = ContentScale.Crop,
                            onError = { imageLoadFailed = true },
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = safeName,
                        color = nxColors.cardDarkText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(nxColors.bg)
                            .border(1.5.dp, nxColors.accent.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = typeTag,
                            color = nxColors.accent,
                            fontFamily = MonocraftFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // last_message (JetBrains Mono, max 1 baris ellipsis)
                Text(
                    text = safeLastMsg,
                    color = nxColors.subText,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (chat.unread > 0) {
                Spacer(modifier = Modifier.width(10.dp))
                // Badge unread (warna accent, Monocraft)
                NxBadge(
                    text = chat.unread.toString(),
                    fillColor = nxColors.accent,
                    textColor = nxColors.cardBrightText
                )
            }
        }
    }
}
