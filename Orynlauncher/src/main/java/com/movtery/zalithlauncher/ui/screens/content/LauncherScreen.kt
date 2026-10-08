/*
 * Zalith Launcher 2
 * Copyright (C) 2025 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.BuildKeys
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.version.installed.PlayTimeRepository
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.MarkdownView
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.ScalingActionButton
import com.movtery.zalithlauncher.ui.components.defaultMarkdownConfig
import com.iffly.compose.markdown.config.MarkdownRenderConfig
import com.iffly.compose.markdown.style.ListTheme
import com.iffly.compose.markdown.style.MarkdownTheme
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.AccountAvatar
import com.movtery.zalithlauncher.ui.screens.content.elements.CommonVersionInfoLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.AboutDialog
import com.movtery.zalithlauncher.ui.screens.content.elements.SideBar
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.screens.game.elements.PerformanceSettingsDialog
import com.movtery.zalithlauncher.ui.screens.game.elements.PerformanceSettingsOperation
import com.movtery.zalithlauncher.ui.screens.main.custom_home.MarkdownBlock
import com.movtery.zalithlauncher.ui.screens.main.custom_home.customHomePage

import com.movtery.zalithlauncher.ui.screens.navigateTo
import com.movtery.zalithlauncher.ui.screens.removeAndNavigateTo
import com.movtery.zalithlauncher.utils.PlayTimeUtils
import com.movtery.zalithlauncher.utils.animation.swapAnimateDpAsState
import com.movtery.zalithlauncher.viewmodel.HomePageState
import com.movtery.zalithlauncher.viewmodel.LocalHomePageViewModel
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel

@Composable
fun LauncherScreen(
    backStackViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    onLaunchGame: (Version?) -> Unit,
    onOpenLink: (String) -> Unit,
    onHomePageEvent: (MarkdownBlock.Button.Event) -> Unit,
    onNavigateToStats: () -> Unit = {},
    onNavigateToPlayTimeStats: () -> Unit = {},
    onNavigateToLog: (String) -> Unit = {},
) {
    BaseScreen(
        screenKey = NormalNavKey.LauncherMain,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->
        var showAboutDialog by remember { mutableStateOf(false) }
        var performanceSettingsState by remember { mutableStateOf<PerformanceSettingsOperation>(PerformanceSettingsOperation.None) }

        if (showAboutDialog) {
            AboutDialog(onDismissRequest = { showAboutDialog = false })
        }

        PerformanceSettingsDialog(
            operation = performanceSettingsState,
            onDismissRequest = { performanceSettingsState = PerformanceSettingsOperation.None }
        )

        V5Home(
            isVisible = isVisible,
            onLaunchGame = onLaunchGame,
            toAccountManageScreen = {
                backStackViewModel.mainScreen.navigateTo(
                    screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                )
            },
            toVersionManageScreen = {
                backStackViewModel.mainScreen.removeAndNavigateTo(
                    remove = NestedNavKey.VersionSettings::class,
                    screenKey = NormalNavKey.VersionsManager
                )
            },
            toVersionSettingsScreen = {
                VersionsManager.currentVersion.value?.let(navigateToVersions)
            },
            toDownloadScreen = { backStackViewModel.navigateToDownload() },
            toFileManagerScreen = {
                backStackViewModel.mainScreen.navigateTo(
                    screenKey = NormalNavKey.BuiltInFileManager()
                )
            },
            toMultiplayerScreen = {
                backStackViewModel.mainScreen.removeAndNavigateTo(
                    removes = backStackViewModel.clearBeforeNavKeys,
                    screenKey = NormalNavKey.Multiplayer
                )
            },
            toSettingsScreen = {
                backStackViewModel.mainScreen.removeAndNavigateTo(
                    removes = backStackViewModel.clearBeforeNavKeys,
                    screenKey = backStackViewModel.settingsScreen
                )
            },
            onFpsClick = { performanceSettingsState = PerformanceSettingsOperation.Fps },
        )
    }
}

@Composable
private fun V5Home(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
    toDownloadScreen: () -> Unit,
    toFileManagerScreen: () -> Unit,
    toMultiplayerScreen: () -> Unit,
    toSettingsScreen: () -> Unit,
    onFpsClick: () -> Unit,
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val backgroundViewModel = LocalBackgroundViewModel.current

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0D0F))
    ) {
        // Everything is sized from the available canvas rather than fixed dp values.
        // This keeps the V5 design visually consistent on different Android densities.
        val outer = maxWidth * 0.0195f
        val headerHeight = maxHeight * 0.135f
        val sideWidth = maxWidth * 0.067f
        val sideGap = maxWidth * 0.032f
        val contentGap = maxWidth * 0.024f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = outer, vertical = maxHeight * 0.018f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "OrynLauncher",
                    modifier = Modifier.size(headerHeight * 0.62f),
                    tint = Color.Unspecified
                )

                Column(modifier = Modifier.padding(start = maxWidth * 0.009f)) {
                    Text(
                        text = "OrynLauncher",
                        color = Color.White,
                        fontSize = (maxWidth * 0.016f).coerceIn(20.dp, 30.dp).value.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Minecraft  •  v5",
                        color = Color.White.copy(alpha = .52f),
                        fontSize = (maxWidth * 0.0085f).coerceIn(11.dp, 16.dp).value.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                V5IconButton(R.drawable.ic_videocam_filled, "Recordings", onFpsClick, headerHeight * 0.46f)
                V5IconButton(R.drawable.ic_folder_filled, "Files", toFileManagerScreen, headerHeight * 0.46f)
                V5IconButton(R.drawable.ic_group_filled, "Multiplayer", toMultiplayerScreen, headerHeight * 0.46f)
                V5IconButton(R.drawable.ic_download_2_filled, "Downloads", toDownloadScreen, headerHeight * 0.46f)
                V5IconButton(R.drawable.ic_settings_filled, "Settings", toSettingsScreen, headerHeight * 0.46f)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = maxHeight * 0.018f),
                horizontalArrangement = Arrangement.spacedBy(sideGap)
            ) {
                Column(
                    modifier = Modifier
                        .width(sideWidth)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(sideWidth * 0.27f))
                        .background(Color(0xFF101214))
                        .border(1.dp, Color.White.copy(alpha = .07f), RoundedCornerShape(sideWidth * 0.27f))
                        .padding(vertical = sideWidth * 0.11f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(sideWidth * 0.075f)
                ) {
                    V5NavItem(R.drawable.ic_home_filled, true, {}, Modifier.size(sideWidth * 0.73f))
                    V5NavItem(R.drawable.ic_assignment_filled, false, toVersionManageScreen, Modifier.size(sideWidth * 0.73f))
                    V5NavItem(R.drawable.ic_group_filled, false, toMultiplayerScreen, Modifier.size(sideWidth * 0.73f))
                    V5NavItem(R.drawable.ic_download_2_filled, false, toDownloadScreen, Modifier.size(sideWidth * 0.73f))
                    V5NavItem(R.drawable.ic_settings_filled, false, toSettingsScreen, Modifier.size(sideWidth * 0.73f))
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(contentGap)
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1.42f)
                            .fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(maxHeight * 0.045f)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        Modifier
                                            .width(maxWidth * 0.005f)
                                            .height(maxHeight * 0.105f)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color.White)
                                    )
                                    Text(
                                        text = "PLAY",
                                        modifier = Modifier.padding(start = maxWidth * 0.026f),
                                        color = Color.White,
                                        fontSize = (maxWidth * 0.032f).coerceIn(30.dp, 48.dp).value.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Your Minecraft, your way",
                                    modifier = Modifier.padding(start = maxWidth * 0.032f, top = 1.dp),
                                    color = Color.White.copy(alpha = .52f),
                                    fontSize = (maxWidth * 0.011f).coerceIn(13.dp, 18.dp).value.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.TopCenter
                            ) {
                                BoxWithConstraints {
                                    val cardWidth = maxWidth * 0.485f
                                    val cardHeight = maxHeight * 0.68f

                                    Surface(
                                        modifier = Modifier.size(cardWidth, cardHeight),
                                        shape = RoundedCornerShape(cardWidth * 0.075f),
                                        color = Color(0xFF17191C),
                                        tonalElevation = 0.dp
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(cardWidth * 0.075f),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            AccountAvatar(
                                                account = account,
                                                onClick = toAccountManageScreen,
                                                modifier = Modifier.size(cardWidth * 0.34f)
                                            )
                                            Text(
                                                text = account?.username ?: "Minecraft account",
                                                color = Color.White,
                                                fontSize = (cardWidth * 0.055f).coerceIn(14.dp, 20.dp).value.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                modifier = Modifier.padding(top = cardWidth * 0.012f)
                                            )
                                            Text(
                                                text = if (account != null) "● Online" else "● Offline",
                                                color = Color.White.copy(alpha = .58f),
                                                fontSize = (cardWidth * 0.038f).coerceIn(11.dp, 15.dp).value.sp
                                            )

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = cardWidth * 0.035f)
                                                    .clip(RoundedCornerShape(cardWidth * 0.08f))
                                                    .background(Color.White.copy(alpha = .09f))
                                                    .clickable(onClick = toAccountManageScreen)
                                                    .padding(
                                                        horizontal = cardWidth * 0.055f,
                                                        vertical = cardWidth * 0.035f
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    painterResource(R.drawable.ic_login),
                                                    null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(cardWidth * 0.07f)
                                                )
                                                Text(
                                                    "Microsoft Account",
                                                    color = Color.White,
                                                    fontSize = (cardWidth * 0.041f).coerceIn(11.dp, 16.dp).value.sp,
                                                    modifier = Modifier.padding(start = cardWidth * 0.04f).weight(1f)
                                                )
                                                Text(
                                                    "›",
                                                    color = Color.White.copy(alpha = .7f),
                                                    fontSize = (cardWidth * 0.085f).coerceIn(20.dp, 30.dp).value.sp
                                                )
                                            }
                                        }
                                    )
                                }
                            }

                            ScalingActionButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(maxHeight * 0.18f),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                                onClick = { onLaunchGame(null) }
                            ) {
                                Text(
                                    "▶   L A U N C H",
                                    color = Color.White,
                                    fontSize = (maxWidth * 0.014f).coerceIn(16.dp, 23.dp).value.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Text(
                                    "›",
                                    color = Color.White.copy(alpha = .7f),
                                    fontSize = (maxWidth * 0.027f).coerceIn(24.dp, 38.dp).value.sp
                                )
                            }
                        }
                    }

                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(maxWidth * 0.055f),
                            color = Color(0xFF15171A),
                            tonalElevation = 0.dp
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                // Reuse the launcher's configured background inside the cosmetics panel.
                                if (backgroundViewModel?.isValid == true && backgroundViewModel.isImage) {
                                    AsyncImage(
                                        model = backgroundViewModel.backgroundFile,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(maxWidth * 0.055f)),
                                        contentScale = ContentScale.Crop,
                                        alpha = 0.28f
                                    )
                                }

                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = .28f))
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(maxWidth * 0.05f)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            Modifier
                                                .width(maxWidth * 0.008f)
                                                .height(maxHeight * 0.105f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color.White)
                                        )
                                        Column(
                                            Modifier
                                                .padding(start = maxWidth * 0.035f)
                                                .weight(1f)
                                        ) {
                                            Text(
                                                "COSMETICS",
                                                color = Color.White,
                                                fontSize = (maxWidth * 0.035f).coerceIn(24.dp, 34.dp).value.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Manage your Minecraft appearance",
                                                color = Color.White.copy(alpha = .52f),
                                                fontSize = (maxWidth * 0.015f).coerceIn(12.dp, 16.dp).value.sp
                                            )
                                        }
                                        Text(
                                            "V5",
                                            color = Color.White.copy(alpha = .72f),
                                            fontSize = (maxWidth * 0.015f).coerceIn(11.dp, 15.dp).value.sp,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(maxWidth * 0.025f))
                                                .background(Color.White.copy(alpha = .08f))
                                                .padding(
                                                    horizontal = maxWidth * 0.025f,
                                                    vertical = maxWidth * 0.015f
                                                )
                                        )
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = maxHeight * 0.055f)
                                            .clip(RoundedCornerShape(maxWidth * 0.045f))
                                            .background(Color.White.copy(alpha = .08f))
                                            .clickable(onClick = toAccountManageScreen)
                                            .padding(maxWidth * 0.035f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            painterResource(R.drawable.ic_group_filled),
                                            null,
                                            tint = Color.White,
                                            modifier = Modifier.size(maxWidth * 0.085f)
                                        )
                                        Column(
                                            Modifier
                                                .padding(start = maxWidth * 0.035f)
                                                .weight(1f)
                                        ) {
                                            Text(
                                                "SKIN & CAPE",
                                                color = Color.White,
                                                fontSize = (maxWidth * 0.025f).coerceIn(15.dp, 22.dp).value.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                "Manage your Minecraft skin and cape in one place",
                                                color = Color.White.copy(alpha = .52f),
                                                fontSize = (maxWidth * 0.018f).coerceIn(11.dp, 15.dp).value.sp
                                            )
                                        }
                                        Text(
                                            "›",
                                            color = Color.White.copy(alpha = .7f),
                                            fontSize = (maxWidth * 0.055f).coerceIn(24.dp, 38.dp).value.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun V5IconButton(
    icon: Int,
    label: String,
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.25f))
            .background(Color.White.copy(alpha = .045f))
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(size * 0.58f)
        )
    }
}

@Composable
private fun V5NavItem(
    icon: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(17.dp))
            .background(if (selected) Color.White.copy(alpha = .88f) else Color.Transparent)
    ) {
        Icon(
            painterResource(icon),
            null,
            tint = if (selected) Color.Black else Color.White.copy(alpha = .9f),
            modifier = Modifier.fillMaxSize(0.48f)
        )
    }
}

@Composable
private fun ContentMenu(
    isVisible: Boolean,
    onHomePageEvent: (MarkdownBlock.Button.Event) -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToPlayTimeStats: () -> Unit = {},
    onNavigateToLog: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val yOffset by swapAnimateDpAsState(
        targetValue = (-40).dp,
        swapIn = isVisible
    )

    val homePageViewModel = LocalHomePageViewModel.current
    val pageState by homePageViewModel.pageState.collectAsStateWithLifecycle()
    val config = defaultMarkdownConfig()

    Column(
        modifier = modifier
            .fillMaxSize()
            .offset { IntOffset(x = 0, y = yOffset.roundToPx()) }
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (BuildConfig.DEBUG) {
            //debug版本关不掉的警告，防止有人把测试版当正式版用 XD
            BackgroundCard(shape = MaterialTheme.shapes.extraLarge) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.generic_warning),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.launcher_version_debug_warning, BuildKeys.LAUNCHER_NAME),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        modifier = Modifier
                            .alpha(0.8f)
                            .align(Alignment.End),
                        text = stringResource(R.string.launcher_version_debug_warning_cant_close),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        // Stats grid fills remaining space — no scroll
        StatsGrid(
            modifier = Modifier.weight(1f),
            onNavigateToStats = onNavigateToStats,
            onNavigateToPlayTimeStats = onNavigateToPlayTimeStats,
            onNavigateToLog = onNavigateToLog
        )

        // Home page content below (only shown when configured)
        when (val state = pageState) {
            is HomePageState.Blank -> {}
            is HomePageState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LoadingIndicator()
                        Text(
                            text = stringResource(R.string.settings_launcher_home_page_loading),
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
            is HomePageState.None -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    customHomePage(
                        blocks = state.page,
                        config = config,
                        onEvent = onHomePageEvent
                    )
                }
            }
        }
    }
}

private const val CHANGELOGS_URL = "https://raw.githubusercontent.com/Star1xr/ZalithLauncher2Plus/refs/heads/main/CHANGELOGS_UPDATE.md"
private const val CHANGELOGS_UPDATE_TR = "https://raw.githubusercontent.com/Star1xr/ZalithLauncher2Plus/refs/heads/main/CHANGELOGS_UPDATE_TR.md"

@Composable
private fun StatsGrid(
    modifier: Modifier = Modifier,
    onNavigateToStats: () -> Unit,
    onNavigateToPlayTimeStats: () -> Unit = {},
    onNavigateToLog: (String) -> Unit,
) {
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val versionNames = remember(versions) { versions.map { it.getVersionName() } }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.stats_today_header),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .alpha(0.5f)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            WeeklyPlayTimeChart(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                versionNames = versionNames
            )
            ChangelogCard(
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DailyPlayTimeCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                versionNames = versionNames,
                onClick = onNavigateToPlayTimeStats
            )
            LastLogCard(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                onNavigateToLog = onNavigateToLog
            )
        }
    }
}

@Composable
private fun WeeklyPlayTimeChart(
    modifier: Modifier = Modifier,
    versionNames: List<String>
) {
    val weekData = remember(versionNames) {
        val dates = PlayTimeRepository.lastNDays(7).reversed()
        dates.map { date ->
            date to PlayTimeRepository.getDailyTotalPlayTime(date, versionNames)
        }
    }

    val maxMs = remember(weekData) { weekData.maxOfOrNull { it.second } ?: 1L }
    val primaryColor = MaterialTheme.colorScheme.primary

    val dayLabels = remember(weekData) {
        val sdf = java.text.SimpleDateFormat("EEE", java.util.Locale.getDefault())
        val parser = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        weekData.map { (date, _) ->
            val d = parser.parse(date)
            if (d != null) sdf.format(d) else date.takeLast(5)
        }
    }

    BackgroundCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                weekData.forEachIndexed { index, (_, ms) ->
                    val fraction = if (maxMs > 0) ms.toFloat() / maxMs else 0f
                    val hours = PlayTimeUtils.getPlayHours(ms)
                    val barAlpha = 0.4f + (fraction * 0.6f).coerceAtMost(0.6f)

                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "%.1f".format(hours),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            modifier = Modifier.alpha(0.8f)
                        )
                        Spacer(Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.55f)
                                .weight(fraction.coerceAtLeast(0.03f))
                                .background(
                                    color = primaryColor.copy(alpha = barAlpha),
                                    shape = RoundedCornerShape(4.dp)
                                )
                        )
                        Spacer(Modifier.weight((1f - fraction).coerceAtLeast(0.001f)))
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = dayLabels.getOrElse(index) { "" },
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 7.sp,
                            modifier = Modifier.alpha(0.5f),
                            maxLines = 1
                        )
                    }
                }
            }

            Text(
                text = stringResource(R.string.stats_play_time_graph),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.5f)
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun DailyPlayTimeCard(
    modifier: Modifier = Modifier,
    versionNames: List<String>,
    onClick: () -> Unit = {}
) {
    val todayMs = remember(versionNames) {
        PlayTimeRepository.getDailyTotalPlayTime(PlayTimeRepository.today(), versionNames)
    }
    val hours = PlayTimeUtils.getPlayHours(todayMs)

    BackgroundCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = "%.1f h".format(hours),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    fontSize = 26.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally).alpha(0.7f),
                    text = stringResource(R.string.stats_today),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(Modifier.weight(1f))
                Text(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = stringResource(R.string.stats_statistics),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.stats_click_for_more),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun LastLogCard(
    modifier: Modifier = Modifier,
    onNavigateToLog: (String) -> Unit
) {
    val currentVersion by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val logFile = remember(currentVersion) {
        currentVersion?.let { VersionsManager.getLatestLog(it) }
    }
    val logExists = remember(logFile) { logFile?.exists() == true }

    BackgroundCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = {
            if (logExists) logFile?.absolutePath?.let { onNavigateToLog(it) }
        },
        enabled = logExists
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.stats_last_log),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
                if (!logExists || logFile == null) {
                    Text(
                        text = stringResource(R.string.stats_no_log),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.alpha(0.6f)
                    )
                } else {
                    Text(
                        text = remember(logFile) {
                            try {
                                logFile.readText().take(2000)
                            } catch (e: Exception) { "" }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .alpha(0.5f)
                            .bottomFade(36.dp)
                    )
                    Text(
                        text = stringResource(R.string.stats_click_for_more),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1
                    )
                }
            }
            if (logExists) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                        contentDescription = stringResource(R.string.generic_open_link),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RightMenuContent(
    modifier: Modifier = Modifier,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
    launchButton: @Composable (
        innerModifier: Modifier,
        onClick: () -> Unit,
        text: @Composable RowScope.() -> Unit
    ) -> Unit,
) {
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()

    ConstraintLayout(
        modifier = modifier
    ) {
        val (accountAvatar, versionManagerLayout, launchButton) = createRefs()

        AccountAvatar(
            modifier = Modifier
                .constrainAs(accountAvatar) {
                    top.linkTo(parent.top)
                    bottom.linkTo(launchButton.top, margin = 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            account = account,
            onClick = toAccountManageScreen
        )

        var showList by remember { mutableStateOf(false) }
        var versionManagerRow by remember { mutableStateOf<LayoutCoordinates?>(null) }
        Box(
            modifier = Modifier.constrainAs(versionManagerLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(launchButton.top)
            },
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            versionManagerRow = coordinates
                        }
                ) {
                    VersionManagerLayout(
                        isRefreshing = isRefreshing,
                        version = version,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        swapToVersionManage = toVersionManageScreen,
                        openListMenu = { showList = true },
                    )
                }
                version?.takeIf { !isRefreshing && it.isValid() }?.let {
                    IconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = toVersionSettingsScreen
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings_filled),
                            contentDescription = stringResource(R.string.versions_manage_settings)
                        )
                    }
                }
            }

            val menuAnchor = versionManagerRow
            val menuAnchorBounds = menuAnchor?.boundsInParent()
            val menuAnchorX = menuAnchorBounds?.left ?: 0f
            val menuAnchorHeight = menuAnchorBounds?.height ?: 0f

            DropdownMenu(
                expanded = showList && menuAnchor != null,
                onDismissRequest = { showList = false },
                modifier = Modifier.width(260.dp),
                offset = DpOffset(
                    x = with(LocalDensity.current) { menuAnchorX.toDp() },
                    y = with(LocalDensity.current) { (-menuAnchorHeight).toDp() } - 8.dp
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                val versions by VersionsManager.versions.collectAsStateWithLifecycle()
                versions.forEach { version0 ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CommonVersionInfoLayout(
                                    modifier = Modifier.weight(1f),
                                    version = version0,
                                    iconSize = 28.dp
                                )
                                IconButton(
                                    onClick = {
                                        onLaunchGame(version0)
                                        showList = false
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                                        contentDescription = stringResource(R.string.main_launch_game),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            if (version == version0) return@DropdownMenuItem
                            VersionsManager.saveVersion(version0)
                            showList = false
                        }
                    )
                }
            }
        }

        launchButton(
            Modifier
                .fillMaxWidth()
                .constrainAs(launchButton) {
                    bottom.linkTo(parent.bottom, margin = 8.dp)
                }
                .padding(PaddingValues(horizontal = 12.dp)),
            {
                onLaunchGame(null)
            },
            {
                MarqueeText(text = stringResource(R.string.main_launch_game))
            }
        )
    }
}

@Composable
private fun RightMenu(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    modifier: Modifier = Modifier,
    toAccountManageScreen: () -> Unit = {},
    toVersionManageScreen: () -> Unit = {},
    toVersionSettingsScreen: () -> Unit = {}
) {
    val xOffset by swapAnimateDpAsState(
        targetValue = 40.dp,
        swapIn = isVisible,
        isHorizontal = true
    )

    BackgroundCard(
        modifier = modifier.offset { IntOffset(x = xOffset.roundToPx(), y = 0) },
        shape = MaterialTheme.shapes.extraLarge
    ) {
        RightMenuContent(
            modifier = Modifier.fillMaxSize(),
            onLaunchGame = onLaunchGame,
            toAccountManageScreen = toAccountManageScreen,
            toVersionManageScreen = toVersionManageScreen,
            toVersionSettingsScreen = toVersionSettingsScreen,
        ) { innerModifier, onClick, text ->
            ScalingActionButton(
                modifier = innerModifier,
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                onClick = onClick,
                content = text
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun VersionManagerLayout(
    isRefreshing: Boolean,
    version: Version?,
    swapToVersionManage: () -> Unit,
    openListMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.large)
            .combinedClickable(
                role = Role.Button,
                onClick = swapToVersionManage,
                onLongClick = {
                    if (version != null) openListMenu()
                }
            )
            .padding(PaddingValues(all = 8.dp))
    ) {
        if (isRefreshing) {
            Box(modifier = Modifier.fillMaxWidth()) {
                LoadingIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            VersionIconImage(
                version = version,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (version == null) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .basicMarquee(iterations = Int.MAX_VALUE),
                    text = stringResource(R.string.versions_manage_no_versions),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                        text = version.getVersionName(),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                    if (version.isValid()) {
                        Text(
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                            text = version.getVersionSummary(),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChangelogCard(
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    val isTurkey = LocalConfiguration.current.locales[0].language == "tr"
    val changelogUrl = if (isTurkey) CHANGELOGS_UPDATE_TR else CHANGELOGS_URL

    LaunchedEffect(Unit) {
        try {
            val text = withContext(Dispatchers.IO) {
                java.net.URL(changelogUrl).readText()
            }
            content = text
        } catch (_: Exception) {
            content = null
        }
        isLoading = false
    }

    BackgroundCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        onClick = { if (content != null) showDialog = true }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 8.dp, end = 12.dp, top = 12.dp, bottom = 12.dp)
            ) {
                val isTablet = LocalConfiguration.current.screenWidthDp >= 600

                Text(
                    text = stringResource(R.string.stats_changelog),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
                when {
                    isLoading -> {
                        Text(
                            text = stringResource(R.string.stats_changelog_loading),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.alpha(0.6f)
                        )
                    }
                    content == null -> {
                        Text(
                            text = stringResource(R.string.generic_error),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.alpha(0.6f)
                        )
                    }
                    else -> {
                        val contentText = content!!
                        val previewText = remember(contentText) {
                            val lines = contentText.lines()
                            if (lines.size <= 2) contentText
                            else lines.dropLast(2).joinToString("\n")
                        }
                        val bodySize = if (isTablet) MaterialTheme.typography.bodySmall.fontSize else MaterialTheme.typography.labelSmall.fontSize
                        val primary = MaterialTheme.colorScheme.primary
                        val onSurface = MaterialTheme.colorScheme.onSurface
                        val cardConfig = remember(bodySize, primary, onSurface) {
                            MarkdownRenderConfig.Builder()
                                .markdownTheme(
                                    MarkdownTheme(
                                        textStyle = TextStyle(fontSize = bodySize, lineHeight = bodySize * 1.4f, color = onSurface),
                                        headStyle = mapOf(
                                            1 to TextStyle(fontSize = bodySize * 1.2f, lineHeight = bodySize * 1.5f, fontWeight = FontWeight.Bold, color = primary),
                                            2 to TextStyle(fontSize = bodySize * 1.1f, lineHeight = bodySize * 1.4f, fontWeight = FontWeight.Bold, color = primary),
                                            3 to TextStyle(fontSize = bodySize, lineHeight = bodySize * 1.3f, fontWeight = FontWeight.SemiBold, color = primary),
                                        ),
                                        listTheme = ListTheme(
                                            markerTextStyle = TextStyle(
                                                fontSize = bodySize,
                                                lineHeight = bodySize * 1.4f,
                                                textAlign = TextAlign.End,
                                                color = onSurface,
                                            ),
                                        ),
                                    )
                                )
                                .build()
                        }
                        MarkdownView(
                            content = previewText,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .bottomFade(48.dp),
                            config = cardConfig,
                        )
                        Text(
                            text = stringResource(R.string.stats_click_for_more),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    if (showDialog && content != null) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.85f),
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    val dialogConfig = defaultMarkdownConfig()
                    MarkdownView(
                        content = "# ${stringResource(R.string.stats_changelog)}\n\n${content!!}",
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        config = dialogConfig
                    )
                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Text(text = stringResource(R.string.generic_close))
                    }
                }
            }
        }
    }
}

private fun Modifier.bottomFade(edgeHeight: androidx.compose.ui.unit.Dp): Modifier = this
    .graphicsLayer {
        clip = true
        compositingStrategy = CompositingStrategy.Offscreen
    }
    .drawWithContent {
        drawContent()
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black, Color.Transparent),
                startY = size.height - edgeHeight.toPx(),
                endY = size.height
            ),
            blendMode = BlendMode.DstIn,
            size = size
        )
    }
