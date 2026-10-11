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
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    text = if (uiState.isServerOnline) "TERMUX LOCAL · ONLINE" else "TERMUX LOCAL · OFFLINE",
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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(nxColors.accent)
                                        .border(3.dp, nxColors.borderBlack, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = "No Chats",
                                        tint = nxColors.cardBrightText,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = if (uiState.searchQuery.isNotBlank()) {
                                        "CHAT TIDAK DITEMUKAN"
                                    } else {
                                        "BELUM ADA DATA CHAT DARI SERVER"
                                    },
                                    color = nxColors.cardDarkText,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (uiState.searchQuery.isNotBlank()) {
                                        "Tidak ada nama chat yang cocok dengan \"${uiState.searchQuery}\"."
                                    } else {
                                        "Pastikan backend Python di Termux aktif pada endpoint:\n$baseUrl/chats?limit=50"
                                    },
                                    color = nxColors.subText,
                                    fontFamily = JetBrainsMonoFontFamily,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    NxButton(
                                        text = "MUAT ULANG",
                                        icon = Icons.Default.Refresh,
                                        onClick = onRefresh,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NxButton(
                                        text = "SETTING URL",
                                        icon = Icons.Default.Settings,
                                        isDarkVariant = true,
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
                            onClick = { onChatClick(chat) }
                        )
                    }
                }
            }
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
            // Avatar bulat (inisial nama, bg accent) + border hitam tebal + hard shadow
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
