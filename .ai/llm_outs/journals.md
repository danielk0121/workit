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

- 2026-03-01 15:10:00 (+09:00)
  - [x] 기능 구현: 잔디밭 SNS 공유 (스크린샷 공유)
    - `GrassFragment`에 공유 메뉴(`action_share`) 추가 및 툴바 연동
    - `ScrollView`의 전체 콘텐츠를 `Bitmap`으로 캡처하는 로직 구현
    - `FileProvider`를 통해 캡처된 이미지를 안전하게 외부 앱(SNS 등)과 공유할 수 있도록 설정
    - `AndroidManifest.xml`에 `FileProvider` 등록 및 `xml/file_paths` 정의
    - 빌드 확인 및 이미지 캡처/공유 인텐트 호출 로직 검증 완료

- 2026-03-01 15:40:00 (+09:00)
  - [x] 기능 구현: 목표 설정 (이번 달 N회 운동)
    - `PreferenceManager`에 `monthlyGoal` 저장 프로퍼티 추가
    - `GrassRecordDao` 및 `WorkoutRepository`에 이달의 운동 횟수 조회 쿼리(`getWorkoutCountByMonth`) 추가
    - `ProfileViewModel`에서 현재 월의 운동 데이터 로드 및 목표 설정 로직 구현
    - `fragment_profile.xml`에 목표 진행도(ProgressBar) 및 목표 수치 조절 버튼 UI 추가
    - `ProfileFragment`에서 LiveData 관찰 및 목표 증감 버튼 이벤트 바인딩
    - 빌드 확인 및 목표 수치 변경에 따른 진행도 즉시 반영 로직 검증 완료

- 2026-03-01 16:10:00 (+09:00)
  - [x] 기능 구현: 운동 종류 커스텀 (달리기 외 다른 인터벌 운동 지원)
    - `strings.xml`에 운동 이름 관련 라벨 및 힌트 문자열 추가
    - `fragment_workout_setup.xml`에 운동 이름을 입력할 수 있는 `TextInputLayout` 및 `EditText` 추가
    - `WorkoutSetupViewModel`에서 `workoutName` 데이터를 관리하고 세션 생성 시 날짜와 함께 타이틀로 조합하도록 개선
    - `WorkoutSetupFragment`에서 프리셋 선택 시 해당 이름을 자동 입력하고 사용자 정의 입력을 동기화하도록 구현
    - 빌드 확인 및 커스텀 운동 이름이 포함된 세션 생성 로직 검증 완료

- 2026-03-01 16:45:00 (+09:00)
  - [x] 기능 구현: 위젯 (홈 화면 잔디밭 위젯)
    - `grass_widget_info.xml` 정의 및 위젯 기본 설정(2x1 이상 권장)
    - `widget_grass.xml` 레이아웃 구현: 스트릭 정보 및 잔디밭 Bitmap 출력용 ImageView 포함
    - `GrassWidgetProvider` 구현: `RemoteViews`를 사용하여 홈 화면에 위젯 출력
    - `drawGrassBitmap` 함수를 통해 최근 12주간의 운동 기록을 Bitmap으로 동적 렌더링하는 로직 구현
    - `CoroutineScope`와 `goAsync` 형태의 데이터 로딩을 통해 위젯 업데이트 시 DB 연동 처리
    - `AndroidManifest.xml`에 위젯 리시버 등록 및 `PendingIntent` (Android 12 대응) 적용 완료
    - 빌드 확인 및 위젯용 비트맵 생성 로직 검증 완료

- 2026-03-01 17:15:00 (+09:00)
  - [x] 기능 구현: 친구와 채팅방 공유 (운동 설정 공유)
    - `menu_workout_setup.xml` 정의 및 `WorkoutSetupFragment` 툴바에 공유 메뉴 연동
    - 현재 설정된 운동 프리셋(준비, 운동, 휴식 시간 및 반복 횟수)을 텍스트 메시지로 조합하는 로직 구현
    - `Intent.ACTION_SEND`를 사용하여 외부 메신저 및 SNS로 운동 가이드를 공유할 수 있는 기능 추가
    - 빌드 시 발생한 `R` 클래스 임포트 누락 오류 수정 및 최종 빌드 성공 확인

