package com.aquajusmin.sakewonomu.sakelist

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.databinding.SakeInfoListItemViewBinding
import com.aquajusmin.sakewonomu.viewitems.viewdatas.SakeInfoCardUiData
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SakeInfoListItemAdapter(
    private val context: Context,
    private var dataList: List<SakeInfoCardUiData>
) : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding =
            if (convertView == null) {
                val inflater = LayoutInflater.from(context)
                val tBinding: SakeInfoListItemViewBinding =
                    SakeInfoListItemViewBinding.inflate(inflater, parent, false)
                // tagにインスタンスをセット(convertViewが存在する場合に使い回すため)
                tBinding.root.tag = tBinding
                tBinding
            } else {
                convertView.tag as SakeInfoListItemViewBinding
            }

        val item = getItem(position) as SakeInfoCardUiData
        binding.item = item
        Log.d("DebugTest", "${item.sakeName} ${item.picture}")
        item.picture?.let {
            Picasso.get().load(it).error(R.drawable.error_image).into(binding.sakePicture)
        } ?: binding.sakePicture.setImageDrawable(null)
        // 即時バインド
        binding.executePendingBindings()

        return binding.root
    }

    fun updateItems(newItems: List<SakeInfoCardUiData>) {
        dataList = newItems
        // 変更の通知
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any {
        return dataList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return dataList.size
    }
}
