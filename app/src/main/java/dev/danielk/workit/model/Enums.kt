package dev.danielk.workit.model

enum class WorkoutState {
    READY,   // 준비 시간
    RUNNING, // 운동 중
    REST,    // 휴식 중
    DONE     // 완료
}

enum class TtsStyle {
    COACH,  // 🔥 코치형
    FRIEND, // 😊 친구형
    INFO    // 📢 정보형
}

enum class MessageType {
    BOT,        // 챗봇 메시지
    USER_QUICK, // 빠른 반응 버튼
    USER_TEXT   // 직접 입력
}

enum class GrassGrade {
    NONE,     // 운동 없음
    PARTIAL,  // 중도 포기
    COMPLETE, // 완주
    BEST,     // 개인 최고 기록
    SPECIAL   // 특별 목표 달성
}
