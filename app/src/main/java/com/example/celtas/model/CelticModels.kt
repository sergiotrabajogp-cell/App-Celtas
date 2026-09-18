package com.example.celtas.model

enum class ExplorerRank(val title: String, val minXp: Int, val badgeIcon: String) {
    NOVATO("Novato del Castro", 0, "🌱"),
    RASTREADOR("Rastreador de Senderos", 100, "🧭"),
    APRENDIZ_BARDO("Aprendiz de Bardo", 250, "🪕"),
    SABIO_ROBLE("Sabio de los Robles", 500, "🍃"),
    GUARDIAN_CELTA("Gran Guardián Celta", 900, "👑")
}

data class ExplorerProfile(
    val name: String = "Arturo",
    val avatarId: String = "bardo", // bardo, druida, guerrero, exploradora
    val goldCoins: Int = 20,
    val xp: Int = 0,
    val rank: ExplorerRank = ExplorerRank.NOVATO,
    val customNotesCount: Int = 0
)

data class StoryChoice(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class StoryChapter(
    val id: String,
    val number: Int,
    val title: String,
    val subtitle: String,
    val region: String,
    val iconEmoji: String,
    val summary: String,
    val paragraphs: List<String>,
    val quiz: StoryChoice,
    val rewardCoins: Int = 50,
    val isCompleted: Boolean = false,
    val isUnlocked: Boolean = true,
    val chronologicalEra: String = "Edad del Hierro",
    val timePeriod: String = "Siglo V a.C."
)

enum class RegionTag {
    GALIA, BRITANIA, HISPANIA, CENTROEUROPA, IRLANDA
}

data class MapLocation(
    val id: String,
    val name: String,
    val ancientName: String,
    val region: RegionTag,
    val xPercent: Float, // 0f to 1f on map
    val yPercent: Float,
    val icon: String,
    val shortFact: String,
    val fullStory: String,
    val connectedChapterId: String? = null,
    val connectedMinigame: String? = null,
    val isVisited: Boolean = false
)

data class ArchaeologyItem(
    val id: String,
    val name: String,
    val era: String,
    val material: String,
    val funFact: String,
    val iconEmoji: String,
    val targetCells: List<Int>, // grid indices from 0..15
    val isDiscovered: Boolean = false
)

data class RuneSymbol(
    val id: String,
    val name: String,
    val symbolChar: String,
    val meaning: String,
    val lore: String,
    val isMatched: Boolean = false
)

data class VillageBuilding(
    val id: String,
    val name: String,
    val cost: Int,
    val icon: String,
    val description: String,
    val isBuilt: Boolean = false
)

data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val funFact: String
)

data class TrophyPrize(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val rarity: String, // Común, Raro, Legendario
    val isUnlocked: Boolean = false
)

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val currentProgress: Int,
    val maxProgress: Int,
    val rewardCoins: Int,
    val isUnlocked: Boolean = false
)

data class JournalEntry(
    val id: String,
    val title: String,
    val category: String, // Curiosidad, Ficha Histórica, Mi Nota
    val content: String,
    val sticker: String,
    val isCustom: Boolean = false,
    val timestamp: String = "Hoy"
)

enum class AccessorySlot(val title: String, val icon: String) {
    HEAD("Cabeza", "👑"),
    BODY("Manto y Capa", "🛡️"),
    NECK("Torque y Joyas", "📿"),
    HAND("Mano y Armas", "⚔️"),
    PET("Compañero", "🦅")
}

enum class ItemSource {
    SHOP,               // Se compra con monedas de oro
    REWARD_STORY,       // Recompensa al completar relatos
    REWARD_ARCHAEOLOGY, // Recompensa de excavación arqueológica
    REWARD_TROPHY,      // Recompensa por trofeos y logros
    REWARD_DAILY        // Recompensa exclusiva por racha diaria
}

data class DailyReward(
    val day: Int,
    val title: String,
    val lore: String,
    val iconEmoji: String,
    val coins: Int,
    val xp: Int,
    val rewardAccessoryId: String? = null,
    val rewardAccessoryName: String? = null
)

data class DailyRewardState(
    val currentStreak: Int = 1,
    val canClaimToday: Boolean = true,
    val lastClaimDate: String? = null,
    val nextDayToClaim: Int = 1,
    val claimedDaysInCycle: Set<Int> = emptySet()
)

data class CelticAccessory(
    val id: String,
    val name: String,
    val slot: AccessorySlot,
    val description: String,
    val iconEmoji: String,
    val price: Int = 0,
    val source: ItemSource = ItemSource.SHOP,
    val sourceRequirement: String = "",
    val isOwned: Boolean = false,
    val isEquipped: Boolean = false,
    val primaryColorHex: Long = 0xFFD97706,
    val secondaryColorHex: Long = 0xFF166534,
    val modelStyle: String = "default"
)

data class CharacterCustomization(
    val archetype: String = "bardo", // bardo, druida, guerrero, exploradora
    val title: String = "Explorador de los Castros",
    val skinToneIndex: Int = 0, // 0 = Pálido Celta, 1 = Brezo Atlántico, 2 = Dorado
    val hairColorIndex: Int = 0, // 0 = Pelirrojo, 1 = Rubio, 2 = Castaño, 3 = Cuervo
    val warPaintIndex: Int = 0, // 0 = Ninguna, 1 = Espirales Glausto, 2 = Trisquel, 3 = Rayas
    val equippedHead: String? = null,
    val equippedBody: String? = null,
    val equippedNeck: String? = null,
    val equippedHand: String? = null,
    val equippedPet: String? = null
)

