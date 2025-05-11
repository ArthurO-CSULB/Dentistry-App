package com.start.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.start.model.Achievement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AchievementViewModel : ViewModel() {
    private val _achievements = MutableStateFlow<List<Achievement>>(defaultAchievements)
    val achievements: StateFlow<List<Achievement>> = _achievements

    companion object {
        val defaultAchievements = listOf(
            Achievement(
                id = "timer_tryer",
                title = "Timer Tryer",
                description = "Used toothbrush timer 3 times",
                requiredProgress = 3
            ),
            Achievement(
                id = "trivia_master",
                title = "Trivia Master",
                description = "Answered 5 trivia questions correctly",
                requiredProgress = 5
            ),
            // Add more achievements as needed
        )
    }

    fun incrementAchievement(id: String) {
        viewModelScope.launch {
            _achievements.update { currentAchievements ->
                currentAchievements.map { achievement ->
                    if (achievement.id == id) {
                        val newProgress = achievement.progress + 1
                        achievement.copy(
                            progress = newProgress,
                            earned = newProgress >= achievement.requiredProgress
                        )
                    } else {
                        achievement
                    }
                }
            }
        }
    }
}