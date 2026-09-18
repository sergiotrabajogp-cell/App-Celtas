package com.example.celtas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celtas.model.MapLocation
import com.example.celtas.viewmodel.CelticViewModel
import com.example.ui.theme.CelticAmber
import com.example.ui.theme.CelticBlue
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
fun MapScreen(
    viewModel: CelticViewModel,
    modifier: Modifier = Modifier
) {
    val locations by viewModel.mapLocations.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    // Pulse animation for pins
    val infiniteTransition = rememberInfiniteTransition(label = "pin_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Parchment)
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header info banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = ParchmentCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🗺️", fontSize = 28.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Mapa del Mundo Celta Antiguo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CelticGreenDark
                    )
                    Text(
                        text = "Toca las marcas mágicas para descubrir fortalezas, minas y bosques sagrados.",
                        fontSize = 12.sp,
                        color = EarthBrown
                    )
                }
            }
        }

        // Illustrated Canvas Map
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.15f)
                .clip(RoundedCornerShape(20.dp))
                .border(3.dp, CelticGold, RoundedCornerShape(20.dp)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val boxWidth = maxWidth
                val boxHeight = maxHeight

                // Decorative Ancient Map Background
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ParchmentDarker)
                ) {
                    val w = size.width
                    val h = size.height

                    // Sea / Ocean tint on the Atlantic west and North
                    drawRect(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFFE0F2FE), Color(0xFFBAE6FD)),
                            center = Offset(w * 0.1f, h * 0.4f),
                            radius = w * 0.7f
                        )
                    )

                    // Land mass outline (Simplified Celtic Western Europe: Iberia, Gaul, British Isles, Central Europe)
                    val landPath = Path().apply {
                        // Iberian Peninsula
                        moveTo(w * 0.12f, h * 0.92f)
                        lineTo(w * 0.38f, h * 0.90f)
                        lineTo(w * 0.35f, h * 0.75f)
                        // Pyrenees to Gaul
                        lineTo(w * 0.45f, h * 0.65f)
                        lineTo(w * 0.82f, h * 0.60f)
                        lineTo(w * 0.88f, h * 0.45f)
                        lineTo(w * 0.55f, h * 0.40f)
                        // Brittany
                        lineTo(w * 0.30f, h * 0.48f)
                        lineTo(w * 0.32f, h * 0.60f)
                        lineTo(w * 0.18f, h * 0.65f)
                        close()
                    }
                    drawPath(
                        path = landPath,
                        color = Color(0xFFDCF0D6)
                    )
                    drawPath(
                        path = landPath,
                        color = Color(0xFF86EFAC),
                        style = Stroke(width = 3f)
                    )

                    // British Isles & Ireland Island
                    val islandsPath = Path().apply {
                        // Britain
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = androidx.compose.ui.geometry.Rect(
                                    offset = Offset(w * 0.28f, h * 0.22f),
                                    size = Size(w * 0.20f, h * 0.28f)
                                ),
                                cornerRadius = CornerRadius(40f, 40f)
                            )
                        )
                        // Ireland
                        addRoundRect(
                            androidx.compose.ui.geometry.RoundRect(
                                rect = androidx.compose.ui.geometry.Rect(
                                    offset = Offset(w * 0.14f, h * 0.20f),
                                    size = Size(w * 0.14f, h * 0.22f)
                                ),
                                cornerRadius = CornerRadius(30f, 30f)
                            )
                        )
                    }
                    drawPath(
                        path = islandsPath,
                        color = Color(0xFFDCF0D6)
                    )
                    drawPath(
                        path = islandsPath,
                        color = Color(0xFF86EFAC),
                        style = Stroke(width = 3f)
                    )

                    // Illustrated River Lines (Danube and Rhine)
                    val riverPath = Path().apply {
                        moveTo(w * 0.45f, h * 0.52f)
                        quadraticTo(w * 0.60f, h * 0.50f, w * 0.78f, h * 0.53f)
                    }
                    drawPath(riverPath, color = CelticBlue.copy(alpha = 0.6f), style = Stroke(width = 4f))

                    // Mountain indicators (Alps and Cantabrian)
                    drawCircle(color = Color(0xFF94A3B8), radius = 10f, center = Offset(w * 0.58f, h * 0.58f))
                    drawCircle(color = Color(0xFF94A3B8), radius = 12f, center = Offset(w * 0.63f, h * 0.57f))
                    drawCircle(color = Color(0xFF94A3B8), radius = 9f, center = Offset(w * 0.28f, h * 0.70f))

                    // Compass Rose / Rosa de los Vientos (Top right)
                    val compassCenter = Offset(w * 0.85f, h * 0.18f)
                    drawCircle(
                        color = CelticGold.copy(alpha = 0.25f),
                        radius = 28f,
                        center = compassCenter
                    )
                    drawCircle(
                        color = CelticGold,
                        radius = 28f,
                        center = compassCenter,
                        style = Stroke(width = 2f)
                    )
                    drawLine(
                        color = CelticBronze,
                        start = Offset(compassCenter.x, compassCenter.y - 26f),
                        end = Offset(compassCenter.x, compassCenter.y + 26f),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = CelticBronze,
                        start = Offset(compassCenter.x - 26f, compassCenter.y),
                        end = Offset(compassCenter.x + 26f, compassCenter.y),
                        strokeWidth = 2f
                    )
                }

                // Region Labels on Map
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "BRITANIA & IRLANDA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = CelticGreenDark.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 24.dp, top = 20.dp)
                    )
                    Text(
                        text = "GALIA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = CelticGreenDark.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.Center)
                    )
                    Text(
                        text = "CENTROEUROPA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = CelticGreenDark.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                    )
                    Text(
                        text = "HISPANIA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = CelticGreenDark.copy(alpha = 0.6f),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 30.dp, bottom = 26.dp)
                    )
                }

                // Interactive Location Pins
                locations.forEach { loc ->
                    val posX = boxWidth * loc.xPercent
                    val posY = boxHeight * loc.yPercent
                    val isSelected = selectedLocation?.id == loc.id

                    Box(
                        modifier = Modifier
                            .padding(start = posX, top = posY)
                            .testTag("pin_${loc.id}")
                            .clickable { viewModel.selectLocation(loc) },
                        contentAlignment = Alignment.Center
                    ) {
                        // Outer pulse ring if visited or selected
                        Box(
                            modifier = Modifier
                                .size(if (isSelected) 46.dp * pulseScale else 36.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) CelticGoldBright.copy(alpha = 0.4f)
                                    else if (loc.isVisited) CelticEmerald.copy(alpha = 0.25f)
                                    else CelticAmber.copy(alpha = 0.25f)
                                )
                        )

                        // Pin icon pill
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) CelticGold
                                    else if (loc.isVisited) CelticGreen
                                    else CelticGreenDark
                                )
                                .border(
                                    width = 2.dp,
                                    color = if (isSelected) Color.White else CelticGoldBright,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = loc.icon, fontSize = 16.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Selected Location Detail Sheet
        AnimatedVisibility(visible = selectedLocation != null) {
            val loc = selectedLocation
            if (loc != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("location_detail_card"),
                    colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                    border = androidx.compose.foundation.BorderStroke(2.dp, CelticGold),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(loc.icon, fontSize = 32.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = loc.name,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = CelticGreenDark
                                    )
                                    Text(
                                        text = "Región: ${loc.region.name} • ${loc.ancientName}",
                                        fontSize = 12.sp,
                                        color = CelticBronze,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            IconButton(
                                onClick = { viewModel.selectLocation(null) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Cerrar",
                                    tint = SlateStone
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Fact Box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CelticGreenLight),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💡", fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loc.shortFact,
                                    fontSize = 13.sp,
                                    color = CelticGreenDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = loc.fullStory,
                            fontSize = 14.sp,
                            color = EarthBrown,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Fast action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Audio story narration
                            OutlinedButton(
                                onClick = {
                                    viewModel.narrationManager.speak(
                                        "${loc.name}. ${loc.shortFact}. ${loc.fullStory}"
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = CelticGreen
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Escuchar", fontSize = 12.sp, color = CelticGreen)
                            }

                            // Travel to Story
                            if (loc.connectedChapterId != null) {
                                Button(
                                    onClick = { viewModel.travelFromMapToStory(loc.connectedChapterId) },
                                    modifier = Modifier.weight(1.2f),
                                    colors = ButtonDefaults.buttonColors(containerColor = CelticGreen),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Ver Relato", fontSize = 12.sp, color = Color.White)
                                }
                            }

                            // Travel to Minigame
                            if (loc.connectedMinigame != null) {
                                Button(
                                    onClick = { viewModel.travelFromMapToMinigame(loc.connectedMinigame) },
                                    modifier = Modifier.weight(1.2f),
                                    colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Gamepad,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Minijuego", fontSize = 12.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Horizontal Carousel of all Places
        Text(
            text = "Lugares Históricos para Explorar",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = CelticGreenDark,
            modifier = Modifier.padding(vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(locations) { loc ->
                Card(
                    modifier = Modifier
                        .width(180.dp)
                        .clickable { viewModel.selectLocation(loc) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (loc.isVisited) CelticGreenLight else ParchmentCard
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (loc.isVisited) CelticEmerald else ParchmentBorder
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(loc.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = loc.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                color = CelticGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = loc.shortFact,
                            fontSize = 11.sp,
                            maxLines = 2,
                            color = SlateStone
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (loc.isVisited) "✓ Visitado (+15🪙)" else "🧭 No explorado",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (loc.isVisited) CelticEmerald else CelticBronze
                            )
                        }
                    }
                }
            }
        }
    }
}