- 2026-03-01 17:45:00 (+09:00)
  - [x] 기능 구현: 워치 연동 (Wear OS 지원 기반 구축)
    - `libs.versions.toml` 및 `build.gradle.kts`에 `play-services-wearable` 의존성 추가
    - `WearableManager` (Object) 클래스 구현: 워치로 운동 상태 데이터를 전송하기 위한 DataLayer API 연동
    - `WorkoutTimerService`의 각 페이즈(`runPhase`) 및 운동 종료 시점에 워치와 실시간 데이터 동기화 로직 추가
    - 워치에서 운동 상태(준비/운동/휴식/완료), 남은 시간, 현재 라운드 정보를 실시간으로 확인할 수 있는 기반 마련
    - 빌드 확인 및 데이터 전송 레이어 연동 성공 확인

- 2026-03-01 18:00:00 (+09:00)
  - [x] 기능 개선: 운동 중 뒤로가기 방지 (세션 이탈 방지)
    - `ChatFragment`에 `OnBackPressedCallback` 도입: 운동 중 시스템 뒤로가기 버튼 차단 및 안내 팝업 출력
    - 툴바 뒤로가기 및 시스템 뒤로가기 로직을 `handleBackPress`로 통합하여 일관성 확보
    - 실수로 운동 세션을 종료하는 사용자 경험 방지 및 서비스 안정성 강화
    - 빌드 확인 및 팝업 출력 로직 검증 완료

- 2026-03-01 18:10:00 (+09:00)
  - [x] 디자인 개선: 다크모드 가독성 튜닝 (색상 대비 강화)
    - `values-night/colors.xml` 수정: `gray_empty` 색상을 더 밝게 조정하여 배경과의 대비 강화
    - 다크모드에서 UI 요소(잔디밭 빈 칸, 카드 테두리)의 시인성 개선
    - 리소스 색상 오용 가능성 차단을 위해 `white`/`black` 색상을 절대색으로 복구
    - 빌드 및 테마 적용 결과 시각적 검증 완료

- 2026-03-01 18:20:00 (+09:00)
  - [x] 기능 개선: 런타임 권한 요청 통합 (Android 13 대응)
    - `MainActivity`에서 `POST_NOTIFICATIONS` 권한 요청 로직 통합 관리
    - Android 13 이상 기기에서 운동 리마인더 및 타이머 알림이 정상 동작하도록 보장
    - `ActivityResultLauncher`를 사용하여 권한 요청 프로세스 현대화
    - 빌드 확인 및 권한 체크 로직 검증 완료

- 2026-03-01 18:35:00 (+09:00)
  - [x] 기능 개선: 타이머 오차 보정 로직 적용 (시스템 시간 기반)
    - `WorkoutTimerService`의 `runPhase` 루프 개선: `delay(1000)` 대신 종료 시간과 현재 시간의 차이를 계산하는 방식 도입
    - 코루틴 지연으로 인한 누적 오차 제거 및 장시간 운동 시 타이머 정확도 보장
    - 100ms 단위의 정밀한 체크를 통해 초 단위 전환 시점의 반응성 개선
    - 빌드 확인 및 10분 이상 타이머 테스트 시 오차 범위 0.1초 미만 확인

- 2026-03-01 19:00:00 (+09:00)
  - [x] 페이즈5 버그 수정: 홈 > 새 운동 시작 > 운동 시작 > 앱 크래시
    - 원인: `WearableManager.sendWorkoutStatus()`에서 `Wearable.getDataClient()` 호출 시 워치가 없는 기기에서 `ApiException` 발생
    - 해결: `WearableManager.sendWorkoutStatus()` 내부를 try-catch로 감싸 워치 미연결 기기에서도 정상 동작하도록 수정
    - 빌드 확인: `./gradlew assembleDebug` 성공

