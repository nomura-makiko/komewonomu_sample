package com.aquajusmin.sakewonomu.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.aquajusmin.sakewonomu.BuildConfig
import com.aquajusmin.sakewonomu.R
import com.aquajusmin.sakewonomu.constants.Constants
import com.aquajusmin.sakewonomu.constants.ScreenAction
import com.aquajusmin.sakewonomu.model.repository.data.SakeInfoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

object KomeWoNoMuUtil {

    fun getPointLabel(value: Int) =
        if (value != Constants.INVALID_POINT_VALUE) "★".repeat(value) + "☆".repeat(5 - value)
        else " ― "

    fun getBitmap(context: Context, uriText: String): Bitmap? {
        if (uriText.isEmpty()) return null
        val uri = parseUri(context, uriText)
        return uri?.let {
            context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(BufferedInputStream(it))
            }
        }
    }

    fun parseUri(context: Context, uriText: String): Uri? {
        val uri = if (uriText.isEmpty()) null
        else Uri.parse(uriText)?.also {
            // DBから読み出し時に、永続化しておかないとコケる？ほんとに？
//            context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        return uri
    }

    fun getDateTextFormat(year: Int, month: Int, day: Int) = "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"

    fun getDateText(date: Date) = SimpleDateFormat.getDateInstance().format(date)

    @SuppressLint("SimpleDateFormat")
    fun getDate(dateText: String) = dateText.replace("/", "").let {
        if (it.isNotEmpty() && it.length != 8 && Regex("\\d*").matches(it)) null
        else Calendar.getInstance().apply {

            set(Calendar.YEAR, it.substring(0,4).toInt())
            set(Calendar.MONTH, it.substring(4,6).toInt())
            set(Calendar.DAY_OF_MONTH, it.substring(6,8).toInt())
        }
    }

    fun getSakeRankText(context: Context, value: Int) = SakeInfoData.allSakeRank()
        .firstOrNull { sakeRank -> sakeRank.value == value }?.let {
            context.getString(it.resId)
        } ?: ""

    fun getSakeTypeText(context: Context, value: Long): String {
        val textArrayList = arrayListOf<String>()
        SakeInfoData.allSakeType().forEach { sakeType ->
            if (sakeType.value.toInt() and value.toInt() != 0) textArrayList.add(context.getString(sakeType.resId))
        }
        return textArrayList.toArray().joinToString(separator = ", ")
    }

    fun getKeyBoardDismissListener(editText: EditText, inputMethodManager: InputMethodManager) = object : View.OnKeyListener {
        override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
            //イベントを取得するタイミングには、ボタンが押されてなおかつエンターキーだったときを指定
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                //キーボードを閉じる
                inputMethodManager.hideSoftInputFromWindow(
                    editText.windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
                return true;
            }
            return false;
        }
    }

    fun saveSharedPrefs(context: Context, key: String, value: Long) {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun saveSharedPrefs(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun saveSharedPrefs(context: Context, key: String, value: Boolean) {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun getStringSharedPrefs(context: Context, key: String): String {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return sharedPref?.getString(key, "") ?: ""
    }

    fun getLongSharedPrefs(context: Context, key: String): Long {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return sharedPref?.getLong(key, 0L) ?: 0L
    }

    fun getBooleanSharedPrefs(context: Context, key: String): Boolean {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        return sharedPref?.getBoolean(key, false) ?: false
    }

    fun checkPermission(context: Context) = context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED

    private fun getPathFromUri(context: Context, resultUri: Uri?): Uri? {
        val uri = resultUri ?: return null

        // DocumentProvider
        Log.e("DebugTest", "uri:" + uri.authority)

        if (DocumentsContract.isDocumentUri(context, uri)) {
            when (uri.authority) {
                // ExternalStorageProvider
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path -> {
//                "com.android.externalstorage.documents" -> {
                    Log.e("DebugTest", "uri is # externalstorage #")

                    val contentUri = ContentUris.withAppendedId(
                        FileProvider.getUriForFile(
                            context,
                            "${BuildConfig.APPLICATION_ID}.provider",
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                        ),
                        DocumentsContract.getDocumentId(uri).toLong()
                    )
                    return getDataColumn(context, contentUri, null, null)
//                    val docId = DocumentsContract.getDocumentId(uri)
//                    val split = docId.split(":".toRegex()).toTypedArray()
//                    val type = split[0]
//                    return if ("primary".equals(type, ignoreCase = true)) {
//                        Environment.getExternalStorageDirectory().toString() + "/" + split[1]
//                    } else {
//                        "/stroage/" + type + "/" + split[1]
//                    }

                }
                // DownloadsProvider
                "com.android.providers.downloads.documents" -> {
                    Log.e("DebugTest", "uri is # downloads #")
                    val id = DocumentsContract.getDocumentId(uri)
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                    return getDataColumn(context, contentUri, null, null)
                }
                // MediaProvider
                "com.android.providers.media.documents" -> {
                    Log.e("DebugTest", "uri is # media #")
//                    val docId = DocumentsContract.getDocumentId(uri)
//                    val split = docId.split(":".toRegex()).toTypedArray()
//                    val type = split[0]
//                    var contentUri: Uri? = null
//                    contentUri = MediaStore.Files.getContentUri("external")
//                    val selection = "_id=?"
//                    return getDataColumn(context, contentUri, selection, arrayOf(split[1]))
                    return MediaStore.getMediaUri(context, uri)
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) { //MediaStore
            Log.e("DebugTest", "uri is # MediaStore #")
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) { // File
            Log.e("DebugTest", "uri is # File #")
            return uri
        }
        return null
    }

    private fun getDataColumn(
        context: Context, uri: Uri?, selection: String?,
        selectionArgs: Array<String?>?
    ): Uri? = context.contentResolver.query(
        uri!!, arrayOf(MediaStore.Files.FileColumns.DATA), selection, selectionArgs, null
    )?.use {
        if (it.moveToFirst()) {
            ContentUris.withAppendedId(uri, it.getLong(it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)))
        } else null
    }

//    fun getDataColumn(context: Context, uri: Uri?, selection: String?,
//                      selectionArgs: Array<String>?): String? {
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns.DATA
//        )
//        uri?.let {
//            context.contentResolver.query(
//                uri, projection, selection, selectionArgs, null)?.use {
//                if (it.moveToFirst()) {
//                    val cindex = it.getColumnIndexOrThrow(projection[0])
//                    return it.getString(cindex)
//                }
//            }
//        }
//        return null
//    }
}
