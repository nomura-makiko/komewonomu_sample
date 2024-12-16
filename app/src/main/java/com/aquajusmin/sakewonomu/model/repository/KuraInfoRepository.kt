package com.aquajusmin.sakewonomu.model.repository

import android.content.Context
import android.util.Log
import com.aquajusmin.sakewonomu.KomeWoNomuApplication
import com.aquajusmin.sakewonomu.model.dao.KuraInfoDao
import com.aquajusmin.sakewonomu.model.db.data.KuraInfo
import com.aquajusmin.sakewonomu.model.repository.data.KuraInfoData
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
@OptIn(DelicateCoroutinesApi::class)
class KuraInfoRepository @Inject constructor() {

    private val kuraInfoDao: KuraInfoDao = KomeWoNomuApplication.database.kuraInfoDao()

    private val _kuraInfoListFlow: MutableStateFlow<List<KuraInfoData>> = MutableStateFlow(arrayListOf())
    val kuraInfoListFlow: StateFlow<List<KuraInfoData>> = _kuraInfoListFlow

    init {
        GlobalScope.launch {
            kuraInfoDao.getTableChangeFlow()?.collect {
                _kuraInfoListFlow.emit(it.map { kuraInfo -> convertModelToData(kuraInfo) })
            }
        }
    }

    fun getKuraInfo(kuraId: Long): KuraInfoData {
        return kuraInfoListFlow.value.firstOrNull { kuraInfo -> kuraInfo.id == kuraId } ?: KuraInfoData()
    }

    suspend fun recordNewKuraInfo(kuraInfoData: KuraInfoData): Long =
        kuraInfoDao.insert(convertDataToModel(kuraInfoData))

    suspend fun updateKuraInfo(kuraInfoData: KuraInfoData) {
        if (kuraInfoData.id > 0) {
            kuraInfoDao.update(convertDataToModel(kuraInfoData))
        }
    }

    private fun convertModelToData(kuraInfo: KuraInfo) = KuraInfoData(
        id = kuraInfo.id,
        name = kuraInfo.name,
        yomigana = kuraInfo.yomigana,
        prefectures = kuraInfo.prefectures,
    )

    private fun convertDataToModel(kuraInfo: KuraInfoData) = KuraInfo(
        id = kuraInfo.id,
        name = kuraInfo.name,
        yomigana = kuraInfo.yomigana,
        prefectures = kuraInfo.prefectures,
    )
}
