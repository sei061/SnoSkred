package com.example.snoskred

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snoskred.model.Post
import com.example.snoskred.repository.Repository
import com.firebase.ui.auth.data.model.User
import kotlinx.coroutines.launch
import retrofit2.Response

class SnoskredViewModel(private val repository: Repository): ViewModel() {

    val user: User? = null

    val myResponse: MutableLiveData<Response<List<Post>>> = MutableLiveData()

    val statResponse: MutableLiveData<Response<List<Post>>> = MutableLiveData()


    fun getStatPost(
        RegionId: Int,
        Språknøkkel: Int
    ) {
        viewModelScope.launch {
            val response: Response<List<Post>> = repository.getStatPost(RegionId, Språknøkkel)
            statResponse.value = response
        }
    }


    fun getPost(
        lat: Double, lng: Double,
        Språknøkkel: Int
    ) {
        viewModelScope.launch {
            val response: Response<List<Post>> = repository.getPost(lat, lng, Språknøkkel)
            myResponse.value = response
        }
    }
}

