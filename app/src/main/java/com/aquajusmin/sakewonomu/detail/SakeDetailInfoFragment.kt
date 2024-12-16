package com.aquajusmin.sakewonomu.detail

import android.app.AlertDialog
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.databinding.SakeDetailInfoFragmentBinding
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileNotFoundException


@AndroidEntryPoint
class SakeDetailInfoFragment : Fragment() {

    private val viewModel by viewModels<SakeDetailInfoViewModel>()
    private lateinit var binding: SakeDetailInfoFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SakeDetailInfoFragmentBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setup(arguments)

        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.pictureUri.collect {
                try {
                    if (it.isNotEmpty()) {
                        lifecycleScope.launch(Dispatchers.Main) {
                            Picasso.get().load(it).error(R.drawable.error_image).into(binding.sakePicture)
                        }
                    }
                } catch (e: FileNotFoundException) {
                }
            }
        }

        binding.sakeName.movementMethod = ScrollingMovementMethod()
        binding.kuraName.movementMethod = ScrollingMovementMethod()
        binding.memo.movementMethod = ScrollingMovementMethod()
        binding.place.movementMethod = ScrollingMovementMethod()

        binding.sakePicture.setOnClickListener { v ->
            val imageViewEnlarged =
                SubsamplingScaleImageView(context)
            ((v as? ImageView)?.drawable as? BitmapDrawable)?.bitmap?.let {
                imageViewEnlarged.setImage(ImageSource.bitmap(it))
                AlertDialog.Builder(activity).apply {
                    setView(imageViewEnlarged)
                    setNegativeButton("戻る", null)
                }.show()
            }
        }
        binding.editButton.setOnClickListener {
            viewModel.transitionEditSakeInfo()
        }
        binding.deleteButton.setOnClickListener {
            AlertDialog.Builder(requireActivity())
                .setMessage(R.string.sake_detail_info_delete_confirm_dialog_message)
                .setPositiveButton(R.string.sake_detail_info_delete_confirm_dialog_positive_button) { _, _ ->
                    viewModel.deleteSakeInfo()
                }
                .setNegativeButton(R.string.sake_detail_info_delete_confirm_dialog_negative_button) { _, _ -> }
                .show()
        }
    }
}
