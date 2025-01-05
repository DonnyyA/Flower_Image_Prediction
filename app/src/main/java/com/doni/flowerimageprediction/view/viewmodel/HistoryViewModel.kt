package com.doni.flowerimageprediction.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doni.flowerimageprediction.model.AppRepository
import com.doni.flowerimageprediction.model.entity.ResultEntity
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: AppRepository): ViewModel() {
    val allResult: LiveData<List<ResultEntity>> = repository.getAllResult()
}