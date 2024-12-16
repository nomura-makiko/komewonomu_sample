package com.aquajusmin.sakewonomu

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.*
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.constants.ScreenType
import com.aquajusmin.sakewonomu.databinding.ActivityMainBinding
import com.aquajusmin.sakewonomu.detail.SakeDetailInfoFragment
import com.aquajusmin.sakewonomu.home.HomeFragment
import com.aquajusmin.sakewonomu.inputsakeinfo.InputSakeInfoFragment
import com.aquajusmin.sakewonomu.sakelist.SakeListFragment
import com.aquajusmin.sakewonomu.util.KomeWoNoMuUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class KomeWoNomuActivity: AppCompatActivity() {
    private val viewModel by viewModels<KomeWoNomuViewModel>()
    private lateinit var binding: ActivityMainBinding

    private val inputMethodManager: InputMethodManager by lazy {
        this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backgroundView.setBackgroundResource(viewModel.getBackgroundDrawableResId())
        setupViews()
    }

    private fun setupViews() {
        // 画面遷移
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.screenType.collect {
                transitionFragment(it.first, it.second)
            }
        }
        // 画面遷移以外のAction
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.screenAction.collect {
                when (it) {
                    ScreenAction.Back -> supportFragmentManager.popBackStack()
                    ScreenAction.Finish -> finish()
                    is ScreenAction.ShowDialog -> showDialog(it.dialog, it.bundle, it.resultListener)
                    is ScreenAction.ShowToast -> showToast(it.textResId, it.args)
                    else -> {}
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (KomeWoNoMuUtil.checkPermission(this)) {
            requestPermission(arrayOf(Manifest.permission.CAMERA))
        }
    }
    private fun requestPermission(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this, permissions, Constants.REQUEST_CODE_PERMISSION)
    }

    private fun transitionFragment(screenType: ScreenType, arguments: Bundle) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            getNextFragment(screenType, arguments)?.let { nextFragment ->
                replace(binding.fragmentContainer.id, nextFragment)
                if (screenType != ScreenType.Top) {
                    addToBackStack(nextFragment::class.java.simpleName)
                }
            }
        }
    }

    private fun getNextFragment(screenType: ScreenType, args: Bundle): Fragment? = when (screenType) {
        ScreenType.Top -> HomeFragment()
        ScreenType.InputInfoScreen -> InputSakeInfoFragment()
        ScreenType.SakeInfoList -> SakeListFragment()
        ScreenType.SakeDetailInfo -> SakeDetailInfoFragment()
    }.apply {
        arguments = args
    }

    private fun showDialog(dialog: DialogFragment, arguments: Bundle, resultListener: (resBundle: Bundle) -> Unit) {
        lifecycleScope.launch(Dispatchers.Main) {
            val dialogName = dialog::class.java.simpleName
            dialog.arguments = arguments
            dialog.show(supportFragmentManager, dialogName)
            dialog.setFragmentResultListener(dialogName) { requestKey, bundle ->
                if (requestKey == dialogName) resultListener(bundle)
            }
        }
    }

    private fun showToast(textResId: Int, args: Array<String>) {
        lifecycleScope.launch(Dispatchers.Main) {
            when (args.size) {
                0 -> Toast.makeText(this@KomeWoNomuActivity, resources.getString(textResId), Toast.LENGTH_LONG).show()
                1 -> Toast.makeText(this@KomeWoNomuActivity, resources.getString(textResId, args[0]), Toast.LENGTH_LONG).show()
                2 -> Toast.makeText(this@KomeWoNomuActivity, resources.getString(textResId, args[0], args[1]), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        //キーボードを閉じる
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        binding.fragmentContainer.requestFocus()
        return super.dispatchTouchEvent(ev)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Constants.REQUEST_CODE_PERMISSION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The user granted the permission.
                } else {
                    // The user canceled the choice or denied the permission.
                    finish()
                }
                return
            }
        }
    }
}
