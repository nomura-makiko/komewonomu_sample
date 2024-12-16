package com.aquajusmin.sakewonomu.sakelist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.BubbleItem
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.InputSakeInfoType
import com.aquajusmin.sakewonomu.constants.SakeListType
import com.aquajusmin.sakewonomu.databinding.SakeListFragmentBinding
import com.aquajusmin.sakewonomu.viewitems.viewdatas.SakeInfoCardUiData
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class SakeListFragment: Fragment() {

    private val viewModel by viewModels<SakeListViewModel>()
    private lateinit var binding: SakeListFragmentBinding

    private val bubbleItemList: List<BubbleItem> by lazy { getBubbleItemInfoList() }
    private val bubbleColorList: List<Int> by lazy { getBubbleColors() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SakeListFragmentBinding.inflate(inflater)
        binding.sakeInfoListView.adapter = SakeInfoListItemAdapter(requireContext(), listOf())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Filters

        // Data
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.filteredSakeInfoList.collect {
                // Charts
                updateChart(it).let { bubbleData ->

                    binding.sakeInfoChartView.data = bubbleData
                    binding.sakeInfoChartView.description = Description().apply {
                        text = "X: ${resources.getString(R.string.sake_detail_info_sweet_point_label)}" +
                                "  Y: ${resources.getString(R.string.sake_detail_info_juicy_point_label)}"
                        textSize = 18.0f
                    }
                    binding.sakeInfoChartView.setMaxVisibleValueCount(0)
                    binding.sakeInfoChartView.invalidate()
                }

                // List
                (binding.sakeInfoListView.adapter as SakeInfoListItemAdapter).updateItems(it)
            }
        }
        binding.sakeInfoListView.onItemClickListener = OnItemClickListener { _, _, position, id ->
            viewModel.showSakeInfoDetail(position)
        }
        updateVisibility(
            SakeListType.valueOf(arguments?.getString(Constants.KEY_INFO_LIST_TYPE, Constants.VALUE_INFO_LIST_TYPE_LIST)
                ?: Constants.VALUE_INFO_LIST_TYPE_LIST)
        )

        // Buttons
        binding.sakeListTab.setOnClickListener { updateVisibility(SakeListType.List) }
        binding.sakeChartTab.setOnClickListener { updateVisibility(SakeListType.Chart) }
    }

    private fun updateVisibility(sakeListType: SakeListType) {
        when (sakeListType) {
            SakeListType.Chart -> {
                binding.sakeInfoListView.visibility = View.GONE
                binding.sakeInfoChartView.visibility = View.VISIBLE
            }
            SakeListType.List -> {
                binding.sakeInfoListView.visibility = View.VISIBLE
                binding.sakeInfoChartView.visibility = View.GONE
            }
        }
    }

    private fun updateChart(list: List<SakeInfoCardUiData>): BubbleData {
        val bubbleList = arrayListOf<BubbleEntry>()
        val dataSets: MutableList<IBubbleDataSet> = ArrayList()
        list.forEach { Log.d("DebugTest", "juicy=${it.juicyPoint}, sweet=${it.sweetPoint}") }// TODO 確認用
        val oneDataSize = Constants.MAX_BUBBLE_SIZE / list.size
        bubbleItemList.forEach { bubbleItem ->
            Log.d("DebugTest", "<${bubbleItem.x} - ${bubbleItem.y}>")

            list.filter { it.juicyPoint == bubbleItem.x && it.sweetPoint == bubbleItem.y }.let {
                if (it.isNotEmpty()) {
                    Log.d("DebugTest", "bubbleSize = ${getBubbleSize(oneDataSize, it.size)}")
                    bubbleList.add(BubbleEntry(
                        bubbleItem.x.toFloat(),
                        bubbleItem.y.toFloat(),
                        getBubbleSize(oneDataSize, it.size)
                    ))
                    dataSets.add(BubbleDataSet(bubbleList, "${bubbleItem.x}-${bubbleItem.y}").apply {
                        colors = bubbleColorList
                    })
                }
            }
        }
        // 表示範囲調整
        val transparentBubbleSet = arrayListOf<BubbleEntry>().apply {
            add(BubbleEntry(BubbleItem.X1Y1.x.toFloat(), BubbleItem.X1Y1.y.toFloat(), 1F))
            add(BubbleEntry(BubbleItem.X1Y5.x.toFloat(), BubbleItem.X1Y1.y.toFloat(), 1F))
            add(BubbleEntry(BubbleItem.X5Y1.x.toFloat(), BubbleItem.X1Y1.y.toFloat(), 1F))
            add(BubbleEntry(BubbleItem.X5Y5.x.toFloat(), BubbleItem.X1Y1.y.toFloat(), 1F))
        }
        dataSets.add(
            BubbleDataSet(transparentBubbleSet, "transparent").apply {
                color = resources.getColor(R.color.bubble_item_transparent, null)
            }
        )
        return BubbleData(dataSets)
    }

    private fun getBubbleSize(oneDataSize: Float, targetDataCount: Int): Float =
        oneDataSize * targetDataCount.toFloat()

    companion object {
        fun getBubbleItemInfoList() = listOf(
            BubbleItem.X1Y1,
            BubbleItem.X1Y2,
            BubbleItem.X1Y3,
            BubbleItem.X1Y4,
            BubbleItem.X1Y5,
            BubbleItem.X2Y1,
            BubbleItem.X2Y2,
            BubbleItem.X2Y3,
            BubbleItem.X2Y4,
            BubbleItem.X2Y5,
            BubbleItem.X3Y1,
            BubbleItem.X3Y2,
            BubbleItem.X3Y3,
            BubbleItem.X3Y4,
            BubbleItem.X3Y5,
            BubbleItem.X4Y1,
            BubbleItem.X4Y2,
            BubbleItem.X4Y3,
            BubbleItem.X4Y4,
            BubbleItem.X4Y5,
            BubbleItem.X5Y1,
            BubbleItem.X5Y2,
            BubbleItem.X5Y3,
            BubbleItem.X5Y4,
            BubbleItem.X5Y5,
        )

        fun getBubbleColors() = listOf(
            R.color.bubble_item_x1y1,
            R.color.bubble_item_x1y2,
            R.color.bubble_item_x1y3,
            R.color.bubble_item_x1y4,
            R.color.bubble_item_x1y5,
            R.color.bubble_item_x2y1,
            R.color.bubble_item_x2y2,
            R.color.bubble_item_x2y3,
            R.color.bubble_item_x2y4,
            R.color.bubble_item_x2y5,
            R.color.bubble_item_x3y1,
            R.color.bubble_item_x3y2,
            R.color.bubble_item_x3y3,
            R.color.bubble_item_x3y4,
            R.color.bubble_item_x3y5,
            R.color.bubble_item_x4y1,
            R.color.bubble_item_x4y2,
            R.color.bubble_item_x4y3,
            R.color.bubble_item_x4y4,
            R.color.bubble_item_x4y5,
            R.color.bubble_item_x5y1,
            R.color.bubble_item_x5y2,
            R.color.bubble_item_x5y3,
            R.color.bubble_item_x5y4,
            R.color.bubble_item_x5y5
        )
    }
}
