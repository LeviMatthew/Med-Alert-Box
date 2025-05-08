package com.app.medalertbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.medalertbox.databinding.ActivityUserProfilesBinding
import com.app.medalertbox.users.AppDatabase
import com.app.medalertbox.users.User
import com.app.medalertbox.users.UserAdapter
import com.app.medalertbox.users.UserDao
import kotlinx.coroutines.launch

class UserProfilesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfilesBinding
    private lateinit var userAdapter: UserAdapter
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserProfilesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Setup RecyclerView
        userAdapter = UserAdapter(emptyList(), userDao) { loadUsers() }
        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewUsers.adapter = userAdapter

        // Load users on start
        loadUsers()

        // 🔹 Add User
        binding.btnAddUser.setOnClickListener {
            val name = binding.editTextName.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    val newUser = User(name = name, email = email)
                    userDao.insertUser(newUser)
                    Log.d("Database", "User added: ${newUser.name}")
                    loadUsers() // Refresh list

                    // Clear input fields
                    binding.editTextName.text.clear()
                    binding.editTextEmail.text.clear()
                }
            }
        }

        // 🔹 Home button click
        binding.btnHome.setOnClickListener {
            val intent = Intent(this, AdminDashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // 🔹 Load Users into RecyclerView
    private fun loadUsers() {
        lifecycleScope.launch {
            val users = userDao.getAllUsers()
            userAdapter.updateUsers(users)
        }
    }
}

