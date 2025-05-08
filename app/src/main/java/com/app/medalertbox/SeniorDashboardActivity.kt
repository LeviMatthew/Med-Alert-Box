package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivitySeniorDashboardBinding
import com.google.firebase.auth.FirebaseAuth

class SeniorDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeniorDashboardBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySeniorDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Handle logout
        binding.btnLogoutSenior.setOnClickListener {
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

        // Navigate to User Profiles
        binding.btnUserProfile.setOnClickListener {
            startActivity(Intent(this, UserProfilesActivity::class.java))
        }

        binding.btnHelp.setOnClickListener {
            startActivity(Intent(this, HelpSupportActivity::class.java))
        }

    }
}
