package com.aquajusmin.sakewonomu.viewitems

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import androidx.databinding.InverseBindingListener
import androidx.databinding.InverseBindingMethod
import androidx.databinding.InverseBindingMethods
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.databinding.SelectPointViewBinding
import com.google.android.material.slider.Slider.OnChangeListener

@InverseBindingMethods(
    InverseBindingMethod(
        type = SelectPointView::class,  // 対象のクラス
        attribute = "pointValue"  // 作成する属性
    )
)
class SelectPointView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: SelectPointViewBinding

    private val buttonMap = hashMapOf<Int, Button>()
    private val onClickListener = OnClickListener { v ->
        buttonMap.entries.firstOrNull { (_, button) -> v == button }
            ?.let { selected(it.key) }
    }
    var changeListener: OnChangeListener? = null

    var selectedValue = Constants.INVALID_POINT_VALUE
        private set

    init {
        binding = SelectPointViewBinding.inflate(LayoutInflater.from(context), this)
        buttonMap.apply {
            put(1, binding.point1)
            put(2, binding.point2)
            put(3, binding.point3)
            put(4, binding.point4)
            put(5, binding.point5)
        }
        binding.point1.setOnClickListener(onClickListener)
        binding.point2.setOnClickListener(onClickListener)
        binding.point3.setOnClickListener(onClickListener)
        binding.point4.setOnClickListener(onClickListener)
        binding.point5.setOnClickListener(onClickListener)

    }

    fun setValue(value: Int) {
        if (value == 0) {
            buttonMap.forEach { changeVisualSet(it.value, UNSELECTED_VISUAL_SET) }
            selectedValue = 0
        }
        if (value > buttonMap.size || value <= 0) return
        selected(value)
    }

    fun getValue(): Int {
        return selectedValue
    }

    fun setOnChangeListener(listener: OnChangeListener) {
        changeListener = listener
    }

    private fun selected(newValue: Int) {
        buttonMap.forEach { (value, button) ->
            if (newValue == value) {
                selectedValue = value
                changeListener?.onChange(value)
                changeVisualSet(button, SELECTED_VISUAL_SET)
            } else changeVisualSet(button, UNSELECTED_VISUAL_SET)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun changeVisualSet(button: Button, visualSet: VisualSet) {
        button.setTextColor(resources.getColor(visualSet.textColorResId, null))
        button.background = resources.getDrawable(visualSet.bgDrawableResId, null)
    }

    interface OnChangeListener {
        abstract fun onChange(newValue: Int)
    }

    companion object {
        private val SELECTED_VISUAL_SET: VisualSet = VisualSet(R.color.selected_text_color, R.drawable.point_button_background_selected)
        private val UNSELECTED_VISUAL_SET: VisualSet = VisualSet(R.color.not_selected_text_color, R.drawable.point_button_background_not_selected)
        data class VisualSet(val textColorResId: Int, val bgDrawableResId: Int)
    }
}
