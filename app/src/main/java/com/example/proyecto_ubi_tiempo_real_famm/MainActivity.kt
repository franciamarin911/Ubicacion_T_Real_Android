package com.example.proyecto_ubi_tiempo_real_famm

import android.Manifest
import android.content.pm.PackageManager
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

    private lateinit var clienteUbicacion: FusedLocationProviderClient
    private lateinit var respuestaUbicacion: LocationCallback
    private lateinit var textoEstado: TextView
    private lateinit var botonRastrear: Button
    private lateinit var vistaMapa: MapView
    private var gestorAnotacionesCirculares: CircleAnnotationManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textoEstado = findViewById(R.id.textoEstado)
        botonRastrear = findViewById(R.id.botonRastrear)
        vistaMapa = findViewById(R.id.mapView)

        clienteUbicacion = LocationServices.getFusedLocationProviderClient(this)

        // *** LLAMADA A MAPBOX ***
        vistaMapa.mapboxMap.loadStyle(Style.MAPBOX_STREETS)

        respuestaUbicacion = object : LocationCallback() {
            override fun onLocationResult(resultadoUbicacion: LocationResult) {
                for (ubicacion in resultadoUbicacion.locations){
                    val latitud = ubicacion.latitude
                    val longitud = ubicacion.longitude

                    textoEstado.text = "Ubicación:\nLat: $latitud\nLng: $longitud"

                    // *** LLAMADA A FIREBASE ***
                    val baseDatos = FirebaseDatabase.getInstance()
                    val miReferencia = baseDatos.getReference("ubicacion_actual")
                    miReferencia.child("lat").setValue(latitud)
                    miReferencia.child("lng").setValue(longitud)

                    // MARCADOR CIRCULAR PARA EL MAPA
                    val puntoPosicion = Point.fromLngLat(longitud, latitud)

                    // *** LLAMADA A MAPBOX PARA EL MARCADOR ***
                    if (gestorAnotacionesCirculares == null) {
                        gestorAnotacionesCirculares = vistaMapa.annotations.createCircleAnnotationManager()
                    }

                    gestorAnotacionesCirculares?.deleteAll()

                    // DISEÑO PARA EL PUNTITO ROJO
                    val opcionesCirculo = CircleAnnotationOptions()
                        .withPoint(puntoPosicion)
                        .withCircleRadius(10.0)
                        .withCircleColor("#EE4E4E")
                        .withCircleStrokeWidth(2.0)
                        .withCircleStrokeColor("#FFFFFF")

                    gestorAnotacionesCirculares?.create(opcionesCirculo)

                    // *** LLAMADA A MAPBOX (CAMARA) ***
                    vistaMapa.mapboxMap.setCamera(
                        CameraOptions.Builder()
                            .center(puntoPosicion)
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
        val solicitudUbicacion = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000).build()

        // INICIA EL GPS
        clienteUbicacion.requestLocationUpdates(solicitudUbicacion, respuestaUbicacion, Looper.getMainLooper())
        Toast.makeText(this, "Rastreo iniciado", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() { super.onStart(); vistaMapa.onStart() }
    override fun onStop() { super.onStop(); vistaMapa.onStop() }
    override fun onDestroy() { super.onDestroy(); vistaMapa.onDestroy() }
}