package com.example.snoskred.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.snoskred.SnoskredModelFactory
import com.example.snoskred.SnoskredViewModel
import com.example.snoskred.databinding.FragmentDetailsBinding
import com.example.snoskred.repository.Repository

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {

    private lateinit var viewModel: SnoskredViewModel
    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val repository = Repository()
        val viewModelFactory = SnoskredModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory)[SnoskredViewModel::class.java]

        _binding = FragmentDetailsBinding.inflate(inflater, container, false)

        viewModel.myResponse.observe(viewLifecycleOwner) { response ->
            when (response.isSuccessful) {
                true -> {
                    response.body()?.let {
                        for (post in it) {
                            val region = post.RegionId
                            val dangerLevel = post.DangerLevel
                            val validFrom = post.ValidFrom
                            val validTo = post.ValidTo
                            val mainText = post.MainText

                            binding.regionName.setText(region)
                            binding.regionName.setText(dangerLevel)
                            binding.regionName.setText(validFrom)
                            binding.regionName.setText(validTo)
                            binding.regionName.setText(mainText)

                        }
                    }

                }
            }
        }

        return binding.root
    }
}