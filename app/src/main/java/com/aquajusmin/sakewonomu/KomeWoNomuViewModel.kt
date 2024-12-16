package com.aquajusmin.sakewonomu

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.constants.ScreenType
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class KomeWoNomuViewModel @Inject constructor(
    screenDispatcher: ScreenDispatcher,
): ViewModel() {
    private val _screenType: MutableStateFlow<Pair<ScreenType, Bundle>> = MutableStateFlow(ScreenType.Top to Bundle())
    val screenType: StateFlow<Pair<ScreenType, Bundle>> = _screenType
    private val _screenAction: MutableSharedFlow<ScreenAction?> = MutableSharedFlow()
    val screenAction: SharedFlow<ScreenAction?> = _screenAction

    init {
        viewModelScope.launch {
            screenDispatcher.observeTransition().collect {
                _screenType.emit(it)
            }
        }
        viewModelScope.launch {
            screenDispatcher.observeDisplayAction().collect {
                _screenAction.emit(it)
            }
        }
    }

    fun getBackgroundDrawableResId(): Int = when (Calendar.getInstance().get(Calendar.MONTH) + 1) {
        3,4, -> R.drawable.root_background_spring
        5,6 -> R.drawable.root_background_newsummer
        7,8 -> R.drawable.root_background_summer
        9,10,11 -> R.drawable.root_background_autumn
        12,1,2 -> R.drawable.root_background_winter
        else -> 0
    }
}
