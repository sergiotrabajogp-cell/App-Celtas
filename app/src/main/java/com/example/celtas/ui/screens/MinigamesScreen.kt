package com.example.celtas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celtas.model.ArchaeologyItem
import com.example.celtas.model.RuneSymbol
import com.example.celtas.model.VillageBuilding
import com.example.celtas.viewmodel.CelticViewModel
import com.example.celtas.viewmodel.MinigameTab
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
fun MinigamesScreen(
    viewModel: CelticViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeMinigame.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Parchment)
    ) {
        // Minigames Tab Navigation Bar
        TabRow(
            selectedTabIndex = activeTab.ordinal,
            containerColor = CelticGreenDark,
            contentColor = CelticGoldBright,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[activeTab.ordinal]),
                    color = CelticGoldBright,
                    height = 3.dp
                )
            }
        ) {
            MinigameTab.values().forEach { tab ->
                val isSelected = activeTab == tab
                Tab(
                    selected = isSelected,
                    onClick = { viewModel.setMinigame(tab) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tab.icon, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) CelticGoldBright else Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    modifier = Modifier.testTag("tab_game_${tab.name.lowercase()}")
                )
            }
        }

        // Active Minigame Screen Content
        when (activeTab) {
            MinigameTab.ARCHAEOLOGY -> ArchaeologyMinigame(viewModel)
            MinigameTab.SYMBOLS -> SymbolsMinigame(viewModel)
            MinigameTab.QUIZ -> QuizTriviaMinigame(viewModel)
            MinigameTab.VILLAGE -> VillageBuilderMinigame(viewModel)
        }
    }
}

