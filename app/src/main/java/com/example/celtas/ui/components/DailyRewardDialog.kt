package com.example.celtas.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.celtas.model.DailyReward
import com.example.celtas.model.DailyRewardState
import com.example.ui.theme.CelticBronze
import com.example.ui.theme.CelticEmerald
import com.example.ui.theme.CelticGold
import com.example.ui.theme.CelticGoldBright
import com.example.ui.theme.CelticGreenDark
import com.example.ui.theme.CelticGreenLight
import com.example.ui.theme.Parchment
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard

@Composable
fun DailyRewardDialog(
    dailyState: DailyRewardState,
    rewardsList: List<DailyReward>,
    onClaim: () -> DailyReward?,
    onDismiss: () -> Unit
) {
    var claimedReward by remember { mutableStateOf<DailyReward?>(null) }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, CelticGold, RoundedCornerShape(24.dp))
                .testTag("daily_reward_dialog"),
            colors = CardDefaults.cardColors(containerColor = Parchment),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with close icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("☀️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Recompensas Diarias",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelticGreenDark
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CelticGreenDark.copy(alpha = 0.1f))
                            .testTag("close_daily_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar ventana de recompensas diarias",
                            tint = CelticGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Vuelve cada día para bendecir a tu clan, conseguir monedas de oro y desbloquear el manto solar 3D exclusivo en el día 7.",
                    fontSize = 13.sp,
                    color = Color(0xFF4B5563),
                    textAlign = TextAlign.Center,
                    lineHeight = 17.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Streak Banner
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CelticGreenDark, CelticGreenLight)
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Racha de Explorador: ${dailyState.currentStreak} ${if (dailyState.currentStreak == 1) "día" else "días"}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Horizontal scroll of 7 days
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(rewardsList) { reward ->
                        val isClaimed = dailyState.claimedDaysInCycle.contains(reward.day)
                        val isCurrentTarget = reward.day == dailyState.nextDayToClaim
                        val canClaim = dailyState.canClaimToday && isCurrentTarget

                        DailyRewardItemCard(
                            reward = reward,
                            isClaimed = isClaimed,
                            isCurrentTarget = isCurrentTarget,
                            canClaim = canClaim,
                            pulseScale = if (canClaim) pulseScale else 1f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Claim Success Banner if just claimed
                AnimatedVisibility(
                    visible = claimedReward != null,
                    enter = fadeIn() + scaleIn()
                ) {
                    claimedReward?.let { reward ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(CelticEmerald.copy(alpha = 0.15f))
                                .border(1.5.dp, CelticEmerald, RoundedCornerShape(14.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🎉 ¡Recompensa Reclamada! 🎉", fontWeight = FontWeight.Bold, color = CelticGreenDark, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "+${reward.coins}🪙 y +${reward.xp}XP${if (reward.rewardAccessoryId != null) " • ¡Manto Solar del Solsticio 3D desbloqueado!" else ""}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF1E293B),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                if (claimedReward != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Claim Button or Already Claimed Notice
                if (dailyState.canClaimToday) {
                    Button(
                        onClick = {
                            val r = onClaim()
                            claimedReward = r
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .scale(pulseScale)
                            .testTag("claim_daily_reward_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎁", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reclamar Día ${dailyState.nextDayToClaim}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1C1917)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFE2E8F0))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏳", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "¡Ya has reclamado hoy! Vuelve mañana para continuar tu racha.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyRewardItemCard(
    reward: DailyReward,
    isClaimed: Boolean,
    isCurrentTarget: Boolean,
    canClaim: Boolean,
    pulseScale: Float
) {
    val isFinalReward = reward.rewardAccessoryId != null
    val borderColor = when {
        isClaimed -> CelticEmerald
        canClaim -> CelticGold
        isCurrentTarget -> CelticBronze
        else -> ParchmentBorder
    }

    val backgroundColor = when {
        isClaimed -> CelticEmerald.copy(alpha = 0.12f)
        canClaim -> CelticGold.copy(alpha = 0.2f)
        isFinalReward -> CelticGoldBright.copy(alpha = 0.15f)
        else -> ParchmentCard
    }

    Card(
        modifier = Modifier
            .width(84.dp)
            .height(134.dp)
            .scale(pulseScale)
            .clip(RoundedCornerShape(14.dp))
            .border(if (canClaim || isClaimed) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (canClaim) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Day label
            Text(
                text = "Día ${reward.day}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isClaimed) CelticEmerald else CelticGreenDark
            )

            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isFinalReward) Brush.radialGradient(listOf(CelticGoldBright, CelticGold))
                        else Brush.radialGradient(listOf(Color.White, Color(0xFFF1F5F9)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(reward.iconEmoji, fontSize = 22.sp)
            }

            // Reward description (coins or cloak)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "+${reward.coins}🪙",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )

                if (isFinalReward) {
                    Text(
                        text = "¡Manto 3D!",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309)
                    )
                }
            }

            // Status indicator
            when {
                isClaimed -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Reclamado",
                        tint = CelticEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                }
                canClaim -> {
                    Text(
                        text = "¡LISTO!",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF92400E)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Bloqueado",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
