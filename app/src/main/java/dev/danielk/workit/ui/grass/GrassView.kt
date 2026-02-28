package dev.danielk.workit.ui.grass

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import dev.danielk.workit.data.db.entity.GrassRecord
import dev.danielk.workit.model.GrassGrade
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class GrassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cellSize = dpToPx(14f)
    private val cellGap = dpToPx(2f)
    private val step = cellSize + cellGap
    private val cornerRadius = dpToPx(2f)

    private var records: Map<String, GrassRecord> = emptyMap()
    private var weeks: List<List<String?>> = emptyList()
    private var weeksCount: Int = 52

    var onDateClick: ((String) -> Unit)? = null

    // Grade color map
    private val gradeColors = mapOf(
        GrassGrade.NONE to Color.parseColor("#E8E8E8"),
        GrassGrade.PARTIAL to Color.parseColor("#9BE9A8"),
        GrassGrade.COMPLETE to Color.parseColor("#40C463"),
        GrassGrade.BEST to Color.parseColor("#F5C543"),
        GrassGrade.SPECIAL to Color.parseColor("#E05B4B")
    )

    fun setWeeksCount(count: Int) {
        weeksCount = count
        weeks = buildWeeks()
        requestLayout()
        invalidate()
    }

    fun setRecords(recordList: List<GrassRecord>) {
        records = recordList.associateBy { it.date }
        weeks = buildWeeks()
        invalidate()
    }

    private fun buildWeeks(): List<List<String?>> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        // Go back weeksCount from Sunday of current week
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        val endCal = cal.clone() as Calendar
        cal.add(Calendar.WEEK_OF_YEAR, -(weeksCount - 1))

        val allWeeks = mutableListOf<List<String?>>()
        while (!cal.after(endCal)) {
            val week = mutableListOf<String?>()
            for (day in 0..6) {
                val dayCal = cal.clone() as Calendar
                dayCal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY + day)
                val today = Calendar.getInstance()
                week.add(
                    if (!dayCal.after(today)) sdf.format(dayCal.time) else null
                )
            }
            allWeeks.add(week)
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }
        return allWeeks
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = (weeks.size * step + cellGap).toInt()
        val h = (7 * step + cellGap).toInt()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        weeks.forEachIndexed { weekIdx, week ->
            week.forEachIndexed { dayIdx, date ->
                val left = weekIdx * step + cellGap
                val top = dayIdx * step + cellGap
                val rect = RectF(left, top, left + cellSize, top + cellSize)
                val grade = if (date != null) records[date]?.grade ?: GrassGrade.NONE else GrassGrade.NONE
                paint.color = gradeColors[grade] ?: Color.LTGRAY
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val weekIdx = (event.x / step).toInt().coerceIn(0, weeks.size - 1)
            val dayIdx = (event.y / step).toInt().coerceIn(0, 6)
            val date = weeks.getOrNull(weekIdx)?.getOrNull(dayIdx)
            if (date != null) {
                onDateClick?.invoke(date)
            }
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun dpToPx(dp: Float): Float =
        dp * resources.displayMetrics.density
}
