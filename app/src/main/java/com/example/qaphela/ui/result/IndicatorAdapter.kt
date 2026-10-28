package com.example.qaphela.ui.result

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qaphela.R
import com.example.qaphela.databinding.ItemIndicatorBinding
import com.example.qaphela.domain.model.DetectedIndicator

class IndicatorAdapter : ListAdapter<DetectedIndicator, IndicatorAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemIndicatorBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DetectedIndicator) {
            val context = binding.root.context
            binding.tvIndicatorTitle.text = item.title
            binding.tvIndicatorDesc.text = item.description

            if (item.isHighRisk) {
                binding.imgIndicatorIcon.setImageResource(R.drawable.ic_warning)
                binding.imgIndicatorIcon.setColorFilter(ContextCompat.getColor(context, R.color.risk_high))
            } else {
                binding.imgIndicatorIcon.setImageResource(R.drawable.ic_info)
                binding.imgIndicatorIcon.setColorFilter(ContextCompat.getColor(context, R.color.risk_suspicious))
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DetectedIndicator>() {
        override fun areItemsTheSame(oldItem: DetectedIndicator, newItem: DetectedIndicator): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: DetectedIndicator, newItem: DetectedIndicator): Boolean {
            return oldItem == newItem
        }
    }
}
