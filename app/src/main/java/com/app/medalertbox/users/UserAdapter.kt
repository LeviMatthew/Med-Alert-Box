package com.app.medalertbox.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.medalertbox.R
import com.app.medalertbox.users.User
import com.app.medalertbox.users.UserDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserAdapter(
    private var users: List<User>,
    private val userDao: UserDao,  // Pass DAO to delete users
    private val onUserDeleted: () -> Unit  // Callback to refresh list
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.txtUserName)
        val userEmail: TextView = itemView.findViewById(R.id.txtUserEmail)
        val deleteButton: ImageView = itemView.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_user_adapter, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.userName.text = user.name
        holder.userEmail.text = user.email

        // Trash Bin Click - Delete User
        holder.deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                userDao.deleteUser(user)
                onUserDeleted() // Refresh list
            }
        }
    }

    override fun getItemCount(): Int = users.size

    // Update User List
    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
