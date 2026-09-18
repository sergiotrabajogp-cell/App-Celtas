package com.example.celtas.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.celtas.model.AccessorySlot
import com.example.celtas.model.CelticAccessory
import com.example.celtas.model.ItemSource
import com.example.celtas.ui.character.CelticCharacter3DView
import com.example.celtas.viewmodel.CelticViewModel
import com.example.ui.theme.CelticGold
import com.example.ui.theme.CelticGreenDark
import com.example.ui.theme.CelticGreenLight
import com.example.ui.theme.ParchmentBorder

enum class CustomizerSubTab(val title: String, val icon: String) {
    WARDROBE("Mi Armario", "🎒"),
    SHOP("Mercado Celta", "🪙"),
    REWARDS("Recompensas", "✨")
}

@Composable
fun CharacterCustomizerScreen(
    viewModel: CelticViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.profile.collectAsState()
    val customization by viewModel.customization.collectAsState()
    val accessories by viewModel.accessories.collectAsState()

    var activeSubTab by remember { mutableStateOf(CustomizerSubTab.WARDROBE) }
    var selectedSlotFilter by remember { mutableStateOf<AccessorySlot?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("character_customizer_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // --- 1. Interactive 3D Viewer Stage ---
        item {
            CelticCharacter3DView(
                customization = customization,
                accessories = accessories,
                characterName = profile.name
            )
        }

        // --- Feedback Notification Banner (e.g. bought or equipped item) ---
        item {
            AnimatedVisibility(visible = feedbackMessage != null) {
                feedbackMessage?.let { msg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { feedbackMessage = null },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F4322)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CelticGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = msg,
                                color = Color(0xFFFDE68A),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color(0xFFFDE68A),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // --- 2. Archetype & Appearance Quick Controls ---
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2B22)),
                border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Arquetipo Celta",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Archetype chips
                    val archetypes = listOf(
                        Triple("bardo", "Bardo", "🪕"),
                        Triple("druida", "Druida", "🌿"),
                        Triple("guerrero", "Guerrero", "⚔️"),
                        Triple("exploradora", "Cazadora", "🏹")
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        archetypes.forEach { (key, name, icon) ->
                            val isSelected = customization.archetype == key
                            val bgColor = if (isSelected) CelticGold else Color(0xFF243A2E)
                            val textColor = if (isSelected) Color.Black else Color(0xFFE2D6C0)

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(bgColor)
                                    .border(
                                        1.dp,
                                        if (isSelected) CelticGold else Color(0x33FFFFFF),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        viewModel.setArchetype(key)
                                        feedbackMessage = "¡Arquetipo cambiado a $name!"
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("archetype_chip_$key")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(icon, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        name,
                                        color = textColor,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // War Paint (Glausto Azul) Selector
                    Text(
                        text = "Pintura Facial de Guerra (Glausto)",
                        color = Color(0xFF38BDF8),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val paintNames = listOf("Sin pintura", "Espirales", "Trisquel", "Rayas")
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        paintNames.forEachIndexed { idx, name ->
                            val isSelected = customization.warPaintIndex == idx
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF0284C7) else Color(0xFF243A2E))
                                    .border(1.dp, if (isSelected) Color(0xFF38BDF8) else Color(0x22FFFFFF), RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setWarPaint(idx) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                                    .testTag("paint_chip_$idx")
                            ) {
                                Text(
                                    name,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hair Color selector
                    Text(
                        text = "Color de Cabello",
                        color = Color(0xFFE2D6C0),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val hairColors = listOf(
                        Pair(Color(0xFFD35400), "Pelirrojo"),
                        Pair(Color(0xFFF1C40F), "Rubio"),
                        Pair(Color(0xFF6E401F), "Castaño"),
                        Pair(Color(0xFF1E272C), "Cuervo")
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        hairColors.forEachIndexed { idx, (color, name) ->
                            val isSelected = customization.hairColorIndex == idx
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) CelticGold else Color(0x44FFFFFF),
                                        shape = CircleShape
                                    )
                                    .clickable { viewModel.setHairColor(idx) }
                                    .testTag("hair_color_$idx")
                            )
                        }
                    }
                }
            }
        }

        // --- 3. Sub-Navigation Tabs: Mi Armario / Mercado Celta / Recompensas ---
        item {
            TabRow(
                selectedTabIndex = activeSubTab.ordinal,
                containerColor = Color(0xFF1B2B22),
                contentColor = CelticGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeSubTab.ordinal]),
                        color = CelticGold,
                        height = 3.dp
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(14.dp))
            ) {
                CustomizerSubTab.values().forEach { tab ->
                    Tab(
                        selected = activeSubTab == tab,
                        onClick = { activeSubTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tab.icon, fontSize = 15.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    tab.title,
                                    fontWeight = if (activeSubTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        modifier = Modifier.testTag("customizer_tab_${tab.name}")
                    )
                }
            }
        }

        // --- Slot Filter Chips (Cabeza, Capa, Cuello, Armas, Compañero) ---
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedSlotFilter == null,
                    onClick = { selectedSlotFilter = null },
                    label = { Text("Todos", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CelticGold,
                        selectedLabelColor = Color.Black
                    )
                )
                AccessorySlot.values().forEach { slot ->
                    FilterChip(
                        selected = selectedSlotFilter == slot,
                        onClick = { selectedSlotFilter = if (selectedSlotFilter == slot) null else slot },
                        label = { Text("${slot.icon} ${slot.title}", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CelticGold,
                            selectedLabelColor = Color.Black
                        ),
                        modifier = Modifier.testTag("slot_filter_${slot.name}")
                    )
                }
            }
        }

        // --- Content for Selected Tab ---
        when (activeSubTab) {
            CustomizerSubTab.WARDROBE -> {
                val ownedAccessories = accessories.filter { item ->
                    item.isOwned && (selectedSlotFilter == null || item.slot == selectedSlotFilter)
                }

                if (ownedAccessories.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 20.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF16231B)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🎒", fontSize = 42.sp)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    "Tu armario está vacío en esta categoría",
                                    color = Color(0xFFE2D6C0),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Visita el Mercado Celta para comprar accesorios con tus monedas de oro o excava reliquias arqueológicas.",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { activeSubTab = CustomizerSubTab.SHOP },
                                    colors = ButtonDefaults.buttonColors(containerColor = CelticGold)
                                ) {
                                    Text("Ir al Mercado Celta", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    items(ownedAccessories, key = { it.id }) { item ->
                        WardrobeItemCard(
                            accessory = item,
                            onEquip = {
                                viewModel.equipAccessory(item.id)
                                feedbackMessage = "Has equipado ${item.name} en tu personaje 3D"
                            },
                            onUnequip = {
                                viewModel.unequipAccessory(item.id)
                                feedbackMessage = "Has quitado ${item.name}"
                            }
                        )
                    }
                }
            }

            CustomizerSubTab.SHOP -> {
                val shopAccessories = accessories.filter { item ->
                    item.source == ItemSource.SHOP && (selectedSlotFilter == null || item.slot == selectedSlotFilter)
                }

                items(shopAccessories, key = { it.id }) { item ->
                    ShopItemCard(
                        accessory = item,
                        userCoins = profile.goldCoins,
                        onBuy = {
                            val success = viewModel.buyAccessory(item.id)
                            if (success) {
                                viewModel.equipAccessory(item.id)
                                feedbackMessage = "¡Compraste y equipaste ${item.name}! (-${item.price} 🪙)"
                            } else {
                                feedbackMessage = "No tienes suficientes monedas de oro (${item.price} 🪙 requeridas)"
                            }
                        },
                        onEquip = {
                            viewModel.equipAccessory(item.id)
                            feedbackMessage = "Has equipado ${item.name}"
                        },
                        onUnequip = {
                            viewModel.unequipAccessory(item.id)
                            feedbackMessage = "Has quitado ${item.name}"
                        }
                    )
                }
            }

            CustomizerSubTab.REWARDS -> {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openDailyRewardDialog() }
                            .testTag("open_daily_from_customizer"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF26200B)),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, CelticGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Text("☀️", fontSize = 28.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Recompensas Diarias del Solsticio",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFFFDE68A)
                                    )
                                    Text(
                                        "Reclama monedas cada día y consigue el Manto Solar 3D el día 7",
                                        fontSize = 11.sp,
                                        color = Color(0xFFD1D5DB)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { viewModel.openDailyRewardDialog() },
                                colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Abrir", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }

                val rewardAccessories = accessories.filter { item ->
                    item.source != ItemSource.SHOP && (selectedSlotFilter == null || item.slot == selectedSlotFilter)
                }

                items(rewardAccessories, key = { it.id }) { item ->
                    RewardItemCard(
                        accessory = item,
                        onEquip = {
                            viewModel.equipAccessory(item.id)
                            feedbackMessage = "Has equipado tu recompensa: ${item.name}"
                        },
                        onUnequip = {
                            viewModel.unequipAccessory(item.id)
                            feedbackMessage = "Has quitado ${item.name}"
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun WardrobeItemCard(
    accessory: CelticAccessory,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    val borderColor = if (accessory.isEquipped) CelticGold else ParchmentBorder
    val containerColor = if (accessory.isEquipped) Color(0xFF1A3324) else Color(0xFF16231B)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("wardrobe_item_${accessory.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = androidx.compose.foundation.BorderStroke(if (accessory.isEquipped) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF243A2E))
                    .border(1.dp, borderColor, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(accessory.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = accessory.name,
                        color = Color(0xFFFAF6EE),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${accessory.slot.icon} ${accessory.slot.title}",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    if (accessory.isEquipped) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CelticGold)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                "EQUIPADO",
                                color = Color.Black,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = accessory.description,
                    color = Color(0xFFD1D5DB),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action Button: Equip / Unequip
            if (accessory.isEquipped) {
                OutlinedButton(
                    onClick = onUnequip,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                    modifier = Modifier.testTag("btn_unequip_${accessory.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Quitar",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Quitar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onEquip,
                    colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                    modifier = Modifier.testTag("btn_equip_${accessory.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Poner",
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Poner", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(
    accessory: CelticAccessory,
    userCoins: Int,
    onBuy: () -> Unit,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    val isOwned = accessory.isOwned
    val canAfford = userCoins >= accessory.price

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("shop_item_${accessory.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16231B)),
        border = androidx.compose.foundation.BorderStroke(1.dp, ParchmentBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF243A2E))
                    .border(1.dp, CelticGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(accessory.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = accessory.name,
                    color = Color(0xFFFAF6EE),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${accessory.slot.icon} ${accessory.slot.title}",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp
                    )
                    if (!isOwned) {
                        Text(
                            text = "🪙 ${accessory.price}",
                            color = CelticGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = accessory.description,
                    color = Color(0xFFD1D5DB),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action
            if (isOwned) {
                if (accessory.isEquipped) {
                    OutlinedButton(
                        onClick = onUnequip,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.testTag("btn_shop_unequip_${accessory.id}")
                    ) {
                        Text("Quitar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                        modifier = Modifier.testTag("btn_shop_equip_${accessory.id}")
                    ) {
                        Text("Poner", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Button(
                    onClick = onBuy,
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CelticGold,
                        disabledContainerColor = Color(0xFF374151)
                    ),
                    modifier = Modifier.testTag("btn_buy_${accessory.id}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Comprar",
                            color = if (canAfford) Color.Black else Color(0xFF9CA3AF),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RewardItemCard(
    accessory: CelticAccessory,
    onEquip: () -> Unit,
    onUnequip: () -> Unit
) {
    val isUnlocked = accessory.isOwned

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("reward_item_${accessory.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUnlocked) Color(0xFF1A3324) else Color(0xFF131D17)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUnlocked) CelticGold else Color(0x33FFFFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isUnlocked) Color(0xFF243A2E) else Color(0xFF1F2937))
                    .border(
                        1.dp,
                        if (isUnlocked) CelticGold else Color(0x33FFFFFF),
                        RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(if (isUnlocked) accessory.iconEmoji else "🔒", fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = accessory.name,
                    color = if (isUnlocked) Color(0xFFFAF6EE) else Color(0xFF9CA3AF),
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${accessory.slot.icon} ${accessory.slot.title}",
                        color = Color(0xFFA7F3D0),
                        fontSize = 11.sp
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isUnlocked) Color(0xFF047857) else Color(0xFF374151))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            if (isUnlocked) "DESBLOQUEADO" else "RECOMPENSA",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isUnlocked) accessory.description else "Cómo conseguirlo: ${accessory.sourceRequirement}",
                    color = if (isUnlocked) Color(0xFFD1D5DB) else Color(0xFFFBBF24),
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Action if unlocked
            if (isUnlocked) {
                if (accessory.isEquipped) {
                    OutlinedButton(
                        onClick = onUnequip,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444)),
                        modifier = Modifier.testTag("btn_reward_unequip_${accessory.id}")
                    ) {
                        Text("Quitar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = onEquip,
                        colors = ButtonDefaults.buttonColors(containerColor = CelticGold),
                        modifier = Modifier.testTag("btn_reward_equip_${accessory.id}")
                    ) {
                        Text("Poner", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF263238))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Bloqueado",
                        color = Color(0xFF9E9E9E),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
