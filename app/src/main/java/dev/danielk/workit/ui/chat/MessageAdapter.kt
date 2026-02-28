package dev.danielk.workit.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.danielk.workit.data.db.entity.ChatMessage
import dev.danielk.workit.databinding.ItemMessageBotBinding
import dev.danielk.workit.databinding.ItemMessageUserBinding
import dev.danielk.workit.model.MessageType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter : ListAdapter<ChatMessage, RecyclerView.ViewHolder>(DIFF) {

    inner class BotViewHolder(private val binding: ItemMessageBotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.tvMessage.text = msg.content
            binding.tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
        }
    }

    inner class UserViewHolder(private val binding: ItemMessageUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(msg: ChatMessage) {
            binding.tvMessage.text = msg.content
            binding.tvTime.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.timestamp))
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (getItem(position).type == MessageType.BOT) VIEW_BOT else VIEW_USER

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_BOT) {
            BotViewHolder(ItemMessageBotBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            UserViewHolder(ItemMessageUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BotViewHolder -> holder.bind(getItem(position))
            is UserViewHolder -> holder.bind(getItem(position))
        }
    }

    companion object {
        private const val VIEW_BOT = 0
        private const val VIEW_USER = 1

        private val DIFF = object : DiffUtil.ItemCallback<ChatMessage>() {
            override fun areItemsTheSame(a: ChatMessage, b: ChatMessage) = a.id == b.id
            override fun areContentsTheSame(a: ChatMessage, b: ChatMessage) = a == b
        }
    }
}
