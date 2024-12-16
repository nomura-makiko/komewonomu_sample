package com.aquajusmin.sakewonomu.model.dao

import androidx.room.*
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.model.db.data.SakeInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface SakeInfoDao {
    @Insert
    suspend fun insert(sakeInfo: SakeInfo)

    @Update
    suspend fun update(sakeInfo: SakeInfo)

    @Delete
    suspend fun delete(sakeInfo: SakeInfo)

    @Query("SELECT * FROM ${Constants.TABLE_SAKE_INFO}")
    fun getAll(): List<SakeInfo>

    @Query("SELECT * FROM ${Constants.TABLE_SAKE_INFO}")
    fun getTableChangeFlow(): Flow<List<SakeInfo>>?
}
