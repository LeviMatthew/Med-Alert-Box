package com.app.medalertbox.gps

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.GeofenceStatusCodes

class GeofenceHelper(private val context: Context) {

    fun getGeofence(id: String, latLng: com.google.android.gms.maps.model.LatLng, radius: Float, transitionTypes: Int): Geofence {
        return Geofence.Builder()
            .setRequestId(id)
            .setCircularRegion(latLng.latitude, latLng.longitude, radius)
            .setTransitionTypes(transitionTypes)
            .setLoiteringDelay(10000)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()
    }

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
    }

    fun getGeofencePendingIntent(): PendingIntent {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun getErrorString(e: Exception): String {
        return when ((e as? com.google.android.gms.common.api.ApiException)?.statusCode) {
            GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "Geofence not available"
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "Too many geofences"
            GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "Too many pending intents"
            else -> e.localizedMessage ?: "Unknown error"
        }
    }
}
