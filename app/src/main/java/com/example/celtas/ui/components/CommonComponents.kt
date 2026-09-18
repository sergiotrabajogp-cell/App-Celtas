package com.example.celtas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celtas.model.Achievement
import com.example.celtas.model.ExplorerProfile
import com.example.celtas.viewmodel.AppTab
import com.example.ui.theme.CelticBronze
import com.example.ui.theme.CelticEmerald
import com.example.ui.theme.CelticGold
import com.example.ui.theme.CelticGoldBright
import com.example.ui.theme.CelticGreen
import com.example.ui.theme.CelticGreenDark
import com.example.ui.theme.CelticGreenLight
import com.example.ui.theme.Parchment
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard

@Composable
fun CelticTopBar(
    profile: ExplorerProfile,
    isSpeaking: Boolean,
    onToggleAudio: () -> Unit,
    modifier: Modifier = Modifier,
    hasDailyRewardReady: Boolean = false,
    onDailyRewardClick: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = CelticGreenDark),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Explorer Identity
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .then(if (onProfileClick != null) Modifier.clickable { onProfileClick() } else Modifier)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(CelticGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    val avatarEmoji = when (profile.avatarId) {
                        "druida" -> "🧙‍♂️"
                        "guerrero" -> "🛡️"
                        "exploradora" -> "🏹"
                        else -> "🪕"
                    }
                    Text(avatarEmoji, fontSize = 22.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = profile.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "${profile.rank.badgeIcon} ${profile.rank.title}",
                        color = CelticGoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Coins, Daily Gift, and Voice indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Gold coin badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(CelticGold, CelticBronze)))
                        .border(1.dp, CelticGoldBright, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🪙", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${profile.goldCoins}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }

                // Daily Reward Gift Button
                if (onDailyRewardClick != null) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (hasDailyRewardReady) CelticGold.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.15f))
                            .clickable { onDailyRewardClick() }
                            .testTag("daily_reward_top_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎁", fontSize = 18.sp)
                        if (hasDailyRewardReady) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .align(Alignment.TopEnd)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEF4444))
                            )
                        }
                    }
                }

                // Audio narration speaker button
                IconButton(
                    onClick = onToggleAudio,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSpeaking) CelticEmerald else Color.White.copy(alpha = 0.15f))
                        .testTag("audio_toggle_btn")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = "Estado de narración oral",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CelticBottomNavigation(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar"),
        containerColor = CelticGreenDark,
        tonalElevation = 8.dp
    ) {
        AppTab.values().forEach { tab ->
            val isSelected = selectedTab == tab
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Text(
                        text = tab.icon,
                        fontSize = if (isSelected) 22.sp else 18.sp
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CelticGoldBright,
                    selectedTextColor = CelticGoldBright,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = CelticGreen
                ),
                modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
            )
        }
    }
}

@Composable
fun AchievementUnlockedBanner(
    achievement: Achievement?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = achievement != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = modifier
    ) {
        if (achievement != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clickable { onDismiss() }
                    .testTag("achievement_popup"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CelticGold),
                border = androidx.compose.foundation.BorderStroke(2.dp, CelticGoldBright),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(achievement.iconEmoji, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "¡LOGRO DESBLOQUEADO!",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Text(
                            text = achievement.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${achievement.rewardCoins} Monedas de Oro 🪙",
                            color = CelticGreenLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar aviso",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}
