package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.textView.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.button.setOnClickListener {
            validateAndLogin()
        }
    }

    private fun validateAndLogin() {
        val email = binding.emailEt.text.toString().trim()
        val password = binding.passET.text.toString().trim()

        when {
            email.isEmpty() -> {
                showError("Please enter your email", binding.emailEt)
                return
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showError("Please enter a valid email address", binding.emailEt)
                return
            }
            password.isEmpty() -> {
                showError("Please enter your password", binding.passET)
                return
            }
            else -> performLogin(email, password)
        }
    }

    private fun performLogin(email: String, password: String) {
        showLoading(true)

        firebaseAuth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { emailTask ->
                if (emailTask.isSuccessful) {
                    val signInMethods = emailTask.result?.signInMethods

                    if (signInMethods.isNullOrEmpty()) {
                        // Email doesn't exist
                        showLoading(false)
                        showError("Email is incorrect", binding.emailEt)
                    } else {
                        // Email exists, check password
                        firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { loginTask ->
                                showLoading(false)
                                if (loginTask.isSuccessful) {
                                    val user = firebaseAuth.currentUser
                                    if (user != null) {
                                        checkUserRole(user)
                                    } else {
                                        showError("Login failed. Please try again.")
                                    }
                                } else {
                                    showError("Password is incorrect", binding.passET)
                                }
                            }
                    }
                } else {
                    showLoading(false)
                    showError("Failed to verify email. Please try again.")
                    Log.e("LoginError", "Error verifying email", emailTask.exception)
                }
            }
    }

    private fun checkUserRole(user: FirebaseUser) {
        showLoading(true)

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { document ->
                showLoading(false)
                when (document.getString("role")?.lowercase()?.trim()) {
                    "admin" -> navigateToDashboard(AdminDashboardActivity::class.java)
                    "senior" -> navigateToDashboard(SeniorDashboardActivity::class.java)
                    "caregiver" -> navigateToDashboard(CaregiverDashboardActivity::class.java)
                    else -> {
                        showError("Unknown user role")
                        handleInvalidRole()
                    }
                }
            }
            .addOnFailureListener { e ->
                showLoading(false)
                showError("Error fetching user data")
                handleInvalidRole()
                Log.e("LoginError", "Error fetching user role", e)
            }
    }

    private fun navigateToDashboard(activityClass: Class<*>) {
        Intent(this, activityClass).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)
        }
        finish()
    }

    private fun handleInvalidRole() {
        firebaseAuth.signOut()
        startActivity(Intent(this, LogInActivity::class.java))
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.button.isEnabled = !show
    }

    private fun showError(message: String, focusField: View? = null) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        focusField?.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.currentUser?.let { user ->
            checkUserRole(user)
        }
    }
}


