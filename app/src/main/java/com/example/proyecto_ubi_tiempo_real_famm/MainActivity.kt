package com.example.proyecto_ubi_tiempo_real_famm

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.firebase.database.FirebaseDatabase

// IMPORTS PARA MAPBOX
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var textoEstado: TextView
    private lateinit var botonRastrear: Button
    private lateinit var mapView: MapView
    private var circleAnnotationManager: CircleAnnotationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoEstado = findViewById(R.id.textoEstado)
        botonRastrear = findViewById(R.id.botonRastrear)
        mapView = findViewById(R.id.mapView)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations){
                    val latitud = location.latitude
                    val longitud = location.longitude
                    //mostrar texto en pantalla
                    textoEstado.text = "Ubicación:\nLat: $latitud\nLng: $longitud"
                    //guardamos en firebase
                    val database = FirebaseDatabase.getInstance()
                    val miReferencia = database.getReference("ubicacion_actual")
                    miReferencia.child("lat").setValue(latitud)
                    miReferencia.child("lng").setValue(longitud)

                    // MARCADOR CIRCULAR (EL PUNTO ROJO)
                    val point = Point.fromLngLat(longitud, latitud)

                    if (circleAnnotationManager == null) {
                        circleAnnotationManager = mapView.annotations.createCircleAnnotationManager()
                    }
                    // Borramos el rastro anterior
                    circleAnnotationManager?.deleteAll()
                    // Configuramos el círculo rojo
                    val circleAnnotationOptions = CircleAnnotationOptions()
                        .withPoint(point)
                        .withCircleRadius(10.0) // Tamaño del punto
                        .withCircleColor("#EE4E4E") // Color rojo vibrante
                        .withCircleStrokeWidth(2.0) // Bordecito
                        .withCircleStrokeColor("#FFFFFF") // Borde blanco para que resalte

                    circleAnnotationManager?.create(circleAnnotationOptions)

                    mapView.mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(point)
                            .zoom(15.0)
                            .build()
                    )
                }
            }
        }

        botonRastrear.setOnClickListener {
            pedirPermisosYEmpezar()
        }
    }

    private fun pedirPermisosYEmpezar() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }

        textoEstado.text = "Buscando satélites..."
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).build()

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        Toast.makeText(this, "Rastreo iniciado", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() { super.onStart(); mapView.onStart() }
    override fun onStop() { super.onStop(); mapView.onStop() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
}