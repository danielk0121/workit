package dev.danielk.workit.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.danielk.workit.databinding.ItemBadgeBinding
import dev.danielk.workit.model.Badge

class BadgeAdapter : ListAdapter<Badge, BadgeAdapter.BadgeViewHolder>(BadgeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val binding = ItemBadgeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BadgeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BadgeViewHolder(private val binding: ItemBadgeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(badge: Badge) {
            binding.tvBadgeName.text = badge.name
            binding.ivBadgeIcon.setImageResource(badge.iconResId)
            
            // Apply grayscale or transparency if not unlocked
            if (badge.isUnlocked) {
                binding.ivBadgeIcon.alpha = 1.0f
                binding.tvBadgeName.alpha = 1.0f
            } else {
                binding.ivBadgeIcon.alpha = 0.3f
                binding.tvBadgeName.alpha = 0.5f
            }
        }
    }

    class BadgeDiffCallback : DiffUtil.ItemCallback<Badge>() {
        override fun areItemsTheSame(oldItem: Badge, newItem: Badge): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Badge, newItem: Badge): Boolean = oldItem == newItem
    }
}
