package com.aquajusmin.sakewonomu.kuradialog

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.model.repository.KuraInfoRepository
import com.aquajusmin.sakewonomu.model.repository.data.KuraInfoData
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import com.aquajusmin.sakewonomu.viewitems.viewdatas.KuraInfoCardUiData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class KuraSelectViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val kuraInfoRepository: KuraInfoRepository,
    private val screenDispatcher: ScreenDispatcher,
): ViewModel() {

//    var kuraName: MutableLiveData<String> = MutableLiveData()
//    var prefecturePosition: MutableLiveData<Int> = MutableLiveData()
//
//    private val _filteredKuraInfoList: MutableStateFlow<List<KuraInfoCardUiData>> = MutableStateFlow(listOf())
//    val filteredKuraInfoList: StateFlow<List<KuraInfoCardUiData>> = _filteredKuraInfoList
//
//    private val _isButtonEnable: MutableLiveData<Boolean> = MutableLiveData()
//    var isButtonEnable: LiveData<Boolean> = _isButtonEnable
//
//    private val prefectureList: List<String> by lazy {
//        context.resources.getStringArray(R.array.prefectures_list).toList()
//    }
//    private var oldPrefecture: String = ""
//
//    fun setup(kuraId: Long?) {
//        kuraId?.let {
//            val kuraInfo = kuraInfoRepository.getKuraInfo(kuraId)
//            kuraName.postValue(kuraInfo.name)
//            prefecturePosition.postValue(prefectureList.firstOrNull { it == kuraInfo.prefectures }?.let {
//                prefectureList.indexOf(it)
//            } ?: 0)
//        }
//
//        viewModelScope.launch {
////            kuraInfoRepository.kuraInfoListFlow.collect { list ->
////                _filteredKuraInfoList.emit(list.map { convertKuraInfoDataToUiData(it) })
////            }
//            val kuraList = listOf(
//                KuraInfoCardUiData(id=2, kuraName = "kura2", prefectures = "新潟県"),
//                KuraInfoCardUiData(id=3, kuraName = "kura3", prefectures = "新潟県"),
//                KuraInfoCardUiData(id=4, kuraName = "kura4", prefectures = "東京都"),
//            )
//
//            if (kuraList.size == 1) {
//                prefectureList.firstOrNull { it == kuraList[0].kuraName }?.let {
//                    prefecturePosition.postValue(prefectureList.indexOf(it))
//                }
//            }
//
//            _filteredKuraInfoList.emit(kuraList)
//        }
//    }
//
//    fun setPrefecture(position: Int) { prefecturePosition.postValue(position) }
//
//    fun pressedButton(kuraName: String, prefecturePosition: Int) {
//        // validation
//        val selectedKuraInfoData = when (filteredKuraInfoList.value.size) {
//            0 -> registerKuraInfo(kuraName, prefecturePosition)
//            1 -> kotlin.run {
//                val kuraData = filteredKuraInfoList.value[0]
//                updateKuraInfoDifferencePrefectureIfNeed(kuraData, prefecturePosition)
//                convertKuraInfoUiDataToData(kuraData)
//            }
//            else -> filteredKuraInfoList.value.firstOrNull { uiData -> uiData.kuraName == kuraName }?.let {
//                updateKuraInfoDifferencePrefectureIfNeed(it, prefecturePosition)
//                convertKuraInfoUiDataToData(it)
//            } ?: registerKuraInfo(kuraName, prefecturePosition)
//        }
//        if (selectedKuraInfoData.name.isNotEmpty()) closeDialog(selectedKuraInfoData)
//    }
//
//    private fun updateKuraInfoDifferencePrefectureIfNeed(uiData: KuraInfoCardUiData, selectedPrefecturePosition: Int) {
//        val newPrefecture = prefectureList[selectedPrefecturePosition]
//        if (oldPrefecture != newPrefecture) {
//            viewModelScope.launch {
//                screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.kura_updated_prefecture_toast))
//            }
//            viewModelScope.launch {
//                kuraInfoRepository.saveKuraInfo(KuraInfoData(id = uiData.id, name = uiData.kuraName, prefectures = newPrefecture))
//            }
//        }
//    }
//
//    private fun registerKuraInfo(kuraName: String, prefecturePosition: Int): KuraInfoData {
//            if (kuraName.isNotEmpty() || 0 <= prefecturePosition) {
//                val newKuraInfoData = KuraInfoData(name = kuraName, prefectures = prefectureList[prefecturePosition])
//                viewModelScope.launch {
//                    kuraInfoRepository.saveKuraInfo(newKuraInfoData)
//                }
//                return newKuraInfoData
//            }
//        viewModelScope.launch { screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.kura_register_error_toast)) }
//        return KuraInfoData()
//    }
//
//    fun selectedKuraList(position: Int) {
//
//        val kuraId = filteredKuraInfoList.value[position].id
//        kuraInfoRepository.getKuraInfo(kuraId).also {
//            if (it.id == kuraId) closeDialog(it)
//        }
//    }
//
//    private fun closeDialog(selectedKuraInfoData: KuraInfoData) {
//        Log.d("DebugTest", "closeDialog :: ${selectedKuraInfoData.id}:${selectedKuraInfoData.name}(${selectedKuraInfoData.prefectures}")
//    }
//
//    private fun convertKuraInfoDataToUiData(kuraInfoData: KuraInfoData) =
//        KuraInfoCardUiData(
//            id = kuraInfoData.id,
//            kuraName = kuraInfoData.name,
//            prefectures = kuraInfoData.prefectures,
//        )
//
//    private fun convertKuraInfoUiDataToData(kuraInfoData: KuraInfoCardUiData) =
//        KuraInfoData(
//            id = kuraInfoData.id,
//            name = kuraInfoData.kuraName,
//            prefectures = kuraInfoData.prefectures,
//        )
}
