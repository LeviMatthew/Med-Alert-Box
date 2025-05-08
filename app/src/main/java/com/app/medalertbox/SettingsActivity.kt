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

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var sharedPreferences: SharedPreferences

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

        setupNavigationButtons()
        setupNotificationToggle()
        setupThemeToggle()
        setupAlarmSoundToggle() // New method
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
                if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_NO
            )

            val message = if (isChecked) "Dark Theme Enabled" else "Dark Theme Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAlarmSoundToggle() {
        // Fetch alarm sound preference from SharedPreferences
        val alarmSoundsEnabled = sharedPreferences.getBoolean(KEY_ALARM_SOUNDS_ENABLED, true)

        // Set the state of the SwitchCompat widget to match the preference
        binding.switchAlarmSound.isChecked = alarmSoundsEnabled

        // Set a listener to update SharedPreferences when the user toggles the switch
        binding.switchAlarmSound.setOnCheckedChangeListener { _, isChecked ->
            // Save the new preference to SharedPreferences
            sharedPreferences.edit().putBoolean(KEY_ALARM_SOUNDS_ENABLED, isChecked).apply()

            // Show a toast to confirm the action
            val message = if (isChecked) "Alarm Sounds Enabled" else "Alarm Sounds Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
