package com.aquajusmin.sakewonomu.kuradialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.databinding.KuraSelectDialogBinding
import com.aquajusmin.sakewonomu.model.repository.KuraInfoRepository
import com.aquajusmin.sakewonomu.model.repository.data.KuraInfoData
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.transition.ScreenDispatcher
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil
import com.aquajusmin.sakewonomu.viewitems.viewdatas.KuraInfoCardUiData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.take
import javax.inject.Inject


@AndroidEntryPoint
class KuraSelectDialog : DialogFragment() {

    private lateinit var binding: KuraSelectDialogBinding

    @Inject
    lateinit var kuraInfoRepository: KuraInfoRepository

    @Inject
    lateinit var screenDispatcher: ScreenDispatcher

    private val inputMethodManager: InputMethodManager by lazy {
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val prefectureList: List<String> by lazy {
        resources.getStringArray(R.array.prefectures_list).toList()
    }
    private var allKuraList: List<KuraInfoData> = listOf()

    private var filterText: String = ""
    private var yomigana: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = KuraSelectDialogBinding.inflate(requireActivity().layoutInflater)
        binding.kuraList.adapter = KuraInfoListItemAdapter(requireContext(), listOf())
        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        createDialogDatas()
        return dialog
    }

    private fun createDialogDatas() {
        // 蔵名
        KomeWoNoMuUtil.getKeyBoardDismissListener(binding.inputKuraName, inputMethodManager)
        binding.inputKuraName.setOnKeyListener(KomeWoNoMuUtil.getKeyBoardDismissListener(binding.inputKuraName, inputMethodManager))
        binding.inputKuraName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
            override fun afterTextChanged(s: Editable?) {
                val changedText = s.toString()
                // 読み仮名処理
                setYomigana(changedText)
                binding.inputKuraYomigana.setText(yomigana, TextView.BufferType.EDITABLE)

                // フィルタしてリスト更新
                filterText = changedText
                val filterdList = if (filterText.isNotEmpty()) {
                    allKuraList.filter { it.name.contains(filterText) || it.yomigana.contains(filterText) }.map { convertKuraInfoDataToUiData(it) }
                } else allKuraList.map { convertKuraInfoDataToUiData(it) }
                (binding.kuraList.adapter as KuraInfoListItemAdapter).updateItems(filterdList)

                // フィルタ後残り1件かつ蔵名完全一致の場合は県・読み仮名も設定する
                if (filterdList.size == 1 && filterdList[0].kuraName == filterText) {
                    with (filterdList[0]) {
                        binding.prefectureSpinner.setSelection(prefectureList.indexOf(prefectureList.firstOrNull { it == prefectures}))
                        binding.inputKuraYomigana.setText(yomigana, TextView.BufferType.EDITABLE)
                    }
                }
            }
        })

        // 読み仮名
        KomeWoNoMuUtil.getKeyBoardDismissListener(binding.inputKuraYomigana, inputMethodManager)
        binding.inputKuraYomigana.setOnKeyListener(KomeWoNoMuUtil.getKeyBoardDismissListener(binding.inputKuraYomigana, inputMethodManager))
        binding.inputKuraYomigana.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                when {
                    // 現在の読み仮名と一致する場合（ループ回避）
                    s.toString() == yomigana -> { }
                    // ひらがなだけでない場合、元の値に戻す
                    getHiraganaOnlyText(s.toString()).length != s.toString().length -> {
                        binding.inputKuraYomigana.setText(yomigana)
                        binding.inputKuraYomigana.setSelection(yomigana.length)
                    }
                    // 新しく読み仮名がひらがなのみで入力された場合
                    else -> yomigana = s.toString()
                }
            }
        })

        // Prefectures spinner
        with(binding.prefectureSpinner) {
            adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.prefectures_list,
                android.R.layout.simple_spinner_item
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        }

        // 蔵リスト
        lifecycleScope.launch(Dispatchers.IO) {
            kuraInfoRepository.kuraInfoListFlow.take(1).collect { kuraInfoList ->
                allKuraList = kuraInfoList
                (binding.kuraList.adapter as KuraInfoListItemAdapter).updateItems(kuraInfoList.map { kuraInfoData -> convertKuraInfoDataToUiData(kuraInfoData) })
            }
        }
        binding.kuraList.isTextFilterEnabled = true
        binding.kuraList.onItemClickListener =
            AdapterView.OnItemClickListener { adapter, _, position, _ ->
                confirmResult(convertKuraInfoUiDataToData(adapter.getItemAtPosition(position) as KuraInfoCardUiData))
            }

        // ボタン
        binding.kuraSaveButton.setOnClickListener {
            val selectedKuraName = binding.inputKuraName.text.toString()
            val selectedKuraYomigana = binding.inputKuraYomigana.text.toString()
            val selectedPrefecture = prefectureList[binding.prefectureSpinner.selectedItemPosition]

            if (invalidateKuraData(selectedKuraName, selectedKuraYomigana, selectedPrefecture)) {
                CoroutineScope(Dispatchers.IO).launch {
                    screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.kura_register_error_toast))
                }
                return@setOnClickListener
            }
            confirmResult(
                KuraInfoData(
                    name = selectedKuraName,
                    yomigana = selectedKuraYomigana,
                    prefectures = selectedPrefecture
                )
            )
        }

        // 現在の蔵選択状態を反映
        arguments?.getLong(Constants.KEY_KURA_INFO_ID)?.let { id ->
            val kuraInfo = kuraInfoRepository.getKuraInfo(id)
            binding.inputKuraName.setText(kuraInfo.name, TextView.BufferType.EDITABLE)
            binding.inputKuraYomigana.setText(kuraInfo.yomigana, TextView.BufferType.EDITABLE)
            binding.prefectureSpinner.setSelection(prefectureList.indexOf(prefectureList.firstOrNull { it == kuraInfo.prefectures}))
        }
    }

    private fun invalidateKuraData(selectedKuraName: String, selectedKuraYomigana: String, selectedPrefecture: String): Boolean {
        var invalidate = false
        if (selectedKuraName.isEmpty()) {
            binding.inputKuraName.setHintTextColor(resources.getColor(R.color.error_color, null))
            invalidate = true
        } else binding.inputKuraName.setHintTextColor(resources.getColor(R.color.hint_text_color, null))
        if (selectedKuraYomigana.isEmpty()) {
            binding.inputKuraYomigana.setHintTextColor(resources.getColor(R.color.error_color, null))
            invalidate = true
        } else binding.inputKuraYomigana.setHintTextColor(resources.getColor(R.color.hint_text_color, null))
        if (selectedPrefecture.isEmpty()) {
            binding.prefectureSpinner.setBackgroundColor(resources.getColor(R.color.error_color, null))
            invalidate = true
        } else binding.prefectureSpinner.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
        return invalidate
    }

    private fun setYomigana(changedText: String) {
        val inputHiraganaText = getHiraganaOnlyText(changedText)
        val filterHiraganaText = getHiraganaOnlyText(filterText)
        when {
            // 全削除
            changedText.isEmpty() -> {
                yomigana = ""
            }
            // 未入力・ひらがなのみ設定
            yomigana.isEmpty() -> {
                yomigana = inputHiraganaText
            }
            // 1文字追加時
            inputHiraganaText.length == filterHiraganaText.length + 1  -> {
                yomigana += inputHiraganaText.last().toString()
            }
            // 1文字削除時
            filterHiraganaText.length == inputHiraganaText.length + 1 -> {
                yomigana = yomigana.substring(0, yomigana.length - 1)
            }
            // 変換時
            filterText.contains(yomigana) && !changedText.contains(yomigana) -> { }
        }
    }

    private fun confirmResult(selectedKuraInfoData: KuraInfoData) {
        if (selectedKuraInfoData.id == 0L) {
            // 新規入力時
            allKuraList.firstOrNull { selectedKuraInfoData.name == it.name }
                ?.let {
                    // 同じ名前あり
                    updateKuraInfo(
                        KuraInfoData(
                            id = it.id,
                            name = selectedKuraInfoData.name,
                            yomigana = selectedKuraInfoData.yomigana,
                            prefectures = selectedKuraInfoData.prefectures
                        )
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.kura_updated_prefecture_toast, arrayOf(selectedKuraInfoData.name)))
                    }
                } ?: insertKuraInfo(selectedKuraInfoData) // 同じ名前なし
        } else dismissWithSelectedKuraId(selectedKuraInfoData.id) // リスト選択時
    }

    private fun insertKuraInfo(kuraInfoData: KuraInfoData) {
        CoroutineScope(Dispatchers.IO).async {
            val newKuraId = kuraInfoRepository.recordNewKuraInfo(kuraInfoData)
            dismissWithSelectedKuraId(newKuraId)
            CoroutineScope(Dispatchers.IO).launch {
                screenDispatcher.doScreenAction(ScreenAction.ShowToast(R.string.kura_register_success_toast, arrayOf(kuraInfoData.name)))
            }
        }.start()
    }

    private fun updateKuraInfo(kuraInfoData: KuraInfoData) {
        CoroutineScope(Dispatchers.IO).launch {
            kuraInfoRepository.updateKuraInfo(kuraInfoData)
        }
        dismissWithSelectedKuraId(kuraInfoData.id)
    }

    private fun dismissWithSelectedKuraId(selectedKuraId: Long) {
        val arguments = Bundle().apply {
            putLong(Constants.KEY_KURA_INFO_ID, selectedKuraId)
        }
        parentFragmentManager.setFragmentResult(this::class.java.simpleName, arguments)
        dismiss()
    }

    companion object {
        private fun convertKuraInfoDataToUiData(kuraInfoData: KuraInfoData) =
            KuraInfoCardUiData(
                id = kuraInfoData.id,
                kuraName = kuraInfoData.name,
                yomigana = kuraInfoData.yomigana,
                prefectures = kuraInfoData.prefectures,
            )

        private fun convertKuraInfoUiDataToData(kuraInfoCardUiData: KuraInfoCardUiData) =
            KuraInfoData(
                id = kuraInfoCardUiData.id,
                name = kuraInfoCardUiData.kuraName,
                yomigana = kuraInfoCardUiData.yomigana,
                prefectures = kuraInfoCardUiData.prefectures,
            )

        private val HIRAGANA_ARRAY = arrayOf('あ','ぃ','い','ぅ','う','ぇ','え','ぉ','お','か','が',
            'き','ぎ','く','ぐ','け','げ','こ','ご','さ','ざ','し','じ','す','ず','せ','ぜ','そ','ぞ',
            'た','だ','ち','ぢ','っ','つ','づ','て','で','と','ど','な','に','ぬ','ね','の','は','ば',
            'ぱ','ひ','び','ぴ','ふ','ぶ','ぷ','へ','べ','ぺ','ほ','ぼ','ぽ','ま','み','む','め','も',
            'ゃ','や','ゅ','ゆ','ょ','よ','ら','り','る','れ','ろ','ゎ','わ','ゐ','ゑ','を','ん',)

        private fun getHiraganaOnlyText(text: String) = text.filter { HIRAGANA_ARRAY.contains(it) }.ifEmpty { "" }
    }
}
