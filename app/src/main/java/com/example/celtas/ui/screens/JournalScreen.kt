package com.example.celtas.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.celtas.model.ExplorerRank
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
fun JournalScreen(
    viewModel: CelticViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val journalEntries by viewModel.journalEntries.collectAsState()

    var showNewNoteDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    var newNoteTitle by remember { mutableStateOf("") }
    var newNoteContent by remember { mutableStateOf("") }
    var selectedSticker by remember { mutableStateOf("🧭") }

    var editNameInput by remember { mutableStateOf(profile.name) }
    var editAvatarInput by remember { mutableStateOf(profile.avatarId) }

    val stickers = listOf("🧭", "🪕", "🌿", "🛡️", "👑", "🐗", "🪙", "🛖", "🔥", "🌀")
    val avatarOptions = listOf(
        "bardo" to ("🪕" to "Bardo Cantor"),
        "druida" to ("🧙‍♂️" to "Sabio Druida"),
        "guerrero" to ("🛡️" to "Guerrero Valiente"),
        "exploradora" to ("🏹" to "Exploradora Celta")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Parchment)
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Explorer Passport Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .testTag("explorer_passport_card"),
            colors = CardDefaults.cardColors(containerColor = CelticGreenDark),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CelticGreenLight)
                                .border(2.dp, CelticGoldBright, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val emoji = when (profile.avatarId) {
                                "druida" -> "🧙‍♂️"
                                "guerrero" -> "🛡️"
                                "exploradora" -> "🏹"
                                else -> "🪕"
                            }
                            Text(emoji, fontSize = 28.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = profile.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${profile.rank.badgeIcon} ${profile.rank.title}",
                                fontSize = 12.sp,
                                color = CelticGoldBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            editNameInput = profile.name
                            editAvatarInput = profile.avatarId
                            showEditProfileDialog = !showEditProfileDialog
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .testTag("edit_profile_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar Carnet",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // XP Progress to next rank
                val nextRankMin = when (profile.rank) {
                    ExplorerRank.NOVATO -> ExplorerRank.RASTREADOR.minXp
                    ExplorerRank.RASTREADOR -> ExplorerRank.APRENDIZ_BARDO.minXp
                    ExplorerRank.APRENDIZ_BARDO -> ExplorerRank.SABIO_ROBLE.minXp
                    ExplorerRank.SABIO_ROBLE -> ExplorerRank.GUARDIAN_CELTA.minXp
                    ExplorerRank.GUARDIAN_CELTA -> 1000
                }
                val progressFraction = (profile.xp.toFloat() / nextRankMin).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Experiencia Histórica: ${profile.xp} XP",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "Meta: $nextRankMin XP",
                        fontSize = 11.sp,
                        color = CelticGoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = CelticGoldBright,
                    trackColor = Color.White.copy(alpha = 0.2f)
                )
            }
        }

        // Edit Profile Dialog/Section
        AnimatedVisibility(visible = showEditProfileDialog) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CelticGold)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Personaliza tu Carnet de Explorador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = CelticGreenDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("Tu Nombre o Apodo Celta") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CelticGreen,
                            unfocusedBorderColor = ParchmentBorder
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Elige tu Personaje Celta:", fontSize = 12.sp, color = EarthBrown, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        avatarOptions.forEach { (id, pair) ->
                            val isChosen = editAvatarInput == id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isChosen) CelticGreenLight else ParchmentDarker)
                                    .border(if (isChosen) 2.dp else 1.dp, if (isChosen) CelticEmerald else ParchmentBorder, RoundedCornerShape(12.dp))
                                    .clickable { editAvatarInput = id }
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(pair.first, fontSize = 24.sp)
                                    Text(pair.second.split(" ")[0], fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.updateProfile(editNameInput, editAvatarInput)
                            showEditProfileDialog = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = CelticGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Add Note Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Páginas del Diario de Campo",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = CelticGreenDark
            )

            Button(
                onClick = { showNewNoteDialog = !showNewNoteDialog },
                colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("add_note_btn")
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Escribir Nota", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Add Note Form
        AnimatedVisibility(visible = showNewNoteDialog) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = ParchmentCard),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, CelticGold)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Nueva Nota del Explorador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CelticGreenDark
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newNoteTitle,
                        onValueChange = { newNoteTitle = it },
                        label = { Text("Título (ej. Mi visita al castro)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newNoteContent,
                        onValueChange = { newNoteContent = it },
                        label = { Text("¿Qué has aprendido o descubierto hoy?") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text("Sello o Pegatina Celta:", fontSize = 11.sp, color = EarthBrown, fontWeight = FontWeight.SemiBold)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(stickers) { stk ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedSticker == stk) CelticGoldBright else ParchmentDarker)
                                    .clickable { selectedSticker = stk },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(stk, fontSize = 18.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showNewNoteDialog = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Cancelar", fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                if (newNoteContent.isNotBlank()) {
                                    viewModel.addJournalNote(newNoteTitle, newNoteContent, selectedSticker)
                                    newNoteTitle = ""
                                    newNoteContent = ""
                                    showNewNoteDialog = false
                                }
                            },
                            modifier = Modifier.weight(1.5f),
                            colors = ButtonDefaults.buttonColors(containerColor = CelticGreen),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Guardar en Diario", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Journal Entries List
        journalEntries.forEach { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("entry_${entry.id}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (entry.isCustom) Color(0xFFFEF9C3) else ParchmentCard
                ),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (entry.isCustom) CelticGold else ParchmentBorder
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(entry.sticker, fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = entry.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = CelticGreenDark
                                )
                                Text(
                                    text = "${entry.category} • ${entry.timestamp}",
                                    fontSize = 11.sp,
                                    color = CelticBronze,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Row {
                            IconButton(
                                onClick = {
                                    viewModel.narrationManager.speak("${entry.title}. ${entry.content}")
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Escuchar ficha",
                                    tint = CelticGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            if (entry.isCustom) {
                                IconButton(
                                    onClick = { viewModel.deleteJournalNote(entry.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Eliminar nota",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = entry.content,
                        fontSize = 13.sp,
                        color = EarthBrown,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
