package com.app.medalertbox.alarmandnotifications



// AlarmAdapter.kt
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.medalertbox.alarmandnotifications.AlarmNotification
import com.app.medalertbox.databinding.ItemAlarmBinding


class AlarmAdapter(private val listener: OnAlarmClickListener) :
    ListAdapter<AlarmNotification, AlarmAdapter.AlarmViewHolder>(AlarmDiffCallback()) {


    interface OnAlarmClickListener {
        fun onAlarmToggle(alarm: AlarmNotification, isActive: Boolean)
        fun onAlarmDelete(alarm: AlarmNotification)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }


    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(alarm: AlarmNotification) {
            binding.apply {
                medicationName.text = alarm.medicationName
                alarmTime.text = "${alarm.time} on ${alarm.date}"
                alarmSwitch.isChecked = alarm.isActive


                alarmSwitch.setOnCheckedChangeListener { _, isChecked ->
                    listener.onAlarmToggle(alarm, isChecked)
                }


                deleteButton.setOnClickListener {
                    listener.onAlarmDelete(alarm)
                }
            }
        }
    }
}


class AlarmDiffCallback : DiffUtil.ItemCallback<AlarmNotification>() {
    override fun areItemsTheSame(oldItem: AlarmNotification, newItem: AlarmNotification): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: AlarmNotification, newItem: AlarmNotification): Boolean {
        return oldItem == newItem
    }
}
