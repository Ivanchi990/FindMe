package com.example.findme.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.findme.R
import com.example.findme.databinding.FragmentMapaBinding
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class FragmentMapa : Fragment()
{
    lateinit var binding: FragmentMapaBinding
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
        val frag = FragmentMapaBinding.inflate(inflater, container, false)
        binding = frag

        binding.floatingActionButton.setOnClickListener{
            mostrarUbi()
        }

        mapView = binding.mapView
        mapView?.getMapboxMap()?.loadStyleUri(Style.MAPBOX_STREETS,
            object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    addAnnotationToMap()
                }
            }
        )

        return frag.root
    }

    private fun mostrarUbi()
    {
        val transaction = fragmentManager?.beginTransaction()

        transaction?.replace(R.id.fragmentContainerView, FragmentMapa2())
        transaction?.addToBackStack(null)
        transaction?.commit()
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
        mapView?.onDestroy()
    }

    private fun addAnnotationToMap()
    {
        bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker_icon
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView!!)
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(-3.650556, 40.418560))
                .withIconImage(it)
            pointAnnotationManager?.create(pointAnnotationOptions)
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap?
    {
        if (sourceDrawable == null)
        {
            return null
        }
        return if (sourceDrawable is BitmapDrawable)
        {
            sourceDrawable.bitmap
        }
        else
        {
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )

            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}