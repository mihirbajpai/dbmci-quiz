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

class QuizViewModel : ViewModel() {
    private val _questionsState = MutableStateFlow<DataState<List<Question>>>(DataState.Loading())
    val questionsState: StateFlow<DataState<List<Question>>> = _questionsState.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _selectedOptionIndex = MutableStateFlow<Int?>(null)
    val selectedOptionIndex: StateFlow<Int?> = _selectedOptionIndex.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    /** True while the cancellable "moving to next question" countdown is running. */
    private val _autoAdvancing = MutableStateFlow(false)
    val autoAdvancing: StateFlow<Boolean> = _autoAdvancing.asStateFlow()

    /** True while the celebration overlay is showing; VM-timed, like [autoAdvancing]. */
    private val _showCelebration = MutableStateFlow(false)
    val showCelebration: StateFlow<Boolean> = _showCelebration.asStateFlow()

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

    /** [autoAdvanceJob] is used to handle [autoAdvancing]*/
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
        val question =
            (_questionsState.value as? DataState.Success)?.value?.getOrNull(_currentIndex.value)
        // Ignore if no current question, or already answered.
        if (question == null || _selectedOptionIndex.value != null) return
        _selectedOptionIndex.value = index

        if (index == question.correctIndex) {
            _streak.value++
            correctCount++
            // New personal best at/above the milestone.
            if (_streak.value > longestStreak) {
                longestStreak = _streak.value
                if (longestStreak >= STREAK_MILESTONE) celebrate()
            }
        } else {
            _streak.value = 0
        }
        startAutoAdvance()
    }

    /** The bottom button: "Skip" for an unanswered question, otherwise "Next". */
    fun onSkipOrNext() {
        if (_selectedOptionIndex.value == null) {
            skippedCount += 1
            _streak.value = 0
        }
        advance()
    }

    /** Cancel text in [AutoAdvanceBar]: cancel the auto advance progress. */
    fun cancelAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
        _autoAdvancing.value = false
    }

    /** After an answer, count down [AUTO_ADVANCE_MS] then move on — unless Cancel stops it. */
    private fun startAutoAdvance() {
        _autoAdvancing.value = true
        autoAdvanceJob = viewModelScope.launch {
            delay(AUTO_ADVANCE_MS.milliseconds)
            _autoAdvancing.value = false
            advance()
        }
    }

    /** Shows the celebration overlay animation on personal record */
    private fun celebrate() {
        viewModelScope.launch {
            _showCelebration.value = true
            delay(AUTO_ADVANCE_MS.milliseconds)
            _showCelebration.value = false
        }
    }

    private fun advance() {
        // currentIndex past the last question is what the UI reads as "finished".
        _currentIndex.value++
        _selectedOptionIndex.value = null
    }

    companion object {
        const val AUTO_ADVANCE_MS = 2_000L
        const val STREAK_MILESTONE = 3
    }
}
