package com.app.medalertbox.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.AdminDashboardActivity
import com.app.medalertbox.databinding.ActivityPrivacySettingsBinding

class PrivacySettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivacySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAccountDetails()
        setupPrivacyPreferences()
        setupHomeButton()
    }

    private fun setupAccountDetails() {
        binding.btnUpdateAccount.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()

            if (name.isBlank() || email.isBlank() || phone.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Save updates to Firebase/Room/SharedPrefs
                Toast.makeText(this, "Account updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupPrivacyPreferences() {
        binding.switchProfileVisibility.setOnCheckedChangeListener { _, isChecked ->
            // TODO: Save visibility setting
            val message = if (isChecked) "Profile Visible" else "Profile Hidden"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        binding.switchShareActivity.setOnCheckedChangeListener { _, isChecked ->
            // TODO: Save sharing setting
            val message = if (isChecked) "Sharing Enabled" else "Sharing Disabled"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupHomeButton() {
        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }
    }
}
