package com.example.snoskred.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.snoskred.R

class HomeFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        Handler(Looper.myLooper()!!).postDelayed({

            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)

        }, 2000)
        (activity as AppCompatActivity).supportActionBar?.hide()


        return view

    }
}