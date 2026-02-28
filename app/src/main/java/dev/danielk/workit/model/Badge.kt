package dev.danielk.workit.model

import androidx.annotation.DrawableRes

data class Badge(
    val id: String,
    val name: String,
    val description: String,
    @DrawableRes val iconResId: Int,
    val isUnlocked: Boolean = false
)
