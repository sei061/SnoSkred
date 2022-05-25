package com.example.snoskred.fragments
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.snoskred.R
import com.example.snoskred.SnoskredModelFactory
import com.example.snoskred.SnoskredViewModel
import com.example.snoskred.databinding.FragmentMapsBinding
import com.example.snoskred.repository.Repository
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: SnoskredViewModel

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //private var permissionDenied = false
    private lateinit var mMap: GoogleMap
    private val pERMISSION_ID = 42

    var currentLocation: LatLng = LatLng(0.0, 0.0)
    private val ofoten = LatLng(68.18, 17.45)

    fun getCurrentDate(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    fun getCurrentDatePlusOneDay(): String {
        val currentPlusOne = LocalDateTime.now().plusDays(1)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return currentPlusOne.format(formatter)
    }


    /*
    private val callback = OnMapReadyCallback { googleMap ->
        val myPlace = LatLng(68.43, 17.42)
        googleMap.addMarker(MarkerOptions().position(myPlace).title("Marker in Narvik"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12f))

    }
*/
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    false
                ) -> {
                }
                permissions.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    false
                ) -> {
                    Manifest.permission.ACCESS_COARSE_LOCATION
                    Log.d("Location", "COARSE LOCATION GRANTED")
                }
                else -> {
                    Log.d("Location", "NO LOCATION GRANTED")
                }
            }
        }

        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()

        val repository = Repository()
        val viewModelFactory = SnoskredModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SnoskredViewModel::class.java]

        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        binding.currentLoc.setOnClickListener {
            viewModel.getPost(currentLocation.latitude, currentLocation.longitude, 1)
            viewModel.myResponse.observe(viewLifecycleOwner) { response ->
                if (response != null) {
                    Log.d("MapsFragment", "Response: ${response.body()}")
                    Log.d("MapsFragment", "Response: ${response.body()?.get(0)?.RegionId}")
                }
            }
        }
        getLastLocation()
        val repository = Repository()
        val viewModelFactory = SnoskredModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SnoskredViewModel::class.java]
        viewModel.getPost(currentLocation.latitude, currentLocation.longitude, 1)
        viewModel.myResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.d("MapsFragment", "Response: ${response.body()}")
                Log.d("MapsFragment", "Response: ${response.body()?.get(0)?.RegionId}")
            }
        }
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)


    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Looper.myLooper()?.let {
            fusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                it
            )
        }

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        currentLocation = LatLng(location.latitude, location.longitude)
                        mMap.clear()
                        mMap.addMarker(MarkerOptions().position(currentLocation))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 10F))
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }


    // Check if location permissions are
    // granted to the application
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false

    }


    // If current location could not be located, use last location
    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            currentLocation = LatLng(mLastLocation.latitude, mLastLocation.longitude)
        }
    }

    // function to check if GPS is on
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            activity?.getSystemService(LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    // Request permissions if not granted before
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            pERMISSION_ID
        )
    }

    // What must happen when permission is granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == pERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        viewModel.myResponse.observe(viewLifecycleOwner) { response ->
            when (response.isSuccessful) {
                true -> {
                    response.body()?.let {
                        for (post in it) {
                            val region = post.RegionId
                            val dangerlevel = post.DangerLevel
                            if (region === 3015)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(ofoten)
                                        .title("Ofoten")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        //.icon(defaultMarkerGreen(dangerlevel <= 2))
                                        .icon(defaultMarkerYellow(dangerlevel in 2..4)))
                                        //.icon(defaultMarkerRed(dangerlevel >= 4)))







                        }
                    }
                }
            }
        }
    }


    private fun defaultMarkerGreen(b: Boolean): BitmapDescriptor? {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
    }

    private fun defaultMarkerRed(b: Boolean): BitmapDescriptor? {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
    }

    private fun defaultMarkerYellow(b: Boolean): BitmapDescriptor? {
        return BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
    }



}






