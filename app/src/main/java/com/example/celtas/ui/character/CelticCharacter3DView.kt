package com.example.celtas.ui.character

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celtas.model.CelticAccessory
import com.example.celtas.model.CharacterCustomization
import com.example.ui.theme.CelticGold
import com.example.ui.theme.CelticGreenDark
import com.example.ui.theme.CelticGreenLight
import com.example.ui.theme.ParchmentBorder
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CelticCharacter3DView(
    customization: CharacterCustomization,
    accessories: List<CelticAccessory>,
    modifier: Modifier = Modifier,
    characterName: String = "Arturo"
) {
    // 3D Camera & Orientation State
    var yawDeg by remember { mutableFloatStateOf(18f) }
    var pitchDeg by remember { mutableFloatStateOf(8f) }
    var zoomScale by remember { mutableFloatStateOf(1.05f) }
    var isAutoRotate by remember { mutableStateOf(false) }

    // Idle breathing & particle transition
    val infiniteTransition = rememberInfiniteTransition(label = "celtic_3d_idle")
    val animTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing)
        ),
        label = "anim_time"
    )

    val autoRotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing)
        ),
        label = "auto_rotate"
    )

    val currentYaw = if (isAutoRotate) (yawDeg + autoRotateAngle) else yawDeg

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .testTag("celtic_3d_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF132219)
        ),
        border = androidx.compose.foundation.BorderStroke(2.dp, CelticGold),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background Celtic Radial Aura
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f + 20f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x3310B981),
                            Color(0x15F59E0B),
                            Color.Transparent
                        ),
                        center = center,
                        radius = size.width * 0.55f
                    )
                )

                // Rune Altar ring lines on the ground
                val groundY = size.height / 2f + 120f
                drawOval(
                    color = Color(0x33F59E0B),
                    topLeft = Offset(center.x - 140f, groundY - 25f),
                    size = androidx.compose.ui.geometry.Size(280f, 50f),
                    style = Stroke(width = 2.5f)
                )
                drawOval(
                    color = Color(0x2210B981),
                    topLeft = Offset(center.x - 170f, groundY - 32f),
                    size = androidx.compose.ui.geometry.Size(340f, 64f),
                    style = Stroke(width = 1.5f)
                )

                // Celtic floating sparkles
                for (k in 0..7) {
                    val angleOffset = (k * 0.785f) + animTime * 0.5f
                    val r = 90f + (k % 3) * 28f
                    val px = center.x + cos(angleOffset) * r
                    val py = center.y - 60f + sin(angleOffset + k) * 35f
                    val sparkleAlpha = (sin(animTime * 2f + k) * 0.4f + 0.5f).coerceIn(0.1f, 0.9f)
                    drawCircle(
                        color = Color(0xFFFBBF24).copy(alpha = sparkleAlpha),
                        radius = 2.5f + (k % 2) * 1.5f,
                        center = Offset(px, py)
                    )
                }
            }

            // 3D Canvas with drag rotation gesture
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            isAutoRotate = false
                            yawDeg += dragAmount.x * 0.65f
                            pitchDeg = (pitchDeg - dragAmount.y * 0.4f).coerceIn(-28f, 40f)
                        }
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.7f, 1.6f)
                        }
                    }
            ) {
                val yawRad = (currentYaw * (Math.PI / 180.0)).toFloat()
                val pitchRad = (pitchDeg * (Math.PI / 180.0)).toFloat()

                // Generate 3D geometry
                val polygons = Celtic3DEngine.buildCelticCharacter(
                    customization = customization,
                    accessories = accessories,
                    time = animTime
                )

                // Project & depth-sort faces
                val projectedPolys = Celtic3DEngine.projectAndSort(
                    polygons = polygons,
                    yawRad = yawRad,
                    pitchRad = pitchRad,
                    viewportWidth = size.width,
                    viewportHeight = size.height,
                    zoom = zoomScale
                )

                // Render all 3D faces using Compose Path
                for (poly in projectedPolys) {
                    if (poly.points.size >= 3) {
                        val path = Path().apply {
                            moveTo(poly.points[0].first, poly.points[0].second)
                            for (i in 1 until poly.points.size) {
                                lineTo(poly.points[i].first, poly.points[i].second)
                            }
                            close()
                        }
                        // Draw face
                        drawPath(path = path, color = poly.shadedColor, style = Fill)

                        // Draw subtle edge outline
                        poly.outlineColor?.let { outline ->
                            drawPath(
                                path = path,
                                color = outline,
                                style = Stroke(width = poly.strokeWidth)
                            )
                        }
                    }
                }
            }

            // Header Overlay: Character Name, Title & Drag Hint
            Column(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val archIcon = when (customization.archetype) {
                        "druida" -> "🌿"
                        "guerrero" -> "⚔️"
                        "exploradora" -> "🏹"
                        else -> "🪕"
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CelticGold.copy(alpha = 0.25f))
                            .border(1.dp, CelticGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(archIcon, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = characterName,
                            color = Color(0xFFFDE68A),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = customization.title,
                            color = Color(0xFF9CA3AF),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Drag rotation hint pill
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x77000000))
                    .border(0.5.dp, Color(0x44FFFFFF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Arrastra para rotar 360°",
                    color = Color(0xFFD1D5DB),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Floating Controls: Zoom +, Zoom -, 360 Rotate Toggle, Reset Camera
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Auto-rotate toggle
                IconButton(
                    onClick = { isAutoRotate = !isAutoRotate },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isAutoRotate) CelticGold else Color(0x881E293B))
                        .border(1.dp, CelticGold, CircleShape)
                        .testTag("btn_auto_rotate")
                ) {
                    Icon(
                        imageVector = Icons.Default.RotateRight,
                        contentDescription = "Rotación continua",
                        tint = if (isAutoRotate) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Reset Camera
                IconButton(
                    onClick = {
                        yawDeg = 18f
                        pitchDeg = 8f
                        zoomScale = 1.05f
                        isAutoRotate = false
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x881E293B))
                        .border(1.dp, Color(0x55FFFFFF), CircleShape)
                        .testTag("btn_reset_camera")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restablecer cámara",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Zoom +
                IconButton(
                    onClick = { zoomScale = (zoomScale + 0.15f).coerceAtMost(1.6f) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x881E293B))
                        .border(1.dp, Color(0x55FFFFFF), CircleShape)
                        .testTag("btn_zoom_in")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Acercar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Zoom -
                IconButton(
                    onClick = { zoomScale = (zoomScale - 0.15f).coerceAtLeast(0.7f) },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0x881E293B))
                        .border(1.dp, Color(0x55FFFFFF), CircleShape)
                        .testTag("btn_zoom_out")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Alejar",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Equipped Accessories Bottom Badges
            val equippedList = accessories.filter { it.isEquipped }
            if (equippedList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xCC0B1610))
                        .border(1.dp, Color(0x44D97706), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Equipado:",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    equippedList.forEach { item ->
                        Text(
                            text = "${item.iconEmoji} ${item.name}",
                            color = Color(0xFFFDE68A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
