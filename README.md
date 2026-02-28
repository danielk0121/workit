# Workit (워킷)

> 채팅으로 기록하는 인터벌 운동 앱

[![Platform](https://img.shields.io/badge/Platform-Android-green)](https://android.com)
[![Language](https://img.shields.io/badge/Language-Kotlin-blue)](https://kotlinlang.org)
[![Min SDK](https://img.shields.io/badge/Min%20SDK-API%2026-orange)](https://developer.android.com)

---

## 소개

기존 인터벌 타이머(복싱 타이머)는 종소리만 울려서 운동을 모르는 사람은 헷갈립니다.
Workit은 채팅 형식으로 운동을 안내하고 기록해, 입문자도 직관적으로 사용할 수 있습니다.

| 기존 인터벌 타이머 | Workit |
|------------------|--------|
| 원형/막대 타이머 | 채팅 메시지 형태 |
| 종소리만 | TTS 음성 + 채팅 알람 + 진동 |
| 운동 후 별도 기록 입력 | 운동 자체가 채팅 기록 |
| 복싱 용어 혼재 | 모든 인터벌 운동에 범용 |
| 기록 없음 | 잔디밭으로 꾸준함 시각화 |

---

## 주요 화면

### 홈 화면
- 잔디밭 미리보기 (최근 3개월)
- 스트릭 배너 (🔥 N일 연속 운동 중!)
- 채팅 목록 (카카오톡 스타일 운동 기록)

### 운동 설정 화면
- 추천 프리셋 선택
  - 🟢 입문자용 — 걷기 2분 / 달리기 1분 / 6회
  - 🟡 초급자용 — 걷기 1분 / 달리기 2분 / 6회
  - 🔴 중급자용 — 걷기 1분 / 달리기 3분 / 8회
- 직접 설정 (준비시간 / 운동시간 / 휴식시간 / 반복횟수)
- TTS 음성 스타일 선택 (코치형 / 친구형 / 정보형)

### 채팅방 (운동 진행 화면)
- 챗봇이 운동 진행 상황을 채팅으로 안내
- 빠른 반응 버튼 (상황별 자동 전환)
- 휴식/완료 시 직접 입력창 활성화

```
[챗봇 🤖] 오늘 운동 시작할게요!
           준비 운동 하면서 기다려주세요 🙆
           ───────────────
           ⏱ 30초 후 출발!

[챗봇 🤖] 🏃 달리세요!
           ───────────────
           [ 3:00 ▶ 진행중 ]

[사용자 💬] 😤 힘들어...

[챗봇 🤖] 😮‍💨 휴식! 잘 했어요.
           [ 1:00 ▶ 진행중 ]

[챗봇 🤖] 🎉 오늘 운동 완료!
           총 시간: 28분 | 인터벌: 6회 완주
```

### 잔디밭 화면
- 깃허브 스타일 52주 히트맵
- 통계 (총 운동일, 최장 스트릭, 현재 스트릭)
- 셀 탭 시 해당 날짜 채팅방으로 이동

---

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Kotlin |
| 빌드 시스템 | Gradle (Kotlin DSL) |
| 최소 SDK | API 26 (Android 8.0) |
| 아키텍처 | MVVM (ViewModel + LiveData) |
| TTS | Android TextToSpeech API |
| 백그라운드 | ForegroundService + AlarmManager |
| DB | Room Database + Kotlin Coroutines |
| UI | RecyclerView, Navigation Component |
| 진동 | Vibrator / VibrationEffect |

---

## 패키지 정보

- **패키지명**: `dev.danielk.workit`
- **타겟 사용자**: 인터벌 운동 입문자 (복싱 타이머가 낯선 사람)
