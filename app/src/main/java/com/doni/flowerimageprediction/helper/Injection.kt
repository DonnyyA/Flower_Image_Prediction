package com.doni.flowerimageprediction.helper

import android.content.Context
import com.doni.flowerimageprediction.model.AppRepository
import com.doni.flowerimageprediction.model.DatabaseResult

object Injection {
    fun provideRepository(context: Context): AppRepository {
        val database = DatabaseResult.getInstance(context)
        val dao = database.resultDao()
        return AppRepository.getInstance(dao)
    }
}