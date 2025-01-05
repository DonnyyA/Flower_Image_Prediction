package com.doni.flowerimageprediction.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.doni.flowerimageprediction.model.AppRepository
import com.doni.flowerimageprediction.model.entity.ResultEntity

class ResultViewModel(private val repository: AppRepository): ViewModel() {
    private val _allResult = MutableLiveData<ResultEntity>()
    val allResult: LiveData<ResultEntity> = _allResult

    fun setResult(result: ResultEntity) {
        _allResult.value = result
    }

    suspend fun saveResult(result: ResultEntity) {
        repository.insertResult(result)
    }
}