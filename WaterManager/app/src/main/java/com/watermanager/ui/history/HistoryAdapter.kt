package com.watermanager.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.watermanager.data.model.WaterLog
import com.watermanager.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(
    private val onDelete: (WaterLog) -> Unit
) : ListAdapter<WaterLog, HistoryAdapter.HistoryViewHolder>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<WaterLog>() {
            override fun areItemsTheSame(a: WaterLog, b: WaterLog) = a.id == b.id
            override fun areContentsTheSame(a: WaterLog, b: WaterLog) = a == b
        }
        private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    }

    inner class HistoryViewHolder(private val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(log: WaterLog) {
            binding.tvLogDate.text = dateFormat.format(Date(log.timestamp))
            binding.tvLogStart.text = "Filled at: ${log.startTime}"
            binding.tvLogDuration.text = "Duration: ${log.durationMinutes} min"
            binding.tvLogEnd.text = "Till: ${log.endTime}"
            binding.tvSentCount.text = "Sent to ${log.sentToCount} tenant(s)"
            binding.btnDeleteLog.setOnClickListener { onDelete(log) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryViewHolder(
            ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) =
        holder.bind(getItem(position))
}
