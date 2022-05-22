package com.example.snoskred

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.snoskred.model.Post
import com.example.snoskred.repository.Repository
import kotlinx.coroutines.launch

class SnoskredViewModel(private val repository: Repository): ViewModel(){

    val myResponse: MutableLiveData<Post> = MutableLiveData()

    fun getPost(){
        viewModelScope.launch {
            val response: Post = repository.getPost()
            myResponse.value = response
        }
    }
}