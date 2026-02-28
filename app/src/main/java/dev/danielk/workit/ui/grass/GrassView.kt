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
import java.util.Locale

class GrassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#AAAAAA")
        textSize = dpToPx(9f)
    }

    private var cellSize = dpToPx(14f)
    private var cellGap = dpToPx(2f)
    private var step = cellSize + cellGap
    private val cornerRadius get() = cellSize * 0.15f

    // 월 레이블 영역 높이
    private val monthLabelHeight get() = dpToPx(14f)

    private var records: Map<String, GrassRecord> = emptyMap()
    private var weeks: List<List<String?>> = emptyList()
    private var weeksCount: Int = 52
    private var fillWidth: Boolean = false

    var onDateClick: ((String) -> Unit)? = null

    private val gradeColors = mapOf(
        GrassGrade.NONE     to Color.parseColor("#E8E8E8"),
        GrassGrade.PARTIAL  to Color.parseColor("#9BE9A8"),
        GrassGrade.COMPLETE to Color.parseColor("#40C463"),
        GrassGrade.BEST     to Color.parseColor("#F5C543"),
        GrassGrade.SPECIAL  to Color.parseColor("#E05B4B")
    )

    fun setWeeksCount(count: Int) {
        weeksCount = count
        weeks = buildWeeks()
        requestLayout()
        invalidate()
    }

    /** 부모 너비에 맞게 셀 크기를 자동 계산하도록 설정 */
    fun setFillWidth(fill: Boolean) {
        fillWidth = fill
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
                week.add(if (!dayCal.after(today)) sdf.format(dayCal.time) else null)
            }
            allWeeks.add(week)
            cal.add(Calendar.WEEK_OF_YEAR, 1)
        }
        return allWeeks
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        if (fillWidth && specWidth > 0 && weeks.isNotEmpty()) {
            // 부모 너비에 맞게 셀 크기 계산
            val totalGap = cellGap * (weeks.size + 1)
            cellSize = ((specWidth - totalGap) / weeks.size).coerceAtLeast(dpToPx(6f))
            step = cellSize + cellGap
        }
        val w = if (fillWidth && specWidth > 0) specWidth
                else (weeks.size * step + cellGap).toInt()
        val h = (monthLabelHeight + 7 * step + cellGap).toInt()
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val monthSdf = SimpleDateFormat("M월", Locale.KOREAN)
        var lastMonth = ""

        weeks.forEachIndexed { weekIdx, week ->
            // 월 레이블: 첫 번째 날짜(일요일)에서 월 확인
            val firstDate = week.firstOrNull { it != null }
            if (firstDate != null) {
                try {
                    val cal = Calendar.getInstance().apply { time = sdf.parse(firstDate)!! }
                    val monthLabel = monthSdf.format(cal.time)
                    if (monthLabel != lastMonth) {
                        lastMonth = monthLabel
                        val x = weekIdx * step + cellGap
                        canvas.drawText(monthLabel, x, monthLabelHeight - dpToPx(2f), labelPaint)
                    }
                } catch (_: Exception) {}
            }

            // 셀 그리기
            week.forEachIndexed { dayIdx, date ->
                val left = weekIdx * step + cellGap
                val top = monthLabelHeight + dayIdx * step + cellGap
                val rect = RectF(left, top, left + cellSize, top + cellSize)
                val grade = if (date != null) records[date]?.grade ?: GrassGrade.NONE else GrassGrade.NONE
                paint.color = gradeColors[grade] ?: Color.LTGRAY
                canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val adjustedY = event.y - monthLabelHeight
            val weekIdx = (event.x / step).toInt().coerceIn(0, weeks.size - 1)
            val dayIdx = (adjustedY / step).toInt().coerceIn(0, 6)
            val date = weeks.getOrNull(weekIdx)?.getOrNull(dayIdx)
            if (date != null) onDateClick?.invoke(date)
            performClick()
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun dpToPx(dp: Float): Float = dp * resources.displayMetrics.density
}
