package com.example.findme

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.findme.databinding.ActivityMainBinding
import com.mapbox.common.location.compat.permissions.PermissionsManager

class MainActivity : AppCompatActivity()
{
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        solicitarPermisos()

        binding = ActivityMainBinding.inflate(layoutInflater)
    }

    fun solicitarPermisos(): Boolean
    {
        if (!PermissionsManager.areLocationPermissionsGranted(this))
        {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
                )
            )
            if (PermissionsManager.areLocationPermissionsGranted(this))
                return true
            else
            {
                false
            }
        }
        return true
    }

    val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when
        {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}

            else ->
            {
                val toast = Toast.makeText(
                    this,
                    "Es necesario acceso a la ubicacion apra cargar MapBox",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }
        }
    }
}