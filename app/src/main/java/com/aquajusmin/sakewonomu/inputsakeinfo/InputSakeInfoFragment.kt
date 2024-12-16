package com.aquajusmin.sakewonomu.inputsakeinfo

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.InputSakeInfoType
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.databinding.InputSakeInfoFragmentBinding
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil
import com.aquajusmin.sakewonomu.viewitems.SelectPointView
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException


@AndroidEntryPoint
class InputSakeInfoFragment: Fragment() {

    private var chooser: ActivityResultLauncher<Unit>? = null

    private val viewModel by viewModels<InputSakeInfoViewModel>()

    private lateinit var binding: InputSakeInfoFragmentBinding

    private val inputMethodManager: InputMethodManager by lazy {
        context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private val radioButtonMap = hashMapOf<Int, RadioButton>()
    private val checkBoxMap = hashMapOf<Long, CheckBox>()

    private val onCheckedChangeListener =
        OnCheckedChangeListener { buttonView, isChecked ->
            checkBoxMap.entries.firstOrNull { (_, checkBox) -> buttonView.id == checkBox.id }?.let {
                viewModel.updateSakeType(it.key, isChecked)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = InputSakeInfoFragmentBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // 自作ActivityResultContractからlauncherを作成する
        chooser = registerForActivityResult(PictureCooserActivityContract(context)) { result ->
            val uri = result ?: return@registerForActivityResult
            // Check for the freshest data.
            viewModel.updatePicture(uri)
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setup(arguments)

        // 日付設定
        binding.recordDate.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.recordDate.setOnClickListener { viewModel.showDateDialog() }

        // キーボード閉じる処理
        binding.sakeName.setOnKeyListener(KomeWoNoMuUtil.getKeyBoardDismissListener(binding.sakeName, inputMethodManager))
        binding.placeName.setOnKeyListener(KomeWoNoMuUtil.getKeyBoardDismissListener(binding.placeName, inputMethodManager))

        // 蔵名設定
        binding.kuraName.setOnClickListener { viewModel.showKuraSelectDialog() }
        // 蔵名ロック
        binding.lockKuraNameIcon.setOnClickListener {
            viewModel.updateKuraLock()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.isKuraLock.collect {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.lockKuraNameIcon.setImageResource(if (it) R.drawable.lock_icon_lock else R.drawable.lock_icon_unlock)
                }
            }
        }

        // 画像設定
        binding.sakePicture.setOnClickListener {
            chooser?.launch(Unit)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.pictureUri.collect {
                try {
                    lifecycleScope.launch(Dispatchers.Main) {
                        if (it.isNotEmpty()) {
                            Picasso.get().load(it).error(R.drawable.error_image).into(binding.sakePicture)
                        } else {
                            binding.sakePicture.setImageDrawable(null)
                        }
                    }
                } catch (e: FileNotFoundException) {
                }
            }
        }

        // 場所ロック
        binding.lockPlaceNameIcon.setOnClickListener {
            viewModel.updatePlaceLock()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.isPlaceLock.collect {
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.lockPlaceNameIcon.setImageResource(if (it) R.drawable.lock_icon_lock else R.drawable.lock_icon_unlock)
                }
            }
        }

        // ボタン類
        if (KomeWoNoMuUtil.getBooleanSharedPrefs(requireContext(), Constants.PREFERENCES_CONTINUE_FLAG)) {
            binding.continueCheckbox.isChecked = true
        }
        binding.continueCheckbox.setOnCheckedChangeListener { _, isChecked ->
            KomeWoNoMuUtil.saveSharedPrefs(requireContext(), Constants.PREFERENCES_CONTINUE_FLAG, isChecked)
        }
        binding.saveButton.setOnClickListener {
            // 保存
            if (!viewModel.saveSakeInfo()) return@setOnClickListener
            if (!binding.continueCheckbox.isChecked || viewModel.inputType.value == InputSakeInfoType.Edit) parentFragmentManager.popBackStack()
            else viewModel.clearData()
        }

        // 評価設定系
        updatePointData(binding.starPointBar, viewModel.starPoint)
        updatePointData(binding.juicyPointBar, viewModel.juicyPoint)
        updatePointData(binding.sweetPointBar, viewModel.sweetPoint)
        updatePointData(binding.richPointBar, viewModel.richPoint)
        updatePointData(binding.scentPointBar, viewModel.scentPoint)

        // 酒ランク
        setupRadioButtons()
        // 酒タイプ
        setupCheckBoxGroup()

        // 編集時の非表示処理
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.inputType.collect {
                val visibility = if (it == InputSakeInfoType.New) View.VISIBLE else View.INVISIBLE
                lifecycleScope.launch(Dispatchers.Main) {
                    binding.lockKuraNameIcon.visibility = visibility
                    binding.lockPlaceNameIcon.visibility = visibility
                    binding.continueCheckbox.visibility = visibility
                }
            }
        }
    }

