package dev.danielk.workit.model

data class WorkoutPreset(
    val name: String,
    val emoji: String,
    val readySeconds: Int,
    val workSeconds: Int,
    val restSeconds: Int,
    val repeatCount: Int
) {
    companion object {
        val BEGINNER = WorkoutPreset(
            name = "입문자용",
            emoji = "🟢",
            readySeconds = 30,
            workSeconds = 60,
            restSeconds = 120,
            repeatCount = 6
        )
        val ELEMENTARY = WorkoutPreset(
            name = "초급자용",
            emoji = "🟡",
            readySeconds = 30,
            workSeconds = 120,
            restSeconds = 60,
            repeatCount = 6
        )
        val INTERMEDIATE = WorkoutPreset(
            name = "중급자용",
            emoji = "🔴",
            readySeconds = 30,
            workSeconds = 180,
            restSeconds = 60,
            repeatCount = 8
        )

        val ALL = listOf(BEGINNER, ELEMENTARY, INTERMEDIATE)
    }
}
