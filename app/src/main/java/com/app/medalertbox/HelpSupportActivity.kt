package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.medalertbox.databinding.ActivityHelpSupportBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HelpSupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHelpSupportBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHelpSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ✅ Retrieve userType from intent OR Firebase if missing
        userType = intent.getStringExtra("userType")?.lowercase()

        if (userType.isNullOrEmpty()) {
            Log.w("HelpSupport", "UserType missing in Intent. Fetching from Firebase.")
            fetchUserRoleFromFirebase()
        } else {
            Log.d("HelpSupport", "UserType received from Intent: $userType")
        }

        // ✅ Handle home button click
        binding.btnHome.setOnClickListener {
            navigateToDashboard()
        }
    }

    private fun fetchUserRoleFromFirebase() {
        val user = firebaseAuth.currentUser
        if (user == null) {
            Toast.makeText(this, "User not logged in!", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    userType = document.getString("role")?.lowercase()
                    Log.d("HelpSupport", "UserType retrieved from Firebase: $userType")
                }
            }
            .addOnFailureListener { e ->
                Log.e("HelpSupport", "Error fetching user type: ${e.message}")
            }
    }

    private fun navigateToDashboard() {
        if (userType.isNullOrEmpty()) {
            Toast.makeText(this, "User type not found!", Toast.LENGTH_SHORT).show()
            Log.e("HelpSupport", "UserType is null or empty")
            return
        }

        val targetActivity = when (userType) {
            "caregiver" -> CaregiverDashboardActivity::class.java
            "senior" -> SeniorDashboardActivity::class.java
            else -> {
                Toast.makeText(this, "Unauthorized access!", Toast.LENGTH_SHORT).show()
                Log.e("HelpSupport", "Unauthorized user type: $userType")
                return
            }
        }

        val intent = Intent(this, targetActivity).apply {
            putExtra("userType", userType)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
