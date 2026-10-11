package com.example

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.NxPanelViewModel
import com.example.ui.components.NxBackgroundPickerDialog
import com.example.ui.components.NxDrawerContent
import com.example.ui.components.NxNotificationBanner
import com.example.ui.components.NxThemePickerDialog
import com.example.ui.screens.ChatRoomScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InfoScreen
import com.example.ui.screens.SettingScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.NxBackgroundCatalog
import com.example.ui.theme.NxBackgroundPreset
import com.example.ui.theme.NxPanelTheme
import com.example.ui.theme.NxThemeOption
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        private var hasShownSplashThisProcess = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: NxPanelViewModel = viewModel()
            val preferences by viewModel.preferences.collectAsStateWithLifecycle()
            val currentTheme = remember(preferences.themeId) {
                NxThemeOption.fromId(preferences.themeId)
            }

            NxPanelTheme(themeOption = currentTheme) {
                NxPanelRootApp(
                    viewModel = viewModel,
                    currentTheme = currentTheme,
                    startAtHome = hasShownSplashThisProcess,
                    onMarkSplashCompleted = { hasShownSplashThisProcess = true }
                )
            }
        }
    }
}

@Composable
fun NxPanelRootApp(
    viewModel: NxPanelViewModel,
    currentTheme: NxThemeOption,
    startAtHome: Boolean,
    onMarkSplashCompleted: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredChats by viewModel.filteredChats.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: if (startAtHome) "home" else "splash"

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val activeBackgroundPreset = remember(preferences.backgroundId, currentTheme) {
        NxBackgroundCatalog.findById(preferences.backgroundId, currentTheme)
    }

    val rootBackgroundModifier = when (activeBackgroundPreset) {
        is NxBackgroundPreset.GradientPreset -> Modifier.background(brush = activeBackgroundPreset.brush)
        is NxBackgroundPreset.SolidPreset -> Modifier.background(color = activeBackgroundPreset.color)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentRoute != "splash",
        drawerContent = {
            NxDrawerContent(
                currentRoute = currentRoute,
                onNavigateChats = {
                    viewModel.triggerFeedback()
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNavigateSetting = {
                    viewModel.triggerFeedback()
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("setting") {
                        launchSingleTop = true
                    }
                },
                onOpenThemePicker = {
                    coroutineScope.launch { drawerState.close() }
                    viewModel.openThemeModal(true)
                },
                onOpenBackgroundPicker = {
                    coroutineScope.launch { drawerState.close() }
                    viewModel.openBackgroundModal(true)
                },
                onNavigateInfo = {
                    viewModel.triggerFeedback()
                    coroutineScope.launch { drawerState.close() }
                    navController.navigate("info") {
                        launchSingleTop = true
                    }
                },
                onExitApp = {
                    viewModel.triggerFeedback()
                    coroutineScope.launch { drawerState.close() }
                    activity?.finish()
                }
            )
        }
    ) {
        // Root Box with Modifier.background(brush) or Modifier.background(color) + WindowInsets (statusBars & navigationBars)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(rootBackgroundModifier)
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            NavHost(
                navController = navController,
                startDestination = if (startAtHome) "home" else "splash",
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = "splash") {
                    SplashScreen(
                        onNavigateToHome = {
                            onMarkSplashCompleted()
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    )
                }

                composable(route = "home") {
                    HomeScreen(
                        uiState = uiState,
                        filteredChats = filteredChats,
                        baseUrl = preferences.baseUrl,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onRefresh = {
                            viewModel.triggerFeedback()
                            viewModel.loadHomeData(showLoading = true)
                        },
                        onOpenDrawer = {
                            viewModel.triggerFeedback()
                            coroutineScope.launch { drawerState.open() }
                        },
                        onChatClick = { chatItem ->
                            val safeName = Uri.encode(chatItem.name.ifBlank { "N/A" })
                            viewModel.openChatRoom(chatItem.id, chatItem.name)
                            navController.navigate("chat/${chatItem.id}/$safeName")
                        },
                        onNavigateToSetting = {
                            viewModel.triggerFeedback()
                            navController.navigate("setting")
                        }
                    )
                }

                composable(
                    route = "chat/{chatId}/{chatName}",
                    arguments = listOf(
                        navArgument("chatId") { type = NavType.LongType },
                        navArgument("chatName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val chatId = backStackEntry.arguments?.getLong("chatId") ?: 0L
                    val rawName = backStackEntry.arguments?.getString("chatName") ?: "N/A"
                    val decodedName = try {
                        Uri.decode(rawName)
                    } catch (_: Exception) {
                        rawName
                    }

                    ChatRoomScreen(
                        chatId = chatId,
                        chatName = decodedName,
                        uiState = uiState,
                        onBack = {
                            viewModel.triggerFeedback()
                            navController.popBackStack()
                        },
                        onRefresh = {
                            viewModel.triggerFeedback()
                            viewModel.refreshMessages(chatId)
                        },
                        onSendMessage = { text, onSent ->
                            viewModel.sendMessage(chatId = chatId, text = text, onMessageSent = onSent)
                        }
                    )
                }

                composable(route = "setting") {
                    SettingScreen(
                        preferences = preferences,
                        uiState = uiState,
                        onOpenDrawer = {
                            viewModel.triggerFeedback()
                            coroutineScope.launch { drawerState.open() }
                        },
                        onSaveBaseUrl = { newUrl ->
                            viewModel.updateBaseUrl(newUrl)
                        },
                        onTestConnection = { urlToTest ->
                            viewModel.testConnection(urlToTest)
                        },
                        onToggleHaptic = { enabled ->
                            viewModel.toggleHaptic(enabled)
                        },
                        onToggleSound = { enabled ->
                            viewModel.toggleSound(enabled)
                        },
                        onOpenThemeModal = {
                            viewModel.openThemeModal(true)
                        },
                        onOpenBackgroundModal = {
                            viewModel.openBackgroundModal(true)
                        }
                    )
                }

                composable(route = "info") {
                    InfoScreen(
                        onOpenDrawer = {
                            viewModel.triggerFeedback()
                            coroutineScope.launch { drawerState.open() }
                        },
                        onNavigateToChats = {
                            viewModel.triggerFeedback()
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = false }
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            // NxNotification: Muncul dari atas, pill/capsule, animasi slide down + scale-in -> diam 2-3 detik -> slide up
            NxNotificationBanner(
                state = uiState.notification,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Modal Ganti Tema (simpan di DataStore -> activity.recreate())
            if (uiState.showThemeModal) {
                NxThemePickerDialog(
                    currentThemeId = preferences.themeId,
                    onSelectTheme = { selectedTheme ->
                        viewModel.updateTheme(selectedTheme.id) {
                            try {
                                activity?.recreate()
                            } catch (_: Exception) {
                            }
                        }
                    },
                    onDismiss = { viewModel.openThemeModal(false) }
                )
            }

            // Modal Ganti Background (6 gradient + 16 solid, simpan di DataStore -> langsung berubah realtime)
            if (uiState.showBackgroundModal) {
                NxBackgroundPickerDialog(
                    currentBackgroundId = preferences.backgroundId,
                    onSelectBackground = { selectedBgId ->
                        viewModel.updateBackground(selectedBgId)
                    },
                    onDismiss = { viewModel.openBackgroundModal(false) }
                )
            }
        }
    }
}