// 1. ARCHAEOLOGY MINIGAME
@Composable
fun ArchaeologyMinigame(viewModel: CelticViewModel) {
    val relics by viewModel.relics.collectAsState()
    val activeRelic by viewModel.activeExcavationRelic.collectAsState()
    val excavatedCells by viewModel.excavatedCells.collectAsState()
    val excavationSuccess by viewModel.excavationSuccess.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Relic selection horizontal bar
        Text(
            text = "Yacimiento Arqueológico: Elige una Reliquia",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = CelticGreenDark,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(relics) { relic ->
                val isSelected = activeRelic?.id == relic.id
                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { viewModel.selectRelicForExcavation(relic) }
                        .testTag("relic_card_${relic.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) CelticGreenLight else ParchmentCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isSelected) CelticGold else ParchmentBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(relic.iconEmoji, fontSize = 24.sp)
                            if (relic.isDiscovered) {
                                Text("✓ Hallado", color = CelticEmerald, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = relic.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = CelticGreenDark
                        )
                    }
                }
            }
        }

        // Active Relic Target Instruction
        val relic = activeRelic
        if (relic != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("⛏️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Buscando: ${relic.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CelticGreenDark
                        )
                        Text(
                            text = "Toca los bloques de tierra para desenterrar los restos antiguos.",
                            fontSize = 12.sp,
                            color = EarthBrown
                        )
                    }
                    IconButton(
                        onClick = { viewModel.selectRelicForExcavation(relic) }
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reiniciar cuadrícula", tint = CelticBronze)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4x4 Archaeological Dig Grid
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .border(2.dp, CelticBronze, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF785A3C)) // Earth trench color
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (row in 0..3) {
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (col in 0..3) {
                                val cellIndex = row * 4 + col
                                val isDug = excavatedCells.contains(cellIndex)
                                val isTarget = relic.targetCells.contains(cellIndex)

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                isDug && isTarget -> Color(0xFFFEF08A) // Gold glow under dirt!
                                                isDug -> Color(0xFF5C4028) // Dug empty dirt
                                                else -> Color(0xFFA17C5B) // Surface dirt block
                                            }
                                        )
                                        .border(
                                            1.dp,
                                            if (isDug && isTarget) CelticGoldBright else Color(0xFF422C1A),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { viewModel.digCell(cellIndex) }
                                        .testTag("dig_cell_$cellIndex"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDug && isTarget) {
                                        Text(relic.iconEmoji, fontSize = 22.sp)
                                    } else if (!isDug) {
                                        Text("🪨", fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Excavation Complete Card & Artifact Lore
            AnimatedVisibility(visible = excavationSuccess) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .testTag("excavation_success_card"),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CelticEmerald),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎉", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "¡DESCUBRIMIENTO ARQUEOLÓGICO!",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = CelticGreenDark
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(relic.iconEmoji, fontSize = 36.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = relic.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = CelticGreenDark
                                )
                                Text(
                                    text = "Época: ${relic.era} • Material: ${relic.material}",
                                    fontSize = 12.sp,
                                    color = SlateStone
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = relic.funFact,
                            fontSize = 13.sp,
                            color = EarthBrown
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "+60 Monedas de Oro 🪙 • Pieza de Museo",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = CelticGold
                            )
                            OutlinedButton(
                                onClick = {
                                    viewModel.narrationManager.speak("${relic.name}. ${relic.funFact}")
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Oír", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// 2. SYMBOLS & RUNES MINIGAME
@Composable
fun SymbolsMinigame(viewModel: CelticViewModel) {
    val symbols by viewModel.symbols.collectAsState()
    val selectedLore by viewModel.selectedRuneForLore.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ParchmentCard),
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🌀", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "El Secreto de los Nudos y Símbolos Celtas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CelticGreenDark
                    )
                    Text(
                        text = "Toca cada símbolo sagrado para descifrar su significado y ganar sabiduría.",
                        fontSize = 12.sp,
                        color = EarthBrown
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid of symbols
        symbols.forEach { sym ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        viewModel.selectRuneForLore(sym)
                        viewModel.matchSymbol(sym.id)
                    }
                    .testTag("symbol_${sym.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (sym.isMatched) CelticGreenLight else ParchmentCard
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp,
                    if (sym.isMatched) CelticEmerald else ParchmentBorder
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (sym.isMatched) Color.White else ParchmentDarker)
                            .border(1.5.dp, CelticGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(sym.symbolChar, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sym.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CelticGreenDark
                        )
                        Text(
                            text = sym.meaning,
                            fontSize = 12.sp,
                            color = SlateStone
                        )
                    }

                    if (sym.isMatched) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Descifrado",
                            tint = CelticEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Button(
                            onClick = {
                                viewModel.selectRuneForLore(sym)
                                viewModel.matchSymbol(sym.id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text("Descifrar", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }

        // Detailed Lore Dialog
        val lore = selectedLore
        if (lore != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF9C3)),
                border = androidx.compose.foundation.BorderStroke(2.dp, CelticGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(lore.symbolChar, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sabiduría del ${lore.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = CelticGreenDark
                            )
                        }
                        IconButton(onClick = { viewModel.selectRuneForLore(null) }) {
                            Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = lore.lore, fontSize = 13.sp, color = EarthBrown, lineHeight = 19.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.narrationManager.speak("${lore.name}. ${lore.meaning}. ${lore.lore}")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CelticGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Escuchar Enseñanza", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// 3. QUIZ TRIVIA MINIGAME: DESAFÍO DEL BARDO
@Composable
fun QuizTriviaMinigame(viewModel: CelticViewModel) {
    val currentQuestion by viewModel.currentQuizQuestion.collectAsState()
    val bardoAnswerStatus by viewModel.bardoAnswerStatus.collectAsState()
    val selectedOption by viewModel.selectedBardoOption.collectAsState()
    val score by viewModel.bardoQuizScore.collectAsState()
    val questionIndex by viewModel.quizIndex.collectAsState()
    val totalQ = viewModel.totalQuizQuestions()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Score Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CelticGreenDark),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🪕", fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Desafío del Bardo",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Pregunta ${questionIndex + 1} de $totalQ",
                            fontSize = 12.sp,
                            color = CelticGoldBright
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CelticGold)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "⭐ $score Aciertos",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Question Card wrapped with key(questionIndex) so it refreshes immediately on next question
        key(questionIndex) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CelticGold),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = currentQuestion.question,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = CelticGreenDark,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    currentQuestion.options.forEachIndexed { optIndex, optionText ->
                        val isAnswered = bardoAnswerStatus != null
                        val isThisCorrect = optIndex == currentQuestion.correctIndex
                        val isThisSelected = optIndex == selectedOption

                        val btnBg = when {
                            !isAnswered -> ParchmentDarker
                            isThisCorrect -> Color(0xFFDCFCE7)
                            isThisSelected -> Color(0xFFFEE2E2)
                            else -> ParchmentDarker.copy(alpha = 0.6f)
                        }
                        val btnBorder = when {
                            !isAnswered -> ParchmentBorder
                            isThisCorrect -> CelticEmerald
                            isThisSelected -> Color(0xFFDC2626)
                            else -> ParchmentBorder.copy(alpha = 0.5f)
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = !isAnswered) {
                                    viewModel.answerBardoQuiz(optIndex)
                                }
                                .testTag("trivia_option_$optIndex"),
                            colors = CardDefaults.cardColors(containerColor = btnBg),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, btnBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(26.dp)
                                            .clip(CircleShape)
                                            .background(Color.White),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${'A' + optIndex}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = CelticGreenDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = optionText,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EarthBrown
                                    )
                                }

                                if (isAnswered) {
                                    if (isThisCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correcto",
                                            tint = CelticEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else if (isThisSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Incorrecto",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Feedback & Next Button
                    AnimatedVisibility(visible = bardoAnswerStatus != null) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (bardoAnswerStatus == true) Color(0xFFDCFCE7) else Color(0xFFFEF2F2)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (bardoAnswerStatus == true) "🎉 ¡Maravilloso acierto! (+15🪙)" else "¡Ánimo! Aprende el dato histórico:",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (bardoAnswerStatus == true) CelticGreenDark else Color(0xFFDC2626)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentQuestion.funFact,
                                        fontSize = 12.sp,
                                        color = EarthBrown
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { viewModel.nextBardoQuestion() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("next_question_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = CelticGreen),
                                shape = RoundedCornerShape(12.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("Siguiente Pregunta", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Avanzar a la siguiente pregunta")
                            }
                        }
                    }
                }
            }
        }
    }
}

// 4. VILLAGE BUILDER MINIGAME: MI CASTRO CELTA
@Composable
fun VillageBuilderMinigame(viewModel: CelticViewModel) {
    val buildings by viewModel.buildings.collectAsState()
    val profile by viewModel.profile.collectAsState()
    var buildFeedback by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Village header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = ParchmentCard),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, CelticGold)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🛖", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Construye tu Castro Celta",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = CelticGreenDark
                            )
                            Text(
                                text = "Estructuras: ${buildings.count { it.isBuilt }} de ${buildings.size}",
                                fontSize = 12.sp,
                                color = SlateStone
                            )
                        }
                    }
                    // Coins
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CelticGold)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "🪙 ${profile.goldCoins}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Temporary feedback
        if (buildFeedback != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = buildFeedback!!,
                color = CelticBronze,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Structures List
        buildings.forEach { bld ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (bld.isBuilt) CelticGreenLight else ParchmentCard
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (bld.isBuilt) CelticEmerald else ParchmentBorder
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (bld.isBuilt) Color.White else ParchmentDarker),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(bld.icon, fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = bld.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CelticGreenDark
                        )
                        Text(
                            text = bld.description,
                            fontSize = 11.sp,
                            color = EarthBrown
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (bld.isBuilt) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CelticEmerald)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Construido ✓", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                val success = viewModel.buildStructure(bld.id)
                                buildFeedback = if (success) {
                                    "¡Estructura levantada con éxito!"
                                } else {
                                    "¡Necesitas más monedas de oro! Completa relatos o minijuegos."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("build_btn_${bld.id}")
                        ) {
                            Text("🪙 ${bld.cost}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
