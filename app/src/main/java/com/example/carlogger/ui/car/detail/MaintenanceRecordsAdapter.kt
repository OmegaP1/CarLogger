package com.example.carlogger.ui.car.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.carlogger.R
import com.example.carlogger.data.model.MaintenanceRecord
import com.example.carlogger.databinding.ItemMaintenanceRecordBinding
import java.text.SimpleDateFormat
import java.util.Locale

class MaintenanceRecordsAdapter(
    private val listener: MaintenanceRecordListener
) : ListAdapter<MaintenanceRecord, MaintenanceRecordsAdapter.MaintenanceRecordViewHolder>(MaintenanceRecordDiffCallback()) {

    interface MaintenanceRecordListener {
        fun onRecordClicked(record: MaintenanceRecord)
        fun onRecordMenuClicked(record: MaintenanceRecord, itemView: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceRecordViewHolder {
        val binding = ItemMaintenanceRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MaintenanceRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MaintenanceRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MaintenanceRecordViewHolder(
        private val binding: ItemMaintenanceRecordBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRecordClicked(getItem(position))
                }
            }

            binding.btnRecordOptions.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRecordMenuClicked(getItem(position), it)
                }
            }
        }

        fun bind(record: MaintenanceRecord) {
            // Set service type
            binding.tvServiceType.text = record.serviceType

            // Format and set date
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.tvDate.text = dateFormat.format(record.date)

            // Format and set odometer
            binding.tvOdometer.text = binding.root.context.getString(
                R.string.formatted_odometer,
                record.odometer.toString()
            )

            // Set description
            binding.tvDescription.text = record.description

            // Format and set cost
            binding.tvCost.text = binding.root.context.getString(
                R.string.formatted_cost,
                record.cost
            )

            // Set location
            binding.tvLocation.text = record.location.takeIf { it.isNotBlank() }
                ?: binding.root.context.getString(R.string.no_data)
        }
    }

    class MaintenanceRecordDiffCallback : DiffUtil.ItemCallback<MaintenanceRecord>() {
        override fun areItemsTheSame(oldItem: MaintenanceRecord, newItem: MaintenanceRecord): Boolean {
            return oldItem.recordId == newItem.recordId
        }

        override fun areContentsTheSame(oldItem: MaintenanceRecord, newItem: MaintenanceRecord): Boolean {
            return oldItem == newItem
        }
    }
}