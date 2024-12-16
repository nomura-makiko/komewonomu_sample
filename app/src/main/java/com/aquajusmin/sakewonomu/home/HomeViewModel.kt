package com.aquajusmin.sakewonomu.home

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquajusmin.sakewonomu.constants.ScreenType
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val screenDispatcher: ScreenDispatcher,
): ViewModel() {
    private val _isEnableRecordContinueButton: LiveData<Boolean> = MutableLiveData(false)
    val isEnableRecordContinueButton: LiveData<Boolean> = _isEnableRecordContinueButton

    private val _recentlySakeInfoList: LiveData<Array<SakeInfoCard?>> = MutableLiveData(arrayOfNulls(3))
    val recentlySakeInfoList: LiveData<Array<SakeInfoCard?>> = _recentlySakeInfoList
    private val _favoriteSakeInfoList: LiveData<Array<SakeInfoCard?>> = MutableLiveData(arrayOfNulls(3))
    val favoriteSakeInfoList: LiveData<Array<SakeInfoCard?>> = _favoriteSakeInfoList

    fun transitionScreen(screenType: ScreenType) {
        transitionScreen(screenType, Bundle())
    }

    fun transitionScreen(screenType: ScreenType, bundle: Bundle) {
        viewModelScope.launch {
            screenDispatcher.transitionScreen(screenType, bundle)
        }
    }
}
