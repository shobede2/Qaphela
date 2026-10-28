package com.example.qaphela.ui.blocklist

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qaphela.data.local.BlocklistEntity
import com.example.qaphela.databinding.ItemBlocklistBinding
import java.util.Date

class BlocklistAdapter : ListAdapter<BlocklistEntity, BlocklistAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBlocklistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemBlocklistBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BlocklistEntity) {
            binding.tvBlocklistIndicator.text = item.indicator
            binding.tvBlocklistType.text = item.indicatorType
            binding.tvBlocklistReason.text = item.reason
            binding.tvBlocklistSource.text = "Source: ${item.source}"
            binding.tvBlocklistUpdated.text = DateFormat.format("dd/MM/yyyy", Date(item.lastUpdated))
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<BlocklistEntity>() {
        override fun areItemsTheSame(oldItem: BlocklistEntity, newItem: BlocklistEntity): Boolean {
            return oldItem.blocklistId == newItem.blocklistId
        }

        override fun areContentsTheSame(oldItem: BlocklistEntity, newItem: BlocklistEntity): Boolean {
            return oldItem == newItem
        }
    }
}
