package com.example.findme.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.findme.R
import com.example.findme.databinding.FragmentMapa2Binding
import com.mapbox.maps.MapView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class FragmentMapa2 : Fragment()
{
    lateinit var binding: FragmentMapa2Binding
    lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val frag = FragmentMapa2Binding.inflate(inflater, container, false)
        binding = frag
        mapView = binding.mapView

        mapView!!.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        mapView!!.getMapboxMap().loadStyleUri(
            Style.SATELLITE_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
        }

        return frag.root
    }


    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView!!.getMapboxMap().setCamera(CameraOptions.Builder().bearing(it).build())

    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView!!.getMapboxMap().setCamera(CameraOptions.Builder().center(it).build())
        mapView!!.gestures.focalPoint = mapView!!.getMapboxMap().pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector)
        {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean
        {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private fun setupGesturesListener()
    {
        mapView!!.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent()
    {
        val locationComponentPlugin = mapView!!.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.kiko_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.kiko_icon,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        locationComponentPlugin.addOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
    }

    private fun onCameraTrackingDismissed()
    {
        Toast.makeText(requireContext(), "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView!!.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView!!.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView!!.gestures.removeOnMoveListener(onMoveListener)
    }

    override fun onStart()
    {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop()
    {
        super.onStop()
        mapView?.onStop()
    }

    override fun onLowMemory()
    {
        super.onLowMemory()
        mapView?.onLowMemory()
    }


    override fun onDestroy()
    {
        super.onDestroy()
        mapView!!.location
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView!!.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView!!.gestures.removeOnMoveListener(onMoveListener)
    }
}