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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.network.MessageItem
import com.example.ui.NxUiState
import com.example.ui.components.NxCard
import com.example.ui.components.NxIconButton
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.MonocraftFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily

@Composable
fun ChatRoomScreen(
    chatId: Long,
    chatName: String,
    uiState: NxUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSendMessage: (String, () -> Unit) -> Unit
) {
    val nxColors = LocalNxColors.current
    var inputText by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll ke pesan terbaru saat buka chat atau saat pesan bertambah
    LaunchedEffect(uiState.messages.size, chatId) {
        if (uiState.messages.isNotEmpty()) {
            try {
                listState.animateScrollToItem(uiState.messages.lastIndex)
            } catch (_: Exception) {
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp)
            .testTag("chat_room_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Top bar: tombol back, nama chat + status online/offline, tombol refresh
        NxCard(
            isBright = false,
            contentPadding = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NxIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = onBack,
                    size = 42.dp,
                    modifier = Modifier.testTag("chat_back_button")
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = chatName.ifBlank { "N/A" },
                        color = nxColors.cardDarkText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(
                                    if (uiState.isServerOnline) nxColors.online else nxColors.warning
                                )
                                .border(1.dp, nxColors.borderBlack, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isServerOnline) "ONLINE · ID #$chatId" else "OFFLINE · ID #$chatId",
                            color = if (uiState.isServerOnline) nxColors.online else nxColors.subText,
                            fontFamily = JetBrainsMonoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                NxIconButton(
                    icon = Icons.Default.Refresh,
                    contentDescription = "Refresh Messages",
                    onClick = onRefresh,
                    size = 42.dp,
                    modifier = Modifier.testTag("chat_refresh_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bodi: LazyColumn bubble chat
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (uiState.isLoadingMessages && uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = nxColors.accent)
                }
            } else if (uiState.messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    NxCard(
                        isBright = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "BELUM ADA PESAN",
                                color = nxColors.accent,
                                fontFamily = MonocraftFontFamily,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Kirim pesan baru di bawah atau tekan tombol refresh di atas.",
                                color = nxColors.subText,
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("messages_list"),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = uiState.messages,
                        key = { "${it.id}_${it.date}_${it.out}" }
                    ) { message ->
                        ChatBubbleItem(message = message)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom: TextField input (border hitam 3dp) + tombol kirim (ikon send, bg accent, border hitam)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val inputShape = RoundedCornerShape(12.dp)
            val shadowOffset = 4.dp

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = shadowOffset, bottom = shadowOffset)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = shadowOffset, y = shadowOffset)
                        .background(color = nxColors.shadow, shape = inputShape)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(inputShape)
                        .background(color = nxColors.cardDark, shape = inputShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = inputShape)
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Tulis pesan...",
                            color = nxColors.subText,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        textStyle = TextStyle(
                            color = nxColors.cardDarkText,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        cursorBrush = SolidColor(nxColors.accent),
                        maxLines = 4,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("message_input_field")
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Tombol kirim (ikon send, bg accent, border hitam) + hard shadow
            val sendBtnShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(end = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .background(color = nxColors.shadow, shape = sendBtnShape)
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(sendBtnShape)
                        .background(color = nxColors.accent, shape = sendBtnShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = sendBtnShape)
                        .clickable(enabled = !uiState.isSendingMessage && inputText.isNotBlank()) {
                            onSendMessage(inputText) {
                                inputText = ""
                            }
                        }
                        .testTag("send_message_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send Message",
                        tint = nxColors.cardBrightText,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubbleItem(message: MessageItem) {
    val nxColors = LocalNxColors.current
    val isOut = message.out
    // Pesan keluar (out=true) = bubble accent tema, text hitam, align kanan.
    // Pesan masuk (out=false) = bubble cardDark, text cardDarkText, align kiri.
    // Semua bubble border hitam 2dp + hard shadow 3dp.
    val bubbleFill = if (isOut) nxColors.accent else nxColors.cardDark
    val bubbleText = if (isOut) nxColors.cardBrightText else nxColors.cardDarkText
    val metaColor = if (isOut) nxColors.cardBrightText.copy(alpha = 0.75f) else nxColors.subText
    val bubbleShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = if (isOut) 12.dp else 4.dp,
        bottomEnd = if (isOut) 4.dp else 12.dp
    )
    val shadowOffset = 3.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOut) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(min = 90.dp, max = 300.dp)
                .padding(end = shadowOffset, bottom = shadowOffset)
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(color = nxColors.shadow, shape = bubbleShape)
            )
            Column(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(color = bubbleFill, shape = bubbleShape)
                    .border(width = 2.dp, color = nxColors.borderBlack, shape = bubbleShape)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (!isOut && message.sender_id != 0L) {
                    Text(
                        text = "ID: ${message.sender_id}",
                        color = nxColors.accent,
                        fontFamily = MonocraftFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                }

                Text(
                    text = message.text.ifBlank { "N/A" },
                    color = bubbleText,
                    fontFamily = SpaceGroteskFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = message.date.ifBlank { "N/A" },
                    color = metaColor,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
