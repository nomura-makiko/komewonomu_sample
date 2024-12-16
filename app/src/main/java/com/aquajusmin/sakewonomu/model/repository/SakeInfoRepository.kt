package com.aquajusmin.sakewonomu.model.repository

import android.util.Log
import com.aquajusmin.sakewonomu.KomeWoNomuApplication
import com.aquajusmin.sakewonomu.model.dao.SakeInfoDao
import com.aquajusmin.sakewonomu.model.db.data.SakeInfo
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@OptIn(DelicateCoroutinesApi::class)
class SakeInfoRepository @Inject constructor() {

    private val sakeInfoDao: SakeInfoDao = KomeWoNomuApplication.database.sakeInfoDao()

    private val _sakeInfoListFlow: MutableStateFlow<List<SakeInfoData>> = MutableStateFlow(arrayListOf())
    val sakeInfoListFlow: StateFlow<List<SakeInfoData>> = _sakeInfoListFlow

    init {
        GlobalScope.launch {
            sakeInfoDao.getTableChangeFlow()?.collect {
                _sakeInfoListFlow.emit(it.map { sakeInfo -> convertModelToData(sakeInfo) })
            }
        }
    }

    fun getSakeInfo(sakeInfoId: Long): SakeInfoData {
        return sakeInfoListFlow.value.firstOrNull { sakeInfo -> sakeInfo.id == sakeInfoId } ?: SakeInfoData()
    }

    fun saveNewRecord(sakeInfoData: SakeInfoData) {
        GlobalScope.launch {
            sakeInfoDao.insert(convertDataToModel(sakeInfoData, true))
        }
    }

    fun updateRecord(sakeInfoData: SakeInfoData) {
        GlobalScope.launch {
            sakeInfoDao.update(convertDataToModel(sakeInfoData, false))
        }
    }

    fun deleteRecord(sakeInfoData: SakeInfoData) {
        GlobalScope.launch {
            sakeInfoDao.delete(convertDataToModel(sakeInfoData, false))
        }
    }

    private fun convertModelToData(sakeInfo: SakeInfo) = SakeInfoData(
        id = sakeInfo.id,
        name = sakeInfo.name,
        picturePath = sakeInfo.picturePath.ifEmpty { null },
        startPoint = sakeInfo.startPoint,
        kuraId = sakeInfo.kuraId,
        sakeRank = sakeInfo.sakeRank,
        juicyPoint = sakeInfo.juicyPoint,
        sweetPoint = sakeInfo.sweetPoint,
        richPoint = sakeInfo.richPoint,
        scentPoint = sakeInfo.scentPoint,
        memo = sakeInfo.memo,
        sakeType = sakeInfo.sakeType,
        place = sakeInfo.place,
        registerDate = sakeInfo.registerDate,
    )

    private fun convertDataToModel(sakeInfoData: SakeInfoData, isNewRecord: Boolean) = SakeInfo(
        id = if (isNewRecord) 0L else sakeInfoData.id,
        name = sakeInfoData.name,
        picturePath = sakeInfoData.picturePath ?: "",
        startPoint = sakeInfoData.startPoint,
        kuraId = sakeInfoData.kuraId,
        sakeRank = sakeInfoData.sakeRank,
        juicyPoint = sakeInfoData.juicyPoint,
        sweetPoint = sakeInfoData.sweetPoint,
        richPoint = sakeInfoData.richPoint,
        scentPoint = sakeInfoData.scentPoint,
        memo = sakeInfoData.memo,
        sakeType = sakeInfoData.sakeType,
        place = sakeInfoData.place,
        registerDate = sakeInfoData.registerDate,
    )
}