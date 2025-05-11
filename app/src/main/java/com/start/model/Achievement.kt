package com.start.model


data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val earned: Boolean = false,
    val progress: Int = 0,
    val requiredProgress: Int = 1
)