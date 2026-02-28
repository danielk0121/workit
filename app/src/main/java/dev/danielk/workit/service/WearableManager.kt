package dev.danielk.workit.service

import android.content.Context
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import dev.danielk.workit.model.WorkoutState

object WearableManager {

    private const val PATH_WORKOUT = "/workout_status"
    private const val KEY_STATE = "state"
    private const val KEY_TIME = "remaining_time"
    private const val KEY_ROUND = "current_round"
    private const val KEY_TOTAL_ROUNDS = "total_rounds"
    private const val KEY_TIMESTAMP = "timestamp"

    fun sendWorkoutStatus(
        context: Context,
        state: WorkoutState,
        remainingSeconds: Int,
        currentRound: Int,
        totalRounds: Int
    ) {
        try {
            val dataClient = Wearable.getDataClient(context)
            val putDataMapReq = PutDataMapRequest.create(PATH_WORKOUT)

            putDataMapReq.dataMap.apply {
                putString(KEY_STATE, state.name)
                putInt(KEY_TIME, remainingSeconds)
                putInt(KEY_ROUND, currentRound)
                putInt(KEY_TOTAL_ROUNDS, totalRounds)
                putLong(KEY_TIMESTAMP, System.currentTimeMillis())
            }

            val putDataReq = putDataMapReq.asPutDataRequest().setUrgent()
            dataClient.putDataItem(putDataReq)
        } catch (_: Exception) {
            // 워치가 연결되지 않은 기기에서는 무시
        }
    }
}
