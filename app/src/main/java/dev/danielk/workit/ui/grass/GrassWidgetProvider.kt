package dev.danielk.workit.ui.grass

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.widget.RemoteViews
import dev.danielk.workit.MainActivity
import dev.danielk.workit.R
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.data.repository.WorkoutRepository
import dev.danielk.workit.model.GrassGrade
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GrassWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.widget_grass)
        
        // Load data in Coroutine
        CoroutineScope(Dispatchers.IO).launch {
            val repository = WorkoutRepository.getInstance(context)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            // Get records for the last 12 weeks
            val cal = Calendar.getInstance()
            cal.add(Calendar.WEEK_OF_YEAR, -11)
            cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            val fromDate = sdf.format(cal.time)
            
            val records = repository.getGrassRecordsSince(fromDate).first().associateBy { it.date }
            val streak = repository.getCurrentStreak()
            
            // Draw grass bitmap
            val bitmap = drawGrassBitmap(context, records, 12)
            
            views.setImageViewBitmap(R.id.iv_widget_grass, bitmap)
            views.setTextViewText(R.id.tv_widget_streak, "🔥 ${streak}일")
            
            // Click to open main activity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.tv_widget_title, pendingIntent)
            views.setOnClickPendingIntent(R.id.iv_widget_grass, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun drawGrassBitmap(context: Context, records: Map<String, GrassRecord>, weeksCount: Int): Bitmap {
        val density = context.resources.displayMetrics.density
        val cellSize = 12f * density
        val cellGap = 2f * density
        val step = cellSize + cellGap
        val cornerRadius = 2f * density

        val w = (weeksCount * step + cellGap).toInt()
        val h = (7 * step + cellGap).toInt()
        
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        val gradeColors = mapOf(
            GrassGrade.NONE to Color.parseColor("#E8E8E8"),
            GrassGrade.PARTIAL to Color.parseColor("#9BE9A8"),
            GrassGrade.COMPLETE to Color.parseColor("#40C463"),
            GrassGrade.BEST to Color.parseColor("#F5C543"),
            GrassGrade.SPECIAL to Color.parseColor("#E05B4B")
        )

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val endCal = cal.clone() as Calendar
        cal.add(Calendar.WEEK_OF_YEAR, -(weeksCount - 1))

        var weekIdx = 0
        while (!cal.after(endCal)) {
            for (dayIdx in 0..6) {
                val dayCal = cal.clone() as Calendar
                dayCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY + dayIdx)
                
                val left = weekIdx * step + cellGap
                val top = dayIdx * step + cellGap
                val rect = RectF(left, top, left + cellSize, top + cellSize)
                
                val dateStr = sdf.format(dayCal.time)
                val grade = if (!dayCal.after(Calendar.getInstance())) {
                    records[dateStr]?.grade ?: GrassGrade.NONE
                } else GrassGrade.NONE
                
                paint.color = gradeColors[grade] ?: Color.LTGRAY
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            }
            weekIdx++
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }
        
        return bitmap
    }
}
