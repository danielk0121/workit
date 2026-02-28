package dev.danielk.workit.ui.stats

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.danielk.workit.databinding.ItemEmotionRoundBinding

class EmotionAdapter(private val flow: List<Pair<Int, List<String>>>) :
    RecyclerView.Adapter<EmotionAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemEmotionRoundBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Pair<Int, List<String>>) {
            binding.tvRound.text = "${item.first}R"
            binding.tvEmotions.text = if (item.second.isEmpty()) "평온 😊" else item.second.joinToString(" ")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemEmotionRoundBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(flow[position])
    }

    override fun getItemCount() = flow.size
}
