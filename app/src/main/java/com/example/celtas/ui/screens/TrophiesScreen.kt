package com.example.celtas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.celtas.model.TrophyPrize
import com.example.celtas.viewmodel.AppTab
import com.example.celtas.viewmodel.CelticViewModel
import com.example.ui.theme.CelticAmber
import com.example.ui.theme.CelticBronze
import com.example.ui.theme.CelticEmerald
import com.example.ui.theme.CelticGold
import com.example.ui.theme.CelticGoldBright
import com.example.ui.theme.CelticGreen
import com.example.ui.theme.CelticGreenDark
import com.example.ui.theme.CelticGreenLight
import com.example.ui.theme.EarthBrown
import com.example.ui.theme.Parchment
import com.example.ui.theme.ParchmentBorder
import com.example.ui.theme.ParchmentCard
import com.example.ui.theme.ParchmentDarker
import com.example.ui.theme.SlateStone

@Composable
fun TrophiesScreen(
    viewModel: CelticViewModel,
    modifier: Modifier = Modifier
) {
    val trophies by viewModel.trophies.collectAsState()
    val achievements by viewModel.achievements.collectAsState()

    var selectedSection by remember { mutableStateOf(0) } // 0: Premios Virtuales, 1: Logros

    val unlockedTrophiesCount = trophies.count { it.isUnlocked }
    val unlockedAchievementsCount = achievements.count { it.isUnlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        // Section Switcher
        TabRow(
            selectedTabIndex = selectedSection,
            containerColor = CelticGreenDark,
            contentColor = CelticGoldBright,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSection]),
                    color = CelticGoldBright,
                    height = 3.dp
                )
            }
        ) {
            Tab(
                selected = selectedSection == 0,
                onClick = { selectedSection = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Premios ($unlockedTrophiesCount/${trophies.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedSection == 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSection == 0) CelticGoldBright else Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                modifier = Modifier.testTag("tab_trophies")
            )
            Tab(
                selected = selectedSection == 1,
                onClick = { selectedSection = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Logros ($unlockedAchievementsCount/${achievements.size})",
                            fontSize = 13.sp,
                            fontWeight = if (selectedSection == 1) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedSection == 1) CelticGoldBright else Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                modifier = Modifier.testTag("tab_achievements")
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            if (selectedSection == 0) {
                // Virtual Trophies Showcase
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🏛️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Sala de Tesoros Celtas",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CelticGreenDark
                            )
                            Text(
                                text = "Tesoros históricos que ganas al explorar relatos y excavar yacimientos.",
                                fontSize = 12.sp,
                                color = EarthBrown
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3D Customizer Link Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setTab(AppTab.PERSONAJE) }
                        .testTag("btn_go_to_customizer_from_trophies"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2B22)),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CelticGold)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🧙‍♂️", fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "¡Equipa tus Recompensas en 3D!",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFFFBBF24)
                                )
                                Text(
                                    text = "Viste a tu héroe celta con los torques, armas y coronas ganadas.",
                                    fontSize = 12.sp,
                                    color = Color(0xFFE2D6C0)
                                )
                            }
                        }
                        Text("Ver 3D ›", color = CelticGoldBright, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                trophies.forEach { trophy ->
                    val isUnlocked = trophy.isUnlocked
                    val cardBg = if (isUnlocked) Color(0xFFFEF9C3) else ParchmentCard
                    val borderCol = if (isUnlocked) CelticGold else ParchmentBorder

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("trophy_${trophy.id}"),
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, borderCol),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 3.dp else 1.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Pedestal Icon
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isUnlocked) Brush.radialGradient(listOf(CelticGoldBright, CelticGold))
                                        else Brush.radialGradient(listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1)))
                                    )
                                    .border(
                                        1.5.dp,
                                        if (isUnlocked) CelticGoldBright else Color(0xFF94A3B8),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isUnlocked) {
                                    Text(trophy.iconEmoji, fontSize = 28.sp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Bloqueado",
                                        tint = SlateStone,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = trophy.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = if (isUnlocked) CelticGreenDark else SlateStone
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (trophy.rarity) {
                                                    "Legendario" -> Color(0xFFEDE9FE)
                                                    "Raro" -> Color(0xFFFEF3C7)
                                                    else -> Color(0xFFF1F5F9)
                                                }
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = trophy.rarity,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (trophy.rarity) {
                                                "Legendario" -> Color(0xFF6D28D9)
                                                "Raro" -> CelticBronze
                                                else -> SlateStone
                                            }
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = trophy.description,
                                    fontSize = 12.sp,
                                    color = if (isUnlocked) EarthBrown else SlateStone.copy(alpha = 0.7f)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = if (isUnlocked) "⭐ ¡Tesoro en tu colección!" else "🔒 Por descubrir",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) CelticEmerald else CelticBronze
                                )
                            }
                        }
                    }
                }
            } else {
                // Achievements List
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎯", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Logros Desbloqueables",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CelticGreenDark
                            )
                            Text(
                                text = "Cumple hitos en la historia celta para ganar medallas y monedas.",
                                fontSize = 12.sp,
                                color = EarthBrown
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                achievements.forEach { ach ->
                    val isUnlocked = ach.isUnlocked
                    val progressRatio = (ach.currentProgress.toFloat() / ach.maxProgress).coerceIn(0f, 1f)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("achievement_${ach.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUnlocked) CelticGreenLight else ParchmentCard
                        ),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isUnlocked) CelticEmerald else ParchmentBorder
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(if (isUnlocked) Color.White else ParchmentDarker),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(ach.iconEmoji, fontSize = 22.sp)
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = ach.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = CelticGreenDark
                                        )
                                        Text(
                                            text = "+${ach.rewardCoins} 🪙",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 12.sp,
                                            color = CelticGold
                                        )
                                    }
                                    Text(
                                        text = ach.description,
                                        fontSize = 12.sp,
                                        color = EarthBrown
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Progress bar
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (isUnlocked) CelticEmerald else CelticGold,
                                    trackColor = ParchmentDarker
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isUnlocked) "¡Completado! ✓" else "${ach.currentProgress}/${ach.maxProgress}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUnlocked) CelticEmerald else SlateStone
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
