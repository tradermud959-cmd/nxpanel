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
        Spacer(modifier = Modifier.height(10.dp))

        // 4. Top Bar Home: bungkus seluruh elemen Top Bar (ikon + judul + badge counter + tombol refresh + tombol drawer)
        // dalam satu card dengan fill CardDark, border hitam 2.5dp, padding 10dp, sudut 12dp, hard shadow #E8E8E8 5dp (double stacked).
        val topBarShape = RoundedCornerShape(12.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 5.dp, bottom = 5.dp)
        ) {
            // Layer BELAKANG: Double Stacked Sticker Shadow (#E8E8E8 + border hitam 2.5dp, offset 5.dp)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 5.dp, y = 5.dp)
                    .background(color = nxColors.shadow, shape = topBarShape)
                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = topBarShape)
            )
            // Layer DEPAN: fill LocalNxColors.current.accent + border hitam 2.5dp + padding 10dp
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(topBarShape)
                    .background(color = nxColors.accent, shape = topBarShape)
                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = topBarShape)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NxComicAppIconFrame(
                    size = 44.dp,
                    shadowOffset = 3.dp
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NXPANEL",
                            color = nxColors.cardBrightText,
                            fontFamily = com.example.ui.theme.ZeroPrimeFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 18.sp,
                            letterSpacing = 1.2.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        // 6. Badge Counter (angka unread di Top Bar): fill hitam pekat, text accent, border hitam 2.5dp, hard shadow #E8E8E8 (double stacked) 3dp
                        NxBadge(
                            text = totalUnread.toString(),
                            fillColor = nxColors.borderBlack,
                            textColor = nxColors.accent,
                            shadowOffset = 3.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (uiState.isServerOnline) "NXSERVER · ONLINE" else "NXSERVER · OFFLINE",
                        color = nxColors.cardBrightText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // 5. Tombol Refresh & Tombol Drawer di Top Bar - fill CardDark, border hitam 2.5dp, hard shadow #E8E8E8 (double stacked) 3dp
                NxIconButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Refresh Chats",
                    onClick = onRefresh,
                    size = 40.dp,
                    shadowOffset = 3.dp,
                    modifier = Modifier.testTag("refresh_chats_button")
                )

                Spacer(modifier = Modifier.width(8.dp))

                NxIconButton(
                    icon = Icons.Default.Menu,
                    contentDescription = "Open Drawer Menu",
                    onClick = onOpenDrawer,
                    size = 40.dp,
                    shadowOffset = 3.dp,
                    modifier = Modifier.testTag("open_drawer_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Search Bar - fill CardDark, border hitam 2.5dp, teks terang, double stacked sticker
        NxSearchBar(
            query = uiState.searchQuery,
            onQueryChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Filter Tabs (USER/GROUP/CHANNEL) - aktif = fill accent, tidak aktif = fill CardDark, double stacked sticker
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
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        // 9. Card "SERVER OFFLINE" di empty state - double stacked sticker
                        val emptyCardShape = RoundedCornerShape(12.dp)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 5.dp, bottom = 5.dp)
                        ) {
                            // Layer BELAKANG: Double Stacked Sticker Shadow
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .offset(x = 5.dp, y = 5.dp)
                                    .background(color = nxColors.shadow, shape = emptyCardShape)
                                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = emptyCardShape)
                            )
                            // Layer DEPAN
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(emptyCardShape)
                                    .background(color = nxColors.cardDark, shape = emptyCardShape)
                                    .border(width = 3.dp, color = nxColors.borderBlack, shape = emptyCardShape)
                                    .padding(horizontal = 18.dp, vertical = 22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // Ikon Neo-Brutalist tebal dengan double stacked shadow
                                val iconShape = RoundedCornerShape(14.dp)
                                Box(
                                    modifier = Modifier.padding(end = 4.dp, bottom = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .offset(x = 4.dp, y = 4.dp)
                                            .background(color = nxColors.shadow, shape = iconShape)
                                            .border(width = 2.5.dp, color = nxColors.borderBlack, shape = iconShape)
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

                                Spacer(modifier = Modifier.height(20.dp))

                                // 8. Tombol MUAT ULANG & SETTING di empty state (double stacked sticker)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                                ) {
                                    NxButton(
                                        text = "MUAT ULANG",
                                        icon = Icons.Default.Refresh,
                                        isDarkVariant = false,
                                        shadowOffset = 5.dp,
                                        onClick = onRefresh,
                                        modifier = Modifier.weight(1f)
                                    )
                                    NxButton(
                                        text = "SETTING",
                                        icon = Icons.Default.Settings,
                                        isDarkVariant = false,
                                        shadowOffset = 5.dp,
                                        onClick = onNavigateToSetting,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // 1. Card Chat List (LazyColumn) - jarak antar card min 14dp
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("chat_list"),
                    contentPadding = PaddingValues(top = 2.dp, bottom = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
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
 * 2. Filter Tabs (USER/GROUP/CHANNEL):
 * Double Stacked Sticker:
 * - Layer BELAKANG: Box #E8E8E8 + border hitam 2.5dp + offset(5.dp, 5.dp)
 * - Layer DEPAN: aktif = fill accent + text hitam, tidak aktif = fill CardDark + text terang, border hitam 3dp
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
        horizontalArrangement = Arrangement.spacedBy(14.dp),
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
    val shadowOffset = 5.dp

    val fillColor = if (isActive) nxColors.accent else nxColors.cardDark
    val textColor = if (isActive) nxColors.cardBrightText else nxColors.cardDarkText

    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .padding(end = shadowOffset, bottom = shadowOffset)
    ) {
        // Layer BELAKANG: Box #E8E8E8 + border hitam 2.5dp + offset(5.dp, 5.dp)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = shape)
        )
        // Layer DEPAN: background tema + border hitam 3dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(color = fillColor, shape = shape)
                .border(width = 3.dp, color = nxColors.borderBlack, shape = shape)
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

/**
 * 3. Search Bar - fill LocalNxColors.current.accent, border hitam 2.5dp, teks & ikon hitam, double stacked sticker
 */
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
        // Layer BELAKANG: Double Stacked Sticker Shadow (#E8E8E8 + border hitam 2.5dp + offset 5.dp)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = shape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = shape)
        )
        // Layer DEPAN: fill LocalNxColors.current.accent, border hitam 2.5dp, teks & ikon hitam
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(color = nxColors.accent, shape = shape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = shape)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = nxColors.cardBrightText,
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
                        color = nxColors.cardBrightText,
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
                        color = nxColors.cardBrightText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    cursorBrush = SolidColor(nxColors.cardBrightText),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_chat_input")
                )
            }
            if (query.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear Search",
                    tint = nxColors.cardBrightText,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onQueryChange("") }
                )
            }
        }
    }
}

