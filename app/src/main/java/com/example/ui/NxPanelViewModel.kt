package com.example.ui

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.NxAppPreferences
import com.example.data.NxPreferencesRepository
import com.example.network.ChatItem
import com.example.network.MessageItem
import com.example.network.NxNetworkRepository
import com.example.network.UserProfile
import com.example.ui.components.NxNotificationState
import com.example.ui.components.NxNotificationType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NxUiState(
    val userProfile: UserProfile? = null,
    val chats: List<ChatItem> = emptyList(),
    val searchQuery: String = "",
    val isLoadingChats: Boolean = false,
    val isServerOnline: Boolean = false,
    val currentChatId: Long? = null,
    val currentChatName: String = "N/A",
    val messages: List<MessageItem> = emptyList(),
    val isLoadingMessages: Boolean = false,
    val isSendingMessage: Boolean = false,
    val isTestingConnection: Boolean = false,
    val notification: NxNotificationState = NxNotificationState(),
    val showThemeModal: Boolean = false,
    val showBackgroundModal: Boolean = false
)

class NxPanelViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsRepo = NxPreferencesRepository(application.applicationContext)
    private val networkRepo = NxNetworkRepository()

    val preferences: StateFlow<NxAppPreferences> = prefsRepo.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NxAppPreferences()
    )

    private val _uiState = MutableStateFlow(NxUiState())
    val uiState: StateFlow<NxUiState> = _uiState.asStateFlow()

    val filteredChats: StateFlow<List<ChatItem>> = combine(
        _uiState,
        preferences
    ) { state, _ ->
        try {
            val q = state.searchQuery.trim()
            if (q.isEmpty()) {
                state.chats
            } else {
                state.chats.filter { it.name.contains(q, ignoreCase = true) }
            }
        } catch (_: Exception) {
            emptyList()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private var notificationJob: Job? = null

    fun triggerFeedback() {
        try {
            val prefs = preferences.value
            val app = getApplication<Application>()
            if (prefs.hapticEnabled) {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val manager = app.getSystemService(VibratorManager::class.java)
                    manager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    app.getSystemService(Vibrator::class.java)
                }
                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(25L, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(25L)
                    }
                }
            }
            if (prefs.soundEnabled) {
                val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 45)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            }
        } catch (_: Exception) {
        }
    }

    fun showNotification(
        message: String,
        type: NxNotificationType = NxNotificationType.ERROR,
        durationMs: Long = 2600L
    ) {
        notificationJob?.cancel()
        notificationJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                notification = NxNotificationState(
                    visible = true,
                    message = message.ifBlank { "Koneksi ke server gagal" },
                    type = type
                )
            )
            delay(durationMs)
            _uiState.value = _uiState.value.copy(
                notification = _uiState.value.notification.copy(visible = false)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun openThemeModal(show: Boolean) {
        triggerFeedback()
        _uiState.value = _uiState.value.copy(showThemeModal = show)
    }

    fun openBackgroundModal(show: Boolean) {
        triggerFeedback()
        _uiState.value = _uiState.value.copy(showBackgroundModal = show)
    }

    fun updateTheme(themeId: String, onDone: () -> Unit) {
        triggerFeedback()
        viewModelScope.launch {
            prefsRepo.setTheme(themeId)
            _uiState.value = _uiState.value.copy(showThemeModal = false)
            onDone()
        }
    }

    fun updateBackground(backgroundId: String) {
        triggerFeedback()
        viewModelScope.launch {
            prefsRepo.setBackground(backgroundId)
        }
    }

    fun updateBaseUrl(newUrl: String) {
        triggerFeedback()
        viewModelScope.launch {
            val clean = newUrl.trim().ifBlank { NxPreferencesRepository.DEFAULT_BASE_URL }
            prefsRepo.setBaseUrl(clean)
            showNotification("Base URL disimpan: $clean", NxNotificationType.INFO, 2200L)
        }
    }

    fun toggleHaptic(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setHapticEnabled(enabled)
            triggerFeedback()
        }
    }

    fun toggleSound(enabled: Boolean) {
        viewModelScope.launch {
            prefsRepo.setSoundEnabled(enabled)
            triggerFeedback()
        }
    }

    fun loadHomeData(showLoading: Boolean = true) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(isLoadingChats = true)
            }
            val url = preferences.value.baseUrl
            val meResult = networkRepo.fetchMe(url)
            val chatsResult = networkRepo.fetchChats(url, limit = 50)

            if (chatsResult.isSuccess) {
                val chatList = chatsResult.getOrDefault(emptyList())
                val me = meResult.getOrNull() ?: _uiState.value.userProfile
                _uiState.value = _uiState.value.copy(
                    chats = chatList,
                    userProfile = me,
                    isServerOnline = true,
                    isLoadingChats = false
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isServerOnline = false,
                    isLoadingChats = false
                )
                showNotification("Koneksi ke server gagal", NxNotificationType.ERROR)
            }
        }
    }

    fun testConnection(customUrl: String? = null) {
        triggerFeedback()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isTestingConnection = true)
            val targetUrl = customUrl?.trim()?.ifBlank { preferences.value.baseUrl } ?: preferences.value.baseUrl
            prefsRepo.setBaseUrl(targetUrl)

            val result = networkRepo.fetchMe(targetUrl)
            result.fold(
                onSuccess = { profile ->
                    _uiState.value = _uiState.value.copy(
                        userProfile = profile,
                        isServerOnline = true,
                        isTestingConnection = false
                    )
                    val displayUser = if (profile.username.isNotBlank() && profile.username != "N/A") {
                        "@${profile.username}"
                    } else {
                        profile.name
                    }
                    showNotification(
                        message = "Terhubung! Halo, $displayUser",
                        type = NxNotificationType.SUCCESS
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isServerOnline = false,
                        isTestingConnection = false
                    )
                    showNotification(
                        message = "Koneksi ke server gagal",
                        type = NxNotificationType.ERROR
                    )
                }
            )
        }
    }

    fun openChatRoom(chatId: Long, chatName: String) {
        triggerFeedback()
        _uiState.value = _uiState.value.copy(
            currentChatId = chatId,
            currentChatName = chatName.ifBlank { "N/A" },
            messages = emptyList(),
            isLoadingMessages = true
        )
        refreshMessages(chatId)
    }

    fun refreshMessages(chatId: Long? = _uiState.value.currentChatId) {
        val id = chatId ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingMessages = true)
            val url = preferences.value.baseUrl
            val result = networkRepo.fetchMessages(url, chatId = id, limit = 50)
            result.fold(
                onSuccess = { msgs ->
                    _uiState.value = _uiState.value.copy(
                        messages = msgs,
                        isServerOnline = true,
                        isLoadingMessages = false
                    )
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isServerOnline = false,
                        isLoadingMessages = false
                    )
                    showNotification("Koneksi ke server gagal", NxNotificationType.ERROR)
                }
            )
        }
    }

    fun sendMessage(chatId: Long, text: String, onMessageSent: () -> Unit) {
        val cleanText = text.trim()
        if (cleanText.isEmpty()) return
        triggerFeedback()
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSendingMessage = true)
            val url = preferences.value.baseUrl
            val result = networkRepo.postMessage(url, chatId = chatId, text = cleanText)
            result.fold(
                onSuccess = { response ->
                    if (response.ok) {
                        val newMsg = MessageItem(
                            id = if (response.id != 0L) response.id else System.currentTimeMillis(),
                            sender_id = _uiState.value.userProfile?.id ?: 0L,
                            text = cleanText,
                            date = "Baru saja",
                            out = true
                        )
                        _uiState.value = _uiState.value.copy(
                            messages = _uiState.value.messages + newMsg,
                            isSendingMessage = false,
                            isServerOnline = true
                        )
                        onMessageSent()
                        refreshMessages(chatId)
                    } else {
                        _uiState.value = _uiState.value.copy(isSendingMessage = false)
                        showNotification("Gagal mengirim pesan", NxNotificationType.ERROR)
                    }
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isSendingMessage = false,
                        isServerOnline = false
                    )
                    showNotification("Koneksi ke server gagal", NxNotificationType.ERROR)
                }
            )
        }
    }
}
