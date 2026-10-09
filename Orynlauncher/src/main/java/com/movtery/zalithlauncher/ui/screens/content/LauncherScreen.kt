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

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.layout.ContentScale
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
import kotlinx.coroutines.isActive
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

    val pageBackground = Color(0xFF07090B)
    val panel = Color(0xFF0D0F11)
    val panelBorder = Color.White.copy(alpha = 0.10f)
    val softPanel = Color.White.copy(alpha = 0.065f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(pageBackground)
            .padding(horizontal = 22.dp, vertical = 10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "OrynLauncher",
                    modifier = Modifier.size(58.dp),
                    tint = Color.Unspecified
                )
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = "OrynLauncher",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Minecraft  •  v5",
                        color = Color.White.copy(alpha = .55f),
                        fontSize = 13.sp
                    )
                }
                Spacer(Modifier.weight(1f))
                V5IconButton(R.drawable.ic_videocam_filled, "Recordings", onFpsClick)
                V5IconButton(R.drawable.ic_folder_filled, "Files", toFileManagerScreen)
                V5IconButton(R.drawable.ic_group_filled, "Multiplayer", toMultiplayerScreen)
                V5IconButton(R.drawable.ic_download_2_filled, "Downloads", toDownloadScreen)
                V5IconButton(R.drawable.ic_settings_filled, "Settings", toSettingsScreen)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .width(82.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(28.dp))
                        .background(panel)
                        .border(1.dp, panelBorder, RoundedCornerShape(28.dp))
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    V5NavItem(R.drawable.ic_home_filled, true) {}
                    V5NavItem(R.drawable.ic_assignment_filled, false, toVersionManageScreen)
                    V5NavItem(R.drawable.ic_group_filled, false, toMultiplayerScreen)
                    V5NavItem(R.drawable.ic_download_2_filled, false, toDownloadScreen)
                    V5NavItem(R.drawable.ic_settings_filled, false, toSettingsScreen)
                }

                Column(
                    modifier = Modifier
                        .weight(6f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .width(4.dp)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White)
                            )
                            Text(
                                text = "PLAY",
                                modifier = Modifier.padding(start = 26.dp),
                                color = Color.White,
                                fontSize = 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .width(365.dp)
                            .height(205.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .background(panel)
                            .border(1.dp, panelBorder, RoundedCornerShape(30.dp))
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AccountAvatar(
                                account = account,
                                onClick = toAccountManageScreen,
                                modifier = Modifier.size(72.dp)
                            )
                            Text(
                                text = account?.username ?: "Minecraft account",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = if (account != null) "● Online" else "● Offline",
                                color = Color.White.copy(alpha = .58f),
                                fontSize = 13.sp
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(softPanel)
                                    .clickable(onClick = toAccountManageScreen)
                                    .padding(horizontal = 18.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painterResource(R.drawable.ic_person_outlined),
                                    null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    "Manage Account",
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 14.dp)
                                        .weight(1f)
                                )
                                Text(
                                    "›",
                                    color = Color.White.copy(alpha = .65f),
                                    fontSize = 28.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clip(RoundedCornerShape(38.dp))
                            .background(Color.White.copy(alpha = .17f))
                            .border(
                                1.dp,
                                Color.White.copy(alpha = .20f),
                                RoundedCornerShape(38.dp)
                            )
                            .clickable { onLaunchGame(null) }
                            .padding(horizontal = 28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "▶",
                            color = Color.White,
                            fontSize = 25.sp
                        )
                        Text(
                            "L A U N C H",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 18.dp)
                        )
                        Spacer(Modifier.weight(1f))
                        Text(
                            "›",
                            color = Color.White.copy(alpha = .75f),
                            fontSize = 34.sp
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(4f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(28.dp))
                        .background(panel)
                        .border(1.dp, panelBorder, RoundedCornerShape(28.dp))
                        .padding(26.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .width(4.dp)
                                .height(52.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White)
                        )
                        Column(
                            Modifier
                                .padding(start = 22.dp)
                                .weight(1f)
                        ) {
                            Text(
                                "COSMETICS",
                                color = Color.White,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Manage your Minecraft appearance",
                                color = Color.White.copy(alpha = .5f),
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            "V5",
                            color = Color.White.copy(alpha = .7f),
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(softPanel)
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(softPanel)
                            .clickable(onClick = toAccountManageScreen)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_group_filled),
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(54.dp)
                        )
                        Column(
                            Modifier
                                .padding(start = 16.dp)
                                .weight(1f)
                        ) {
                            Text(
                                "SKIN & CAPE",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "Manage your Minecraft\nskin and cape in one place",
                                color = Color.White.copy(alpha = .52f),
                                fontSize = 13.sp
                            )
                        }
                        Text(
                            "›",
                            color = Color.White.copy(alpha = .65f),
                            fontSize = 34.sp
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    MinecraftOverworldShowcase(
                        isVisible = isVisible,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                }
            }
        }
    }
}


/**
 * Real, muted, looping cinematic playback. Place licensed footage at
 * Orynlauncher/src/main/res/raw/oryn_cinematic.mp4. Never fake video with Canvas.
 */
@Composable
private fun MinecraftOverworldShowcase(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val videoResId = remember(context) {
        context.resources.getIdentifier("oryn_cinematic", "raw", context.packageName)
    }
    var playbackError by remember { mutableStateOf(false) }
    var appStarted by remember(lifecycleOwner) {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(androidx.lifecycle.Lifecycle.State.STARTED))
    }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            when (event) {
                androidx.lifecycle.Lifecycle.Event.ON_START -> appStarted = true
                androidx.lifecycle.Lifecycle.Event.ON_STOP -> appStarted = false
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    if (videoResId == 0) {
        Box(modifier = modifier.background(Color(0xFF111418)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("ORYN CINEMATIC", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                Text("Licensed video asset is not bundled yet", color = Color.White.copy(alpha = .62f), fontSize = 11.sp, textAlign = TextAlign.Center)
            }
        }
        return
    }

    val player = androidx.compose.runtime.remember(context, videoResId) {
        androidx.media3.exoplayer.ExoPlayer.Builder(context).build().apply {
            setMediaItem(androidx.media3.common.MediaItem.fromUri("android.resource://${context.packageName}/$videoResId"))
            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
            volume = 0f
            prepare()
        }
    }

    androidx.compose.runtime.DisposableEffect(player) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                playbackError = true
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player, isVisible, appStarted, playbackError) {
        if (isVisible && appStarted && !playbackError) {
            player.playWhenReady = true
            player.play()
        } else {
            player.pause()
        }
    }

    Box(modifier = modifier.background(Color(0xFF111418))) {
        if (!playbackError) {
            androidx.compose.ui.viewinterop.AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->
                    androidx.media3.ui.PlayerView(viewContext).apply {
                        useController = false
                        setShutterBackgroundColor(android.graphics.Color.rgb(17, 20, 24))
                        this.player = player
                    }
                },
                update = { it.player = player }
            )
        } else {
            Text("Cinematic video unavailable", modifier = Modifier.align(Alignment.Center), color = Color.White.copy(alpha = .72f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun V5IconButton(icon: Int, label: String, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(52.dp).padding(3.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
    }
}

@Composable
private fun V5NavItem(icon: Int, selected: Boolean, onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(58.dp).clip(RoundedCornerShape(17.dp))
            .background(if (selected) Color.White.copy(alpha=.86f) else Color.Transparent)
            .padding(13.dp)
    ) {
        Icon(painterResource(icon), null, tint=if(selected) Color.Black else Color.White.copy(alpha=.9f))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
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
