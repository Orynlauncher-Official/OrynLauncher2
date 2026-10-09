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
 * Animated, layered voxel-world panorama for the Play screen. The user-uploaded
 * reference is not bundled because redistribution permission has not been
 * established; this offline renderer instead uses parallax terrain, detailed
 * stepped ridgelines, forest depth, water, a village silhouette and biome
 * transitions. Animation only runs while both the screen and app are visible.
 */
@Composable
private fun MinecraftOverworldShowcase(
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = remember { Animatable(0f) }
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
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

    LaunchedEffect(isVisible, appStarted) {
        if (isVisible && appStarted) {
            while (true) {
                progress.animateTo(1f, animationSpec = tween(72000, easing = LinearEasing))
                progress.snapTo(0f)
            }
        } else {
            progress.stop()
        }
    }

    Canvas(modifier = modifier.background(Color(0xFF172A32))) {
        val w = size.width
        val h = size.height
        val t = progress.value
        val phase = t * 6f
        val scene = phase.toInt().coerceIn(0, 5)
        val blend = phase - scene
        val smooth = blend * blend * (3f - 2f * blend)
        val shift = (t * w * .32f) % w
        val horizon = h * .64f

        // Atmospheric sky, sunlight bloom, and distant haze.
        drawRect(Brush.verticalGradient(
            listOf(Color(0xFF243D53), Color(0xFF7196A3), Color(0xFFCAD0B5)),
            startY = 0f, endY = h
        ))
        drawCircle(Color(0xFFFFE7B0).copy(alpha = .13f), h * .23f,
            androidx.compose.ui.geometry.Offset(w * (.77f - t * .06f), h * .20f))
        drawCircle(Color(0xFFFFE9B8).copy(alpha = .88f), h * .055f,
            androidx.compose.ui.geometry.Offset(w * (.77f - t * .06f), h * .20f))
        for (i in 0..5) {
            val cw = w * (.10f + (i % 3) * .025f)
            val x = ((i * .23f + t * (.11f + (i % 2) * .025f)) % 1.35f) * w - cw
            val y = h * (.12f + (i % 3) * .075f)
            val cloud = Color(0xFFDCE5DF).copy(alpha = .22f + (i % 2) * .08f)
            drawRect(cloud, androidx.compose.ui.geometry.Offset(x, y), androidx.compose.ui.geometry.Size(cw, h * .018f))
            drawRect(cloud, androidx.compose.ui.geometry.Offset(x + cw * .18f, y - h * .018f), androidx.compose.ui.geometry.Size(cw * .48f, h * .027f))
            drawRect(cloud, androidx.compose.ui.geometry.Offset(x + cw * .58f, y - h * .008f), androidx.compose.ui.geometry.Size(cw * .25f, h * .018f))
        }

        // Three overlapping ridgelines provide depth and continuous parallax.
        fun ridge(points: List<Pair<Float, Float>>, color: Color, offset: Float) {
            val path = Path()
            path.moveTo(0f, h)
            path.lineTo(0f, points.first().second * h)
            points.forEachIndexed { index, p ->
                val x = (p.first * w - offset * (index % 2 + 1)) % (w * 1.3f)
                path.lineTo(x, p.second * h)
                path.lineTo((x + w * .035f), (p.second + .018f) * h)
            }
            path.lineTo(w, h)
            path.close()
            drawPath(path, color)
        }
        ridge(listOf(.00f to .57f, .10f to .43f, .17f to .48f, .28f to .31f, .36f to .43f,
            .48f to .25f, .58f to .45f, .71f to .32f, .81f to .46f, .91f to .36f, 1.0f to .48f),
            Color(0xFF647D83).copy(alpha = .75f), shift * .18f)
        ridge(listOf(.00f to .66f, .12f to .53f, .23f to .60f, .34f to .46f, .46f to .61f,
            .58f to .49f, .71f to .62f, .83f to .48f, 1.0f to .61f),
            Color(0xFF405D56), shift * .35f)

        // Distant snow facets are intentionally small and tied to the mountain scene.
        if (scene == 3 || scene == 2) {
            val snow = Color(0xFFE1E6D9).copy(alpha = if (scene == 3) .95f else .35f)
            listOf(
                listOf(.255f to .36f, .28f to .31f, .305f to .365f, .285f to .35f),
                listOf(.45f to .31f, .48f to .25f, .51f to .32f, .48f to .30f),
                listOf(.68f to .38f, .71f to .32f, .735f to .39f, .71f to .37f)
            ).forEach { pts ->
                val p = Path().apply {
                    moveTo(pts[0].first * w, pts[0].second * h)
                    for (j in 1 until pts.size) lineTo(pts[j].first * w, pts[j].second * h)
                    close()
                }
                drawPath(p, snow)
            }
        }

        // Layered foothills with stepped grass and exposed earth faces.
        drawRect(Color(0xFF2B493B), androidx.compose.ui.geometry.Offset(0f, horizon), androidx.compose.ui.geometry.Size(w, h - horizon))
        for (layer in 0..2) {
            val base = h * (.66f + layer * .085f)
            val bw = w / (25f - layer * 4f)
            val movement = shift * (0.48f + layer * .22f)
            for (i in -2..28) {
                val x = ((i * bw + movement) % (w + bw * 2)) - bw
                val n = ((i * 17 + layer * 11) % 7 + 7) % 7
                val top = base + n * h * .006f
                val grass = when (scene) {
                    1 -> Color(0xFF7D9550)
                    4 -> Color(0xFF536D43)
                    else -> Color(0xFF527B49)
                }
                drawRect(grass.copy(alpha = .82f - layer * .12f),
                    androidx.compose.ui.geometry.Offset(x, top),
                    androidx.compose.ui.geometry.Size(bw + 1f, h * .035f))
                drawRect(Color(0xFF5C4938).copy(alpha = .88f),
                    androidx.compose.ui.geometry.Offset(x, top + h * .035f),
                    androidx.compose.ui.geometry.Size(bw + 1f, h * (.055f + layer * .012f)))
                drawRect(Color(0xFF283D32).copy(alpha = .48f),
                    androidx.compose.ui.geometry.Offset(x + bw * .6f, top + h * .035f),
                    androidx.compose.ui.geometry.Size(bw * .4f, h * .055f))
            }
        }

        // Water scene: broad lake with a stepped shoreline and moving highlights.
        val waterAlpha = when (scene) { 2 -> 1f; 1, 3 -> .32f + smooth * .25f; else -> .10f }
        val lake = Path().apply {
            moveTo(w * .35f, h); lineTo(w * .90f, h)
            lineTo(w * .83f, h * .82f); lineTo(w * .70f, h * .77f)
            lineTo(w * .66f, h * .72f); lineTo(w * .52f, h * .75f)
            lineTo(w * .47f, h * .82f); lineTo(w * .39f, h * .86f); close()
        }
        drawPath(lake, Color(0xFF2F8297).copy(alpha = waterAlpha))
        drawPath(lake, Brush.verticalGradient(
            listOf(Color(0xFF77C2C7).copy(alpha = waterAlpha * .55f), Color(0xFF174C68).copy(alpha = waterAlpha)),
            startY = h * .72f, endY = h
        ))
        for (i in 0..9) {
            val x = ((i * .117f + t * .08f) % .95f) * w
            val y = h * (.79f + (i % 4) * .032f)
            drawRect(Color(0xFFB3E0D5).copy(alpha = waterAlpha * .55f),
                androidx.compose.ui.geometry.Offset(x, y),
                androidx.compose.ui.geometry.Size(w * (.025f + (i % 3) * .012f), h * .006f))
        }

        // Forest layers: varied trunks, stepped crowns, highlights and shadows.
        val forestDensity = when (scene) { 0, 4, 5 -> 1f; 2 -> .42f; else -> .68f }
        for (layer in 0..1) {
            val count = if (layer == 0) 22 else 15
            val step = w / count
            for (i in 0 until count) {
                val rawX = i * step + ((i * 19) % 13) * w * .003f
                val x = (rawX + shift * (if (layer == 0) .7f else .38f)) % (w + step) - step * .5f
                val depth = .55f + ((i * 7 + layer * 3) % 6) * .075f
                val baseY = h * (.78f + (i % 4) * .025f)
                val trunkW = step * (.12f + (i % 3) * .025f) * depth
                val trunkH = h * (.11f + (i % 4) * .012f) * depth
                val canopyW = step * (.72f + (i % 3) * .20f) * depth
                val canopyH = h * (.13f + (i % 3) * .018f) * depth
                val cherry = scene == 4
                val leaves = when {
                    cherry -> Color(0xFFC47DA0)
                    scene == 1 -> Color(0xFF73934B)
                    layer == 0 -> Color(0xFF254B37)
                    else -> Color(0xFF356548)
                }
                val a = forestDensity * (if (layer == 0) .96f else .65f)
                drawRect(Color(0xFF493A2D).copy(alpha = a),
                    androidx.compose.ui.geometry.Offset(x, baseY - trunkH),
                    androidx.compose.ui.geometry.Size(trunkW, trunkH))
                drawRect(leaves.copy(alpha = a),
                    androidx.compose.ui.geometry.Offset(x - canopyW * .34f, baseY - trunkH - canopyH * .65f),
                    androidx.compose.ui.geometry.Size(canopyW, canopyH * .70f))
                drawRect(leaves.copy(alpha = a * .95f),
                    androidx.compose.ui.geometry.Offset(x - canopyW * .17f, baseY - trunkH - canopyH),
                    androidx.compose.ui.geometry.Size(canopyW * .68f, canopyH * .62f))
                drawRect(leaves.copy(alpha = a * .8f),
                    androidx.compose.ui.geometry.Offset(x - canopyW * .45f, baseY - trunkH - canopyH * .38f),
                    androidx.compose.ui.geometry.Size(canopyW * .30f, canopyH * .43f))
                // Lit leaf faces give the canopies block volume rather than flat silhouettes.
                drawRect(Color(0xFF9BB875).copy(alpha = a * .23f),
                    androidx.compose.ui.geometry.Offset(x - canopyW * .06f, baseY - trunkH - canopyH * .90f),
                    androidx.compose.ui.geometry.Size(canopyW * .42f, canopyH * .13f))
                if (cherry && i % 3 == 0) {
                    drawRect(Color(0xFFFFD9E8).copy(alpha = a * .8f),
                        androidx.compose.ui.geometry.Offset(x + canopyW * .12f, baseY - trunkH - canopyH * .54f),
                        androidx.compose.ui.geometry.Size(canopyW * .13f, canopyH * .10f))
                }
            }
        }

        // Tiny medieval village silhouette appears on the plains/mountain approach.
        val villageAlpha = when (scene) { 1 -> 1f; 2, 3 -> .48f; else -> .12f }
        for (i in 0..4) {
            val x = w * (.40f + i * .075f) - shift * .18f
            val y = h * (.72f + (i % 2) * .025f)
            val houseW = w * (.035f + (i % 2) * .012f)
            drawRect(Color(0xFF5B5144).copy(alpha = villageAlpha),
                androidx.compose.ui.geometry.Offset(x, y - h * .075f),
                androidx.compose.ui.geometry.Size(houseW, h * .075f))
            val roof = Path().apply {
                moveTo(x - houseW * .12f, y - h * .075f)
                lineTo(x + houseW * .5f, y - h * .12f)
                lineTo(x + houseW * 1.12f, y - h * .075f)
                close()
            }
            drawPath(roof, Color(0xFF403A35).copy(alpha = villageAlpha))
            drawRect(Color(0xFFD8B879).copy(alpha = villageAlpha * .8f),
                androidx.compose.ui.geometry.Offset(x + houseW * .38f, y - h * .035f),
                androidx.compose.ui.geometry.Size(houseW * .15f, h * .035f))
        }

        // Cinematic foreground vignette and horizon mist.
        drawRect(Brush.verticalGradient(
            listOf(Color.Transparent, Color(0xFF142820).copy(alpha = .25f), Color(0xFF0D1C1B).copy(alpha = .68f)),
            startY = h * .48f, endY = h
        ))
        drawRect(Brush.horizontalGradient(
            listOf(Color(0xFF08151C).copy(alpha = .28f), Color.Transparent,
                Color.Transparent, Color(0xFF08151C).copy(alpha = .30f))
        ))
        drawRect(Brush.verticalGradient(
            listOf(Color.Transparent, Color(0xFFC6D9D0).copy(alpha = .16f)),
            startY = h * .48f, endY = h * .73f
        ))
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
