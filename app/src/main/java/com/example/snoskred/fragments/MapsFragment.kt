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
import androidx.navigation.findNavController
import com.example.snoskred.MainActivity
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private lateinit var viewModel: SnoskredViewModel

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    //private var permissionDenied = false
    private lateinit var mMap: GoogleMap
    private val pERMISSION_ID = 42

    var currentLocation: LatLng = LatLng(0.0, 0.0)
    private val ofoten = LatLng(68.452, 17.458)
    private val nordenSkiold = LatLng(77.8666, 15.3166)
    private val finnmarksKyst = LatLng(70.2673, 21.6014)
    private val vestFinnmark = LatLng(70.000, 25.000)
    private val nordTroms = LatLng(69.3333, 17.4999)
    private val lyngsAlp = LatLng(69.7903, 20.1695)
    private val tromsø = LatLng(69.6492, 18.9553)
    private val sørTroms = LatLng(68.747, 17.8055)
    private val indreTroms = LatLng(68.8605, 18.3505)
    private val lofotVesterål = LatLng(68.6908, 15.4078)
    private val salten = LatLng(67.259184, 15.391632)
    private val svartisen = LatLng(66.6333308, 14.0000)
    private val helgeland = LatLng(66.03499986, 12.70499718)


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
        (activity as MainActivity).supportActionBar?.title = "Kart"
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
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 9F))
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
    var etnavn = 0

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.setOnInfoWindowClickListener(this)


        viewModel.myResponse.observe(viewLifecycleOwner) { response ->
            when (response.isSuccessful) {
                true -> {
                    response.body()?.let {
                        for (post in it) {
                            val region = post.RegionId
                            etnavn = region
                            val dangerLevel = post.DangerLevel
                            var color: Float = 0F
                            when (dangerLevel) {
                                0 -> color = BitmapDescriptorFactory.HUE_GREEN
                                2 -> color = BitmapDescriptorFactory.HUE_YELLOW
                                4 -> color = BitmapDescriptorFactory.HUE_RED
                            }
                            var icon = BitmapDescriptorFactory.defaultMarker(color)
                            if (region === 3015)

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(ofoten)
                                        .title("Ofoten")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )

                            if (region === 3003)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(nordenSkiold)
                                        .title("Nordenskiöld Land")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3006)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(finnmarksKyst)
                                        .title("Finnmarkskysten")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3007)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(vestFinnmark)
                                        .title("Vest-Finnmark")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3009)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(nordTroms)
                                        .title("Nord-Troms")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3010)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(lyngsAlp)
                                        .title("Lyngen")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3011)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(tromsø)
                                        .title("Tromsø")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3012)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(sørTroms)
                                        .title("Sør-Troms")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3013)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(indreTroms)
                                        .title("Indre-Troms")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3014)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(lofotVesterål)
                                        .title("Lofoten og Vesterålen")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3016)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(salten)
                                        .title("Salten")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3018)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(helgeland)
                                        .title("Helgeland")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon)
                                )
                            if (region === 3017)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(svartisen)
                                        .title("Helgeland")
                                        .snippet(response.body()?.get(0)?.MainText)
                                        .icon(icon))
                        }


                    }
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker ) {
        val regionNavn = marker.title as String
        val regionId = etnavn
        val action = MapsFragmentDirections.actionMapFragmentToStatFragment(regionNavn, regionId)
        view?.findNavController()?.navigate(action)
    }

}
