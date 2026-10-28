package com.example.qaphela.ui.chatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qaphela.databinding.ItemChatMessageBinding
import com.example.qaphela.domain.model.ChatMessage

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: ChatMessage) {
            if (message.isUser) {
                binding.cardUserMessage.visibility = View.VISIBLE
                binding.tvUserMessage.text = message.text
                binding.cardAiMessage.visibility = View.GONE
            } else {
                binding.cardAiMessage.visibility = View.VISIBLE
                binding.tvAiMessage.text = message.text
                binding.cardUserMessage.visibility = View.GONE
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}
