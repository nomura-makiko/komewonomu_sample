package com.aquajusmin.sakewonomu.kuradialog

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.aquajusmin.sakewonomu.databinding.KuraListItemViewBinding
import com.aquajusmin.sakewonomu.viewitems.viewdatas.KuraInfoCardUiData

class KuraInfoListItemAdapter(
    private val context: Context,
    private var dataList: List<KuraInfoCardUiData>
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding =
            if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                val tBinding: KuraListItemViewBinding =
                    KuraListItemViewBinding.inflate(inflater, parent, false)
                // tagにインスタンスをセット(convertViewが存在する場合に使い回すため)
                tBinding.root.tag = tBinding
                tBinding
            } else {
                convertView.tag as KuraListItemViewBinding
            }

        binding.item = getItem(position)
        // 即時バインド
        binding.executePendingBindings()

        return binding.root
    }

    fun updateItems(newItems: List<KuraInfoCardUiData>) {
        dataList = newItems
        // 変更の通知
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): KuraInfoCardUiData {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataList.size
    }
}
