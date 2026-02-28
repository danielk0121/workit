package dev.danielk.workit.data

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dev.danielk.workit.model.TtsStyle

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var ttsStyle: TtsStyle
        get() = TtsStyle.valueOf(prefs.getString(KEY_TTS_STYLE, TtsStyle.COACH.name) ?: TtsStyle.COACH.name)
        set(value) = prefs.edit().putString(KEY_TTS_STYLE, value.name).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var isReminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_ENABLED, value).apply()

    var monthlyGoal: Int
        get() = prefs.getInt(KEY_MONTHLY_GOAL, 10)
        set(value) = prefs.edit().putInt(KEY_MONTHLY_GOAL, value).apply()

    var isSampleDataInserted: Boolean
        get() = prefs.getBoolean(KEY_SAMPLE_DATA_INSERTED, false)
        set(value) = prefs.edit().putBoolean(KEY_SAMPLE_DATA_INSERTED, value).apply()

    fun applySettings() {
        val mode = if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    companion object {
        private const val PREFS_NAME = "workit_prefs"
        private const val KEY_TTS_STYLE = "key_tts_style"
        private const val KEY_DARK_MODE = "key_dark_mode"
        private const val KEY_REMINDER_ENABLED = "key_reminder_enabled"
        private const val KEY_MONTHLY_GOAL = "key_monthly_goal"
        private const val KEY_SAMPLE_DATA_INSERTED = "key_sample_data_inserted"
        
        @Volatile private var INSTANCE: PreferenceManager? = null
        fun getInstance(context: Context): PreferenceManager {
            return INSTANCE ?: synchronized(this) {
                PreferenceManager(context).also { INSTANCE = it }
            }
        }
    }
}
