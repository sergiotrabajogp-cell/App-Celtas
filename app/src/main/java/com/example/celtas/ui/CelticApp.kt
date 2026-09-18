package com.example.celtas.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.celtas.ui.components.AchievementUnlockedBanner
import com.example.celtas.ui.components.CelticBottomNavigation
import com.example.celtas.ui.components.CelticTopBar
import com.example.celtas.ui.components.DailyRewardDialog
import com.example.celtas.ui.screens.CharacterCustomizerScreen
import com.example.celtas.ui.screens.JournalScreen
import com.example.celtas.ui.screens.MapScreen
import com.example.celtas.ui.screens.MinigamesScreen
import com.example.celtas.ui.screens.StoryScreen
import com.example.celtas.ui.screens.TrophiesScreen
import com.example.celtas.viewmodel.AppTab
import com.example.celtas.viewmodel.CelticViewModel

@Composable
fun CelticApp(
    viewModel: CelticViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val recentAchievement by viewModel.recentAchievement.collectAsState()
    val dailyRewardState by viewModel.dailyRewardState.collectAsState()
    val showDailyRewardDialog by viewModel.showDailyRewardDialog.collectAsState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CelticTopBar(
                profile = profile,
                isSpeaking = isSpeaking,
                hasDailyRewardReady = dailyRewardState.canClaimToday,
                onDailyRewardClick = { viewModel.openDailyRewardDialog() },
                onToggleAudio = {
                    if (isSpeaking) {
                        viewModel.narrationManager.stop()
                    } else {
                        viewModel.playOrPauseNarration()
                    }
                },
                onProfileClick = {
                    viewModel.setTab(AppTab.PERSONAJE)
                }
            )
        },
        bottomBar = {
            CelticBottomNavigation(
                selectedTab = currentTab,
                onTabSelected = { viewModel.setTab(it) }
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.MAPA -> MapScreen(viewModel = viewModel)
                AppTab.RELATOS -> StoryScreen(viewModel = viewModel)
                AppTab.PERSONAJE -> CharacterCustomizerScreen(viewModel = viewModel)
                AppTab.MINIJUEGOS -> MinigamesScreen(viewModel = viewModel)
                AppTab.DIARIO -> JournalScreen(viewModel = viewModel)
                AppTab.PREMIOS -> TrophiesScreen(viewModel = viewModel)
            }

            // Achievement popup banner
            AchievementUnlockedBanner(
                achievement = recentAchievement,
                onDismiss = { viewModel.dismissAchievementBanner() },
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Daily Reward Dialog popup
            if (showDailyRewardDialog) {
                DailyRewardDialog(
                    dailyState = dailyRewardState,
                    rewardsList = viewModel.getDailyRewardsList(),
                    onClaim = { viewModel.claimDailyReward() },
                    onDismiss = { viewModel.closeDailyRewardDialog() }
                )
            }
        }
    }
}
