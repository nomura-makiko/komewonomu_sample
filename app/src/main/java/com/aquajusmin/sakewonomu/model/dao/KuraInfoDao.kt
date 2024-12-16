package com.aquajusmin.sakewonomu.model.dao

import androidx.room.*
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.model.db.data.KuraInfo
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow

@Dao
interface KuraInfoDao {
    @Insert
    suspend fun insert(sakeInfo: KuraInfo): Long

    @Update
    suspend fun update(sakeInfo: KuraInfo)

    @Delete
    suspend fun delete(sakeInfo: KuraInfo)

    @Query("SELECT * FROM ${Constants.TABLE_KURA_INFO}")
    fun getTableChangeFlow(): Flow<List<KuraInfo>>?
}