package com.example.ui.screens

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.network.MessageItem
import com.example.ui.NxUiState
import com.example.ui.components.NxCard
import com.example.ui.components.NxIconButton
import com.example.ui.theme.JetBrainsMonoFontFamily
import com.example.ui.theme.LocalNxColors
import com.example.ui.theme.MonocraftFontFamily
import com.example.ui.theme.SpaceGroteskFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private const val MAX_PHOTO_SIZE_BYTES = 5L * 1024L * 1024L // 5 MB

private data class PhotoMetadata(
    val sizeText: String = "Memuat info ukuran...",
    val dimensionText: String = "Memuat dimensi...",
    val ratioText: String = "-"
)

@Composable
fun ChatRoomScreen(
    chatId: Long,
    chatName: String,
    baseUrl: String = "http://127.0.0.1:8080",
    uiState: NxUiState,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSendMessage: (String, Long?, () -> Unit) -> Unit,
    onSendPhoto: (String, String, Long?, () -> Unit) -> Unit = { _, _, _, _ -> }
) {
    val nxColors = LocalNxColors.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var inputText by rememberSaveable { mutableStateOf("") }
    var selectedPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPhotoBase64 by remember { mutableStateOf<String?>(null) }
    var isEncodingPhoto by remember { mutableStateOf(false) }

    // State untuk Reply Pesan
    var replyingToMessage by remember { mutableStateOf<MessageItem?>(null) }

    // State untuk Fullscreen Photo Viewer
    var fullscreenPhotoUrl by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    val messagesById = remember(uiState.messages) {
        uiState.messages.associateBy { it.id }
    }

    // SAF Image Picker (tanpa runtime permission)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            coroutineScope.launch {
                isEncodingPhoto = true
                val validationResult = withContext(Dispatchers.IO) {
                    readAndValidateImage(context, uri)
                }
                isEncodingPhoto = false
                validationResult.fold(
                    onSuccess = { base64String ->
                        selectedPhotoUri = uri
                        selectedPhotoBase64 = base64String
                    },
                    onFailure = { error ->
                        selectedPhotoUri = null
                        selectedPhotoBase64 = null
                        Toast.makeText(
                            context,
                            error.message ?: "Foto terlalu besar, maksimal 5MB",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }

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
                        val targetReplyMsg = message.reply_to_msg_id?.let { messagesById[it] }
                        ChatBubbleItem(
                            message = message,
                            baseUrl = baseUrl,
                            repliedMessage = targetReplyMsg,
                            onLongPressReply = { replyingToMessage = message },
                            onPhotoClick = { resolvedUrl -> fullscreenPhotoUrl = resolvedUrl }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Reply Bar (Saat mode reply aktif: di atas TextField input)
        val activeReply = replyingToMessage
        if (activeReply != null) {
            val replyShape = RoundedCornerShape(8.dp)
            val senderLabel = if (activeReply.out) "Kamu" else "Lawan bicara"
            val rawSnippet = when {
                activeReply.text.isNotBlank() && activeReply.text != "N/A" -> activeReply.text
                activeReply.has_photo -> "📷 [Foto]"
                else -> ""
            }
            val snippet = if (rawSnippet.length > 50) {
                rawSnippet.take(50) + "..."
            } else {
                rawSnippet
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .clip(replyShape)
                    .background(color = nxColors.cardDark, shape = replyShape)
                    .border(width = 2.dp, color = nxColors.accent, shape = replyShape)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("reply_bar"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Replying",
                    tint = nxColors.accent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = senderLabel,
                        color = nxColors.accent,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = snippet,
                        color = nxColors.cardDarkText,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color = nxColors.accent, shape = CircleShape)
                        .border(width = 1.5.dp, color = nxColors.borderBlack, shape = CircleShape)
                        .clickable { replyingToMessage = null }
                        .testTag("cancel_reply_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Batal Reply",
                        tint = nxColors.cardBrightText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Preview foto sebelum kirim (di atas TextField, Box border hitam 2.5dp, hard shadow putih #E8E8E8 3dp double stacked)
        if (selectedPhotoUri != null) {
            val previewShape = RoundedCornerShape(12.dp)
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .padding(end = 3.dp, bottom = 3.dp)
                    .testTag("photo_preview_box")
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 3.dp, y = 3.dp)
                        .background(color = nxColors.shadow, shape = previewShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = previewShape)
                )
                Row(
                    modifier = Modifier
                        .clip(previewShape)
                        .background(color = nxColors.cardDark, shape = previewShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = previewShape)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(76.dp)
                    ) {
                        AsyncImage(
                            model = selectedPhotoUri,
                            contentDescription = "Selected Photo Preview",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = 2.dp,
                                    color = nxColors.borderBlack,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                        // Tombol X kecil di pojok kanan atas preview untuk batal kirim foto
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 4.dp, y = (-4).dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color = nxColors.accent, shape = CircleShape)
                                .border(width = 2.dp, color = nxColors.borderBlack, shape = CircleShape)
                                .clickable(enabled = !uiState.isSendingMessage) {
                                    selectedPhotoUri = null
                                    selectedPhotoBase64 = null
                                }
                                .testTag("cancel_photo_preview_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Batal Kirim Foto",
                                tint = nxColors.cardBrightText,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        Text(
                            text = if (uiState.isSendingMessage) "MENGIRIM..." else "FOTO SIAP DIKIRIM",
                            color = nxColors.accent,
                            fontFamily = MonocraftFontFamily,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (inputText.isBlank()) {
                                "Ketik teks di bawah untuk caption (opsional)"
                            } else {
                                "Caption: $inputText"
                            },
                            color = nxColors.cardDarkText,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Bottom: Tombol Attachment (📎) + TextField input (border hitam 3dp) + tombol kirim (ikon send, bg accent, border hitam)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val attachShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .padding(end = 3.dp, bottom = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(x = 3.dp, y = 3.dp)
                        .background(color = nxColors.shadow, shape = attachShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = attachShape)
                )
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(attachShape)
                        .background(color = nxColors.cardDark, shape = attachShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = attachShape)
                        .clickable(enabled = !uiState.isSendingMessage && !isEncodingPhoto) {
                            imagePickerLauncher.launch("image/*")
                        }
                        .testTag("attach_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach Photo",
                        tint = nxColors.accent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

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
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = inputShape)
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
                            text = if (selectedPhotoUri != null) "Tulis caption foto..." else "Tulis pesan...",
                            color = nxColors.subText,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        enabled = !uiState.isSendingMessage,
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

            val canSend = !uiState.isSendingMessage && !isEncodingPhoto &&
                (selectedPhotoBase64 != null || inputText.isNotBlank())
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
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = sendBtnShape)
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(sendBtnShape)
                        .background(color = nxColors.accent, shape = sendBtnShape)
                        .border(width = 3.dp, color = nxColors.borderBlack, shape = sendBtnShape)
                        .clickable(enabled = canSend) {
                            val photoB64 = selectedPhotoBase64
                            val replyId = replyingToMessage?.id
                            if (photoB64 != null) {
                                onSendPhoto(inputText, photoB64, replyId) {
                                    inputText = ""
                                    selectedPhotoUri = null
                                    selectedPhotoBase64 = null
                                    replyingToMessage = null
                                }
                            } else {
                                onSendMessage(inputText, replyId) {
                                    inputText = ""
                                    replyingToMessage = null
                                }
                            }
                        }
                        .testTag("send_message_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isSendingMessage || isEncodingPhoto) {
                        CircularProgressIndicator(
                            color = nxColors.cardBrightText,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
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

    // Fullscreen Photo Viewer Dialog
    val activePhotoUrl = fullscreenPhotoUrl
    if (activePhotoUrl != null) {
        FullscreenPhotoViewerDialog(
            photoUrl = activePhotoUrl,
            onDismiss = { fullscreenPhotoUrl = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatBubbleItem(
    message: MessageItem,
    baseUrl: String,
    repliedMessage: MessageItem?,
    onLongPressReply: () -> Unit,
    onPhotoClick: (String) -> Unit
) {
    val nxColors = LocalNxColors.current
    val isOut = message.out

    // Pesan keluar (out=true) = bubble accent tema, text hitam, align kanan.
    // Pesan masuk (out=false) = bubble cardDark, text cardDarkText, align kiri.
    // Semua bubble border hitam 2.5dp + double stacked hard shadow 3dp.
    val bubbleFill = if (isOut) nxColors.accent else nxColors.cardDark
    val bubbleText = if (isOut) nxColors.cardBrightText else nxColors.cardDarkText
    // Timestamp warna kontras: out = text hitam opacity 60%, in = text terang opacity 70%
    val timestampColor = if (isOut) {
        nxColors.cardBrightText.copy(alpha = 0.6f)
    } else {
        nxColors.cardDarkText.copy(alpha = 0.7f)
    }

    val bubbleShape = RoundedCornerShape(
        topStart = 12.dp,
        topEnd = 12.dp,
        bottomStart = if (isOut) 12.dp else 4.dp,
        bottomEnd = if (isOut) 4.dp else 12.dp
    )
    val shadowOffset = 3.dp

    val resolvedPhotoUrl = remember(message.photo_url, baseUrl) {
        resolveMessagePhotoUrl(message.photo_url, baseUrl)
    }

    val formattedDate = remember(message.date) {
        formatHumanFriendlyTimestamp(message.date)
    }

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
                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = bubbleShape)
            )
            Column(
                modifier = Modifier
                    .clip(bubbleShape)
                    .background(color = bubbleFill, shape = bubbleShape)
                    .border(width = 2.5.dp, color = nxColors.borderBlack, shape = bubbleShape)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onLongPressReply
                    )
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
                    Spacer(modifier = Modifier.height(4.dp))
                }

                // TUGAS 3.5: Quote kecil di atas teks utama jika reply_to_msg_id != null
                if (message.reply_to_msg_id != null) {
                    val quoteBg = nxColors.borderBlack.copy(alpha = if (isOut) 0.14f else 0.35f)
                    val leftBarColor = if (isOut) nxColors.borderBlack else nxColors.accent
                    val quoteSenderColor = if (isOut) nxColors.cardBrightText else nxColors.accent
                    val quoteSnippet = if (repliedMessage != null) {
                        val txt = when {
                            repliedMessage.text.isNotBlank() && repliedMessage.text != "N/A" -> repliedMessage.text
                            repliedMessage.has_photo -> "📷 [Foto]"
                            else -> ""
                        }
                        if (txt.length > 50) txt.take(50) + "..." else txt
                    } else {
                        "[Pesan tidak ditemukan]"
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(quoteBg)
                    ) {
                        // Border kiri tebal accent 3dp
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(nxColors.accent)
                        )
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            if (repliedMessage != null) {
                                Text(
                                    text = if (repliedMessage.out) "Kamu" else "Lawan bicara",
                                    color = quoteSenderColor,
                                    fontFamily = SpaceGroteskFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = quoteSnippet,
                                color = bubbleText.copy(alpha = 0.88f),
                                fontFamily = JetBrainsMonoFontFamily,
                                fontSize = 11.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                // TUGAS 1: Jika has_photo = true, tampilkan AsyncImage (max 220.dp x 220.dp, Fit, rounded 8dp, border hitam 2dp)
                if (message.has_photo && resolvedPhotoUrl != null) {
                    val imageShape = RoundedCornerShape(8.dp)
                    AsyncImage(
                        model = resolvedPhotoUrl,
                        contentDescription = "Chat Photo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .widthIn(max = 220.dp)
                            .heightIn(max = 220.dp)
                            .clip(imageShape)
                            .background(nxColors.bg, imageShape)
                            .border(width = 2.dp, color = nxColors.borderBlack, shape = imageShape)
                            .combinedClickable(
                                onClick = { onPhotoClick(resolvedPhotoUrl) },
                                onLongClick = onLongPressReply
                            )
                    )

                    val captionText = message.text.trim()
                    if (captionText.isNotEmpty() && captionText != "N/A") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = captionText,
                            color = bubbleText,
                            fontFamily = SpaceGroteskFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Jika has_photo = false (atau fallback), tampilkan bubble teks seperti existing
                    val displayBody = if (message.has_photo && message.text.isBlank()) {
                        "📷 [Foto]"
                    } else {
                        message.text.ifBlank { "N/A" }
                    }
                    Text(
                        text = displayBody,
                        color = bubbleText,
                        fontFamily = SpaceGroteskFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // TUGAS 4: Timestamp format manusiawi di bagian bawah bubble
                Text(
                    text = formattedDate,
                    color = timestampColor,
                    fontFamily = JetBrainsMonoFontFamily,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}

/**
 * TUGAS 2: FULLSCREEN PHOTO VIEWER
 * - Background hitam pekat (#000000)
 * - Tombol close (X) di pojok kanan atas, style Neo-Brutalist mini
 * - Gambar besar dengan contentScale = ContentScale.Fit + pinch-to-zoom (pointerInput + detectTransformGestures)
 * - Info Foto di bawah gambar (Ukuran KB/MB, Dimensi piksel W x H px, dan rasio W:H) dengan font JetBrainsMono-Regular
 */
@Composable
private fun FullscreenPhotoViewerDialog(
    photoUrl: String,
    onDismiss: () -> Unit
) {
    val nxColors = LocalNxColors.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var metadata by remember(photoUrl) { mutableStateOf(PhotoMetadata()) }

    LaunchedEffect(photoUrl) {
        metadata = withContext(Dispatchers.IO) {
            fetchPhotoMetadata(photoUrl)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF000000))
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .testTag("fullscreen_photo_viewer")
        ) {
            // Area Gambar dengan Pinch-to-Zoom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(photoUrl) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 5f)
                            scale = newScale
                            offset = if (newScale > 1f) {
                                Offset(
                                    x = offset.x + pan.x,
                                    y = offset.y + pan.y
                                )
                            } else {
                                Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = photoUrl,
                    contentDescription = "Fullscreen Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 72.dp)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        )
                )
            }

            // Tombol Close (X) di pojok kanan atas, style Neo-Brutalist mini
            val closeShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 16.dp, end = 16.dp)
                    .padding(end = 3.dp, bottom = 3.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .offset(x = 3.dp, y = 3.dp)
                        .background(color = nxColors.shadow, shape = closeShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = closeShape)
                )
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(closeShape)
                        .background(color = nxColors.accent, shape = closeShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = closeShape)
                        .clickable { onDismiss() }
                        .testTag("close_fullscreen_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Fullscreen Photo",
                        tint = nxColors.cardBrightText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Info Foto di bawah gambar (JetBrainsMono-Regular)
            val infoShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(end = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset(x = 4.dp, y = 4.dp)
                        .background(color = nxColors.shadow, shape = infoShape)
                        .border(width = 2.5.dp, color = nxColors.borderBlack, shape = infoShape)
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(infoShape)
                        .background(color = nxColors.cardDark, shape = infoShape)
                        .border(width = 2.5.dp, color = nxColors.accent, shape = infoShape)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "UKURAN: ${metadata.sizeText}  ·  DIMENSI: ${metadata.dimensionText}  ·  RASIO: ${metadata.ratioText}",
                        color = nxColors.cardDarkText,
                        fontFamily = JetBrainsMonoFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun resolveMessagePhotoUrl(rawUrl: String?, baseUrl: String): String? {
    val clean = rawUrl?.trim()
    if (clean.isNullOrEmpty() || clean.equals("null", ignoreCase = true)) {
        return null
    }
    return if (clean.startsWith("http://") || clean.startsWith("https://")) {
        clean
    } else {
        val cleanBase = baseUrl.trim().removeSuffix("/")
        val cleanPath = if (clean.startsWith("/")) clean else "/$clean"
        "$cleanBase$cleanPath"
    }
}

private fun fetchPhotoMetadata(photoUrl: String): PhotoMetadata {
    return try {
        val connection = (URL(photoUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 6000
            readTimeout = 8000
            requestMethod = "GET"
            doInput = true
        }
        connection.connect()
        val contentLength = connection.contentLengthLong
        val bytes = connection.inputStream.use { it.readBytes() }
        connection.disconnect()

        val actualSizeBytes = if (contentLength > 0L) contentLength else bytes.size.toLong()
        val sizeStr = if (actualSizeBytes >= 1024L * 1024L) {
            String.format(Locale.US, "%.2f MB", actualSizeBytes.toDouble() / (1024.0 * 1024.0))
        } else if (actualSizeBytes >= 1024L) {
            String.format(Locale.US, "%.1f KB", actualSizeBytes.toDouble() / 1024.0)
        } else {
            "$actualSizeBytes B"
        }

        // Decode bounds dengan BitmapFactory.Options(inJustDecodeBounds = true)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        val w = options.outWidth
        val h = options.outHeight

        if (w > 0 && h > 0) {
            val divisor = gcd(w, h)
            val rw = w / divisor
            val rh = h / divisor
            PhotoMetadata(
                sizeText = sizeStr,
                dimensionText = "$w x $h px",
                ratioText = "$rw:$rh"
            )
        } else {
            PhotoMetadata(
                sizeText = sizeStr,
                dimensionText = "N/A",
                ratioText = "N/A"
            )
        }
    } catch (_: Exception) {
        PhotoMetadata(
            sizeText = "N/A",
            dimensionText = "N/A",
            ratioText = "N/A"
        )
    }
}

private tailrec fun gcd(a: Int, b: Int): Int {
    return if (b == 0) kotlin.math.abs(a).coerceAtLeast(1) else gcd(b, a % b)
}

/**
 * TUGAS 4: Format Tanggal & Waktu di Bubble:
 * - Hari ini -> "14:30"
 * - Kemarin -> "Kemarin, 14:30"
 * - Minggu ini -> "Sen, 14:30"
 * - Lebih lama -> "08 Okt, 14:30"
 * Jika gagal parse, kembalikan string original.
 */
private fun formatHumanFriendlyTimestamp(rawDate: String): String {
    val trimmed = rawDate.trim()
    if (trimmed.isEmpty() || trimmed == "N/A" || trimmed.equals("Baru saja", ignoreCase = true)) {
        return trimmed.ifEmpty { "N/A" }
    }
    return try {
        val parsedMillis = parseIsoOrDateToMillis(trimmed) ?: return rawDate
        val msgCal = Calendar.getInstance().apply { timeInMillis = parsedMillis }
        val nowCal = Calendar.getInstance()

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timePart = timeFormat.format(Date(parsedMillis))

        val isSameDay = msgCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR) &&
            msgCal.get(Calendar.DAY_OF_YEAR) == nowCal.get(Calendar.DAY_OF_YEAR)
        if (isSameDay) {
            return timePart
        }

        val yesterdayCal = (nowCal.clone() as Calendar).apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val isYesterday = msgCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
            msgCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR)
        if (isYesterday) {
            return "Kemarin, $timePart"
        }

        val diffMillis = nowCal.timeInMillis - msgCal.timeInMillis
        val sevenDaysMillis = 7L * 24L * 60L * 60L * 1000L
        if (diffMillis in 1..sevenDaysMillis) {
            val dayName = when (msgCal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> "Sen"
                Calendar.TUESDAY -> "Sel"
                Calendar.WEDNESDAY -> "Rab"
                Calendar.THURSDAY -> "Kam"
                Calendar.FRIDAY -> "Jum"
                Calendar.SATURDAY -> "Sab"
                Calendar.SUNDAY -> "Min"
                else -> "Hari"
            }
            return "$dayName, $timePart"
        }

        val monthName = when (msgCal.get(Calendar.MONTH)) {
            Calendar.JANUARY -> "Jan"
            Calendar.FEBRUARY -> "Feb"
            Calendar.MARCH -> "Mar"
            Calendar.APRIL -> "Apr"
            Calendar.MAY -> "Mei"
            Calendar.JUNE -> "Jun"
            Calendar.JULY -> "Jul"
            Calendar.AUGUST -> "Agu"
            Calendar.SEPTEMBER -> "Sep"
            Calendar.OCTOBER -> "Okt"
            Calendar.NOVEMBER -> "Nov"
            Calendar.DECEMBER -> "Des"
            else -> ""
        }
        val dayOfMonth = String.format(Locale.US, "%02d", msgCal.get(Calendar.DAY_OF_MONTH))
        "$dayOfMonth $monthName, $timePart"
    } catch (_: Exception) {
        rawDate
    }
}

private fun parseIsoOrDateToMillis(raw: String): Long? {
    // Normalize fractional seconds and timezone for SimpleDateFormat compatibility across minSdk 24+
    var normalized = raw.trim()
    // Remove microseconds if present (e.g. .123456+00:00 -> .123+00:00)
    normalized = normalized.replace(Regex("\\.(\\d{3})\\d+"), ".$1")

    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd HH:mm"
    )

    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            if (!pattern.endsWith("X") && !pattern.endsWith("Z") && raw.endsWith("Z")) {
                sdf.timeZone = TimeZone.getTimeZone("UTC")
            }
            val date = sdf.parse(normalized)
            if (date != null) {
                return date.time
            }
        } catch (_: Exception) {
        }
    }

    // Fallback manual ISO-8601 timezone normalization (+00:00 -> +0000)
    return try {
        val fallbackStr = if (normalized.endsWith("Z")) {
            normalized.removeSuffix("Z") + "+0000"
        } else {
            normalized.replace(Regex("([+-]\\d{2}):(\\d{2})$"), "$1$2")
        }
        val fallbackPatterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ssZ"
        )
        for (p in fallbackPatterns) {
            try {
                val sdf = SimpleDateFormat(p, Locale.US)
                val d = sdf.parse(fallbackStr)
                if (d != null) return d.time
            } catch (_: Exception) {
            }
        }
        null
    } catch (_: Exception) {
        null
    }
}

private fun readAndValidateImage(context: Context, uri: Uri): Result<String> {
    return try {
        val resolver = context.contentResolver
        var fileSize = -1L

        resolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (sizeIdx != -1 && !cursor.isNull(sizeIdx)) {
                    fileSize = cursor.getLong(sizeIdx)
                }
            }
        }

        if (fileSize < 0L) {
            resolver.openAssetFileDescriptor(uri, "r")?.use { afd ->
                if (afd.length >= 0L) {
                    fileSize = afd.length
                }
            }
        }

        if (fileSize > MAX_PHOTO_SIZE_BYTES) {
            return Result.failure(IllegalArgumentException("Foto terlalu besar, maksimal 5MB"))
        }

        val bytes = resolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        } ?: return Result.failure(IllegalArgumentException("Gagal membaca file foto"))

        if (bytes.size.toLong() > MAX_PHOTO_SIZE_BYTES) {
            return Result.failure(IllegalArgumentException("Foto terlalu besar, maksimal 5MB"))
        }

        val encoded = Base64.encodeToString(bytes, Base64.NO_WRAP)
        Result.success(encoded)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
