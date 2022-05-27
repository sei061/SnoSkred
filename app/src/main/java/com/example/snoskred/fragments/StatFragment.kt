package com.example.snoskred.fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.snoskred.SnoskredModelFactory
import com.example.snoskred.SnoskredViewModel
import com.example.snoskred.databinding.FragmentStatBinding
import com.example.snoskred.repository.Repository
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate


/**
 * A simple [Fragment] subclass.
 * Use the [StatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StatFragment : Fragment() {


    private lateinit var viewModel: SnoskredViewModel
    private var _binding: FragmentStatBinding? = null
    private val binding get() = _binding!!


    private val args: StatFragmentArgs by navArgs()

    //create an arraylist
    lateinit var barEntries: ArrayList<BarEntry>
    lateinit var barDataSet: BarDataSet
    lateinit var barData: BarData


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val repository = Repository()
        val viewModelFactory = SnoskredModelFactory(repository)
        val regionId = args.regionId
        viewModel = ViewModelProvider(this, viewModelFactory)[SnoskredViewModel::class.java]
        viewModel.getStatPost(regionId, Språknøkkel = 1)
        viewModel.statResponse.observe(viewLifecycleOwner) { response ->

            if (response.isSuccessful) {
                Log.d("FITTA", "Response: ${response.body()}")
                barEntries = ArrayList()
                response.body()?.find {
                    it.RegionId == regionId
                }?.let { BarEntry(0f, it.DangerLevel.toFloat()) }?.let { barEntries.add(it) }

                barDataSet = BarDataSet(barEntries, "Skredfare nivå denne dag")
                barData = BarData(barDataSet)
                barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 230)
                binding.barchart.data = barData
                binding.barchart.description.textSize = 13f
                barDataSet.valueTextColor= Color.BLACK
                barDataSet.valueTextSize = 15f
                binding.barchart.animateY(200)
                binding.barchart.animateX(200)
                binding.barchart.description.isEnabled = false
                binding.barchart.axisLeft.axisMinimum = 0f
                binding.barchart.axisRight.axisMinimum = 0f






                binding.barchart.axisRight.axisMaximum = 6f
                binding.barchart.axisLeft.axisMaximum = 6f





//                        barDataSet = BarDataSet(barEntries, "Skredfare nivå")
//                        barData = BarData(barDataSet)
//                        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS, 230)
//                        binding.barchart.data = barData
//                        binding.barchart.description.textSize = 10f
//                        barDataSet.valueTextColor= Color.BLACK
//                        barDataSet.valueTextSize = 10f
//                        binding.barchart.animateY(2000)
//                        binding.barchart.animateX(2000)
//                        binding.barchart.description.isEnabled = false
//                        binding.barchart.axisLeft.axisMinimum = 0f
//                        binding.barchart.axisRight.axisMinimum = 0f
//                        binding.barchart.axisRight.axisMaximum = 2f
//                        binding.barchart.data = barData

                    }
                }


        _binding = FragmentStatBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

}

