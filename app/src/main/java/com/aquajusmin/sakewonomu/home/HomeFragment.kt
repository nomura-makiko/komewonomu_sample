package com.aquajusmin.sakewonomu.home

import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.InputSakeInfoType
import com.aquajusmin.sakewonomu.constants.ScreenType
import com.aquajusmin.sakewonomu.databinding.HomeFragmentBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment: Fragment() {

    private val viewModel by viewModels<HomeViewModel>()
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recordButton.setOnClickListener {
            viewModel.transitionScreen(ScreenType.InputInfoScreen, Bundle().apply {
                putString(Constants.KEY_INPUT_TYPE, InputSakeInfoType.New.name)
            })
        }
        binding.showListButton.setOnClickListener {
            viewModel.transitionScreen(ScreenType.SakeInfoList)
        }
        binding.recentlyListLabel.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.recentlyListMore.setOnClickListener {
            viewModel.transitionScreen(ScreenType.SakeInfoList, Bundle().apply {
                putString(Constants.KEY_INFO_LIST_TYPE, Constants.VALUE_INFO_LIST_TYPE_CHART)
            })
        }
        binding.favoriteListLabel.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.favoriteListMore.setOnClickListener {
            viewModel.transitionScreen(ScreenType.SakeInfoList, Bundle().apply {
                putString(Constants.KEY_INFO_LIST_TYPE, Constants.VALUE_INFO_LIST_TYPE_LIST)
            })
        }
        viewModel.recentlySakeInfoList.observe(viewLifecycleOwner) { recentlyList ->
            binding.recentlyInfo1.setSakeInfo(recentlyList[0])
            binding.recentlyInfo2.setSakeInfo(recentlyList[1])
            binding.recentlyInfo3.setSakeInfo(recentlyList[2])

        }
        viewModel.favoriteSakeInfoList.observe(viewLifecycleOwner) { favoriteList ->
            binding.favoriteInfo1.setSakeInfo(favoriteList[0])
            binding.favoriteInfo2.setSakeInfo(favoriteList[1])
            binding.favoriteInfo3.setSakeInfo(favoriteList[2])
        }
    }
}
