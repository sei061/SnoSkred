package com.example.snoskred.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.snoskred.R
import com.example.snoskred.SnoskredModelFactory
import com.example.snoskred.SnoskredViewModel
import com.example.snoskred.databinding.FragmentMapsBinding
import com.example.snoskred.databinding.FragmentStatBinding
import com.example.snoskred.repository.Repository
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.google.android.gms.maps.model.LatLng


/**
 * A simple [Fragment] subclass.
 * Use the [StatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatFragment : Fragment(){


    private lateinit var viewModel: SnoskredViewModel
    private var _binding: FragmentStatBinding? = null
    private val binding get() = _binding!!


    private val args: StatFragmentArgs by navArgs()
    var currentLocation: LatLng = LatLng(0.0, 0.0)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val regionNavn = args.regionNavn
        val repository = Repository()
        val viewModelFactory = SnoskredModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SnoskredViewModel::class.java]
        _binding = FragmentStatBinding.inflate(inflater, container, false)
        viewModel.myResponse.observe(viewLifecycleOwner) { response ->
            if (response != null) {
                Log.d("StatsFragment", "Response: ${response.body()}")
                Log.d("StatsFragment", "Response: ${response.body()?.get(0)?.RegionId}")
            }
            // Inflate the layout for this fragment
        }
        val view = binding.root
        return view
    }


}