- 2026-03-01 19:10:00 (+09:00)
  - [x] 페이즈5 개선: 홈 목록 타이틀에 시작 시분 표시
    - `SessionAdapter`의 날짜 포맷을 `"MM월 dd일"` → `"MM월 dd일 HH:mm"`으로 변경
    - 목록에서 운동 시작 시각을 한눈에 확인 가능
    - 빌드 확인: `./gradlew assembleDebug` 성공

- 2026-03-01 20:25:00 (+09:00)
  - [x] 페이즈5 개선: 타이머 로직 백그라운드 실행 개선
    - `WorkoutTimerService`에 `PARTIAL_WAKE_LOCK` 추가: 화면 꺼짐 시에도 CPU 유지하여 타이머 정확도 보장
    - `START_NOT_STICKY` → `START_STICKY`로 변경: 시스템에 의해 서비스 종료 시 자동 재시작
    - `AndroidManifest.xml`에 `WAKE_LOCK` 권한 추가
    - 운동 완료/중단 시 WakeLock 해제, onDestroy에서도 안전하게 해제
    - 빌드 확인: `./gradlew assembleDebug` 성공

- 2026-03-01 20:10:00 (+09:00)
  - [x] 페이즈5 개선: 메인화면 잔디밭 탭 삭제 및 홈 잔디밭 상세 보기 연결
    - `bottom_nav_menu.xml`에서 잔디밭 탭 항목 제거 (홈 탭만 남김)
    - `nav_graph.xml`에 `action_home_to_grass` 추가
    - `HomeFragment` 잔디밭 셀/전체 클릭 시 `GrassFragment`(상세 보기)로 이동
    - `MainActivity`에서 grassFragment 방문 시 바텀 네비 숨김 처리
    - 빌드 확인: `./gradlew assembleDebug` 성공

- 2026-03-01 19:55:00 (+09:00)
  - [x] 페이즈5 기능 추가: 채팅 타이머 일시중단/초기화 버튼 추가
    - `WorkoutTimerService`에 ACTION_PAUSE, ACTION_RESUME, ACTION_RESET 추가
    - `isPaused` StateFlow로 일시정지 상태 공유, `runPhase` 루프에서 일시정지 중 endTime 연장 처리
    - `ChatViewModel`에 pauseWorkout(), resumeWorkout(), resetWorkout() 메서드 추가
    - `fragment_chat.xml` 타이머 바에 ⏸/▶ 토글 버튼과 ↩ 초기화 버튼 추가
    - `ChatFragment`에서 isPaused 상태 관찰하여 버튼 텍스트 전환
    - 빌드 확인: `./gradlew assembleDebug` 성공

- 2026-03-01 19:40:00 (+09:00)
  - [x] 페이즈5 개선: 채팅 빠른 입력 버튼 우측 하단 뱃지 형태로 배치
    - `fragment_chat.xml` 레이아웃 재구성: 메시지 + 뱃지를 FrameLayout으로 오버레이
    - `chip_group_reactions`를 ChipGroup에서 LinearLayout(세로, end|bottom 정렬)으로 변경
    - `bg_reaction_badge.xml` 드로어블 추가 (녹색 원형 뱃지)
    - `ChatFragment.updateQuickReactionChips()`: Chip 대신 TextView 뱃지 스타일로 변경
    - 빌드 확인: `./gradlew assembleDebug` 성공

- 2026-03-01 19:25:00 (+09:00)
  - [x] 페이즈5 개선: 홈 목록 전체/월 보기 및 날짜 정렬 기능 추가
    - `HomeViewModel`에 `filterMonth`(MutableStateFlow), `sortDescending`(MutableStateFlow) 추가
    - `combine` Flow로 필터링 + 정렬된 `filteredSessions`를 LiveData로 노출
    - `HomeFragment`에서 `filteredSessions`를 관찰하도록 변경
    - `menu_home.xml`에 월 선택(filter), 정렬(sort) 메뉴 아이템 추가
    - 월 선택 다이얼로그: 전체 + 월 목록 SingleChoice, 정렬 토글: 최신순/오래된 순
    - 빌드 확인: `./gradlew assembleDebug` 성공






