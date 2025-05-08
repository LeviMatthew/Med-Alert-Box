package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityAdminDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Handle logout
        binding.btnLogoutAdmin.setOnClickListener {
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

        // Navigate to Inventory Management
        binding.btnInventory.setOnClickListener {
            startActivity(Intent(this, InventoryManagementActivity::class.java))
        }

        // Navigate to GPS Tracking & Geofencing
        binding.btnGPS.setOnClickListener {
            startActivity(Intent(this, GPSTrackingGeofencing::class.java))
        }

        // Navigate to User Profiles
        binding.btnUser.setOnClickListener {
            startActivity(Intent(this, UserProfilesActivity::class.java))
        }

        // Navigate to Reports & Analytics
        binding.btnReports.setOnClickListener {
            startActivity(Intent(this, ReportAnalyticsActivity::class.java))
        }

        // Navigate to Settings
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }
}
