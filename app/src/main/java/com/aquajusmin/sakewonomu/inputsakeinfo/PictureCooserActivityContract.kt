package com.aquajusmin.sakewonomu.inputsakeinfo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.aquajusmin.sakewonomu.BuildConfig
import java.io.File
import java.util.*


class PictureCooserActivityContract(private val context: Context?) : ActivityResultContract<Unit, Uri?>() {

    private var cameraUri: Uri? = null
    private var imageFileName: String = ""
    private var cameraStorageUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

    // 端末のファイルを選択するためのIntent
    private val getContentIntent
        get() = context?.let {
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE);
                type = "image/jpeg";
            }
        }

    // カメラ撮影するためのIntent
    private val takePictureIntent
        @SuppressLint("SimpleDateFormat")
        get() = context?.let {
            cameraUri = createSaveFileUri(context)

            Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, cameraUri)
            }
        }

    override fun createIntent(context: Context, input: Unit): Intent {
        // OpenDocumentとTakePictureから選択できるChooserを起動する
        return Intent.createChooser(getContentIntent, "選択").apply {
            MediaStore.Images.Media.DATA
            // 端末にカメラがあればTakePictureの選択肢を追加
            val hasCameraFeature = context.packageManager
                ?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
                ?: false
            if (hasCameraFeature) {
                putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(takePictureIntent))
                Log.d("DebugTest", "createIntent :: cameraUri=$cameraUri")
            }
        }
    }

    private fun createSaveFileUri(context: Context): Uri? {
        imageFileName = Calendar.getInstance().let {
            "${it.get(Calendar.YEAR)}" +
                    (it.get(Calendar.MONTH) + 1).toString().padStart(2, '0') +
                    (it.get(Calendar.DATE)).toString().padStart(2, '0') +
                    "_${(it.get(Calendar.HOUR_OF_DAY)).toString().padStart(2, '0')}" +
                    (it.get(Calendar.MINUTE)).toString().padStart(2, '0')
        }

//        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/KomeWoNomu")
//        if (!storageDir.exists()) {
//            storageDir.mkdir()
//        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "${BuildConfig.APPLICATION_ID}_$imageFileName")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        cameraStorageUri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        return context.contentResolver.insert(cameraStorageUri, values)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return if (context == null || resultCode != Activity.RESULT_OK) {
            null
        } else {
            // OpenDocumentで選択した場合は intent?.data に、
            // TakePicutreでカメラ撮影して保存されたファイルはcacheUriに、
            // content://〜 形式のUriが入っている
            // 画像選択の場合
            Log.d("DebugTest", "picture=${intent?.data}, camera=$cameraUri")
            intent?.data?.also {
                val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(it, takeFlags)

                // カメラ用に用意したURIを削除
//                val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
//                val values = ContentValues().apply {
//                    put(MediaStore.Images.Media.DISPLAY_NAME, "${BuildConfig.APPLICATION_ID}_$imageFileName")
//                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
//                }
                cameraUri?.let { uri ->
                    uri.path?.let { path -> File(path).delete() }
                    context.contentResolver.delete(cameraStorageUri, MediaStore.Files.FileColumns.DISPLAY_NAME + "=?", arrayOf(imageFileName))
                }
            } ?: cameraUri
        }
    }
}