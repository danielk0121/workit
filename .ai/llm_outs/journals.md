# 작업 일지 (journal)

- 2026-03-01 10:00:00 (+09:00)
  - AI 프롬프트 확인 테스트 수행
  - .ai/tmp/test.txt 파일 생성 (날짜 및 10글자 문장 기록)

- 2026-03-01 11:35:00 (+09:00)
  - [x] 기능 추가: 운동 완료 시 스트릭 축하 챗봇 메시지 (3일, 7일, 30일)
    - `WorkoutRepository`에 현재 스트릭을 조회하는 `getCurrentStreak` 함수 추가
    - `ChatViewModel`의 `onWorkoutComplete` 시점에 스트릭을 확인하여 축하 메시지 발송 로직 구현
    - `BotScript`의 스트릭 관련 메시지들을 활용하여 사용자 동기부여 강화
