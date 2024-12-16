package com.aquajusmin.sakewonomu.inputsakeinfo

import android.content.Context
import android.content.Intent
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.InputSakeInfoType
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.kuradialog.KuraSelectDialog
import com.aquajusmin.sakewonomu.model.repository.KuraInfoRepository
import com.aquajusmin.sakewonomu.model.repository.SakeInfoRepository
import com.aquajusmin.sakewonomu.model.repository.data.KuraInfoData
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil
import com.aquajusmin.sakewonomu.viewitems.DatePickerFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class InputSakeInfoViewModel @Inject constructor(
    @ApplicationContext val context: Context,
    private val screenDispatcher: ScreenDispatcher,
    private val sakeInfoRepository: SakeInfoRepository,
    private val kuraInfoRepository: KuraInfoRepository,
): ViewModel() {

    private val _inputType: MutableStateFlow<InputSakeInfoType> = MutableStateFlow(InputSakeInfoType.New)
    val inputType :StateFlow<InputSakeInfoType> = _inputType

    private var data: SakeInfoData? = null

    val date: MutableLiveData<String> = MutableLiveData()
    val sakeName: MutableLiveData<String> = MutableLiveData()
    val kuraInfo: MutableLiveData<KuraInfoData> = MutableLiveData()
    val sakeRank: MutableStateFlow<Int> = MutableStateFlow(SakeInfoData.SakeRank.Other.value)
    val pictureUri: MutableStateFlow<String> = MutableStateFlow("")
    val starPoint: MutableLiveData<Int> = MutableLiveData(Constants.INVALID_POINT_VALUE)
    val juicyPoint: MutableLiveData<Int> = MutableLiveData(Constants.INVALID_POINT_VALUE)
    val sweetPoint: MutableLiveData<Int> = MutableLiveData(Constants.INVALID_POINT_VALUE)
    val richPoint: MutableLiveData<Int> = MutableLiveData(Constants.INVALID_POINT_VALUE)
    val scentPoint: MutableLiveData<Int> = MutableLiveData(Constants.INVALID_POINT_VALUE)
    val memo: MutableLiveData<String> = MutableLiveData()
    val sakeType: MutableStateFlow<Long> = MutableStateFlow(SakeInfoData.SakeType.None.value)
    val place: MutableLiveData<String> = MutableLiveData()

    private val _isKuraLock: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isKuraLock: StateFlow<Boolean> = _isKuraLock
    private val _isPlaceLock: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isPlaceLock: StateFlow<Boolean> = _isPlaceLock

    init {
        viewModelScope.launch {
            kuraInfoRepository.kuraInfoListFlow.collect {
                kuraInfo.value?.id?.let { id ->
                    kuraInfo.postValue(kuraInfoRepository.getKuraInfo(id)) // 蔵情報の都道府県だけ更新された時に反映できていないため
                }
            }
        }
    }

    fun setup(bundle: Bundle?) {
        // 新規or編集
        bundle?.getString(Constants.KEY_INPUT_TYPE)?.let {
            viewModelScope.launch { _inputType.emit(InputSakeInfoType.valueOf(it)) }
        }
        bundle?.getParcelable<SakeInfoData>(Constants.KEY_SAKE_INFO_DATA)?.let {
            data = it
            date.postValue(it.registerDate.ifNullOrEmpty { getDefaultDateText() })
            sakeName.postValue(it.name)
            kuraInfo.postValue(kuraInfoRepository.getKuraInfo(it.kuraId))
            starPoint.postValue(it.startPoint)
            juicyPoint.postValue(it.juicyPoint)
            sweetPoint.postValue(it.sweetPoint)
            richPoint.postValue(it.richPoint)
            scentPoint.postValue(it.scentPoint)
            memo.postValue(it.memo)
            place.postValue(it.place)
            viewModelScope.launch {
                sakeRank.emit(it.sakeRank)
                sakeType.emit(it.sakeType)
            }
            if (it.picturePath?.isNotEmpty() == true) {
                // TODO あとで修正いるかも
                KomeWoNoMuUtil.parseUri(context, it.picturePath)?.let { uri -> updatePicture(uri) }
            }
        } ?: kotlin.run {
            setupLockValues()
            date.postValue(KomeWoNoMuUtil.getDateText(Date()))
        }
    }

    private fun setupLockValues() {
        if (inputType.value != InputSakeInfoType.New) return
        KomeWoNoMuUtil.getLongSharedPrefs(context, Constants.PREFERENCES_LOCK_KURA_ID).let {
            if (it != 0L) {
                viewModelScope.launch { _isKuraLock.emit(true) }
                kuraInfo.postValue(kuraInfoRepository.getKuraInfo(it))
            }
        }
        KomeWoNoMuUtil.getStringSharedPrefs(context, Constants.PREFERENCES_LOCK_PLACE_NAME).let {
            if (it.isNotEmpty()) {
                viewModelScope.launch { _isPlaceLock.emit(true) }
                place.postValue(it)
            }
        }
    }

    fun updateSakeType(value: Long, isChecked: Boolean) {
        val newValue = if (isChecked) {
            sakeType.value or value
        } else {
            if (sakeType.value and value != 0L) sakeType.value xor value else sakeType.value
        }
        viewModelScope.launch { sakeType.emit(newValue) }
    }

    fun updatePicture(uri: Uri) {
        Log.d("DebugTest", "--- updatePicture : $uri ---")
        viewModelScope.launch { pictureUri.emit(uri.toString()) }
        if (inputType.value == InputSakeInfoType.Edit) return
        context.contentResolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)?.let {dateTimeStr ->
                viewModelScope.launch {
                    date.postValue(dateTimeStr.substring(0, 10).replace(":", "/"))
                }
            }
        }
    }

    fun updateKuraLock() {
        if (inputType.value != InputSakeInfoType.New) return
        if (isKuraLock.value) {
            // ロックされているので、ロック状態解除
            KomeWoNoMuUtil.saveSharedPrefs(context, Constants.PREFERENCES_LOCK_KURA_ID, 0L)
            viewModelScope.launch { _isKuraLock.emit(false) }
        } else {
            // ロックされていないので、ロックする
            kuraInfo.value?.let {
                KomeWoNoMuUtil.saveSharedPrefs(context, Constants.PREFERENCES_LOCK_KURA_ID, it.id)
                viewModelScope.launch { _isKuraLock.emit(true) }
            }
        }
    }

    fun updatePlaceLock() {
        if (inputType.value != InputSakeInfoType.New) return
        if (isPlaceLock.value) {
            // ロックされているので、ロック状態解除
            KomeWoNoMuUtil.saveSharedPrefs(context, Constants.PREFERENCES_LOCK_PLACE_NAME, "")
            viewModelScope.launch { _isPlaceLock.emit(false) }
        } else {
            // ロックされていないので、ロックする
            place.value?.let {
                KomeWoNoMuUtil.saveSharedPrefs(context, Constants.PREFERENCES_LOCK_PLACE_NAME, it)
                viewModelScope.launch { _isPlaceLock.emit(true) }
            }
        }
    }

    fun saveSakeInfo(): Boolean {
        if ((sakeName.value == null || sakeName.value!!.isEmpty()) && pictureUri.value.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.input_info_toast_validation))
            }
            return false
        }
        if (inputType.value == InputSakeInfoType.New) {
            // 保存時にロック系の状態を保持する
            if (isKuraLock.value) {
                kuraInfo.value?.let {
                    KomeWoNoMuUtil.saveSharedPrefs(context, Constants.PREFERENCES_LOCK_KURA_ID, it.id)
                }
            }
            if (isPlaceLock.value) {
                place.value?.let {
                    KomeWoNoMuUtil.saveSharedPrefs(context, Constants.PREFERENCES_LOCK_PLACE_NAME, it)
                }
            }
        }
        // データ保存する
        CoroutineScope(Dispatchers.IO).launch {
            screenDispatcher.doScreenAction(
                ScreenAction.ShowToast(
                    if (inputType.value == InputSakeInfoType.New) R.string.input_info_register_success_toast
                    else R.string.input_info_update_success_toast,
                    arrayOf(sakeName.value?.ifEmpty { Constants.SAKE_NO_NAME } ?: Constants.SAKE_NO_NAME)
                )
            )
        }
        data?.let { updateSakeInfoData(it.id) } ?: insertSakeInfoData()
        return true
    }

    fun clearData() {
        data = null
        date.postValue("")
        sakeName.postValue("")
        kuraInfo.postValue(KuraInfoData())
        starPoint.postValue(0)
        juicyPoint.postValue(0)
        sweetPoint.postValue(0)
        richPoint.postValue(0)
        scentPoint.postValue(0)
        memo.postValue("")
        place.postValue("")
        viewModelScope.launch {
            sakeRank.emit(SakeInfoData.SakeRank.Other.value)
            sakeType.emit(SakeInfoData.SakeType.None.value)
            pictureUri.emit("")
        }
        setupLockValues()
    }

    private fun insertSakeInfoData() {
        sakeInfoRepository.saveNewRecord(
            SakeInfoData(
                name = sakeName.value ?: "",
                picturePath = pictureUri.value,
                startPoint = starPoint.value ?: 0,
                kuraId = kuraInfo.value?.id ?: 0,
                sakeRank = sakeRank.value,
                juicyPoint = juicyPoint.value ?: 0,
                sweetPoint = sweetPoint.value ?: 0,
                richPoint = richPoint.value ?: 0,
                scentPoint = scentPoint.value ?: 0,
                memo = memo.value ?: "",
                sakeType = sakeType.value,
                place = place.value ?: "",
                registerDate = date.value ?: getDefaultDateText(),
            )
        )
    }

    private fun updateSakeInfoData(id: Long) {
        sakeInfoRepository.updateRecord(
            SakeInfoData(
                id = id,
                name = sakeName.value ?: "",
                picturePath = pictureUri.value,
                startPoint = starPoint.value ?: 0,
                kuraId = kuraInfo.value?.id ?: 0,
                sakeRank = sakeRank.value,
                juicyPoint = juicyPoint.value ?: 0,
                sweetPoint = sweetPoint.value ?: 0,
                richPoint = richPoint.value ?: 0,
                scentPoint = scentPoint.value ?: 0,
                memo = memo.value ?: "",
                sakeType = sakeType.value,
                place = place.value ?: "",
                registerDate = date.value.ifNullOrEmpty { getDefaultDateText() }
            ),
        )
    }

    fun showDateDialog() {
        viewModelScope.launch {
            screenDispatcher.doScreenAction(
                ScreenAction.ShowDialog(
                    DatePickerFragment(KomeWoNoMuUtil.getDate(date.value ?: "") ?: Calendar.getInstance())
                    { year, month, day ->
                        date.postValue(KomeWoNoMuUtil.getDateTextFormat(year, month, day))
                    },
                    Bundle()
                ) { _ -> }
            )
        }
    }
    private fun getDefaultDateText() = KomeWoNoMuUtil.getDateText(Date())

    fun showKuraSelectDialog() {
        viewModelScope.launch {
            screenDispatcher.doScreenAction(
                ScreenAction.ShowDialog(
                    KuraSelectDialog(),
                    Bundle().apply {
                        kuraInfo.value?.let { putLong(Constants.KEY_KURA_INFO_ID, it.id) }
                    }
                ) { resultBundle ->
                    val kuraId = resultBundle.getLong(Constants.KEY_KURA_INFO_ID, -1L)
                    if (kuraId > 0L) {
                        val selectedKuraInfo = kuraInfoRepository.getKuraInfo(kuraId)
                        kuraInfo.postValue(
                            if (selectedKuraInfo.id > 0L) selectedKuraInfo
                            else KuraInfoData(id = kuraId)
                        )
                    }
                }
            )
        }
    }

    fun String?.ifNullOrEmpty(ifEmptyFunction: () -> String): String {
        return if (this != null && isNotEmpty()) return this
        else ifEmptyFunction()
    }
}
