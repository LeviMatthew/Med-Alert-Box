package com.app.medalertbox

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.app.medalertbox.databinding.ActivitySettingsBinding
import com.app.medalertbox.settings.ChangePasswordActivity
import com.app.medalertbox.settings.PrivacySettingsActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    companion object {
        private const val PREFS_NAME = "settings"
        private const val KEY_NOTIFICATIONS_ENABLED = "notifications_enabled"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_ALARM_SOUNDS_ENABLED = "alarm_sounds_enabled"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupNavigationButtons()
        setupNotificationToggle()
        setupThemeToggle()
        setupAlarmSoundToggle()
        validateUserRole()
    }

    private fun setupNavigationButtons() {
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        binding.btnChangePassword.setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        binding.btnPrivacySettings.setOnClickListener {
            startActivity(Intent(this, PrivacySettingsActivity::class.java))
        }
    }

    private fun setupNotificationToggle() {
        val notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        binding.switchNotifications.isChecked = notificationsEnabled

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, isChecked).apply()
            val message = if (isChecked) "Notifications Enabled" else "Notifications Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupThemeToggle() {
        val darkThemeEnabled = sharedPreferences.getBoolean(KEY_DARK_THEME, false)
        binding.switchTheme.isChecked = darkThemeEnabled

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_DARK_THEME, isChecked).apply()

            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )

            val message = if (isChecked) "Dark Theme Enabled" else "Dark Theme Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAlarmSoundToggle() {
        val alarmSoundsEnabled = sharedPreferences.getBoolean(KEY_ALARM_SOUNDS_ENABLED, true)
        binding.switchAlarmSound.isChecked = alarmSoundsEnabled

        binding.switchAlarmSound.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean(KEY_ALARM_SOUNDS_ENABLED, isChecked).apply()
            val message = if (isChecked) "Alarm Sounds Enabled" else "Alarm Sounds Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateUserRole() {
        val userId = auth.currentUser?.uid
        if (userId.isNullOrEmpty()) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role") ?: "Unknown"
                    if (role == "Unknown") {
                        Toast.makeText(this, "Unknown role. Contact support.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "User data not found in Firestore.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load user role: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}