    private fun updatePointData(view: SelectPointView, liveData: MutableLiveData<Int>) {
        view.setOnChangeListener(object : SelectPointView.OnChangeListener {
            override fun onChange(newValue: Int) {
                liveData.postValue(newValue)
            }
        })
    }

    private fun setupRadioButtons() {
        radioButtonMap.apply {
            put(SakeInfoData.SakeRank.JunGin.value, binding.junginButton)
            put(SakeInfoData.SakeRank.Daigin.value, binding.daiginButton)
            put(SakeInfoData.SakeRank.Ginjou.value, binding.ginjouButton)
            put(SakeInfoData.SakeRank.JunDai.value, binding.jundaiButton)
            put(SakeInfoData.SakeRank.Honjouzou.value, binding.honjouzouButton)
            put(SakeInfoData.SakeRank.Junmai.value, binding.junmaiButton)
            put(SakeInfoData.SakeRank.TokuJun.value, binding.tokujunButton)
            put(SakeInfoData.SakeRank.Other.value, binding.otherButton)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.sakeRank.collect {
                radioButtonMap[it]?.let { button ->
                    if (button.isChecked) return@collect
                    lifecycleScope.launch(Dispatchers.Main) {
                        binding.sakeRankRadioGroup1.check(button.id)
                        binding.sakeRankRadioGroup2.check(button.id)
                    }
                }
            }
        }
        binding.sakeRankRadioGroup1.setOnCheckedChangeListener { _, checkedId ->
            radioButtonMap.entries.firstOrNull { (_, radio) -> radio.id == checkedId }?.let {
                if (viewModel.sakeRank.value == it.key) return@setOnCheckedChangeListener
                if (binding.sakeRankRadioGroup2.checkedRadioButtonId > 0)
                    binding.sakeRankRadioGroup2.clearCheck()
                lifecycleScope.launch(Dispatchers.IO) { viewModel.sakeRank.emit(it.key) }
            }
        }
        binding.sakeRankRadioGroup2.setOnCheckedChangeListener { _, checkedId ->
            radioButtonMap.entries.firstOrNull { (_, radio) -> radio.id == checkedId }?.let {
                if (viewModel.sakeRank.value == it.key) return@setOnCheckedChangeListener
                if (binding.sakeRankRadioGroup1.checkedRadioButtonId > 0)
                    binding.sakeRankRadioGroup1.clearCheck()
                lifecycleScope.launch(Dispatchers.IO) { viewModel.sakeRank.emit(it.key) }
            }
        }
    }

    private fun setupCheckBoxGroup() {
        checkBoxMap.apply {
            put(SakeInfoData.SakeType.Nama.value, binding.namaCheck)
            put(SakeInfoData.SakeType.Gensyu.value, binding.gensyuCheck)
            put(SakeInfoData.SakeType.Nigori.value, binding.nigoriCheck)
            put(SakeInfoData.SakeType.Kimoto.value, binding.kimotoCheck)
            put(SakeInfoData.SakeType.Sizuku.value, binding.sizukuCheck)
            put(SakeInfoData.SakeType.Happou.value, binding.happouCheck)
            put(SakeInfoData.SakeType.Yamahai.value, binding.yamahaiCheck)
            put(SakeInfoData.SakeType.Kosyu.value, binding.kosyuCheck)
            put(SakeInfoData.SakeType.Kijousyu.value, binding.kijousyuCheck)
            put(SakeInfoData.SakeType.Hiyaoroshi.value, binding.hiyaoroshiCheck)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.sakeType.collect { sakeType ->
                lifecycleScope.launch(Dispatchers.Main) {
                    checkBoxMap.forEach { (value, checkBox) ->
                        checkBox.isChecked =
                            sakeType != 0L && sakeType and value != 0L
                    }
                }
            }
        }

        binding.namaCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.gensyuCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.nigoriCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.kimotoCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.sizukuCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.happouCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.yamahaiCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.kosyuCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.kijousyuCheck.setOnCheckedChangeListener(onCheckedChangeListener)
        binding.hiyaoroshiCheck.setOnCheckedChangeListener(onCheckedChangeListener)
    }
}
