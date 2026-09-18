package com.example.celtas.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.celtas.audio.NarrationManager
import com.example.celtas.data.CelticAdventureRepository
import com.example.celtas.model.AccessorySlot
import com.example.celtas.model.Achievement
import com.example.celtas.model.ArchaeologyItem
import com.example.celtas.model.CelticAccessory
import com.example.celtas.model.CharacterCustomization
import com.example.celtas.model.DailyReward
import com.example.celtas.model.DailyRewardState
import com.example.celtas.model.ExplorerProfile
import com.example.celtas.model.JournalEntry
import com.example.celtas.model.MapLocation
import com.example.celtas.model.QuizQuestion
import com.example.celtas.model.RuneSymbol
import com.example.celtas.model.StoryChapter
import com.example.celtas.model.TrophyPrize
import com.example.celtas.model.VillageBuilding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val icon: String) {
    MAPA("Mapa", "🗺️"),
    RELATOS("Relatos", "📖"),
    PERSONAJE("Personaje 3D", "🧙‍♂️"),
    MINIJUEGOS("Juegos", "🎮"),
    DIARIO("Diario", "📓"),
    PREMIOS("Premios", "🏆")
}


enum class MinigameTab(val title: String, val icon: String) {
    ARCHAEOLOGY("Excavación", "🔍"),
    SYMBOLS("Runas y Nudos", "🌀"),
    QUIZ("Desafío Bardo", "⭐"),
    VILLAGE("Mi Castro", "🛖")
}

class CelticViewModel(application: Application) : AndroidViewModel(application) {

    val repository = CelticAdventureRepository(application)
    val narrationManager = NarrationManager(application)

    // Current navigation tab
    private val _currentTab = MutableStateFlow(AppTab.MAPA)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    // Current active story for narration
    private val _activeChapter = MutableStateFlow<StoryChapter?>(null)
    val activeChapter: StateFlow<StoryChapter?> = _activeChapter.asStateFlow()

    private val _currentParagraph = MutableStateFlow(0)
    val currentParagraph: StateFlow<Int> = _currentParagraph.asStateFlow()

    private val _quizSelectedAnswer = MutableStateFlow<Int?>(null)
    val quizSelectedAnswer: StateFlow<Int?> = _quizSelectedAnswer.asStateFlow()

    private val _quizIsCorrect = MutableStateFlow<Boolean?>(null)
    val quizIsCorrect: StateFlow<Boolean?> = _quizIsCorrect.asStateFlow()

    // Map selection
    private val _selectedLocation = MutableStateFlow<MapLocation?>(null)
    val selectedLocation: StateFlow<MapLocation?> = _selectedLocation.asStateFlow()

    // Minigames state
    private val _activeMinigame = MutableStateFlow(MinigameTab.ARCHAEOLOGY)
    val activeMinigame: StateFlow<MinigameTab> = _activeMinigame.asStateFlow()

    // Archaeology excavation
    private val _activeExcavationRelic = MutableStateFlow<ArchaeologyItem?>(null)
    val activeExcavationRelic: StateFlow<ArchaeologyItem?> = _activeExcavationRelic.asStateFlow()

    private val _excavatedCells = MutableStateFlow<Set<Int>>(emptySet())
    val excavatedCells: StateFlow<Set<Int>> = _excavatedCells.asStateFlow()

    private val _excavationSuccess = MutableStateFlow(false)
    val excavationSuccess: StateFlow<Boolean> = _excavationSuccess.asStateFlow()

    // Symbols minigame
    private val _selectedRuneForLore = MutableStateFlow<RuneSymbol?>(null)
    val selectedRuneForLore: StateFlow<RuneSymbol?> = _selectedRuneForLore.asStateFlow()

    // Bardo Quiz Minigame
    private val quizQuestionsList: List<QuizQuestion> = repository.getQuizQuestions()
    private val _quizIndex = MutableStateFlow(0)
    val quizIndex: StateFlow<Int> = _quizIndex.asStateFlow()

    val currentQuizQuestion: StateFlow<QuizQuestion> = _quizIndex.map { idx ->
        quizQuestionsList[idx.coerceIn(0, quizQuestionsList.size - 1)]
    }.stateIn(viewModelScope, SharingStarted.Eagerly, quizQuestionsList[0])

    private val _bardoQuizScore = MutableStateFlow(0)
    val bardoQuizScore: StateFlow<Int> = _bardoQuizScore.asStateFlow()

    private val _bardoAnswerStatus = MutableStateFlow<Boolean?>(null)
    val bardoAnswerStatus: StateFlow<Boolean?> = _bardoAnswerStatus.asStateFlow()

    private val _selectedBardoOption = MutableStateFlow<Int?>(null)
    val selectedBardoOption: StateFlow<Int?> = _selectedBardoOption.asStateFlow()

