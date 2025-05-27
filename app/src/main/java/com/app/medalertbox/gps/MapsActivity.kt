package com.app.medalertbox.gps

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.medalertbox.R
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient
    private lateinit var geofenceHelper: GeofenceHelper

    private val GEOFENCE_ID = "USER_LOCATION_GEOFENCE"
    private val GEOFENCE_RADIUS = 100f
    private val LOCATION_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofenceHelper = GeofenceHelper(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissions = mutableListOf(Manifest.permission.ACCESS_FINE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        val notGranted = permissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, notGranted.toTypedArray(), LOCATION_PERMISSION_CODE)
        } else {
            enableMyLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val userLatLng = LatLng(location.latitude, location.longitude)

                // Zoom bounds
                val bounds = LatLngBounds.builder()
                    .include(LatLng(userLatLng.latitude + 0.0015, userLatLng.longitude + 0.0015))
                    .include(LatLng(userLatLng.latitude - 0.0015, userLatLng.longitude - 0.0015))
                    .build()
                map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                map.addMarker(MarkerOptions().position(userLatLng).title("You are here"))
                drawGeofenceCircle(userLatLng, GEOFENCE_RADIUS)
                removeGeofencesThenAdd(userLatLng, GEOFENCE_RADIUS)

            } else {
                Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawGeofenceCircle(latLng: LatLng, radius: Float) {
        map.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius.toDouble())
                .strokeColor(0x550000FF)
                .fillColor(0x220000FF)
                .strokeWidth(4f)
        )
    }

    @SuppressLint("MissingPermission")
    private fun removeGeofencesThenAdd(latLng: LatLng, radius: Float) {
        geofencingClient.removeGeofences(geofenceHelper.getGeofencePendingIntent())
            .addOnCompleteListener {
                addGeofence(latLng, radius)
            }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng: LatLng, radius: Float) {
        val geofence = geofenceHelper.getGeofence(
            GEOFENCE_ID,
            latLng,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )
        val request = geofenceHelper.getGeofencingRequest(geofence)
        val intent = geofenceHelper.getGeofencePendingIntent()

        geofencingClient.addGeofences(request, intent)
            .addOnSuccessListener {
                Toast.makeText(this, "Geofence Added", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                val error = geofenceHelper.getErrorString(it)
                Toast.makeText(this, "Failed: $error", Toast.LENGTH_LONG).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                enableMyLocation()
            } else {
                Toast.makeText(this, "All location permissions are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
