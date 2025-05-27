package com.app.medalertbox.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        // Check if the event is null or has an error
        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorMessage = "Geofencing error: ${geofencingEvent?.errorCode ?: "Unknown error"}"
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            Log.e("GeofenceReceiver", errorMessage)
            return
        }

        // Get the transition type
        val transitionType = geofencingEvent.geofenceTransition
        val transitionMessage = when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> "Entered geofence"
            Geofence.GEOFENCE_TRANSITION_EXIT -> "Exited geofence"
            Geofence.GEOFENCE_TRANSITION_DWELL -> "Dwelling in geofence"
            else -> "Unknown geofence transition"
        }

        // Get list of triggering geofences
        val triggeringGeofences = geofencingEvent.triggeringGeofences

        // Safely iterate through geofences
        if (!triggeringGeofences.isNullOrEmpty()) {
            for (geofence in triggeringGeofences) {
                val requestId = geofence.requestId
                val message = "$transitionMessage: $requestId"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                Log.d("GeofenceReceiver", message)
            }
        } else {
            Toast.makeText(context, "No triggering geofences found", Toast.LENGTH_SHORT).show()
            Log.w("GeofenceReceiver", "Triggering geofences list is null or empty")
        }
    }
}
