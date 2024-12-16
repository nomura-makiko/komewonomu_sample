package com.aquajusmin.sakewonomu.viewitems

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.aquajusmin.sakewonomu.databinding.SakeInfoCardViewBinding
import com.aquajusmin.sakewonomu.home.SakeInfoCard

class SakeInfoCardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val binding: SakeInfoCardViewBinding

    init {
        binding = SakeInfoCardViewBinding.inflate(LayoutInflater.from(context))
    }

    fun setSakeInfo(sakeInfo: SakeInfoCard?) {
        sakeInfo?.let {
            binding.date.text = sakeInfo.date
            binding.sakeName.text = sakeInfo.sakeName
            binding.memo.text = sakeInfo.memo
            binding.starPointValue.text = getStarPointValue(sakeInfo.starPoint)
            visibility = View.VISIBLE
        } ?: run { visibility = View.INVISIBLE }
    }

    private fun getStarPointValue(starPoint: Int) =
        if (starPoint != 0) "★".repeat(starPoint) else ""
}
