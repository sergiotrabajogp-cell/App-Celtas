package com.example.celtas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celtas.model.StoryChapter
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
fun StoryScreen(
    viewModel: CelticViewModel,
    modifier: Modifier = Modifier
) {
    val chapters by viewModel.chapters.collectAsState()
    val activeChapter by viewModel.activeChapter.collectAsState()
    val currentParagraph by viewModel.currentParagraph.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val quizSelectedAnswer by viewModel.quizSelectedAnswer.collectAsState()
    val quizIsCorrect by viewModel.quizIsCorrect.collectAsState()
    val speechRate by viewModel.narrationManager.speechRate.collectAsState()

    val infiniteTransition = rememberInfiniteTransition(label = "bardo_animation")
    val bardoBounce by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bardo_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Parchment)
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Chapter selector carousel
        Text(
            text = "Relatos Históricos del Bardo Celta",
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = CelticGreenDark,
            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
        )

        // --- CHRONOLOGICAL TIMELINE (Inicio celta -> Llegada de los Romanos) ---
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF162D1E)),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.2.dp, CelticGold)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⏳", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Línea del Tiempo Celta",
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "800 a.C. ➔ Llegada de Roma",
                        color = Color(0xFFCBD5E1),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    chapters.forEachIndexed { index, ch ->
                        val isSelected = activeChapter?.id == ch.id
                        val nodeBg = when {
                            isSelected -> CelticGold
                            ch.isCompleted -> CelticEmerald
                            else -> Color(0xFF243B2C)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(nodeBg)
                                .clickable { viewModel.selectChapter(ch) }
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${ch.iconEmoji} ${ch.chronologicalEra}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White
                                )
                                Text(
                                    text = ch.timePeriod,
                                    fontSize = 9.sp,
                                    color = if (isSelected) Color(0xFF1E293B) else Color(0xFF94A3B8)
                                )
                            }
                        }
                        if (index < chapters.size - 1) {
                            Text("➔", color = Color(0xFFFDE68A), fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            items(chapters) { chapter ->
                val isSelected = activeChapter?.id == chapter.id
                val cardBg = when {
                    isSelected -> CelticGreenDark
                    chapter.isCompleted -> CelticGreenLight
                    else -> ParchmentCard
                }
                val borderCol = when {
                    isSelected -> CelticGoldBright
                    chapter.isCompleted -> CelticEmerald
                    else -> ParchmentBorder
                }

                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .clickable(enabled = chapter.isUnlocked) {
                            viewModel.selectChapter(chapter)
                        }
                        .testTag("chapter_${chapter.id}"),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderCol),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(chapter.iconEmoji, fontSize = 24.sp)
                            if (chapter.isCompleted) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Completado",
                                    tint = if (isSelected) CelticGoldBright else CelticEmerald,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (!chapter.isUnlocked) {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Bloqueado",
                                    tint = SlateStone,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        // Era & Period Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSelected) Color(0xFF1E3A2B) else Color(0xFFE2E8F0))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = chapter.timePeriod,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color(0xFFFDE68A) else Color(0xFF475569)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Capítulo ${chapter.number}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) CelticGoldBright else CelticBronze
                        )
                        Text(
                            text = chapter.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2,
                            color = if (isSelected) Color.White else EarthBrown
                        )
                    }
                }
            }
        }

        // Active Chapter Reader View
        val chapter = activeChapter
        if (chapter != null) {
            // Storyteller Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(containerColor = CelticGreenLight),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, CelticEmerald)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(if (isSpeaking) bardoBounce else 1f)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, CelticGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isSpeaking) "🗣️" else "🪕", fontSize = 28.sp)
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bardo Oisín narra para ti:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CelticBronze
                        )
                        Text(
                            text = chapter.title,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = CelticGreenDark
                        )
                        Text(
                            text = "${chapter.subtitle} • ${chapter.region}",
                            fontSize = 12.sp,
                            color = EarthBrown
                        )
                    }
                }
            }

            // Audio Player Controls
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = CelticGreenDark),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Paragraph indicator
                    Text(
                        text = "Párrafo ${currentParagraph + 1} de ${chapter.paragraphs.size}",
                        color = CelticGoldBright,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { (currentParagraph + 1f) / chapter.paragraphs.size },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = CelticGoldBright,
                        trackColor = Color.White.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Media Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous paragraph
                        IconButton(
                            onClick = { viewModel.prevParagraph() },
                            enabled = currentParagraph > 0,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Párrafo anterior",
                                tint = Color.White
                            )
                        }

                        // Play/Pause main button
                        Button(
                            onClick = { viewModel.playOrPauseNarration() },
                            modifier = Modifier
                                .height(46.dp)
                                .testTag("play_pause_narration_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSpeaking) CelticAmber else CelticGold
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isSpeaking) "Pausar narración" else "Escuchar historia",
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSpeaking) "Pausar Voz" else "Narrar Cuento",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                        }

                        // Next paragraph
                        IconButton(
                            onClick = { viewModel.nextParagraph() },
                            enabled = currentParagraph < chapter.paragraphs.size - 1,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Siguiente párrafo",
                                tint = Color.White
                            )
                        }
                    }

                    // Speed selector
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Velocidad:", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                        listOf(0.85f to "Lenta", 1.0f to "Normal", 1.15f to "Rápida").forEach { (rate, label) ->
                            val isCurrent = (speechRate - rate) in -0.05f..0.05f
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isCurrent) CelticGoldBright else Color.White.copy(alpha = 0.1f))
                                    .clickable { viewModel.narrationManager.setSpeed(rate) }
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    color = if (isCurrent) CelticGreenDark else Color.White,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Paragraph Cards (Read-along with visual highlight)
            chapter.paragraphs.forEachIndexed { index, paragraphText ->
                val isActive = currentParagraph == index
                val bgColor by animateColorAsState(
                    targetValue = if (isActive) Color(0xFFFEF9C3) else ParchmentCard,
                    label = "para_bg"
                )
                val borderCol by animateColorAsState(
                    targetValue = if (isActive) CelticGold else ParchmentBorder,
                    label = "para_border"
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { viewModel.jumpToParagraph(index) }
                        .testTag("paragraph_$index"),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(if (isActive) 2.dp else 1.dp, borderCol),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 4.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (isActive) CelticGold else ParchmentDarker),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isActive) Color.White else EarthBrown
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = paragraphText,
                            fontSize = 15.sp,
                            color = EarthBrown,
                            lineHeight = 22.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // End-of-Chapter Quiz: Desafío de Comprensión del Bardo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .testTag("story_quiz_section"),
                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                shape = RoundedCornerShape(18.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, CelticGold),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Desafío del Bardo: ¿Recuerdas la historia?",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CelticGreenDark
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = chapter.quiz.question,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = EarthBrown
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    chapter.quiz.options.forEachIndexed { optIndex, optionText ->
                        val isSelected = quizSelectedAnswer == optIndex
                        val isCorrect = optIndex == chapter.quiz.correctIndex
                        val optBg = when {
                            quizSelectedAnswer == null -> ParchmentDarker
                            isSelected && isCorrect -> Color(0xFFDCFCE7)
                            isSelected && !isCorrect -> Color(0xFFFEE2E2)
                            else -> ParchmentDarker
                        }
                        val optBorder = when {
                            isSelected && isCorrect -> CelticEmerald
                            isSelected && !isCorrect -> Color(0xFFDC2626)
                            else -> ParchmentBorder
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = quizIsCorrect != true) {
                                    viewModel.answerStoryQuiz(optIndex)
                                }
                                .testTag("quiz_option_$optIndex"),
                            colors = CardDefaults.cardColors(containerColor = optBg),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, optBorder),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) CelticGreen else Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${'A' + optIndex}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else EarthBrown
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optionText,
                                    fontSize = 14.sp,
                                    color = EarthBrown,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Result feedback
                    AnimatedVisibility(visible = quizSelectedAnswer != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        if (quizIsCorrect == true) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCFCE7)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🎉", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "¡Correcto, noble explorador!",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            color = CelticGreenDark
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = chapter.quiz.explanation,
                                        fontSize = 12.sp,
                                        color = CelticGreenDark
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "Recompensa: +${chapter.rewardCoins} Monedas de Oro 🪙",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = CelticGold
                                        )
                                        Icon(
                                            imageVector = Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = CelticEmerald,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = "¡Casi lo logras! Inténtalo de nuevo o repasa el relato.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFDC2626)
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
