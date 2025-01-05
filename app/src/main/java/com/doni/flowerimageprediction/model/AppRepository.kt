package com.doni.flowerimageprediction.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.doni.flowerimageprediction.model.dao.ResultDao
import com.doni.flowerimageprediction.model.entity.ResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val resultDao: ResultDao) {

    suspend fun insertResult(result: ResultEntity) {
        resultDao.insertResult(result)
    }

    suspend fun deleteResult(result: ResultEntity) {
        resultDao.deleteResult(result)
    }

    fun getAllResult(): LiveData<List<ResultEntity>> = liveData {
        val result: LiveData<List<ResultEntity>> = resultDao.getAllResult()
        emitSource(result)
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null
        fun getInstance(
            resultDao: ResultDao,
        ): AppRepository =
            instance ?: synchronized(this) {
                instance ?: AppRepository(resultDao)
            }.also { instance = it }
    }
}