# 작업 일지 (journal)

- 2026-03-01 10:00:00 (+09:00)
  - AI 프롬프트 확인 테스트 수행
  - .ai/tmp/test.txt 파일 생성 (날짜 및 10글자 문장 기록)

- 2026-03-01 10:20:00 (+09:00)
  - [x] 버그 수정: 홈 > 새운동 시작 > 채팅 > 운동 시작 버튼 클릭 > 앱 크래시
    - 원인: Android 14 대응 `foregroundServiceType` 불일치 (코드-HEALTH vs 매니페스트-dataSync)
    - 해결: 매니페스트에 `FOREGROUND_SERVICE_HEALTH` 권한 추가 및 서비스 타입을 `health`로 통일
  - [x] 버그 수정: 홈 > 운동 목록에서 운동 하나 선택 > 채팅방 이동 > 채팅 입력창 클릭 > 입력 불가
    - 원인: 과거 세션 진입 시 `isCompleted` 여부에 따른 입력창 활성화 로직 오류
    - 해결: 과거 기록 열람 시 무조건 입력 활성화 및 `null` 안전성 보완
  - 빌드 확인: `./gradlew assembleDebug` 성공 및 미사용 변수 경고 해결

- 2026-03-01 10:45:00 (+09:00)
  - [x] 기능 구현: 다크모드, 라이트 모드 대응 및 설정 화면 추가
    - `res/values-night/colors.xml`, `themes.xml`을 통한 테마 시스템 구축
    - `SettingsFragment` 및 레이아웃 구현 (다크모드 스위치 포함)
    - `HomeFragment` 툴바에 설정 메뉴 추가 및 내비게이션 연결
    - 빌드 확인 및 테마 전환 로직 검증 완료

- 2026-03-01 11:00:00 (+09:00)
  - [x] 기능 개선: 잔디밭 셀 클릭 시 해당 채팅방 이동
    - `GrassView`의 `onDateClick` 콜백을 `GrassFragment`에서 구현
    - 클릭된 날짜의 `sessionId`를 조회하여 `ChatFragment`로 내비게이션 연결
    - 데이터가 없는 경우를 위한 Toast 안내 추가
    - `nav_graph.xml`에 `action_grass_to_chat` 정의

- 2026-03-01 11:20:00 (+09:00)
  - [x] 기능 추가: 홈 화면 상단 잔디밭 미리보기 (최근 3개월)
    - `GrassView`를 확장하여 주차 수를 설정할 수 있는 `setWeeksCount` 기능 추가
    - `fragment_home.xml` 상단에 잔디밭 미리보기 및 스트릭 배너 레이아웃 배치
    - `HomeViewModel`에서 잔디밭 데이터 및 스트릭 계산 로직 보완
    - 홈 상단 잔디밭 클릭 시에도 해당 채팅방으로 이동하도록 연결

- 2026-03-01 11:35:00 (+09:00)
  - [x] 기능 추가: 운동 완료 시 스트릭 축하 챗봇 메시지 (3일, 7일, 30일)
    - `WorkoutRepository`에 현재 스트릭을 조회하는 `getCurrentStreak` 함수 추가
    - `ChatViewModel`의 `onWorkoutComplete` 시점에 스트릭을 확인하여 축하 메시지 발송 로직 구현
    - `BotScript`의 스트릭 관련 메시지들을 활용하여 사용자 동기부여 강화

- 2026-03-01 11:55:00 (+09:00)
  - [x] 기능 추가: 채팅방 제목 편집 기능
    - `WorkoutSessionDao`에 제목 업데이트 쿼리(`updateTitle`) 추가
    - `ChatViewModel` 및 `WorkoutRepository`에 제목 수정 로직 구현
    - `ChatFragment` 툴바에 수정 메뉴 추가 및 `AlertDialog` 기반 편집 UI 구현
    - 빌드 시 발생한 `R` 임포트 누락 오류 수정 및 최종 검증 완료

- 2026-03-01 12:15:00 (+09:00)
  - [x] 기능 추가: 운동 세션 상세 통계 화면 (감정 흐름)
    - `StatsFragment` 및 `EmotionAdapter` 구현: 라운드별 사용자의 빠른 반응 분석 및 시각화
    - `nav_graph.xml` 및 `ChatFragment` 툴바 메뉴에 통계 화면 이동 경로 추가
    - 채팅 메시지 텍스트 분석(Regex)을 통한 라운드 매핑 로직 구현
    - View Binding ID 오타 수정 및 최종 빌드 검증 완료

