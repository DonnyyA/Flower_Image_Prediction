package com.doni.flowerimageprediction.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.doni.flowerimageprediction.helper.Injection
import com.doni.flowerimageprediction.model.AppRepository
import com.doni.flowerimageprediction.view.viewmodel.HistoryViewModel
import com.doni.flowerimageprediction.view.viewmodel.ResultViewModel

class ViewModelFactory private constructor(
    private val appRepository: AppRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(appRepository) as T
            }

            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel(appRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}