package com.example.dbmciquiz.view.screen.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dbmciquiz.data.model.Question
import com.example.dbmciquiz.data.repository.QuizRepository
import com.example.dbmciquiz.view.DataState
import com.example.dbmciquiz.view.screen.quiz.QuizViewModel.Companion.AUTO_ADVANCE_MS
import com.example.dbmciquiz.view.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/** The quiz-in-progress state the UI observes. Mutated only via [QuizViewModel.updateUi]. */
data class QuizUiState(
    var currentIndex: Int = 0,
    var selectedOptionIndex: Int? = null,
    var streak: Int = 0,
    var autoAdvancing: Boolean = false,
    var showCelebration: Boolean = false,
)

class QuizViewModel : ViewModel() {
    // Load lifecycle, kept separate from quiz progress: the splash/quiz/error switch reads this.
    private val _questionsState = MutableStateFlow<DataState<List<Question>>>(DataState.Loading())
    val questionsState: StateFlow<DataState<List<Question>>> = _questionsState.asStateFlow()

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    /**
     * [longestStreak], [correctCount] and [skippedCount] are read once at finish and handed to
     * Result as nav args.
     */
    var longestStreak = 0
        private set
    var correctCount = 0
        private set
    var skippedCount = 0
        private set

    private var autoAdvanceJob: Job? = null

    init {
        fetchQuestions()
    }

    fun fetchQuestions() {
        viewModelScope.launch {
            _questionsState.update { QuizRepository.fetchQuestions() }
        }
    }

    fun selectOption(index: Int) {
        // Ignore if the question isn't loaded yet, or it's already answered.
        val question =
            (_questionsState.value as? DataState.Success)?.value?.getOrNull(_uiState.value.currentIndex)
        if (_uiState.value.selectedOptionIndex != null || question == null) return
        updateUi { selectedOptionIndex = index }

        if (index == question.correctIndex) {
            correctCount++
            val newStreak = _uiState.value.streak + 1
            updateUi { streak = newStreak }
            // A new personal best at/above the milestone triggers a celebration.
            if (newStreak > longestStreak) {
                longestStreak = newStreak
                if (longestStreak >= STREAK_MILESTONE) celebrate()
            }
        } else {
            updateUi { streak = 0 }
        }
        startAutoAdvance()
    }

    /** The bottom button: "Skip" for an unanswered question, otherwise "Next". */
    fun onSkipOrNext() {
        if (_uiState.value.selectedOptionIndex == null) {
            skippedCount++
            updateUi { streak = 0 }
        }
        advance()
    }

    /** Cancel text in [AutoAdvanceBar]: stops the auto-advance countdown. */
    fun cancelAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
        updateUi { autoAdvancing = false }
    }

    /** After an answer, count down [AUTO_ADVANCE_MS] then move on — unless Cancel stops it. */
    private fun startAutoAdvance() {
        updateUi { autoAdvancing = true }
        autoAdvanceJob = viewModelScope.launch {
            delay(AUTO_ADVANCE_MS.milliseconds)
            advance()
        }
    }

    /** Shows the celebration overlay on a new personal-record streak. */
    private fun celebrate() {
        viewModelScope.launch {
            updateUi { showCelebration = true }
            delay(AUTO_ADVANCE_MS.milliseconds)
            updateUi { showCelebration = false }
        }
    }

    private fun advance() {
        // Cancel any pending auto-advance so a manual skip/swipe/next doesn't double-advance.
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
        // currentIndex past the last question is what the UI reads as "finished".
        updateUi {
            currentIndex++
            selectedOptionIndex = null
            autoAdvancing = false
        }
    }

    /** Publishes a fresh copy of the UI state with [block]'s assignments applied. */
    private inline fun updateUi(block: QuizUiState.() -> Unit) {
        _uiState.value = _uiState.value.copy().apply(block)
    }

    companion object {
        const val AUTO_ADVANCE_MS = 2_000L
        const val STREAK_MILESTONE = 3
    }
}
