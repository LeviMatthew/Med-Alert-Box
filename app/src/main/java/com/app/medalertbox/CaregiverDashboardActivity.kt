package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityCaregiverDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

class CaregiverDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCaregiverDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCaregiverDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        // Handle logout
        binding.btnLogoutCaregiver.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LogInActivity::class.java))
            finish()
        }
        // Navigate to Medication Schedule
        binding.btnMedication.setOnClickListener {
            startActivity(Intent(this, MedicationScheduleActivity::class.java))
        }
        // Navigate to Alarm Notifications
        binding.btnAlarm.setOnClickListener {
            startActivity(Intent(this, AlarmNotificationsActivity::class.java))
        }
        // Navigate to GPS Tracking & Geofencing
        binding.btnGPS.setOnClickListener {
            startActivity(Intent(this, GPSTrackingGeofencing::class.java))
        }
        // Navigate to Reports & Analytics
        binding.btnReports.setOnClickListener {
            startActivity(Intent(this, ReportAnalyticsActivity::class.java))
        }

        binding.btnHelp.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }

    }
}

