package com.doni.flowerimageprediction.model.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.doni.flowerimageprediction.model.entity.ResultEntity

@Dao
interface ResultDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: ResultEntity)

    @Delete
    suspend fun deleteResult(result: ResultEntity)

    @Query("SELECT * FROM resultentity")
    fun getAllResult(): LiveData<List<ResultEntity>>

}