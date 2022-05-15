package com.example.snoskred.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.snoskred.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

 class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnRequestPermissionsResultCallback {

    private var permissionDenied = false
    private lateinit var mMap: GoogleMap


    private val callback = OnMapReadyCallback { googleMap ->
        val myPlace = LatLng(68.43, 17.42)
        googleMap.addMarker(MarkerOptions().position(myPlace).title("Marker in Narvik"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12f))

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        enableMyLocation()


    }

    override fun onMarkerClick(p0: Marker): Boolean = false


    @SuppressLint("MissingPermission")
    private fun enableMyLocation() = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        } else {
            Toast.makeText(
                requireContext(),
                "Beklager, du har valgt å ikke dele din posisjon",
                Toast.LENGTH_SHORT
            ).show()
            view?.findNavController()?.navigate(R.id.action_mapFragment_to_loginFragment)
            permissionDenied = true
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(requireContext(), "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(requireContext(), "Current location:\n$p0", Toast.LENGTH_LONG)
            .show()
    }











}







