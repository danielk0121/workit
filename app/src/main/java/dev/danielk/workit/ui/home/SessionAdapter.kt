package dev.danielk.workit.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.danielk.workit.data.db.entity.WorkoutSession
import dev.danielk.workit.databinding.ItemSessionBinding
import dev.danielk.workit.tts.BotScript
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SessionAdapter(
    private val onClick: (WorkoutSession) -> Unit,
    private val onLongClick: (WorkoutSession) -> Unit,
    private val onSelectionChanged: (Int) -> Unit = {}
) : ListAdapter<WorkoutSession, SessionAdapter.ViewHolder>(DIFF) {

    private val selectedIds = mutableSetOf<Long>()
    var isSelectionMode = false
        private set

    fun enterSelectionMode(id: Long) {
        isSelectionMode = true
        selectedIds.add(id)
        notifyDataSetChanged()
        onSelectionChanged(selectedIds.size)
    }

    fun toggleSelection(id: Long) {
        if (selectedIds.contains(id)) selectedIds.remove(id) else selectedIds.add(id)
        notifyDataSetChanged()
        onSelectionChanged(selectedIds.size)
    }

    fun clearSelection() {
        isSelectionMode = false
        selectedIds.clear()
        notifyDataSetChanged()
        onSelectionChanged(0)
    }

    fun getSelectedIds(): Set<Long> = selectedIds.toSet()

    inner class ViewHolder(private val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: WorkoutSession) {
            binding.tvTitle.text = session.title
            binding.tvDate.text = SimpleDateFormat("MM월 dd일 HH:mm", Locale.KOREAN)
                .format(Date(session.date))
            binding.tvStatus.text = if (session.isCompleted) "✅ 완주" else "⚠️ 중단"
            binding.tvDuration.text = if (session.totalDurationSeconds > 0)
                "⏱ ${BotScript.formatTime(session.totalDurationSeconds)}" else ""

            val isSelected = selectedIds.contains(session.id)
            binding.root.setCardBackgroundColor(
                if (isSelected) Color.parseColor("#C8E6C9") else Color.TRANSPARENT
            )

            binding.root.setOnClickListener {
                if (isSelectionMode) toggleSelection(session.id)
                else onClick(session)
            }
            binding.root.setOnLongClickListener {
                if (!isSelectionMode) enterSelectionMode(session.id)
                else toggleSelection(session.id)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WorkoutSession>() {
            override fun areItemsTheSame(a: WorkoutSession, b: WorkoutSession) = a.id == b.id
            override fun areContentsTheSame(a: WorkoutSession, b: WorkoutSession) = a == b
        }
    }
}
