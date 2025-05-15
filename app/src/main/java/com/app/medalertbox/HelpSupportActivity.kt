package com.app.medalertbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityHelpSupportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpSupportBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userType: String = "unknown"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Get userType from intent or fetch from Firebase
        userType = intent.getStringExtra("userType")?.lowercase() ?: "unknown"
        if (userType == "unknown") {
            fetchUserRoleFromFirebase()
        }

        // Back to Dashboard
        binding.btnHome.setOnClickListener {
            navigateToDashboard()
        }

        // FAQ Button
        binding.btnFaq.setOnClickListener {
            showDialog("Frequently Asked Questions", """
                • How to set a medication reminder?
                  → Go to the Medication tab and tap the '+' button.
                
                • How to reset my password?
                  → Tap 'Forgot Password' on the login screen.

                • Can I edit a reminder?
                  → Yes, tap the reminder in the list and select 'Edit'.

                • What happens if I miss a medication?
                  → The app logs it and you’ll see a missed alert.

                • Can I turn off reminders temporarily?
                  → Use the 'Pause Reminders' option in Settings.

                • How to change my registered phone/email?
                  → Go to Account Settings > Edit Profile.

                • Is there a dark mode?
                  → Yes, enable it under Settings > Display.

                • Can I sync with Google Calendar?
                  → Coming soon in future updates.

                • Can multiple caregivers access one account?
                  → Yes, if granted permission by the primary user.

                • How to enable alarm sound?
                  → Ensure media volume is up and not in Do Not Disturb mode.

                • Is an internet connection required?
                  → Only for syncing data and updates. Reminders work offline.

                • How to backup my data?
                  → Use Settings > Backup to Cloud.

                • Can I export medication logs?
                  → Yes, available under 'Reports'.

                • How to delete my account?
                  → Contact support via the Help & Support section.

                • Can I add custom medication images?
                  → Yes, during medication entry, tap the image icon.

                • What types of medications are supported?
                  → Pills, liquids, injections, and more.

                • Can I set reminders for appointments too?
                  → Yes, use the 'Appointments' section.

                • How to adjust time zone?
                  → App auto-syncs with phone time zone.

                • How do I change the language?
                  → Settings > Language & Region.

                • Can I use this app for family members?
                  → Yes, add profiles under Family Management.
            """.trimIndent())
        }

        // Troubleshooting Button
        binding.btnTroubleshooting.setOnClickListener {
            showDialog("Troubleshooting", """
                • Alarm not ringing?
                  → Check battery optimization settings.

                • Notifications not showing?
                  → Ensure app has notification permission.

                • App crashes on start?
                  → Reinstall or clear cache from phone settings.

                • Reminder times incorrect?
                  → Check your device’s time zone settings.

                • Alarms duplicated?
                  → Clear cache and reconfigure alarms.

                • App running slow?
                  → Restart the phone and free up memory.

                • Reminders not saving?
                  → Ensure all required fields are filled.

                • Can't log in?
                  → Check credentials or reset password.

                • App not installing?
                  → Check for sufficient storage space.

                • No sound during alarm?
                  → Increase media volume or change alarm tone.

                • Alarm repeats too frequently?
                  → Edit frequency settings in medication setup.

                • Notifications delayed?
                  → Check network and background data usage.

                • Feedback button not working?
                  → Ensure email app is installed.

                • Social media link broken?
                  → Try opening it in a browser.

                • Location not detected?
                  → Enable location services on device.

                • Medication images not loading?
                  → Check internet connection.

                • Can't edit medication?
                  → Make sure the medication is not archived.

                • App stuck on loading?
                  → Force close and relaunch.

                • "User not found" error?
                  → Double-check login credentials.

                • Can't delete a medication?
                  → Swipe left and confirm deletion.
            """.trimIndent())
        }

        // Contact email
        binding.tvEmail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            startActivity(Intent.createChooser(intent, "Send Email"))
        }

        // Phone
        binding.tvPhone.setOnClickListener {
            startActivity(Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+11234567890")
            })
        }

        // Social
        binding.tvSocial.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/MyAppSupport")))
        }

        // Feedback button
        binding.btnSubmitFeedback.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:cagapejohnmatthew@gmail.com")
                putExtra(Intent.EXTRA_SUBJECT, "Feedback for MedAlertBox")
            }
            startActivity(Intent.createChooser(intent, "Send Feedback"))
        }
    }

    private fun fetchUserRoleFromFirebase() {
        val user = firebaseAuth.currentUser ?: run {
            showToast("User not logged in!")
            navigateToLogin()
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                userType = document.getString("role")?.lowercase() ?: "unknown"
                Log.d("HelpSupport", "UserType: $userType")
            }
            .addOnFailureListener {
                showToast("Failed to fetch role.")
                finish()
            }
    }

    private fun navigateToDashboard() {
        val targetActivity = when (userType) {
            "caregiver" -> CaregiverDashboardActivity::class.java
            "senior" -> SeniorDashboardActivity::class.java
            else -> {
                showToast("Access denied.")
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

    private fun showDialog(title: String, message: String) {
        val scrollView = ScrollView(this)
        val textView = TextView(this).apply {
            text = message
            setPadding(40, 40, 40, 40)
            textSize = 16f
        }
        scrollView.addView(textView)

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(scrollView)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
