package com.aquajusmin.sakewonomu.detail

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.InputSakeInfoType
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.constants.ScreenType
import com.aquajusmin.sakewonomu.model.repository.KuraInfoRepository
import com.aquajusmin.sakewonomu.model.repository.SakeInfoRepository
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SakeDetailInfoViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val screenDispatcher: ScreenDispatcher,
    private val kuraInfoRepository: KuraInfoRepository,
    private val sakeInfoRepository: SakeInfoRepository,
): ViewModel() {

    private var sakeInfoTransitionData: SakeInfoData? = null

    private val _date: MutableLiveData<String> = MutableLiveData()
    val date: LiveData<String> = _date
    private val _sakeName: MutableLiveData<String> = MutableLiveData()
    val sakeName: LiveData<String> = _sakeName
    private val _kuraName: MutableLiveData<String> = MutableLiveData()
    val kuraName: LiveData<String> = _kuraName
    private val _prefecturesName: MutableLiveData<String> = MutableLiveData()
    val prefecturesName: LiveData<String> = _prefecturesName
    private val _pictureUri: MutableStateFlow<String> = MutableStateFlow("")
    val pictureUri: StateFlow<String> = _pictureUri
    private val _sakeRank: MutableLiveData<String> = MutableLiveData()
    val sakeRank: LiveData<String> = _sakeRank
    private val _starPoint: MutableLiveData<String> = MutableLiveData()
    val starPoint: LiveData<String> = _starPoint
    private val _juicyPoint: MutableLiveData<String> = MutableLiveData()
    val juicyPoint: LiveData<String> = _juicyPoint
    private val _sweetPoint: MutableLiveData<String> = MutableLiveData()
    val sweetPoint: LiveData<String> = _sweetPoint
    private val _richPoint: MutableLiveData<String> = MutableLiveData()
    val richPoint: LiveData<String> = _richPoint
    private val _scentPoint: MutableLiveData<String> = MutableLiveData()
    val scentPoint: LiveData<String> = _scentPoint
    private val _memo: MutableLiveData<String> = MutableLiveData()
    val memo: LiveData<String> = _memo
    private val _sakeType: MutableLiveData<String> = MutableLiveData()
    val sakeType: LiveData<String> = _sakeType
    private val _place: MutableLiveData<String> = MutableLiveData()
    val place: LiveData<String> = _place

    init {
        viewModelScope.launch {
            sakeInfoRepository.sakeInfoListFlow.collect { list ->
                sakeInfoTransitionData?.let {
                    updateItems(it.id)
                }
            }
        }
    }

    fun setup(bundle: Bundle?) {
        bundle?.getLong(Constants.KEY_SAKE_INFO_ID)?.let { id ->
            updateItems(id)
        }
    }

    private fun updateItems(id: Long) {
        sakeInfoRepository.getSakeInfo(id).let {
            sakeInfoTransitionData = it
            _date.postValue(it.registerDate)
            _sakeName.postValue(it.name)
            val kuraInfo = kuraInfoRepository.getKuraInfo(it.kuraId)
            _kuraName.postValue(kuraInfo.name)
            _prefecturesName.postValue(kuraInfo.prefectures)
            _sakeRank.postValue(KomeWoNoMuUtil.getSakeRankText(context, it.sakeRank))
            _starPoint.postValue(KomeWoNoMuUtil.getPointLabel(it.startPoint))
            _juicyPoint.postValue(KomeWoNoMuUtil.getPointLabel(it.juicyPoint))
            _sweetPoint.postValue(KomeWoNoMuUtil.getPointLabel(it.sweetPoint))
            _richPoint.postValue(KomeWoNoMuUtil.getPointLabel(it.richPoint))
            _scentPoint.postValue(KomeWoNoMuUtil.getPointLabel(it.scentPoint))
            _memo.postValue(it.memo)
            _sakeType.postValue(KomeWoNoMuUtil.getSakeTypeText(context, it.sakeType))
            _place.postValue(it.place)
            viewModelScope.launch {
                _pictureUri.emit(it.picturePath ?: "")
            }
        }
    }

    fun transitionEditSakeInfo() {
        viewModelScope.launch {
            screenDispatcher.transitionScreen(ScreenType.InputInfoScreen, Bundle().apply {
                    putString(Constants.KEY_INPUT_TYPE, InputSakeInfoType.Edit.name)
                    putParcelable(Constants.KEY_SAKE_INFO_DATA, sakeInfoTransitionData)
                }
            )
        }
    }

    fun deleteSakeInfo() {
        sakeInfoTransitionData?.let {
            viewModelScope.launch {
                sakeInfoRepository.deleteRecord(it)
                screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.sake_detail_info_deleted_info, arrayOf(it.name)))
                screenDispatcher.doScreenAction(ScreenAction.Back)
            }
        }
    }
}
