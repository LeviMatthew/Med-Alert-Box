package com.app.medalertbox

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.app.medalertbox.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class LogInActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // ✅ Navigate to SignUpActivity
        binding.textView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // ✅ Navigate to ForgotPasswordActivity
        binding.forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        // ✅ Handle login button click
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString().trim()
            val password = binding.passET.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        if (user != null) {
                            Log.d("LogInActivity", "Login successful! Checking role for user: ${user.uid}")
                            checkUserRole(user) // Proceed to role check immediately
                        }
                    } else {
                        val errorMessage = task.exception?.localizedMessage ?: "Login failed. Please try again."
                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        Log.e("LogInActivity", "Login error: $errorMessage")
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = firebaseAuth.currentUser
        if (user != null) {
            Log.d("LogInActivity", "User already logged in. Checking role...")
            checkUserRole(user)
        }
    }

    private fun checkUserRole(user: FirebaseUser) {
        val userId = user.uid

        Log.d("LogInActivity", "Fetching role for user: $userId")

        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")?.lowercase() // Ensure case-insensitive comparison

                    // ✅ Debugging log to confirm role retrieval
                    Log.d("FirestoreRoleCheck", "User role retrieved: $role")

                    when (role) {
                        "admin" -> navigateToDashboard(AdminDashboardActivity::class.java)
                        "senior" -> navigateToDashboard(SeniorDashboardActivity::class.java)
                        "caregiver" -> navigateToDashboard(CaregiverDashboardActivity::class.java)
                        else -> {
                            Toast.makeText(this, "Unknown role! Contact support.", Toast.LENGTH_SHORT).show()
                            Log.e("LogInActivity", "Unrecognized role: $role")
                            navigateToDashboard(MainActivity::class.java) // Default screen
                        }
                    }
                } else {
                    Toast.makeText(this, "User role not found!", Toast.LENGTH_SHORT).show()
                    Log.e("LogInActivity", "User role field missing in Firestore")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching role: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("LogInActivity", "Firestore error: ${e.message}")
            }
    }

    private fun navigateToDashboard(activityClass: Class<*>) {
        Log.d("Navigation", "Navigating to: ${activityClass.simpleName}")
        val intent = Intent(this, activityClass)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
class MedAlertApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        val prefs = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val darkThemeEnabled = prefs.getBoolean("dark_theme", false)

        if (darkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
