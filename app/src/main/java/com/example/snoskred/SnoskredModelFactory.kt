package com.example.snoskred

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snoskred.repository.Repository

class SnoskredModelFactory(
    private val repository: Repository
    ): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SnoskredViewModel(repository) as T
    }
}