    // Observables from repo
    val profile: StateFlow<ExplorerProfile> = repository.profile
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.profile.value)

    val chapters: StateFlow<List<StoryChapter>> = repository.chapters
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.chapters.value)

    val mapLocations: StateFlow<List<MapLocation>> = repository.mapLocations
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.mapLocations.value)

    val relics: StateFlow<List<ArchaeologyItem>> = repository.relics
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.relics.value)

    val symbols: StateFlow<List<RuneSymbol>> = repository.symbols
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.symbols.value)

    val buildings: StateFlow<List<VillageBuilding>> = repository.buildings
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.buildings.value)

    val trophies: StateFlow<List<TrophyPrize>> = repository.trophies
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.trophies.value)

    val achievements: StateFlow<List<Achievement>> = repository.achievements
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.achievements.value)

    val journalEntries: StateFlow<List<JournalEntry>> = repository.journalEntries
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.journalEntries.value)

    val customization: StateFlow<CharacterCustomization> = repository.customization
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.customization.value)

    val accessories: StateFlow<List<CelticAccessory>> = repository.accessories
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.accessories.value)

    val dailyRewardState: StateFlow<DailyRewardState> = repository.dailyRewardState
        .stateIn(viewModelScope, SharingStarted.Eagerly, repository.dailyRewardState.value)

    private val _showDailyRewardDialog = MutableStateFlow(false)
    val showDailyRewardDialog: StateFlow<Boolean> = _showDailyRewardDialog.asStateFlow()

    val recentAchievement: StateFlow<Achievement?> = repository.recentUnlockedAchievement

    val isSpeaking: StateFlow<Boolean> = narrationManager.isSpeaking

    init {
        // Auto-show daily reward if ready to claim
        if (repository.dailyRewardState.value.canClaimToday) {
            _showDailyRewardDialog.value = true
        }

        // Default first story as active
        val initialChapters = repository.chapters.value
        if (initialChapters.isNotEmpty()) {
            _activeChapter.value = initialChapters.first()
        }
        val initialRelics = repository.relics.value
        if (initialRelics.isNotEmpty()) {
            _activeExcavationRelic.value = initialRelics.firstOrNull { !it.isDiscovered } ?: initialRelics.first()
        }
    }

    fun openDailyRewardDialog() {
        _showDailyRewardDialog.value = true
    }

    fun closeDailyRewardDialog() {
        _showDailyRewardDialog.value = false
    }

    fun getDailyRewardsList(): List<DailyReward> = repository.getDailyRewardsList()

    fun claimDailyReward(): DailyReward? {
        return repository.claimDailyReward()
    }

    // Tab Navigation
    fun setTab(tab: AppTab) {
        narrationManager.stop()
        _currentTab.value = tab
    }

    fun setMinigame(game: MinigameTab) {
        _activeMinigame.value = game
    }

    // Story Narration Handling
    fun selectChapter(chapter: StoryChapter) {
        narrationManager.stop()
        _activeChapter.value = chapter
        _currentParagraph.value = 0
        _quizSelectedAnswer.value = null
        _quizIsCorrect.value = null
    }

    fun playOrPauseNarration() {
        val chapter = _activeChapter.value ?: return
        if (narrationManager.isSpeaking.value) {
            narrationManager.stop()
        } else {
            speakCurrentParagraph(chapter)
        }
    }

    private fun speakCurrentParagraph(chapter: StoryChapter) {
        val paragraphIndex = _currentParagraph.value
        if (paragraphIndex in chapter.paragraphs.indices) {
            val textToSpeak = chapter.paragraphs[paragraphIndex]
            narrationManager.speak(textToSpeak, paragraphIndex) {
                // When finishes, auto advance if not last
                viewModelScope.launch {
                    if (_currentParagraph.value < chapter.paragraphs.size - 1) {
                        _currentParagraph.value += 1
                        speakCurrentParagraph(chapter)
                    }
                }
            }
        }
    }

    fun nextParagraph() {
        val chapter = _activeChapter.value ?: return
        if (_currentParagraph.value < chapter.paragraphs.size - 1) {
            _currentParagraph.value += 1
            if (narrationManager.isSpeaking.value) {
                speakCurrentParagraph(chapter)
            }
        }
    }

    fun prevParagraph() {
        val chapter = _activeChapter.value ?: return
        if (_currentParagraph.value > 0) {
            _currentParagraph.value -= 1
            if (narrationManager.isSpeaking.value) {
                speakCurrentParagraph(chapter)
            }
        }
    }

    fun jumpToParagraph(index: Int) {
        val chapter = _activeChapter.value ?: return
        if (index in chapter.paragraphs.indices) {
            _currentParagraph.value = index
            if (narrationManager.isSpeaking.value) {
                speakCurrentParagraph(chapter)
            }
        }
    }

    fun answerStoryQuiz(optionIndex: Int) {
        val chapter = _activeChapter.value ?: return
        _quizSelectedAnswer.value = optionIndex
        val correct = optionIndex == chapter.quiz.correctIndex
        _quizIsCorrect.value = correct
        if (correct) {
            repository.completeChapter(chapter.id)
            // Update active chapter with completed state
            _activeChapter.value = chapter.copy(isCompleted = true)
        }
    }

    // Map Actions
    fun selectLocation(loc: MapLocation?) {
        _selectedLocation.value = loc
        if (loc != null) {
            repository.markLocationVisited(loc.id)
        }
    }

    fun travelFromMapToStory(chapterId: String) {
        val found = chapters.value.find { it.id == chapterId }
        if (found != null && found.isUnlocked) {
            selectChapter(found)
            _selectedLocation.value = null
            setTab(AppTab.RELATOS)
        }
    }

    fun travelFromMapToMinigame(minigameId: String) {
        val target = when (minigameId) {
            "game_archaeology" -> MinigameTab.ARCHAEOLOGY
            "game_symbols" -> MinigameTab.SYMBOLS
            "game_village" -> MinigameTab.VILLAGE
            else -> MinigameTab.QUIZ
        }
        _activeMinigame.value = target
        _selectedLocation.value = null
        setTab(AppTab.MINIJUEGOS)
    }

    // Archaeology minigame
    fun selectRelicForExcavation(relic: ArchaeologyItem) {
        _activeExcavationRelic.value = relic
        _excavatedCells.value = emptySet()
        _excavationSuccess.value = false
    }

    fun digCell(cellIndex: Int) {
        val relic = _activeExcavationRelic.value ?: return
        if (_excavationSuccess.value) return

        val newSet = _excavatedCells.value + cellIndex
        _excavatedCells.value = newSet

        // Check if all target cells are uncovered
        val allUncovered = relic.targetCells.all { newSet.contains(it) }
        if (allUncovered) {
            _excavationSuccess.value = true
            repository.discoverRelic(relic.id)
        }
    }

    // Symbols minigame
    fun selectRuneForLore(rune: RuneSymbol?) {
        _selectedRuneForLore.value = rune
    }

    fun matchSymbol(runeId: String) {
        repository.markSymbolSolved(runeId)
    }

    fun resetSymbolsGame() {
        repository.resetSymbols()
    }

    // Bardo Quiz Trivia
    fun getCurrentQuizQuestion(): QuizQuestion {
        val idx = _quizIndex.value.coerceIn(0, quizQuestionsList.size - 1)
        return quizQuestionsList[idx]
    }

    fun totalQuizQuestions(): Int = quizQuestionsList.size

    fun answerBardoQuiz(optionIndex: Int) {
        if (_bardoAnswerStatus.value != null) return // Already answered this question
        _selectedBardoOption.value = optionIndex
        val currentQ = getCurrentQuizQuestion()
        val isCorrect = optionIndex == currentQ.correctIndex
        _bardoAnswerStatus.value = isCorrect

        if (isCorrect) {
            val newScore = _bardoQuizScore.value + 1
            _bardoQuizScore.value = newScore
            repository.addRewards(15, 20)
            repository.reportQuizMasterScore(newScore)
        }
    }

    fun nextBardoQuestion() {
        _bardoAnswerStatus.value = null
        _selectedBardoOption.value = null
        if (_quizIndex.value < quizQuestionsList.size - 1) {
            _quizIndex.value += 1
        } else {
            // Loop or restart
            _quizIndex.value = 0
        }
    }

    // Village Construction
    fun buildStructure(buildingId: String): Boolean {
        return repository.buildVillageStructure(buildingId)
    }

    // Journal
    fun addJournalNote(title: String, content: String, sticker: String) {
        repository.addCustomJournalEntry(title, content, sticker)
    }

    fun deleteJournalNote(id: String) {
        repository.deleteJournalEntry(id)
    }

    fun updateProfile(name: String, avatar: String) {
        repository.updateProfileName(name)
        repository.updateAvatar(avatar)
    }

    // Character Customizer & 3D Accessories
    fun setArchetype(archetype: String) {
        repository.updateArchetype(archetype)
    }

    fun setCharacterTitle(title: String) {
        repository.updateCharacterTitle(title)
    }

    fun setSkinTone(index: Int) {
        repository.updateSkinTone(index)
    }

    fun setHairColor(index: Int) {
        repository.updateHairColor(index)
    }

    fun setWarPaint(index: Int) {
        repository.updateWarPaint(index)
    }

    fun buyAccessory(accessoryId: String): Boolean {
        return repository.buyAccessory(accessoryId)
    }

    fun equipAccessory(accessoryId: String) {
        repository.equipAccessory(accessoryId)
    }

    fun unequipAccessory(accessoryId: String) {
        repository.unequipAccessory(accessoryId)
    }

    fun dismissAchievementBanner() {
        repository.dismissRecentAchievement()
    }

    override fun onCleared() {
        super.onCleared()
        narrationManager.shutdown()
    }
}
