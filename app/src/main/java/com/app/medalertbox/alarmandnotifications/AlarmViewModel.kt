package com.app.medalertbox.alarmandnotifications


// AlarmViewModel.kt
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class AlarmViewModel(private val repository: AlarmRepository) : ViewModel() {
    val allAlarms: LiveData<List<AlarmNotification>> = repository.allAlarms.asLiveData()


    fun insert(alarm: AlarmNotification) = viewModelScope.launch {
        repository.insert(alarm)
    }


    fun update(alarm: AlarmNotification) = viewModelScope.launch {
        repository.update(alarm)
    }


    fun delete(alarm: AlarmNotification) = viewModelScope.launch {
        repository.delete(alarm)
    }
}