package com.xrdoge.nsfw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.xrdoge.nsfw.ui.NsfwViewModel
import com.xrdoge.nsfw.ui.explorer.ExplorerScreen
import com.xrdoge.nsfw.ui.home.HomeScreen
import com.xrdoge.nsfw.ui.settings.SettingsScreen
import com.xrdoge.nsfw.ui.theme.NsfwTheme
import com.xrdoge.nsfw.ui.tokens.TokensScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NsfwTheme {
                val vm: NsfwViewModel = viewModel()
                val state by vm.state.collectAsStateWithLifecycle()
                val nav = rememberNavController()
                val snackbar = remember { SnackbarHostState() }
                val backStack by nav.currentBackStackEntryAsState()
                val route = backStack?.destination?.route ?: "home"

                LaunchedEffect(state.error, state.notice) {
                    val message = state.error ?: state.notice
                    if (!message.isNullOrBlank()) {
                        snackbar.showSnackbar(message)
                        vm.dismissMessage()
                    }
                }

                val tabs = listOf(
                    Tab("home", "Home", Icons.Outlined.Home),
                    Tab("explorer", "Creator", Icons.Outlined.Explore),
                    Tab("tokens", "Content", Icons.Outlined.AccountBalanceWallet),
                    Tab("settings", "Settings", Icons.Outlined.Settings),
                )

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbar) },
                    bottomBar = {
                        NavigationBar {
                            tabs.forEach { tab ->
                                NavigationBarItem(
                                    selected = route == tab.route,
                                    onClick = { nav.navigate(tab.route) { launchSingleTop = true } },
                                    icon = { Icon(tab.icon, contentDescription = tab.label) },
                                    label = { Text(tab.label) },
                                )
                            }
                        }
                    },
                ) { padding ->
                    NavHost(
                        navController = nav,
                        startDestination = "home",
                        modifier = Modifier.padding(padding),
                    ) {
                        composable("home") {
                            HomeScreen(
                                state = state,
                                favoriteCreators = vm.favoriteCreators(),
                                activeSubscription = vm.activeSubscription(),
                                drafts = state.drafts,
                                onRefresh = vm::refreshPlatform,
                                onOpenCreatorHub = { nav.navigate("explorer") },
                                onOpenContentHub = { nav.navigate("tokens") },
                                onDismissDraft = vm::dismissDraft,
                            )
                        }
                        composable("explorer") {
                            ExplorerScreen(
                                query = state.query,
                                creators = vm.filteredCreators(),
                                favoriteCreatorIds = state.settings.favoriteCreatorIds,
                                drafts = state.drafts,
                                onQueryChange = vm::onQueryChange,
                                onCreatorInterest = vm::registerCreatorInterest,
                                onToggleFavorite = vm::toggleFavoriteCreator,
                            )
                        }
                        composable("tokens") {
                            TokensScreen(
                                state = state,
                                feed = vm.filteredPosts(),
                                creators = vm.filteredCreators(),
                                onSelectTier = vm::selectSubscriptionTier,
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                state = state,
                                onSave = { alias, mode, allowDm, showPreview ->
                                    vm.updateSettings(alias, mode, allowDm, showPreview)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class Tab(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)
