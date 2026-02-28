package dev.danielk.workit.tts

import dev.danielk.workit.model.TtsStyle
import dev.danielk.workit.model.WorkoutState

object BotScript {

    fun getReadyMessage(style: TtsStyle, readySeconds: Int): Pair<String, String> {
        val chat = "오늘 운동 시작할게요!\n준비 운동 하면서 기다려주세요 🙆\n───────────────\n⏱ ${readySeconds}초 후 출발!"
        val tts = when (style) {
            TtsStyle.COACH -> "준비해! ${readySeconds}초 후 출발!"
            TtsStyle.FRIEND -> "곧 달릴 거야, 준비해~"
            TtsStyle.INFO -> "${readySeconds}초 후 운동 시작합니다"
        }
        return chat to tts
    }

    fun getCountdownWarningMessage(style: TtsStyle, seconds: Int): Pair<String, String> {
        val chat = "${seconds}초 남았어요, 자리 잡아주세요! 🎯"
        val tts = when (style) {
            TtsStyle.COACH -> "${seconds}초! 준비해!"
            TtsStyle.FRIEND -> "곧 시작이야, 준비~"
            TtsStyle.INFO -> "${seconds}초 후 전환됩니다"
        }
        return chat to tts
    }

    fun getRunningMessage(style: TtsStyle, round: Int, totalRounds: Int, workSeconds: Int): Pair<String, String> {
        val timeStr = formatTime(workSeconds)
        val chat = "🏃 달리세요! ($round/$totalRounds)\n───────────────\n[ $timeStr ▶ 진행중 ]"
        val tts = when (style) {
            TtsStyle.COACH -> "달려! 지금이야!"
            TtsStyle.FRIEND -> "출발! 같이 하자!"
            TtsStyle.INFO -> "운동 시작합니다"
        }
        return chat to tts
    }

    fun getRestMessage(style: TtsStyle, round: Int, totalRounds: Int, restSeconds: Int): Pair<String, String> {
        val timeStr = formatTime(restSeconds)
        val chat = "😮‍💨 휴식! 잘 했어요.\n${round}/${totalRounds} 라운드 완료!\n걷기 시작!\n───────────────\n[ $timeStr ▶ 진행중 ]"
        val tts = when (style) {
            TtsStyle.COACH -> "쉬어! 잘했어!"
            TtsStyle.FRIEND -> "휴식~ 천천히 걸어봐"
            TtsStyle.INFO -> "휴식 시간입니다"
        }
        return chat to tts
    }

    fun getDoneMessage(style: TtsStyle, totalRounds: Int, totalSeconds: Int): Pair<String, String> {
        val timeStr = formatTime(totalSeconds)
        val chat = "🎉 오늘 운동 완료!\n총 시간: $timeStr\n인터벌: ${totalRounds}회 완주\n수고했어요 💪"
        val tts = when (style) {
            TtsStyle.COACH -> "끝! 오늘도 해냈다!"
            TtsStyle.FRIEND -> "다 했다! 진짜 수고했어~"
            TtsStyle.INFO -> "운동이 종료되었습니다"
        }
        return chat to tts
    }

    fun getStreakMessage(streak: Int): String = when (streak) {
        3 -> "3일째 운동하고 있어요! 🔥"
        7 -> "7일 연속! 불꽃이 타오르고 있어요! 🔥🔥"
        14 -> "2주 연속 워킷러! 대단해요! 💪"
        30 -> "한 달 달성! 🏆 한 달 워킷러 칭호를 얻었어요!"
        else -> "${streak}일 연속 운동 중이에요! 계속 화이팅!"
    }

    fun getExerciseWarning(): String =
        "달리면서 타이핑은 위험해요! 😄 휴식 시간에 입력할 수 있어요."

    fun formatTime(seconds: Int): String {
        val m = seconds / 60
        val s = seconds % 60
        return if (m > 0) "${m}분 ${s}초" else "${s}초"
    }

    fun getQuickReactions(state: WorkoutState): List<String> = when (state) {
        WorkoutState.RUNNING -> listOf("😤 힘들어", "💪 괜찮아", "🎵 신난다")
        WorkoutState.REST -> listOf("😮‍💨 숨차", "😊 좋아", "💧 물마심", "🦵 다리아파")
        WorkoutState.DONE -> listOf("🏆 최고였어", "😅 겨우했어", "💀 죽겠다", "🔥 더할수있어")
        else -> emptyList()
    }
}