- 2026-03-01 12:35:00 (+09:00)
  - [x] 기능 개선: GrassRecord BEST 등급 판정 로직 고도화
    - 역대 최고 운동 시간 및 최다 라운드 기록을 조회하는 DAO 쿼리 추가
    - `WorkoutRepository`의 `updateGrassAfterWorkout`에서 현재 기록과 역대 최고 기록을 비교하여 `BEST` 등급 부여
    - 미사용 변수(`existing`) 제거 및 최종 빌드 검증 완료

- 2026-03-01 13:00:00 (+09:00)
  - [x] 기능 구현: 프로필 화면 (스트릭, 배지, TTS 기본 스타일 설정)
    - `SettingsFragment`를 `ProfileFragment`로 전면 개편하여 사용자 프로필 중심의 UI 구축
    - `ProfileViewModel` 및 `BadgeAdapter` 구현으로 스트릭 정보 및 배지 목록(더미 데이터) 시각화
    - `PreferenceManager`를 도입하여 `TtsStyle` 및 다크 모드 설정을 `SharedPreferences`에 영구 저장 및 앱 실행 시 자동 적용
    - `HomeFragment` 툴바 메뉴와 내비게이션 경로(`nav_graph.xml`)를 '프로필'로 업데이트
    - 빌드 확인 및 TTS 스타일 설정/테마 전환 로직 최종 검증 완료

- 2026-03-01 13:20:00 (+09:00)
  - [x] 프롬프트 개선: Golden Loop 규칙 강화 및 자동 커밋 권한 명문화
    - `PROMPT.md`에 "이 지침서가 사용자의 명시적 요청을 대신함"을 명시
    - 시스템 기본 수칙과 충돌 시 프로젝트 지침을 우선하도록 AI 판단 근거 강화
    - Golden Loop의 마지막 단계(커밋)를 별도 확인 없이 수행하도록 지침 수정

- 2026-03-01 14:00:00 (+09:00)
  - [x] 기능 구현: 배지 시스템 (7일 연속, 첫 운동, 한 달 워킷러)
    - `Badge` 데이터 클래스를 `model` 패키지로 분리하여 재사용성 강화
    - `ProfileViewModel`에서 `WorkoutRepository`의 실데이터를 조회하여 배지 해금 상태 계산 로직 구현
    - `BadgeAdapter`가 새 `Badge` 모델을 참조하도록 수정 및 잠금 상태 스타일링(투명도) 적용
    - `getMaxStreak` 및 `getTotalWorkoutDays` 기반의 조건부 해금 로직 검증 완료

- 2026-03-01 14:15:00 (+09:00)
  - [x] 기능 구현: 운동 완료 시 AI 챗봇 자동 요약
    - `ChatViewModel`의 `onWorkoutComplete` 시점에 사용자 반응(USER_QUICK) 분석 로직 추가
    - '😤 힘들어' 및 '💪 괜찮아' 반응 횟수에 따른 맞춤형 격려 요약 메시지 발송 기능 구현
    - `addMessage`를 메인 스레드 즉시 반영(`value =`) 방식으로 수정하여 요약 시점의 데이터 무결성 확보
    - 빌드 확인 및 요약 메시지 생성 로직 검증 완료

- 2026-03-01 14:35:00 (+09:00)
  - [x] 기능 구현: 매일 운동 리마인더 알림 (선택적)
    - `PreferenceManager`에 리마인더 활성화 상태 저장 로직 추가
    - `fragment_profile.xml`에 리마인더 설정 스위치 UI 추가 및 `ProfileFragment` 바인딩
    - `AlarmManager` 및 `BroadcastReceiver`를 활용한 `ReminderManager` 구현 (매일 오후 8시 알림)
    - `AndroidManifest.xml`에 `ReminderReceiver` 등록 및 `POST_NOTIFICATIONS` 권한 확인
    - 빌드 확인 및 설정 스위치 연동 로직 검증 완료

- 2026-03-01 14:50:00 (+09:00)
  - [x] 기능 구현: 음악 볼륨 덕킹 (TTS 발화 시 음악 자동으로 낮추기)
    - `TtsManager`에 `AudioManager` 및 `AudioFocusRequest`를 통한 오디오 포커스 제어 로직 도입
    - TTS 발화 시작 시 `AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK`을 요청하여 배경 음악 볼륨을 자동으로 낮춤
    - `UtteranceProgressListener`를 등록하여 TTS 발화 완료 시 오디오 포커스를 즉시 해제하도록 구현
    - Android 8.0(Oreo) 이상(`AudioFocusRequest`) 및 이하 버전 호환성 확보
    - 빌드 확인 및 오디오 포커스 전환 로직 검증 완료