/**
 * 1. Card Chat List (LazyColumn):
 * - Double Stacked Sticker: Layer belakang #E8E8E8 + border hitam 2.5dp + offset(5.dp, 5.dp)
 * - Layer depan: fill LocalNxColors.current.accent, border hitam 3dp, text hitam, badge putih tulang
 * 6. Badge Counter (angka unread di chat item): fill hitam pekat, text accent, border hitam 2.5dp, hard shadow #E8E8E8 (double stacked) 3dp.
 */
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

    val cardShape = RoundedCornerShape(12.dp)
    val shadowOffset = 5.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = shadowOffset, bottom = shadowOffset)
            .testTag("chat_item_${chat.id}")
    ) {
        // Layer BELAKANG: Double Stacked Sticker Shadow (#E8E8E8 + border hitam 2.5dp + offset 5.dp)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(color = nxColors.shadow, shape = cardShape)
                .border(width = 2.5.dp, color = nxColors.borderBlack, shape = cardShape)
        )

        // Layer DEPAN: fill LocalNxColors.current.accent, border hitam 3dp, text hitam
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .background(color = nxColors.accent, shape = cardShape)
                .border(width = 3.dp, color = nxColors.borderBlack, shape = cardShape)
                .clickable { onClick() }
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar bulat (CircleShape) dengan border hitam tebal 2.5dp dan double stacked hard shadow #E8E8E8 3dp
            Box(
                modifier = Modifier.padding(end = 3.dp, bottom = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .offset(x = 3.dp, y = 3.dp)
                        .background(color = nxColors.shadow, shape = CircleShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(color = nxColors.cardDark, shape = CircleShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = nxColors.accent,
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
                        color = nxColors.cardBrightText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    // Badge tipe chat putih tulang (#E8E8E8) dengan border hitam
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(nxColors.shadow)
                            .border(2.dp, nxColors.borderBlack, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = typeTag,
                            color = nxColors.borderBlack,
                            fontFamily = MonocraftFontFamily,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // last_message (JetBrains Mono, text hitam kontras, max 1 baris ellipsis)
                Text(
                    text = safeLastMsg,
                    color = nxColors.cardBrightText.copy(alpha = 0.8f),
                    fontFamily = JetBrainsMonoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (chat.unread > 0) {
                Spacer(modifier = Modifier.width(10.dp))
                // 6. Badge Counter (angka unread di chat item) - fill hitam pekat, text accent, border hitam 2.5dp, hard shadow #E8E8E8 (double stacked) 3dp
                NxBadge(
                    text = chat.unread.toString(),
                    fillColor = nxColors.borderBlack,
                    textColor = nxColors.accent,
                    shadowOffset = 3.dp
                )
            }
        }
    }
}
