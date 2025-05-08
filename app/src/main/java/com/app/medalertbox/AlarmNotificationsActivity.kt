package com.app.medalertbox

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.medalertbox.alarmandnotifications.*
import com.app.medalertbox.databinding.ActivityAlarmNotificationsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class AlarmNotificationsActivity : AppCompatActivity(), AlarmAdapter.OnAlarmClickListener {
    private lateinit var binding: ActivityAlarmNotificationsBinding
    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var alarmManagerHelper: AlarmManager
    private val viewModel: AlarmViewModel by viewModels {
        AlarmViewModelFactory(
            AlarmRepository(
                (application as AlarmApplication).database.alarmNotificationDao()
            )
        )
    }
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userType: String = "unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmNotificationsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        alarmManagerHelper = AlarmManager(this)

        userType = intent.getStringExtra("userType")?.lowercase() ?: "unknown"
        Log.d("AlarmNotifications", "Received userType: $userType")

        if (userType == "unknown") {
            fetchUserRoleFromFirebase()
        } else {
            checkUserAuthorization()
        }

        binding.homeButton.setOnClickListener { navigateToDashboard() }
    }

    private fun fetchUserRoleFromFirebase() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            showToast("User not logged in!")
            navigateToLogin()
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userType = document.getString("role")?.lowercase() ?: "unknown"
                    Log.d("AlarmNotifications", "UserType retrieved from Firebase: $userType")
                    checkUserAuthorization()
                } else {
                    showToast("User role not found!")
                    finish()
                }
            }
            .addOnFailureListener { e ->
                Log.e("AlarmNotifications", "Error fetching user role: ${e.message}")
                showToast("Failed to retrieve user role")
                finish()
            }
    }

    private fun checkUserAuthorization() {
        if (userType !in listOf("admin", "caregiver")) {
            showToast("Unauthorized access!")
            navigateToDashboard()
        } else {
            setupRecyclerView()
            observeAlarms()
            setupClickListeners()
        }
    }

    private fun navigateToDashboard() {
        val targetActivity = when (userType) {
            "admin" -> AdminDashboardActivity::class.java
            "caregiver" -> CaregiverDashboardActivity::class.java
            else -> {
                showToast("Unauthorized access!")
                finish()
                return
            }
        }

        startActivity(Intent(this, targetActivity).apply {
            putExtra("userType", userType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LogInActivity::class.java))
        finish()
    }

    private fun setupRecyclerView() {
        alarmAdapter = AlarmAdapter(this)
        binding.alarmsRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AlarmNotificationsActivity)
            adapter = alarmAdapter
        }
    }

    private fun observeAlarms() {
        viewModel.allAlarms.observe(this) { alarms ->
            if (alarms != null) {
                alarmAdapter.submitList(alarms)
            } else {
                Log.w("AlarmNotifications", "Alarms list is null")
            }
        }
    }

    private fun setupClickListeners() {
        binding.addAlarmButton.setOnClickListener {
            val medicationName = binding.medicationNameEditText.text.toString().trim()
            if (medicationName.isBlank()) {
                showToast("Please enter medication name")
                return@setOnClickListener
            }

            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                val time = formatTime(hourOfDay, minute)
                val date = getCurrentDate()

                val alarm = AlarmNotification(
                    medicationName = medicationName,
                    time = time,
                    date = date
                )

                viewModel.insert(alarm)
                alarmManagerHelper.scheduleAlarm(alarm)

                binding.medicationNameEditText.text.clear()
                showToast("Alarm set for $time on $date")
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
        }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }.time)
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Date())
    }

    override fun onAlarmToggle(alarm: AlarmNotification, isActive: Boolean) {
        val updatedAlarm = alarm.copy(isActive = isActive)
        viewModel.update(updatedAlarm)

        if (isActive) {
            alarmManagerHelper.scheduleAlarm(updatedAlarm)
        } else {
            alarmManagerHelper.cancelAlarm(alarm.id)
        }
    }

    override fun onAlarmDelete(alarm: AlarmNotification) {
        viewModel.delete(alarm)
        alarmManagerHelper.cancelAlarm(alarm.id)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}