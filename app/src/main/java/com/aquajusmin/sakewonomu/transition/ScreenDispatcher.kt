package com.aquajusmin.sakewonomu.transition

import android.os.Bundle
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.constants.ScreenType
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class ScreenDispatcher @Inject constructor() {
    private val screen: MutableStateFlow<Pair<ScreenType, Bundle>> = MutableStateFlow(ScreenType.Top to Bundle())
    private val action: MutableSharedFlow<ScreenAction?> = MutableSharedFlow()

    fun observeTransition(): StateFlow<Pair<ScreenType, Bundle>> = screen

    fun observeDisplayAction(): SharedFlow<ScreenAction?> = action

    suspend fun transitionScreen(screenType: ScreenType, bundle: Bundle) {
        screen.emit(screenType to bundle)
    }

    suspend fun doScreenAction(screenAction: ScreenAction) {
        action.emit(screenAction)
    }
}
