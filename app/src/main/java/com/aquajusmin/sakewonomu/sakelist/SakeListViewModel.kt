package com.aquajusmin.sakewonomu.sakelist

import android.content.Context
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.ScreenType
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import com.aquajusmin.sakewonomu.model.repository.KuraInfoRepository
import com.aquajusmin.sakewonomu.model.repository.SakeInfoRepository
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil
import com.aquajusmin.sakewonomu.viewitems.viewdatas.SakeInfoCardUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SakeListViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val transitionDispatcher: ScreenDispatcher,
    private val sakeInfoRepository: SakeInfoRepository,
    private val kuraInfoRepository: KuraInfoRepository,
): ViewModel() {
    private val _allSakeInfoList: MutableStateFlow<List<SakeInfoCardUiData>> = MutableStateFlow(listOf())
    val allSakeInfoList: StateFlow<List<SakeInfoCardUiData>> = _allSakeInfoList

    private val _filteredSakeInfoList: MutableStateFlow<List<SakeInfoCardUiData>> = MutableStateFlow(listOf())
    val filteredSakeInfoList: StateFlow<List<SakeInfoCardUiData>> = _filteredSakeInfoList

    init {
        viewModelScope.launch {
            sakeInfoRepository.sakeInfoListFlow.collect { list ->
                _filteredSakeInfoList.emit(list.map { convertSakeInfoDataToUiData(it) })
            }
        }
    }

    fun showSakeInfoDetail(position: Int) {
        val sakeInfoId = filteredSakeInfoList.value[position].id
        viewModelScope.launch {
            transitionDispatcher.transitionScreen(ScreenType.SakeDetailInfo, Bundle().apply {
                putLong(Constants.KEY_SAKE_INFO_ID, sakeInfoId)
            })
        }
    }

    private fun convertSakeInfoDataToUiData(sakeInfoData: SakeInfoData): SakeInfoCardUiData {
        val kuraInfo = kuraInfoRepository.getKuraInfo(kuraId = sakeInfoData.kuraId)
        return SakeInfoCardUiData(
            id = sakeInfoData.id,
            date = sakeInfoData.registerDate,
            sakeName = sakeInfoData.name,
            kuraName = kuraInfo.name,
            prefectures = kuraInfo.prefectures,
            starPoint = sakeInfoData.startPoint,
            picture = sakeInfoData.picturePath?.let { KomeWoNoMuUtil.parseUri(context, sakeInfoData.picturePath) },
            memo = sakeInfoData.memo,
            juicyPoint = sakeInfoData.juicyPoint,
            sweetPoint = sakeInfoData.sweetPoint,
        )
    }
}
