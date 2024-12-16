package com.aquajusmin.sakewonomu.constants

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.aquajusmin.sakewonomu.R

enum class ScreenType {
    Top, InputInfoScreen, SakeInfoList, SakeDetailInfo,
}

sealed class ScreenAction {
    object Back : ScreenAction()
    data class ShowDialog(val dialog: DialogFragment, val bundle: Bundle, val resultListener: (bundle: Bundle) -> Unit): ScreenAction()
    data class ShowToast(val textResId: Int, val args: Array<String> = arrayOf()): ScreenAction() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ShowToast

            if (textResId != other.textResId) return false
            if (!args.contentEquals(other.args)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = textResId
            result = 31 * result + args.contentHashCode()
            return result
        }
    }

    object Finish: ScreenAction()
}

enum class InputSakeInfoType {
    New, Edit
}

enum class SakeListType {
    List, Chart
}
sealed class BubbleItem (val x: Int, val y: Int) {
    object X1Y1: BubbleItem(1, 1)
    object X1Y2: BubbleItem(1, 2)
    object X1Y3: BubbleItem(1, 3)
    object X1Y4: BubbleItem(1, 4)
    object X1Y5: BubbleItem(1, 5)
    object X2Y1: BubbleItem(2, 1)
    object X2Y2: BubbleItem(2, 2)
    object X2Y3: BubbleItem(2, 3)
    object X2Y4: BubbleItem(2, 4)
    object X2Y5: BubbleItem(2, 5)
    object X3Y1: BubbleItem(3, 1)
    object X3Y2: BubbleItem(3, 2)
    object X3Y3: BubbleItem(3, 3)
    object X3Y4: BubbleItem(3, 4)
    object X3Y5: BubbleItem(3, 5)
    object X4Y1: BubbleItem(4, 1)
    object X4Y2: BubbleItem(4, 2)
    object X4Y3: BubbleItem(4, 3)
    object X4Y4: BubbleItem(4, 4)
    object X4Y5: BubbleItem(4, 5)
    object X5Y1: BubbleItem(5, 1)
    object X5Y2: BubbleItem(5, 2)
    object X5Y3: BubbleItem(5, 3)
    object X5Y4: BubbleItem(5, 4)
    object X5Y5: BubbleItem(5, 5)
}
