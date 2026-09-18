package com.example.celtas.data

import android.content.Context
import android.content.SharedPreferences
import com.example.celtas.model.AccessorySlot
import com.example.celtas.model.Achievement
import com.example.celtas.model.ArchaeologyItem
import com.example.celtas.model.CelticAccessory
import com.example.celtas.model.CharacterCustomization
import com.example.celtas.model.DailyReward
import com.example.celtas.model.DailyRewardState
import com.example.celtas.model.ExplorerProfile
import com.example.celtas.model.ExplorerRank
import com.example.celtas.model.ItemSource
import com.example.celtas.model.JournalEntry
import com.example.celtas.model.MapLocation
import com.example.celtas.model.QuizQuestion
import com.example.celtas.model.RegionTag
import com.example.celtas.model.RuneSymbol
import com.example.celtas.model.StoryChapter
import com.example.celtas.model.StoryChoice
import com.example.celtas.model.TrophyPrize
import com.example.celtas.model.VillageBuilding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class CelticAdventureRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("celtic_adventure_prefs", Context.MODE_PRIVATE)

    // Observable states
    private val _dailyRewardState = MutableStateFlow(loadDailyRewardState())
    val dailyRewardState: StateFlow<DailyRewardState> = _dailyRewardState.asStateFlow()

    private val _profile = MutableStateFlow(loadProfile())
    val profile: StateFlow<ExplorerProfile> = _profile.asStateFlow()

    private val _customization = MutableStateFlow(loadCustomization())
    val customization: StateFlow<CharacterCustomization> = _customization.asStateFlow()

    private val _accessories = MutableStateFlow(loadAccessories())
    val accessories: StateFlow<List<CelticAccessory>> = _accessories.asStateFlow()

    private val _chapters = MutableStateFlow(loadChapters())
    val chapters: StateFlow<List<StoryChapter>> = _chapters.asStateFlow()

    private val _mapLocations = MutableStateFlow(loadMapLocations())
    val mapLocations: StateFlow<List<MapLocation>> = _mapLocations.asStateFlow()

    private val _relics = MutableStateFlow(loadRelics())
    val relics: StateFlow<List<ArchaeologyItem>> = _relics.asStateFlow()

    private val _symbols = MutableStateFlow(loadSymbols())
    val symbols: StateFlow<List<RuneSymbol>> = _symbols.asStateFlow()

    private val _buildings = MutableStateFlow(loadBuildings())
    val buildings: StateFlow<List<VillageBuilding>> = _buildings.asStateFlow()

    private val _trophies = MutableStateFlow(loadTrophies())
    val trophies: StateFlow<List<TrophyPrize>> = _trophies.asStateFlow()

    private val _achievements = MutableStateFlow(loadAchievements())
    val achievements: StateFlow<List<Achievement>> = _achievements.asStateFlow()

    private val _journalEntries = MutableStateFlow(loadJournalEntries())
    val journalEntries: StateFlow<List<JournalEntry>> = _journalEntries.asStateFlow()

    private val _recentUnlockedAchievement = MutableStateFlow<Achievement?>(null)
    val recentUnlockedAchievement: StateFlow<Achievement?> = _recentUnlockedAchievement.asStateFlow()

    // --- Profile Actions ---
    fun updateProfileName(newName: String) {
        val updated = _profile.value.copy(name = newName.trim().ifEmpty { "Explorador" })
        _profile.value = updated
        saveProfile(updated)
    }

    fun updateAvatar(avatarId: String) {
        val updated = _profile.value.copy(avatarId = avatarId)
        _profile.value = updated
        saveProfile(updated)
    }

    fun addRewards(coins: Int, xp: Int) {
        val current = _profile.value
        val newXp = current.xp + xp
        val newCoins = current.goldCoins + coins

        val newRank = when {
            newXp >= ExplorerRank.GUARDIAN_CELTA.minXp -> ExplorerRank.GUARDIAN_CELTA
            newXp >= ExplorerRank.SABIO_ROBLE.minXp -> ExplorerRank.SABIO_ROBLE
            newXp >= ExplorerRank.APRENDIZ_BARDO.minXp -> ExplorerRank.APRENDIZ_BARDO
            newXp >= ExplorerRank.RASTREADOR.minXp -> ExplorerRank.RASTREADOR
            else -> ExplorerRank.NOVATO
        }

        val updated = current.copy(goldCoins = newCoins, xp = newXp, rank = newRank)
        _profile.value = updated
        saveProfile(updated)

        // Check rank achievements
        incrementAchievementProgress("ach_rank", newXp)
    }

    // --- Daily Rewards System (Solsticio y Racha Diaria) ---
    fun getDailyRewardsList(): List<DailyReward> {
        return listOf(
            DailyReward(
                day = 1,
                title = "Bendición del Alba Celta",
                lore = "Los primeros rayos del amanecer sobre el castro bendicen tus pasos.",
                iconEmoji = "🌅",
                coins = 10,
                xp = 20
            ),
            DailyReward(
                day = 2,
                title = "Ofrenda de Trigo y Cebada",
                lore = "Grano recién tostado para compartir con los vecinos de la aldea.",
                iconEmoji = "🌾",
                coins = 15,
                xp = 30
            ),
            DailyReward(
                day = 3,
                title = "Agua del Manantial Sagrado",
                lore = "Agua pura de roca que nutre las raíces de los antiguos robles.",
                iconEmoji = "💧",
                coins = 20,
                xp = 45
            ),
            DailyReward(
                day = 4,
                title = "Muérdago Dorado de los Druidas",
                lore = "Rama sagrada cortada con hoz de oro bajo la luna llena.",
                iconEmoji = "🌿",
                coins = 30,
                xp = 60
            ),
            DailyReward(
                day = 5,
                title = "Lingote de Cobre y Estaño",
                lore = "Metales nobles para forjar espadas y broches en el taller del herrero.",
                iconEmoji = "⛏️",
                coins = 45,
                xp = 80
            ),
            DailyReward(
                day = 6,
                title = "Gema de Ámbar Solar",
                lore = "Gema brillante traída por mercaderes que atrapa la luz del mediodía.",
                iconEmoji = "💎",
                coins = 60,
                xp = 100
            ),
            DailyReward(
                day = 7,
                title = "Manto Solar del Solsticio (¡Premio Legendario 3D!)",
                lore = "¡Gran premio por 7 días de racha! Desbloquea el manto ceremonial dorado para tu personaje en 3D.",
                iconEmoji = "☀️",
                coins = 100,
                xp = 150,
                rewardAccessoryId = "acc_daily_sun_cloak",
                rewardAccessoryName = "Manto Solar del Solsticio"
            )
        )
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadDailyRewardState(): DailyRewardState {
        val today = getTodayDateString()
        val lastClaim = prefs.getString("daily_last_claim_date", null)
        val streak = prefs.getInt("daily_streak", 0)
        val claimedCycleSet = prefs.getStringSet("daily_claimed_cycle", emptySet()) ?: emptySet()
        val claimedInts = claimedCycleSet.mapNotNull { it.toIntOrNull() }.toSet()

        if (lastClaim == null) {
            return DailyRewardState(
                currentStreak = 0,
                canClaimToday = true,
                lastClaimDate = null,
                nextDayToClaim = 1,
                claimedDaysInCycle = emptySet()
            )
        }

        if (lastClaim == today) {
            // Already claimed today!
            val currentDay = if (streak == 0) 1 else ((streak - 1) % 7) + 1
            return DailyRewardState(
                currentStreak = streak,
                canClaimToday = false,
                lastClaimDate = lastClaim,
                nextDayToClaim = (currentDay % 7) + 1,
                claimedDaysInCycle = claimedInts
            )
        }

        // Check if yesterday or older
        var isConsecutive = false
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val lastDate = sdf.parse(lastClaim)
            val todayDate = sdf.parse(today)
            if (lastDate != null && todayDate != null) {
                val diffMillis = todayDate.time - lastDate.time
                val diffDays = diffMillis / (1000 * 60 * 60 * 24)
                if (diffDays == 1L) {
                    isConsecutive = true
                }
            }
        } catch (_: Exception) {
            isConsecutive = false
        }

        val effectiveStreak = if (isConsecutive) streak else 0
        val effectiveClaimed = if (isConsecutive) claimedInts else emptySet()
        val nextDay = (effectiveStreak % 7) + 1

        return DailyRewardState(
            currentStreak = effectiveStreak,
            canClaimToday = true,
            lastClaimDate = lastClaim,
            nextDayToClaim = nextDay,
            claimedDaysInCycle = effectiveClaimed
        )
    }

    fun claimDailyReward(): DailyReward? {
        val state = _dailyRewardState.value
        if (!state.canClaimToday) return null

        val rewards = getDailyRewardsList()
        val dayIndex = (state.nextDayToClaim - 1).coerceIn(0, rewards.size - 1)
        val reward = rewards[dayIndex]

        val today = getTodayDateString()
        val newStreak = state.currentStreak + 1
        val newClaimed = (state.claimedDaysInCycle + reward.day).toMutableSet()
        val isLastDayOfCycle = reward.day == 7
        if (isLastDayOfCycle) {
            // Completed 7 days cycle!
            newClaimed.clear()
        }

        // Save preferences
        prefs.edit()
            .putString("daily_last_claim_date", today)
            .putInt("daily_streak", newStreak)
            .putStringSet("daily_claimed_cycle", newClaimed.map { it.toString() }.toSet())
            .apply()

        // Award coins & XP
        addRewards(reward.coins, reward.xp)

        // Award accessory if day 7
        reward.rewardAccessoryId?.let { accId ->
            unlockRewardAccessory(accId)
        }

        val updatedState = DailyRewardState(
            currentStreak = newStreak,
            canClaimToday = false,
            lastClaimDate = today,
            nextDayToClaim = (reward.day % 7) + 1,
            claimedDaysInCycle = if (isLastDayOfCycle) setOf(7) else newClaimed
        )
        _dailyRewardState.value = updatedState
        return reward
    }

    // --- Character Customization & 3D Accessories ---
    fun updateCustomization(newCustom: CharacterCustomization) {
        _customization.value = newCustom
        saveCustomization(newCustom)
        if (_profile.value.avatarId != newCustom.archetype) {
            updateAvatar(newCustom.archetype)
        }
    }

    fun updateArchetype(archetype: String) {
        val updated = _customization.value.copy(archetype = archetype)
        updateCustomization(updated)
    }

    fun updateCharacterTitle(newTitle: String) {
        val updated = _customization.value.copy(title = newTitle.trim().ifEmpty { "Explorador Celta" })
        updateCustomization(updated)
    }

    fun updateSkinTone(index: Int) {
        val updated = _customization.value.copy(skinToneIndex = index.coerceIn(0, 2))
        updateCustomization(updated)
    }

    fun updateHairColor(index: Int) {
        val updated = _customization.value.copy(hairColorIndex = index.coerceIn(0, 3))
        updateCustomization(updated)
    }

    fun updateWarPaint(index: Int) {
        val updated = _customization.value.copy(warPaintIndex = index.coerceIn(0, 3))
        updateCustomization(updated)
    }

    fun buyAccessory(accessoryId: String): Boolean {
        val currentList = _accessories.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == accessoryId }
        if (index != -1 && !currentList[index].isOwned) {
            val item = currentList[index]
            if (_profile.value.goldCoins >= item.price) {
                val newCoins = _profile.value.goldCoins - item.price
                val updatedProf = _profile.value.copy(goldCoins = newCoins)
                _profile.value = updatedProf
                saveProfile(updatedProf)

                currentList[index] = item.copy(isOwned = true)
                _accessories.value = currentList
                saveOwnedAccessories(currentList.filter { it.isOwned }.map { it.id }.toSet())
                addRewards(0, 35) // Bonus XP for acquiring gear
                return true
            }
        }
        return false
    }

    fun equipAccessory(accessoryId: String) {
        val currentList = _accessories.value.toMutableList()
        val targetIndex = currentList.indexOfFirst { it.id == accessoryId }
        if (targetIndex != -1 && currentList[targetIndex].isOwned) {
            val target = currentList[targetIndex]
            for (i in currentList.indices) {
                if (currentList[i].slot == target.slot && currentList[i].isEquipped) {
                    currentList[i] = currentList[i].copy(isEquipped = false)
                }
            }
            currentList[targetIndex] = target.copy(isEquipped = true)
            _accessories.value = currentList

            var custom = _customization.value
            custom = when (target.slot) {
                AccessorySlot.HEAD -> custom.copy(equippedHead = target.id)
                AccessorySlot.BODY -> custom.copy(equippedBody = target.id)
                AccessorySlot.NECK -> custom.copy(equippedNeck = target.id)
                AccessorySlot.HAND -> custom.copy(equippedHand = target.id)
                AccessorySlot.PET -> custom.copy(equippedPet = target.id)
            }
            _customization.value = custom
            saveCustomization(custom)
            saveEquippedAccessories(currentList.filter { it.isEquipped }.map { it.id }.toSet())
        }
    }

    fun unequipAccessory(accessoryId: String) {
        val currentList = _accessories.value.toMutableList()
        val targetIndex = currentList.indexOfFirst { it.id == accessoryId }
        if (targetIndex != -1 && currentList[targetIndex].isEquipped) {
            val target = currentList[targetIndex]
            currentList[targetIndex] = target.copy(isEquipped = false)
            _accessories.value = currentList

            var custom = _customization.value
            custom = when (target.slot) {
                AccessorySlot.HEAD -> if (custom.equippedHead == target.id) custom.copy(equippedHead = null) else custom
                AccessorySlot.BODY -> if (custom.equippedBody == target.id) custom.copy(equippedBody = null) else custom
                AccessorySlot.NECK -> if (custom.equippedNeck == target.id) custom.copy(equippedNeck = null) else custom
                AccessorySlot.HAND -> if (custom.equippedHand == target.id) custom.copy(equippedHand = null) else custom
                AccessorySlot.PET -> if (custom.equippedPet == target.id) custom.copy(equippedPet = null) else custom
            }
            _customization.value = custom
            saveCustomization(custom)
            saveEquippedAccessories(currentList.filter { it.isEquipped }.map { it.id }.toSet())
        }
    }

    fun unlockRewardAccessory(accessoryId: String) {
        val currentList = _accessories.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == accessoryId }
        if (index != -1 && !currentList[index].isOwned) {
            currentList[index] = currentList[index].copy(isOwned = true)
            _accessories.value = currentList
            saveOwnedAccessories(currentList.filter { it.isOwned }.map { it.id }.toSet())
        }
    }

    // --- Story Chapter Completion ---
    fun completeChapter(chapterId: String) {
        val currentList = _chapters.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == chapterId }
        if (index != -1) {
            val completedChapter = currentList[index]
            if (!completedChapter.isCompleted) {
                currentList[index] = completedChapter.copy(isCompleted = true)

                // Unlock next chapter
                if (index + 1 < currentList.size) {
                    currentList[index + 1] = currentList[index + 1].copy(isUnlocked = true)
                }

                _chapters.value = currentList
                saveCompletedChapters(currentList.filter { it.isCompleted }.map { it.id }.toSet())

                // Reward player
                addRewards(completedChapter.rewardCoins, 75)

                // Check achievements
                incrementAchievementProgress("ach_first_story", 1)
                val completedCount = currentList.count { it.isCompleted }
                incrementAchievementProgress("ach_all_stories", completedCount)

                // Unlock Trophy if first story
                if (completedCount == 1) {
                    unlockTrophy("trophy_bardo_horn")
                }
                if (completedCount >= 5) {
                    unlockTrophy("trophy_celtic_crown")
                    unlockRewardAccessory("trophy_crown")
                }
            }
        }
    }

    // --- Map Location Visit ---
    fun markLocationVisited(locationId: String) {
        val currentList = _mapLocations.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == locationId }
        if (index != -1 && !currentList[index].isVisited) {
            currentList[index] = currentList[index].copy(isVisited = true)
            _mapLocations.value = currentList
            saveVisitedLocations(currentList.filter { it.isVisited }.map { it.id }.toSet())
            addRewards(15, 20)

            val visitedCount = currentList.count { it.isVisited }
            incrementAchievementProgress("ach_explorer_map", visitedCount)
            if (visitedCount >= 4) {
                unlockTrophy("trophy_map_compass")
            }
        }
    }

    // --- Archaeology Actions ---
    fun discoverRelic(relicId: String) {
        val currentList = _relics.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == relicId }
        if (index != -1 && !currentList[index].isDiscovered) {
            val item = currentList[index].copy(isDiscovered = true)
            currentList[index] = item
            _relics.value = currentList
            saveDiscoveredRelics(currentList.filter { it.isDiscovered }.map { it.id }.toSet())

            addRewards(60, 50)
            val discoveredCount = currentList.count { it.isDiscovered }
            incrementAchievementProgress("ach_archaeologist", discoveredCount)

            // Unlock specific trophies & wearable accessories
            if (relicId == "relic_torque") {
                unlockTrophy("trophy_gold_torc")
                unlockRewardAccessory("relic_torque")
            }
            if (relicId == "relic_carnyx") {
                unlockTrophy("trophy_carnyx")
                unlockRewardAccessory("relic_carnyx")
            }
            if (relicId == "relic_sword") {
                unlockRewardAccessory("relic_sword")
            }
            if (discoveredCount == currentList.size) {
                unlockTrophy("trophy_gundestrup")
            }
        }
    }

    // --- Village Building ---
    fun buildVillageStructure(buildingId: String): Boolean {
        val currentList = _buildings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == buildingId }
        if (index != -1 && !currentList[index].isBuilt) {
            val building = currentList[index]
            if (_profile.value.goldCoins >= building.cost) {
                // Deduct coins
                val newCoins = _profile.value.goldCoins - building.cost
                _profile.value = _profile.value.copy(goldCoins = newCoins)
                saveProfile(_profile.value)

                currentList[index] = building.copy(isBuilt = true)
                _buildings.value = currentList
                saveBuiltBuildings(currentList.filter { it.isBuilt }.map { it.id }.toSet())

                addRewards(0, 40)
                val builtCount = currentList.count { it.isBuilt }
                incrementAchievementProgress("ach_architect", builtCount)
                if (builtCount >= 5) {
                    unlockTrophy("trophy_castro_fortress")
                }
                return true
            }
        }
        return false
    }

    // --- Symbol Matching Game ---
    fun markSymbolSolved(symbolId: String) {
        val currentList = _symbols.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == symbolId }
        if (index != -1 && !currentList[index].isMatched) {
            currentList[index] = currentList[index].copy(isMatched = true)
            _symbols.value = currentList
            addRewards(20, 25)

            val matchedCount = currentList.count { it.isMatched }
            incrementAchievementProgress("ach_celtic_symbols", matchedCount)
            if (matchedCount >= 5) {
                unlockTrophy("trophy_triskelion")
            }
        }
    }

    fun resetSymbols() {
        val resetList = _symbols.value.map { it.copy(isMatched = false) }
        _symbols.value = resetList
    }

    // --- Journal Custom Notes ---
    fun addCustomJournalEntry(title: String, content: String, sticker: String) {
        val newEntry = JournalEntry(
            id = "user_note_${System.currentTimeMillis()}",
            title = title.ifEmpty { "Nota de Explorador" },
            category = "Mi Cuaderno",
            content = content,
            sticker = sticker,
            isCustom = true,
            timestamp = "Reciente"
        )
        val updated = listOf(newEntry) + _journalEntries.value
        _journalEntries.value = updated
        saveCustomJournalNotes(updated.filter { it.isCustom })

        addRewards(25, 20)
        incrementAchievementProgress("ach_journal_writer", updated.count { it.isCustom })
    }

    fun deleteJournalEntry(id: String) {
        val updated = _journalEntries.value.filterNot { it.id == id }
        _journalEntries.value = updated
        saveCustomJournalNotes(updated.filter { it.isCustom })
    }

    // --- Achievements & Trophies ---
    private fun incrementAchievementProgress(achievementId: String, newProgress: Int) {
        val currentList = _achievements.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == achievementId }
        if (index != -1) {
            val ach = currentList[index]
            val progress = minOf(ach.maxProgress, newProgress)
            val unlockedNow = progress >= ach.maxProgress && !ach.isUnlocked
            val updated = ach.copy(
                currentProgress = progress,
                isUnlocked = ach.isUnlocked || unlockedNow
            )
            currentList[index] = updated
            _achievements.value = currentList
            saveAchievements(currentList)

            if (unlockedNow) {
                _recentUnlockedAchievement.value = updated
                addRewards(ach.rewardCoins, 50)
            }
        }
    }

    fun dismissRecentAchievement() {
        _recentUnlockedAchievement.value = null
    }

    private fun unlockTrophy(trophyId: String) {
        val currentList = _trophies.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == trophyId }
        if (index != -1 && !currentList[index].isUnlocked) {
            currentList[index] = currentList[index].copy(isUnlocked = true)
            _trophies.value = currentList
            saveTrophies(currentList)
            addRewards(30, 40)

            if (trophyId == "trophy_celtic_crown") {
                unlockRewardAccessory("trophy_crown")
            }
            if (trophyId == "trophy_castro_fortress") {
                unlockRewardAccessory("acc_white_stag")
            }
        }
    }

    // --- Persistence Helpers ---
    private fun saveProfile(p: ExplorerProfile) {
        prefs.edit()
            .putString("prof_name", p.name)
            .putString("prof_avatar", p.avatarId)
            .putInt("prof_coins", p.goldCoins)
            .putInt("prof_xp", p.xp)
            .apply()
    }

    private fun loadProfile(): ExplorerProfile {
        val name = prefs.getString("prof_name", "Arturo") ?: "Arturo"
        val avatar = prefs.getString("prof_avatar", "bardo") ?: "bardo"
        val coins = prefs.getInt("prof_coins", 20)
        val xp = prefs.getInt("prof_xp", 0)
        val rank = when {
            xp >= ExplorerRank.GUARDIAN_CELTA.minXp -> ExplorerRank.GUARDIAN_CELTA
            xp >= ExplorerRank.SABIO_ROBLE.minXp -> ExplorerRank.SABIO_ROBLE
            xp >= ExplorerRank.APRENDIZ_BARDO.minXp -> ExplorerRank.APRENDIZ_BARDO
            xp >= ExplorerRank.RASTREADOR.minXp -> ExplorerRank.RASTREADOR
            else -> ExplorerRank.NOVATO
        }
        return ExplorerProfile(name, avatar, coins, xp, rank)
    }

    private fun saveCompletedChapters(ids: Set<String>) {
        prefs.edit().putStringSet("completed_chapters", ids).apply()
    }

    private fun saveVisitedLocations(ids: Set<String>) {
        prefs.edit().putStringSet("visited_locations", ids).apply()
    }

    private fun saveDiscoveredRelics(ids: Set<String>) {
        prefs.edit().putStringSet("discovered_relics", ids).apply()
    }

    private fun saveBuiltBuildings(ids: Set<String>) {
        prefs.edit().putStringSet("built_buildings", ids).apply()
    }

    private fun saveTrophies(list: List<TrophyPrize>) {
        val set = list.filter { it.isUnlocked }.map { it.id }.toSet()
        prefs.edit().putStringSet("unlocked_trophies", set).apply()
    }

    private fun saveAchievements(list: List<Achievement>) {
        val obj = JSONObject()
        for (a in list) {
            val item = JSONObject().apply {
                put("progress", a.currentProgress)
                put("unlocked", a.isUnlocked)
            }
            obj.put(a.id, item)
        }
        prefs.edit().putString("achievements_data", obj.toString()).apply()
    }

    private fun saveCustomJournalNotes(list: List<JournalEntry>) {
        val arr = JSONArray()
        for (e in list) {
            val item = JSONObject().apply {
                put("id", e.id)
                put("title", e.title)
                put("category", e.category)
                put("content", e.content)
                put("sticker", e.sticker)
                put("timestamp", e.timestamp)
            }
            arr.put(item)
        }
        prefs.edit().putString("custom_journal_entries", arr.toString()).apply()
    }

    private fun saveCustomization(c: CharacterCustomization) {
        prefs.edit()
            .putString("cust_archetype", c.archetype)
            .putString("cust_title", c.title)
            .putInt("cust_skin", c.skinToneIndex)
            .putInt("cust_hair", c.hairColorIndex)
            .putInt("cust_paint", c.warPaintIndex)
            .putString("cust_head", c.equippedHead)
            .putString("cust_body", c.equippedBody)
            .putString("cust_neck", c.equippedNeck)
            .putString("cust_hand", c.equippedHand)
            .putString("cust_pet", c.equippedPet)
            .apply()
    }

    private fun loadCustomization(): CharacterCustomization {
        val archetype = prefs.getString("cust_archetype", "bardo") ?: "bardo"
        val title = prefs.getString("cust_title", "Explorador de los Castros") ?: "Explorador de los Castros"
        val skin = prefs.getInt("cust_skin", 0)
        val hair = prefs.getInt("cust_hair", 0)
        val paint = prefs.getInt("cust_paint", 0)
        val head = prefs.getString("cust_head", "acc_crown_oak")
        val body = prefs.getString("cust_body", null)
        val neck = prefs.getString("cust_neck", null)
        val hand = prefs.getString("cust_hand", null)
        val pet = prefs.getString("cust_pet", null)

        return CharacterCustomization(
            archetype = archetype,
            title = title,
            skinToneIndex = skin,
            hairColorIndex = hair,
            warPaintIndex = paint,
            equippedHead = head,
            equippedBody = body,
            equippedNeck = neck,
            equippedHand = hand,
            equippedPet = pet
        )
    }

    private fun saveOwnedAccessories(ids: Set<String>) {
        prefs.edit().putStringSet("owned_accessories", ids).apply()
    }

    private fun saveEquippedAccessories(ids: Set<String>) {
        prefs.edit().putStringSet("equipped_accessories", ids).apply()
    }

    private fun loadAccessories(): List<CelticAccessory> {
        val owned = prefs.getStringSet("owned_accessories", setOf("acc_crown_oak")) ?: setOf("acc_crown_oak")
        val equipped = prefs.getStringSet("equipped_accessories", setOf("acc_crown_oak")) ?: setOf("acc_crown_oak")

        val discoveredRelics = prefs.getStringSet("discovered_relics", emptySet()) ?: emptySet()
        val unlockedTrophies = prefs.getStringSet("unlocked_trophies", emptySet()) ?: emptySet()

        val allOwned = owned.toMutableSet()
        if (discoveredRelics.contains("relic_torque")) allOwned.add("relic_torque")
        if (discoveredRelics.contains("relic_sword")) allOwned.add("relic_sword")
        if (discoveredRelics.contains("relic_carnyx")) allOwned.add("relic_carnyx")
        if (unlockedTrophies.contains("trophy_celtic_crown")) allOwned.add("trophy_crown")
        if (unlockedTrophies.contains("trophy_castro_fortress")) allOwned.add("acc_white_stag")

        return listOf(
            // --- TIENDA: CABEZA ---
            CelticAccessory(
                id = "acc_crown_oak",
                name = "Corona de Roble Sagrado",
                slot = AccessorySlot.HEAD,
                description = "Hojas de roble verde y bellotas doradas bendecidas por los sabios druidas.",
                iconEmoji = "🌿",
                price = 120,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_crown_oak"),
                isEquipped = equipped.contains("acc_crown_oak"),
                primaryColorHex = 0xFF166534
            ),
            CelticAccessory(
                id = "acc_helmet_horns",
                name = "Casco Celta de Bronce",
                slot = AccessorySlot.HEAD,
                description = "Casco de batalla con cornamenta de ciervo y protector nasal de bronce.",
                iconEmoji = "🪖",
                price = 460,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_helmet_horns"),
                isEquipped = equipped.contains("acc_helmet_horns"),
                primaryColorHex = 0xFFD97706
            ),
            CelticAccessory(
                id = "acc_gold_diadem",
                name = "Diadema Real de Oro",
                slot = AccessorySlot.HEAD,
                description = "Corona fina de oro con un ámbar del mar Báltico en el centro.",
                iconEmoji = "👑",
                price = 750,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_gold_diadem"),
                isEquipped = equipped.contains("acc_gold_diadem"),
                primaryColorHex = 0xFFFBBF24
            ),
            CelticAccessory(
                id = "acc_druid_hood",
                name = "Capucha Mística del Bosque",
                slot = AccessorySlot.HEAD,
                description = "Cálida capucha verde tejida con lana y tintes de corteza de abedul.",
                iconEmoji = "🧙‍♂️",
                price = 160,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_druid_hood"),
                isEquipped = equipped.contains("acc_druid_hood"),
                primaryColorHex = 0xFF14532D
            ),

            // --- TIENDA: MANTO Y CAPA ---
            CelticAccessory(
                id = "acc_tartan_cloak",
                name = "Capa Tartán con Fíbula",
                slot = AccessorySlot.BODY,
                description = "Capa ondeante de cuadros rojos y verdes, unida con un broche ceremonial.",
                iconEmoji = "🧣",
                price = 280,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_tartan_cloak"),
                isEquipped = equipped.contains("acc_tartan_cloak"),
                primaryColorHex = 0xFFB91C1C
            ),
            CelticAccessory(
                id = "acc_bronze_armor",
                name = "Coraza de Espirales",
                slot = AccessorySlot.BODY,
                description = "Armadura de bronce grabada a mano con espirales protectoras solares.",
                iconEmoji = "🛡️",
                price = 600,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_bronze_armor"),
                isEquipped = equipped.contains("acc_bronze_armor"),
                primaryColorHex = 0xFFD97706
            ),
            CelticAccessory(
                id = "acc_wolf_fur",
                name = "Piel de Lobo de los Montes",
                slot = AccessorySlot.BODY,
                description = "Manto de piel cálida para resistir el viento frío del invierno.",
                iconEmoji = "🐺",
                price = 220,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_wolf_fur"),
                isEquipped = equipped.contains("acc_wolf_fur"),
                primaryColorHex = 0xFF4B5563
            ),

            // --- TIENDA: CUELLO ---
            CelticAccessory(
                id = "acc_torque_gold",
                name = "Torque de Plata y Oro",
                slot = AccessorySlot.NECK,
                description = "Collar rígido celta trenzado con cabezas de jabalíes en los extremos.",
                iconEmoji = "📿",
                price = 520,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_torque_gold"),
                isEquipped = equipped.contains("acc_torque_gold"),
                primaryColorHex = 0xFFF59E0B
            ),
            CelticAccessory(
                id = "acc_triskel_amulet",
                name = "Amuleto del Trisquel",
                slot = AccessorySlot.NECK,
                description = "Medallón tallado en serpentina verde con el triple giro de la naturaleza.",
                iconEmoji = "🌀",
                price = 70,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_triskel_amulet"),
                isEquipped = equipped.contains("acc_triskel_amulet"),
                primaryColorHex = 0xFF10B981
            ),

            // --- TIENDA: MANO Y ARMAS ---
            CelticAccessory(
                id = "acc_celtic_shield",
                name = "Escudo Celta con Umbo",
                slot = AccessorySlot.HAND,
                description = "Escudo de madera de roble forrado en cuero verde y umbo central de bronce.",
                iconEmoji = "🛡️",
                price = 320,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_celtic_shield"),
                isEquipped = equipped.contains("acc_celtic_shield"),
                primaryColorHex = 0xFF166534
            ),
            CelticAccessory(
                id = "acc_bardo_harp",
                name = "Arpa Gaélica del Bardo",
                slot = AccessorySlot.HAND,
                description = "Pequeña arpa de madera de sauce con cuerdas sonoras para cantar relatos.",
                iconEmoji = "🪕",
                price = 380,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_bardo_harp"),
                isEquipped = equipped.contains("acc_bardo_harp"),
                primaryColorHex = 0xFF78350F
            ),

            // --- TIENDA: COMPAÑEROS ---
            CelticAccessory(
                id = "acc_morrigan_crow",
                name = "Cuervo de la Sabiduría",
                slot = AccessorySlot.PET,
                description = "Fiel cuervo de plumaje oscuro que viaja sobre tu hombro y aletea.",
                iconEmoji = "🦅",
                price = 880,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_morrigan_crow"),
                isEquipped = equipped.contains("acc_morrigan_crow"),
                primaryColorHex = 0xFF1E293B
            ),
            CelticAccessory(
                id = "acc_celtic_hound",
                name = "Sabueso Celta de Caza",
                slot = AccessorySlot.PET,
                description = "Perro noble y leal de pelo rojizo que cuida a su amigo explorador.",
                iconEmoji = "🐕",
                price = 1050,
                source = ItemSource.SHOP,
                isOwned = allOwned.contains("acc_celtic_hound"),
                isEquipped = equipped.contains("acc_celtic_hound"),
                primaryColorHex = 0xFFD97706
            ),

            // --- RECOMPENSA DIARIA EXCLUSIVA (7 DÍAS DE RACHA) ---
            CelticAccessory(
                id = "acc_daily_sun_cloak",
                name = "Manto Solar del Solsticio",
                slot = AccessorySlot.BODY,
                description = "Manto ceremonial dorado tejido en honor al dios solar Lugh. Exclusivo de 7 días de racha.",
                iconEmoji = "☀️",
                price = 0,
                source = ItemSource.REWARD_DAILY,
                sourceRequirement = "Alcanza 7 días de racha en las Recompensas Diarias",
                isOwned = allOwned.contains("acc_daily_sun_cloak"),
                isEquipped = equipped.contains("acc_daily_sun_cloak"),
                primaryColorHex = 0xFFF59E0B
            ),

            // --- RECOMPENSAS HISTÓRICAS DE EXCAVACIÓN Y TROFEOS ---
            CelticAccessory(
                id = "relic_torque",
                name = "Gran Torque de Oro Puro",
                slot = AccessorySlot.NECK,
                description = "¡Auténtico collar sagrado desenterrado en la excavación arqueológica!",
                iconEmoji = "✨",
                price = 0,
                source = ItemSource.REWARD_ARCHAEOLOGY,
                sourceRequirement = "Desentierra el Torque en Arqueología",
                isOwned = allOwned.contains("relic_torque"),
                isEquipped = equipped.contains("relic_torque"),
                primaryColorHex = 0xFFF59E0B
            ),
            CelticAccessory(
                id = "relic_sword",
                name = "Espada Ceremonial de Bronce",
                slot = AccessorySlot.HAND,
                description = "Espada ritual con empuñadura forjada rescatada de la tierra.",
                iconEmoji = "⚔️",
                price = 0,
                source = ItemSource.REWARD_ARCHAEOLOGY,
                sourceRequirement = "Desentierra la Espada en Arqueología",
                isOwned = allOwned.contains("relic_sword"),
                isEquipped = equipped.contains("relic_sword"),
                primaryColorHex = 0xFFE2E8F0
            ),
            CelticAccessory(
                id = "relic_carnyx",
                name = "Carnyx Sagrado con Cabeza de Jabalí",
                slot = AccessorySlot.HAND,
                description = "Trompeta ceremonial gigante de bronce hallada en el yacimiento.",
                iconEmoji = "🎺",
                price = 0,
                source = ItemSource.REWARD_ARCHAEOLOGY,
                sourceRequirement = "Desentierra el Carnyx en Arqueología",
                isOwned = allOwned.contains("relic_carnyx"),
                isEquipped = equipped.contains("relic_carnyx"),
                primaryColorHex = 0xFFD97706
            ),
            CelticAccessory(
                id = "trophy_crown",
                name = "Corona de Robles y Acebo",
                slot = AccessorySlot.HEAD,
                description = "Otorgada únicamente a quienes completan todos los relatos del Bardo.",
                iconEmoji = "🍃",
                price = 0,
                source = ItemSource.REWARD_STORY,
                sourceRequirement = "Completa los 5 Relatos Celtas",
                isOwned = allOwned.contains("trophy_crown"),
                isEquipped = equipped.contains("trophy_crown"),
                primaryColorHex = 0xFF166534
            ),
            CelticAccessory(
                id = "acc_white_stag",
                name = "Ciervo Blanco de Leyenda",
                slot = AccessorySlot.PET,
                description = "Mágica criatura de los bosques profundos que acompaña a los guardianes.",
                iconEmoji = "🦌",
                price = 0,
                source = ItemSource.REWARD_TROPHY,
                sourceRequirement = "Construye tu Castro Celta",
                isOwned = allOwned.contains("acc_white_stag"),
                isEquipped = equipped.contains("acc_white_stag"),
                primaryColorHex = 0xFFF8FAFC
            )
        )
    }


    // --- Content Initialization ---
    private fun loadChapters(): List<StoryChapter> {
        val completed = prefs.getStringSet("completed_chapters", emptySet()) ?: emptySet()
        val baseChapters = listOf(
            StoryChapter(
                id = "ch_1",
                number = 1,
                title = "El Despertar de la Edad del Hierro",
                subtitle = "El nacimiento de los pueblos celtas en Europa",
                region = "Europa Central (Hallstatt)",
                iconEmoji = "⚒️",
                chronologicalEra = "Orígenes del Hierro",
                timePeriod = "Siglo VIII a.C.",
                summary = "Viaja en el tiempo al origen de los celtas, cuando descubrieron el secreto de forjar el hierro y las minas de sal en el corazón de Europa.",
                paragraphs = listOf(
                    "¡Hola joven explorador celta! Imagina viajar 2.800 años atrás en una máquina del tiempo. En las montañas de Europa Central, un grupo de pueblos descubrió algo revolucionario: ¡el trabajo del hierro!",
                    "Hasta entonces usaban bronce, que era blando. Pero con el hierro forjaron herramientas fuertes para cultivar la tierra y proteger sus aldeas. Este primer periodo se llama la cultura de Hallstatt.",
                    "También extraían sal de profundas minas en la roca. Para ellos, la sal era como el oro blanco: servía para conservar la comida durante los largos inviernos nevados y comerciar con tribus vecinas.",
                    "Poco a poco, con sus carros de caballos y sus túnicas tejidas a mano, las familias celtas se expandieron por Francia, las Islas Británicas y la Península Ibérica, llevando consigo sus tradiciones y canciones."
                ),
                quiz = StoryChoice(
                    question = "¿Qué metal duro y resistente comenzaron a forjar los celtas en sus inicios?",
                    options = listOf("El Hierro", "El Plástico", "El Aluminio", "El Cristal"),
                    correctIndex = 0,
                    explanation = "¡Bravo! El dominio del hierro permitió a los celtas crear mejores herramientas y comenzar su gran expansión por Europa."
                ),
                rewardCoins = 20,
                isCompleted = completed.contains("ch_1"),
                isUnlocked = true
            ),
            StoryChapter(
                id = "ch_2",
                number = 2,
                title = "La Vida en el Castro Fortificado",
                subtitle = "Murallas de piedra, casas redondas y el clan",
                region = "Hispania (Galicia y Norte)",
                iconEmoji = "🛖",
                chronologicalEra = "Cultura Castreña",
                timePeriod = "Siglos V-IV a.C.",
                summary = "Descubre cómo los niños y familias celtas vivían en castros sobre las colinas, con casas redondas de paja y muros protectores.",
                paragraphs = listOf(
                    "Cuando los celtas llegaron a las costas y colinas verdes, construyeron poblados muy especiales llamados 'castros'. ¡Eran aldeas mágicas protegidas por murallas de piedra redondas!",
                    "Las casas no eran cuadradas como las nuestras, sino circulares. Las paredes eran de piedra y barro, y los techos cónicos de paja y retama resistían las tormentas más fuertes del Atlántico.",
                    "Un niño de ocho años como Bran o Aura se levantaba con el canto del gallo: ayudaba a llevar a las cabras y ovejas al prado, recolectaba moras y aprendía a hilar lana de colores.",
                    "Por la noche, toda la familia se reunía junto a la 'lareira' (el fuego central) para comer pan recién horneado con miel y escuchar las aventuras que narraba el abuelo del clan."
                ),
                quiz = StoryChoice(
                    question = "¿Qué forma tenían las casas tradicionales dentro de un castro celta?",
                    options = listOf("Circulares (redondas)", "Triangulares", "Cuadradas con chimenea", "Hexagonales"),
                    correctIndex = 0,
                    explanation = "¡Exacto! Las casas celtas eran redondas para aprovechar mejor el calor del fuego central y resistir los vientos."
                ),
                rewardCoins = 25,
                isCompleted = completed.contains("ch_2"),
                isUnlocked = completed.contains("ch_1")
            ),
            StoryChapter(
                id = "ch_3",
                number = 3,
                title = "Los Sabios Druidas y la Fiesta de Samhain",
                subtitle = "La magia del roble, las estrellas y el origen de Halloween",
                region = "Galia e Irlanda",
                iconEmoji = "🌿",
                chronologicalEra = "Apogeo Espiritual",
                timePeriod = "Siglo III a.C.",
                summary = "Acompaña a los druidas en los bosques sagrados de robles y averigua cómo nació la fiesta de Samhain.",
                paragraphs = listOf(
                    "En los bosques sagrados vivían los druidas, los maestros más respetados de la comunidad. No solo eran sacerdotes: eran los médicos, maestros, astrónomos y guardianes de la memoria.",
                    "Para los celtas, el árbol más venerado era el Roble centenario. Cuando crecía en él una planta mágica llamada muérdago, el druida mayor la cortaba con una hoz de oro puro.",
                    "La noche del 31 de octubre celebraban 'Samhain', el fin de la cosecha y el inicio del año nuevo celta. Apagaban todos los fuegos de la aldea y encendían una gigantesca Hoguera Sagrada en la cima de la colina.",
                    "De ese fuego nuevo cada familia encendía una antorcha para llevar bendición y calor a su hogar. Se ponían máscaras divertidas para ahuyentar a los duendes traviesos... ¡así nació lo que hoy llamamos Halloween!"
                ),
                quiz = StoryChoice(
                    question = "¿Qué fiesta celta celebraba el final de las cosechas y dio origen a Halloween?",
                    options = listOf("Samhain", "San Valentín", "El Carnaval Romano", "La Fiesta del Río"),
                    correctIndex = 0,
                    explanation = "¡Increíble! Samhain marcaba el año nuevo celta y el agradecimiento a la Madre Tierra por los alimentos cosechados."
                ),
                rewardCoins = 30,
                isCompleted = completed.contains("ch_3"),
                isUnlocked = completed.contains("ch_2")
            ),
            StoryChapter(
                id = "ch_4",
                number = 4,
                title = "El Trueque y la Ruta de los Metales",
                subtitle = "Mercados de ganado, estaño, ámbar y oro",
                region = "Britania y Europa Central",
                iconEmoji = "⚖️",
                chronologicalEra = "Comercio y Expansión",
                timePeriod = "Siglo II a.C.",
                summary = "Aprende cómo los celtas hacían cuentas sin dinero de papel: ¡utilizaban el ingenioso sistema del trueque con vacas, grano y hermosos torques de oro!",
                paragraphs = listOf(
                    "En el mundo celta no existían billetes ni tarjetas. ¿Cómo compraban ropa, espadas o vasijas de cerámica? ¡A través del 'Trueque' y el cálculo matemático con animales y granos!",
                    "Una vaca lechera podía equivaler a tres ovejas, y una oveja valía cuatro sacos de trigo tostado. Los niños celtas aprendían a contar utilizando pequeñas piedras de río pulidas y muescas en varas de madera.",
                    "Viajeros y mercaderes celtas recorrían grandes distancias en carros con ruedas de hierro para llevar estaño de Britania y ámbar del Mar Báltico.",
                    "En las ferias de los castros, los orfebres vendían el codiciado 'Torque': collares dorados abiertos con extremos de cabezas de ciervo que lucían los héroes y líderes del clan."
                ),
                quiz = StoryChoice(
                    question = "¿Cómo conseguían los celtas cosas en el mercado antes de usar monedas de papel?",
                    options = listOf("Con el Trueque (intercambio justo)", "Con tarjetas electrónicas", "Pidiendo por teléfono", "Esperando regalos del cielo"),
                    correctIndex = 0,
                    explanation = "¡Muy bien! Mediante el trueque intercambiaban trigo, ganado, lana y metales calculando el valor de cada producto."
                ),
                rewardCoins = 35,
                isCompleted = completed.contains("ch_4"),
                isUnlocked = completed.contains("ch_3")
            ),
            StoryChapter(
                id = "ch_5",
                number = 5,
                title = "Viriato y la Defensa de las Colinas",
                subtitle = "La gran resistencia frente al avance de Roma",
                region = "Hispania Celtíbera",
                iconEmoji = "⚔️",
                chronologicalEra = "Las Guerras Celtíberas",
                timePeriod = "Siglo II - I a.C.",
                summary = "Conoce a Viriato y a los valientes defensores de Numancia, quienes protegieron sus castros y colinas con astucia y valentía.",
                paragraphs = listOf(
                    "Hacia el año 150 a.C., una poderosa civilización del sur comenzó a avanzar con grandes legiones disciplinadas: ¡el Imperio Romano!",
                    "En Hispania, los pueblos celtas y celtíberos amaban profundamente su libertad. Uno de sus líderes más legendarios fue Viriato, un pastor que conocía cada risco, cueva y bosque de las montañas.",
                    "En lugar de enfrentarse en campo abierto a los grandes ejércitos, Viriato inventó tácticas de sorpresa: aparecía rápidamente entre la niebla y desaparecía en las colinas antes de que los soldados enemigos pudieran reaccionar.",
                    "En Numancia, otra famosa ciudad celtíbera, los habitantes resistieron durante años con un valor asombroso que los propios generales romanos reconocieron con respeto y admiración."
                ),
                quiz = StoryChoice(
                    question = "¿Qué líder celta e hispano fue famoso por defender sus colinas con astucia frente a Roma?",
                    options = listOf("Viriato", "Alejandro Magno", "Napoleón", "Hércules"),
                    correctIndex = 0,
                    explanation = "¡Magnífico! Viriato demostró que el conocimiento de la naturaleza y el coraje podían plantar cara a los ejércitos más poderosos."
                ),
                rewardCoins = 40,
                isCompleted = completed.contains("ch_5"),
                isUnlocked = completed.contains("ch_4")
            ),
            StoryChapter(
                id = "ch_6",
                number = 6,
                title = "Boudica: La Reina de la Llama Libre",
                subtitle = "La reina celta que unió a los clanes de Britania",
                region = "Britania (Reino Unido)",
                iconEmoji = "🛡️",
                chronologicalEra = "La Lucha por Britania",
                timePeriod = "Siglo I d.C. (Año 60 d.C.)",
                summary = "Descubre la épica historia de Boudica, la reina guerrera de pelo rojizo que condujo a su pueblo en defensa de sus derechos.",
                paragraphs = listOf(
                    "En la isla de Britania, las mujeres celtas tenían los mismos derechos que los hombres: podían ser propietarias, diplomáticas, juezas y grandes líderes de guerra.",
                    "La más famosa de todas fue la reina Boudica, gobernante de la tribu de los Iceni. Tenía una larga melena roja, ojos decididos, una túnica de vivos colores y un impresionante torque de oro.",
                    "Cuando los gobernadores romanos intentaron despojar a sus hijas y a su pueblo de sus tierras sagradas, Boudica subió a su carro de guerra tirado por veloces corceles y unió a las tribus vecinas.",
                    "Aunque el imperio era gigantesco, la valentía de Boudica se convirtió en una leyenda eterna de honor, justicia y amor inquebrantable por su tierra natal."
                ),
                quiz = StoryChoice(
                    question = "¿Qué vehículo conducía la reina celta Boudica durante sus batallas?",
                    options = listOf("Un carro de guerra tirado por caballos", "Un tanque acorazado", "Una bicicleta veloz", "Un barco pirata con velas"),
                    correctIndex = 0,
                    explanation = "¡Genial! Los celtas eran expertos aurigas y conducían carros ligeros de dos ruedas con gran destreza."
                ),
                rewardCoins = 45,
                isCompleted = completed.contains("ch_6"),
                isUnlocked = completed.contains("ch_5")
            ),
            StoryChapter(
                id = "ch_7",
                number = 7,
                title = "La Huella Eterna: El Legado Celta",
                subtitle = "La unión con Roma y lo que nos queda hoy en día",
                region = "Europa y la Actualidad",
                iconEmoji = "🌟",
                chronologicalEra = "El Legado Inmortal",
                timePeriod = "Siglo I d.C. hasta Hoy",
                summary = "Comprende cómo los celtas y romanos se fusionaron y qué maravillas celtas seguimos disfrutando hoy: gaitas, nombres de ríos y cuentos.",
                paragraphs = listOf(
                    "Con el paso del tiempo, el mundo celta y el mundo romano se encontraron y comenzaron a convivir. Los romanos trajeron sus calzadas de piedra y sus acueductos, y los celtas les enseñaron la forja del hierro, los toneles de madera y los pantalones cómodos.",
                    "Muchas de nuestras ciudades y ríos conservan aún sus nombres celtas originales: palabras como 'Segovia' (lugar de victoria), 'Lugo' (en honor al dios Lugus) o el río Támesis en Londres.",
                    "La música de las gaitas en Galicia, Asturias, Irlanda y Escocia sigue tocando las mismas melodías mágicas que los bardos componían junto a la hoguera hace dos mil años.",
                    "¡Felicidades explorador! Has completado el viaje por toda la historia celta. Recuerda siempre su lección más valiosa: respetar a los animales, cuidar los árboles y proteger la libertad con una sonrisa valiente."
                ),
                quiz = StoryChoice(
                    question = "¿Qué instrumento musical tradicional sigue vivo hoy gracias a la herencia celta?",
                    options = listOf("La Gaita celta", "La Guitarra eléctrica", "La Batería electrónica", "El Sintetizador"),
                    correctIndex = 0,
                    explanation = "¡Enhorabuena! La gaita y los sones celtas siguen alegrando fiestas y romerías en Galicia, Escocia, Irlanda y Asturias."
                ),
                rewardCoins = 50,
                isCompleted = completed.contains("ch_7"),
                isUnlocked = completed.contains("ch_6")
            )
        )
        return baseChapters
    }

    private fun loadMapLocations(): List<MapLocation> {
        val visited = prefs.getStringSet("visited_locations", emptySet()) ?: emptySet()
        return listOf(
            MapLocation(
                id = "loc_barona",
                name = "Castro de Baroña",
                ancientName = "Castrum Baronia",
                region = RegionTag.HISPANIA,
                xPercent = 0.20f,
                yPercent = 0.72f,
                icon = "🌊",
                shortFact = "Aldea costera fortificada en Galicia con vistas al océano.",
                fullStory = "Construido sobre una península rocosa que se adentra en el Atlántico, los celtas de Baroña vivían de la pesca, el marisqueo y la ganadería sin necesidad de murallas gigantes porque el mar los protegía.",
                connectedChapterId = "ch_1",
                connectedMinigame = "game_village",
                isVisited = visited.contains("loc_barona")
            ),
            MapLocation(
                id = "loc_broceliande",
                name = "Bosque de Brocelianda",
                ancientName = "Brecilien",
                region = RegionTag.GALIA,
                xPercent = 0.35f,
                yPercent = 0.50f,
                icon = "🌳",
                shortFact = "El bosque sagrado de los robles gigantes y manantiales puros.",
                fullStory = "En este místico bosque de Bretaña los druidas se reunían para celebrar el solsticio de verano y cortar el muérdago sagrado con sus hoces de oro.",
                connectedChapterId = "ch_2",
                connectedMinigame = "game_symbols",
                isVisited = visited.contains("loc_broceliande")
            ),
            MapLocation(
                id = "loc_hallstatt",
                name = "Minas de Hallstatt",
                ancientName = "Hal",
                region = RegionTag.CENTROEUROPA,
                xPercent = 0.65f,
                yPercent = 0.55f,
                icon = "⛏️",
                shortFact = "La cuna del oro blanco: minas de sal y forja de bronce.",
                fullStory = "Aquí comenzó la Edad del Hierro celta. La sal marina subterránea permitía conservar los alimentos durante el gélido invierno y comerciar con toda Europa.",
                connectedChapterId = "ch_3",
                connectedMinigame = "game_archaeology",
                isVisited = visited.contains("loc_hallstatt")
            ),
            MapLocation(
                id = "loc_bibracte",
                name = "Oppidum de Bibracte",
                ancientName = "Bibracte Capital",
                region = RegionTag.GALIA,
                xPercent = 0.48f,
                yPercent = 0.56f,
                icon = "🏰",
                shortFact = "Gran ciudad celta en la cumbre del monte Beuvray.",
                fullStory = "Un oppidum con cientos de talleres de orfebres, herreros y esmaltadores que creaban las mejores joyas de Europa occidental.",
                connectedChapterId = "ch_3",
                connectedMinigame = "game_village",
                isVisited = visited.contains("loc_bibracte")
            ),
            MapLocation(
                id = "loc_tara",
                name = "Colina Sagrada de Tara",
                ancientName = "Teamhair na Rí",
                region = RegionTag.IRLANDA,
                xPercent = 0.22f,
                yPercent = 0.28f,
                icon = "👑",
                shortFact = "El trono ceremonial de los reyes de Irlanda.",
                fullStory = "En la colina de Tara se alza la mítica 'Piedra del Destino' (Lia Fáil). Se decía que rugía de alegría cuando un rey de buen corazón la tocaba.",
                connectedChapterId = "ch_4",
                connectedMinigame = "game_quiz",
                isVisited = visited.contains("loc_tara")
            ),
            MapLocation(
                id = "loc_tintagel",
                name = "Acantilados de Cornualles",
                ancientName = "Kernow",
                region = RegionTag.BRITANIA,
                xPercent = 0.27f,
                yPercent = 0.38f,
                icon = "🛡️",
                shortFact = "Tierra de bardos, minas de estaño y la reina Boudica.",
                fullStory = "Poblado por diestros marineros y mineros del estaño necesario para crear bronce. Aquí los bardos cantaban epopeyas bajo las estrellas.",
                connectedChapterId = "ch_5",
                connectedMinigame = "game_quiz",
                isVisited = visited.contains("loc_tintagel")
            )
        )
    }

    private fun loadRelics(): List<ArchaeologyItem> {
        val discovered = prefs.getStringSet("discovered_relics", emptySet()) ?: emptySet()
        return listOf(
            ArchaeologyItem(
                id = "relic_torque",
                name = "Gran Torque de Oro Puro",
                era = "Siglo I a.C.",
                material = "Oro con hilos trenzados",
                funFact = "Pesa casi un kilogramo de oro macizo. Los artesanos celtas retorcían decenas de hilos de oro a mano.",
                iconEmoji = "👑",
                targetCells = listOf(5, 6, 9, 10),
                isDiscovered = discovered.contains("relic_torque")
            ),
            ArchaeologyItem(
                id = "relic_carnyx",
                name = "Trompeta Carnyx de Bronce",
                era = "Siglo II a.C.",
                material = "Bronce martilleado",
                funFact = "Medía casi dos metros de alto y terminaba en una feroz cabeza de jabalí con lengua articulada que se movía al soplar.",
                iconEmoji = "🎺",
                targetCells = listOf(1, 2, 5, 9),
                isDiscovered = discovered.contains("relic_carnyx")
            ),
            ArchaeologyItem(
                id = "relic_sword",
                name = "Espada Ceremonial con Empuñadura",
                era = "Cultura de La Tène",
                material = "Hierro forjado y bronce",
                funFact = "La empuñadura tiene forma humana antropomorfa para dar fuerza y precisión en la defensa.",
                iconEmoji = "⚔️",
                targetCells = listOf(0, 4, 8, 12),
                isDiscovered = discovered.contains("relic_sword")
            ),
            ArchaeologyItem(
                id = "relic_cauldron",
                name = "Caldero Mágico Plateado",
                era = "Siglo I a.C.",
                material = "Plata con relieves mitológicos",
                funFact = "Inspirado en el Caldero de Gundestrup; muestra al dios Cernunnos rodeado de ciervos y lobos del bosque.",
                iconEmoji = "🍲",
                targetCells = listOf(6, 7, 10, 11),
                isDiscovered = discovered.contains("relic_cauldron")
            ),
            ArchaeologyItem(
                id = "relic_brooch",
                name = "Broche Celtíbero de Fíbula",
                era = "Siglo IV a.C.",
                material = "Bronce con esmalte rojo",
                funFact = "Los celtas no usaban botones para sus capas de lana; ¡las sujetaban con estas elegantes fíbulas de aguja!",
                iconEmoji = "🧷",
                targetCells = listOf(2, 3, 6, 7),
                isDiscovered = discovered.contains("relic_brooch")
            ),
            ArchaeologyItem(
                id = "relic_coin",
                name = "Moneda del Caballo Solar",
                era = "Siglo II a.C.",
                material = "Electrum (aleación de oro y plata)",
                funFact = "Representa a Epona o al caballo solar corriendo bajo las estrellas. No solo era dinero, sino un amuleto.",
                iconEmoji = "🪙",
                targetCells = listOf(10, 11, 14, 15),
                isDiscovered = discovered.contains("relic_coin")
            )
        )
    }

    private fun loadSymbols(): List<RuneSymbol> {
        return listOf(
            RuneSymbol(
                id = "sym_triskel",
                name = "Triskelion (Trisquel)",
                symbolChar = "🌀",
                meaning = "Tierra, Agua y Aire en movimiento continuo",
                lore = "El símbolo más famoso del arte celta. Sus tres espirales representan el ciclo eterno de la vida y la armonía con los elementos."
            ),
            RuneSymbol(
                id = "sym_triquetra",
                name = "Triqueta Sagrada",
                symbolChar = "♾️",
                meaning = "Protección y unión eterna sin principio ni fin",
                lore = "Un nudo continuo que no puede romperse. Simboliza la mente, el cuerpo y el espíritu conectados."
            ),
            RuneSymbol(
                id = "sym_tree",
                name = "Crann Bethadh (Árbol de la Vida)",
                symbolChar = "🌳",
                meaning = "Conexión entre las raíces terrenales y las estrellas",
                lore = "Para los celtas los árboles eran los guardianes del mundo. Sus raíces bebían de la tierra profunda y sus ramas tocaban el cielo."
            ),
            RuneSymbol(
                id = "sym_cross",
                name = "Rueda Solar Celta",
                symbolChar = "☀️",
                meaning = "Las cuatro estaciones y el paso del sol",
                lore = "Una cruz dentro de una circunferencia que marcaba los solsticios y equinoccios para sembrar y cosechar con éxito."
            ),
            RuneSymbol(
                id = "sym_spiral",
                name = "Espiral Doble de la Sabiduría",
                symbolChar = "🔄",
                meaning = "El viaje del aprendizaje y la memoria",
                lore = "Los bardos trazaban esta espiral en sus tablillas para recordar que siempre se puede aprender algo nuevo cada día."
            )
        )
    }

    private fun loadBuildings(): List<VillageBuilding> {
        val built = prefs.getStringSet("built_buildings", emptySet()) ?: emptySet()
        return listOf(
            VillageBuilding(
                id = "bld_palloza",
                name = "Casa Redonda (Palloza)",
                cost = 60,
                icon = "🛖",
                description = "Vivienda circular con zócalo de piedra y tejado cónico de paja aislante.",
                isBuilt = built.contains("bld_palloza")
            ),
            VillageBuilding(
                id = "bld_wall",
                name = "Muralla de Piedra del Castro",
                cost = 120,
                icon = "🧱",
                description = "Robusto parapeto defensivo que resguarda a las familias en la cima de la colina.",
                isBuilt = built.contains("bld_wall")
            ),
            VillageBuilding(
                id = "bld_granary",
                name = "Hórreo y Granero Comunal",
                cost = 85,
                icon = "🌾",
                description = "Almacén elevado del suelo sobre pilotes para proteger la cebada y las nueces de la humedad.",
                isBuilt = built.contains("bld_granary")
            ),
            VillageBuilding(
                id = "bld_forge",
                name = "Taller del Herrero Forjador",
                cost = 160,
                icon = "⚒️",
                description = "Fuelle de cuero y yunque de piedra donde nacen herramientas de hierro y hermosos torques.",
                isBuilt = built.contains("bld_forge")
            ),
            VillageBuilding(
                id = "bld_fire",
                name = "Hoguera Sagrada del Bardo",
                cost = 90,
                icon = "🔥",
                description = "El corazón de la aldea donde se cantan epopeyas al son del arpa bajo las estrellas.",
                isBuilt = built.contains("bld_fire")
            ),
            VillageBuilding(
                id = "bld_oak",
                name = "Bosquecillo Sagrado de Encinas",
                cost = 200,
                icon = "🌲",
                description = "Espacio de serenidad donde las lechuzas y los ciervos pasean en armonía con los druidas.",
                isBuilt = built.contains("bld_oak")
            )
        )
    }

    private fun loadTrophies(): List<TrophyPrize> {
        val unlocked = prefs.getStringSet("unlocked_trophies", emptySet()) ?: emptySet()
        return listOf(
            TrophyPrize(
                id = "trophy_bardo_horn",
                title = "Cuerno del Joven Bardo",
                description = "Otorgado al completar tu primer relato histórico celta con éxito.",
                iconEmoji = "📯",
                rarity = "Común",
                isUnlocked = unlocked.contains("trophy_bardo_horn")
            ),
            TrophyPrize(
                id = "trophy_gold_torc",
                title = "Torque Real de Oro",
                description = "Desenterrado en la excavación arqueológica. Símbolo de los reyes.",
                iconEmoji = "👑",
                rarity = "Raro",
                isUnlocked = unlocked.contains("trophy_gold_torc")
            ),
            TrophyPrize(
                id = "trophy_triskelion",
                title = "Medallón Triskelion Sagrado",
                description = "Por dominar todos los símbolos ancestrales celtas en el taller.",
                iconEmoji = "🌀",
                rarity = "Raro",
                isUnlocked = unlocked.contains("trophy_triskelion")
            ),
            TrophyPrize(
                id = "trophy_carnyx",
                title = "Carnyx de la Colina",
                description = "La trompeta con cabeza de jabalí que resonaba en los valles.",
                iconEmoji = "🎺",
                rarity = "Raro",
                isUnlocked = unlocked.contains("trophy_carnyx")
            ),
            TrophyPrize(
                id = "trophy_map_compass",
                title = "Brújula de los Cuatro Vientos",
                description = "Por explorar y viajar a cuatro sitios históricos del mapa ilustrado.",
                iconEmoji = "🧭",
                rarity = "Raro",
                isUnlocked = unlocked.contains("trophy_map_compass")
            ),
            TrophyPrize(
                id = "trophy_castro_fortress",
                title = "Insignia del Castro Inexpugnable",
                description = "Por construir casi toda la aldea celta con tus monedas de oro.",
                iconEmoji = "🏰",
                rarity = "Legendario",
                isUnlocked = unlocked.contains("trophy_castro_fortress")
            ),
            TrophyPrize(
                id = "trophy_gundestrup",
                title = "Gran Caldero de Plata",
                description = "Por desenterrar y rescatar todos los tesoros arqueológicos.",
                iconEmoji = "🍲",
                rarity = "Legendario",
                isUnlocked = unlocked.contains("trophy_gundestrup")
            ),
            TrophyPrize(
                id = "trophy_celtic_crown",
                title = "Corona de Robles y Acebo",
                description = "Por completar todos los relatos de la historia de los celtas.",
                iconEmoji = "🌿",
                rarity = "Legendario",
                isUnlocked = unlocked.contains("trophy_celtic_crown")
            )
        )
    }

    private fun loadAchievements(): List<Achievement> {
        val jsonStr = prefs.getString("achievements_data", null)
        val data = if (jsonStr != null) JSONObject(jsonStr) else JSONObject()

        fun getSavedProgress(id: String, defaultProg: Int, defaultUnlocked: Boolean): Pair<Int, Boolean> {
            if (data.has(id)) {
                val item = data.getJSONObject(id)
                return Pair(item.optInt("progress", defaultProg), item.optBoolean("unlocked", defaultUnlocked))
            }
            return Pair(defaultProg, defaultUnlocked)
        }

        val a1 = getSavedProgress("ach_first_story", 0, false)
        val a2 = getSavedProgress("ach_all_stories", 0, false)
        val a3 = getSavedProgress("ach_explorer_map", 0, false)
        val a4 = getSavedProgress("ach_archaeologist", 0, false)
        val a5 = getSavedProgress("ach_celtic_symbols", 0, false)
        val a6 = getSavedProgress("ach_architect", 0, false)
        val a7 = getSavedProgress("ach_journal_writer", 0, false)
        val a8 = getSavedProgress("ach_quiz_master", 0, false)

        return listOf(
            Achievement(
                id = "ach_first_story",
                title = "Primer Relato del Bardo",
                description = "Completa tu primer relato narrado y responde su pregunta.",
                iconEmoji = "📖",
                currentProgress = a1.first,
                maxProgress = 1,
                rewardCoins = 20,
                isUnlocked = a1.second
            ),
            Achievement(
                id = "ach_all_stories",
                title = "Gran Cronista Celta",
                description = "Escucha y completa los 5 relatos de la historia celta.",
                iconEmoji = "📜",
                currentProgress = a2.first,
                maxProgress = 5,
                rewardCoins = 60,
                isUnlocked = a2.second
            ),
            Achievement(
                id = "ach_explorer_map",
                title = "Viajero de los Valles",
                description = "Visita y descubre 5 ubicaciones en el mapa ilustrado.",
                iconEmoji = "🗺️",
                currentProgress = a3.first,
                maxProgress = 5,
                rewardCoins = 35,
                isUnlocked = a3.second
            ),
            Achievement(
                id = "ach_archaeologist",
                title = "Arqueólogo Intrépido",
                description = "Desentierra 4 reliquias ocultas bajo la tierra.",
                iconEmoji = "🔍",
                currentProgress = a4.first,
                maxProgress = 4,
                rewardCoins = 40,
                isUnlocked = a4.second
            ),
            Achievement(
                id = "ach_celtic_symbols",
                title = "Maestro de las Runas",
                description = "Empareja 5 símbolos celtas y conoce su sabiduría.",
                iconEmoji = "✨",
                currentProgress = a5.first,
                maxProgress = 5,
                rewardCoins = 30,
                isUnlocked = a5.second
            ),
            Achievement(
                id = "ach_architect",
                title = "Arquitecto del Castro",
                description = "Construye 5 estructuras para tu aldea celta.",
                iconEmoji = "🛖",
                currentProgress = a6.first,
                maxProgress = 5,
                rewardCoins = 50,
                isUnlocked = a6.second
            ),
            Achievement(
                id = "ach_journal_writer",
                title = "Diario de Campo",
                description = "Escribe al menos 3 notas personales en tu diario de explorador.",
                iconEmoji = "✍️",
                currentProgress = a7.first,
                maxProgress = 3,
                rewardCoins = 25,
                isUnlocked = a7.second
            ),
            Achievement(
                id = "ach_quiz_master",
                title = "Sabio de la Tribu",
                description = "Acierta 6 preguntas en el Desafío del Bardo.",
                iconEmoji = "⭐",
                currentProgress = a8.first,
                maxProgress = 6,
                rewardCoins = 45,
                isUnlocked = a8.second
            )
        )
    }

    fun reportQuizMasterScore(score: Int) {
        incrementAchievementProgress("ach_quiz_master", score)
    }

    private fun loadJournalEntries(): List<JournalEntry> {
        val initialCuriosities = listOf(
            JournalEntry(
                id = "cur_1",
                title = "¿Pantalones de cuadros hace 2.500 años?",
                category = "Curiosidad",
                content = "¡Sí! Mientras los romanos y griegos vestían túnicas, los celtas inventaron los pantalones de lana llamados 'braccae' para protegerse del frío y montar a caballo con comodidad.",
                sticker = "👖"
            ),
            JournalEntry(
                id = "cur_2",
                title = "Las casas circulares eran más calientes",
                category = "Arquitectura",
                content = "Las casas redondas no tenían esquinas frías donde se acumulara el viento, y el fuego central repartía el calor por igual a toda la familia mientras dormían sobre camas de heno seco.",
                sticker = "🛖"
            ),
            JournalEntry(
                id = "cur_3",
                title = "Los bardos tenían memoria prodigiosa",
                category = "Historia",
                content = "Como los celtas no solían escribir sus relatos sagrados, los bardos entrenaban durante 20 años para memorizar miles de versos, canciones y leyes históricas.",
                sticker = "🪕"
            ),
            JournalEntry(
                id = "cur_4",
                title = "El jabalí era el animal más valiente",
                category = "Naturaleza",
                content = "En las leyendas celtas, el jabalí salvaje representaba la tenacidad indomable y el valor en el bosque. Por eso decoraban sus trompetas Carnyx y sus escudos con su figura.",
                sticker = "🐗"
            ),
            JournalEntry(
                id = "cur_5",
                title = "Pelo rubio teñido con agua de cal",
                category = "Moda Antigua",
                content = "A los guerreros galos les gustaba peinarse hacia atrás usando agua con cal natural para aclarar su pelo a un rubio brillante que parecía melena de león.",
                sticker = "🦁"
            )
        )

        // Load custom notes from prefs
        val customJsonStr = prefs.getString("custom_journal_entries", null)
        val customList = mutableListOf<JournalEntry>()
        if (customJsonStr != null) {
            try {
                val arr = JSONArray(customJsonStr)
                for (i in 0 until arr.length()) {
                    val obj = arr.getJSONObject(i)
                    customList.add(
                        JournalEntry(
                            id = obj.optString("id"),
                            title = obj.optString("title"),
                            category = obj.optString("category"),
                            content = obj.optString("content"),
                            sticker = obj.optString("sticker"),
                            isCustom = true,
                            timestamp = obj.optString("timestamp")
                        )
                    )
                }
            } catch (e: Exception) {
                // ignore
            }
        }

        return customList + initialCuriosities
    }

    fun getQuizQuestions(): List<QuizQuestion> {
        return listOf(
            QuizQuestion(
                id = 1,
                question = "¿Qué tipo de viviendas construían los celtas en los castros fortificados?",
                options = listOf("Tiendas de piel nómadas", "Casas circulares de piedra y paja", "Pirámides escalonadas", "Castillos de ladrillo"),
                correctIndex = 1,
                funFact = "Eran redondas para repartir mejor el calor y resistir los fuertes vientos atlánticos."
            ),
            QuizQuestion(
                id = 2,
                question = "¿Cómo se llamaba la joya rígida y circular de oro o bronce que llevaban al cuello?",
                options = listOf("El Torque", "La Corona de laureles", "La Fíbula de plata", "El Broche solar"),
                correctIndex = 0,
                funFact = "El torque era símbolo de nobleza, valor y protección otorgada por los dioses."
            ),
            QuizQuestion(
                id = 3,
                question = "¿Quiénes eran los grandes sabios, maestros, médicos y astrónomos de la sociedad celta?",
                options = listOf("Los Gladiadores", "Los Centuriones", "Los Druidas", "Los Faraones"),
                correctIndex = 2,
                funFact = "Los druidas dedicaban hasta dos décadas a memorizar conocimientos de medicina botánica y estrellas."
            ),
            QuizQuestion(
                id = 4,
                question = "¿Qué milenaria festividad celta de agradecimiento a las cosechas dio origen a Halloween?",
                options = listOf("Beltaine", "Imbolc", "Lughnasadh", "Samhain"),
                correctIndex = 3,
                funFact = "En Samhain apagaban los fuegos domésticos y encendían una gran hoguera sagrada comunitaria."
            ),
            QuizQuestion(
                id = 5,
                question = "¿Qué monumental instrumento de viento de bronce celta culminaba en una cabeza de jabalí?",
                options = listOf("El Carnyx", "El Shofar", "La Trompeta romana", "La Flauta de sauce"),
                correctIndex = 0,
                funFact = "Medía casi dos metros y poseía una lengüeta móvil que vibraba al compás de la respiración."
            ),
            QuizQuestion(
                id = 6,
                question = "¿Qué prenda de vestir cómoda y abrigada inventaron los pueblos celtas para montar?",
                options = listOf("Las sandalias con alas", "Los pantalones a cuadros (braccae)", "Las capas de seda", "Las togas de lino"),
                correctIndex = 1,
                funFact = "Llamados 'braccae', sorprendieron a griegos y romanos que solo usaban túnicas y faldones."
            ),
            QuizQuestion(
                id = 7,
                question = "¿Cuál era el árbol considerado el más sagrado por los druidas debido a su fuerza y longevidad?",
                options = listOf("El Ciprés del sur", "El Olivo milenario", "El Roble sagrado", "El Abeto invernal"),
                correctIndex = 2,
                funFact = "Bajo los robles centenarios se impartía justicia y se celebraban las asambleas del pueblo."
            ),
            QuizQuestion(
                id = 8,
                question = "¿Qué planta sagrada cortaban los druidas con una hoz de oro en las ramas de los robles?",
                options = listOf("El Muérdago dorado", "El Trébol de agua", "La Manzanilla silvestre", "El Espino blanco"),
                correctIndex = 0,
                funFact = "No dejaban que cayera al suelo para que conservara toda su pureza y poderes curativos."
            ),
            QuizQuestion(
                id = 9,
                question = "¿Qué reina celta de Britania unió a diversas tribus para defender la libertad de su pueblo?",
                options = listOf("Cleopatra VII", "Zenobia de Palmira", "Boudica de los Iceni", "Dido de Cartago"),
                correctIndex = 2,
                funFact = "Descrita como una líder de gran elocuencia, larga melena roja y un resplandeciente torque al cuello."
            ),
            QuizQuestion(
                id = 10,
                question = "¿Qué representan las tres espirales entrelazadas del emblemático símbolo del Triskelion?",
                options = listOf("El Pasado, la Nieve y el Hierro", "La Tierra, el Agua y el Aire en movimiento", "Tres reyes de la mitología", "Las tres lunas del invierno"),
                correctIndex = 1,
                funFact = "Simboliza el ciclo perpetuo de renovación de la naturaleza y la vida en constante fluir."
            )
        )
    }
}
