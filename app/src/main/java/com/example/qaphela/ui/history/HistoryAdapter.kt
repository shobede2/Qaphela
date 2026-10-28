package com.example.qaphela.ui.history

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qaphela.R
import com.example.qaphela.data.local.ScanEntity
import com.example.qaphela.databinding.ItemHistoryBinding
import java.util.Date

class HistoryAdapter(
    private val onItemClick: (ScanEntity) -> Unit,
    private val onDeleteClick: (ScanEntity) -> Unit
) : ListAdapter<ScanEntity, HistoryAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ScanEntity) {
            val context = binding.root.context
            binding.tvHistoryType.text = if (item.scanType == "SMS") {
                context.getString(R.string.scan_sms_title)
            } else {
                context.getString(R.string.check_link_title)
            }

            binding.imgHistoryTypeIcon.setImageResource(
                if (item.scanType == "SMS") R.drawable.ic_sms else R.drawable.ic_link
            )

            binding.tvHistoryContent.text = item.content
            binding.tvHistoryTime.text = DateFormat.format("dd MMM yyyy, HH:mm", Date(item.scanTime))

            val (riskText, colorRes) = when (item.riskLevel) {
                "HIGH_RISK" -> Pair(context.getString(R.string.risk_high), R.color.risk_high)
                "SUSPICIOUS" -> Pair(context.getString(R.string.risk_suspicious), R.color.risk_suspicious)
                "NO_INDICATORS" -> Pair(context.getString(R.string.risk_no_indicators), R.color.risk_low)
                else -> Pair(context.getString(R.string.risk_unable), R.color.risk_unknown)
            }

            binding.tvHistoryRiskBadge.text = riskText
            binding.tvHistoryRiskBadge.setBackgroundColor(ContextCompat.getColor(context, colorRes))

            binding.root.setOnClickListener { onItemClick(item) }
            binding.btnDeleteHistoryItem.setOnClickListener { onDeleteClick(item) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ScanEntity>() {
        override fun areItemsTheSame(oldItem: ScanEntity, newItem: ScanEntity): Boolean {
            return oldItem.scanId == newItem.scanId
        }

        override fun areContentsTheSame(oldItem: ScanEntity, newItem: ScanEntity): Boolean {
            return oldItem == newItem
        }
    }
}
