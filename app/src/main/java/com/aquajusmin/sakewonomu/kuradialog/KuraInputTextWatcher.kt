//package com.aquajusmin.sakewonomu.kuradialog
//
//import android.text.Editable
//import android.text.SpannableStringBuilder
//import android.text.TextWatcher
//import android.text.style.TtsSpan
//import android.util.Log
//
//
//class KuraInputTextWatcher(private val listener: Listener): TextWatcher {
//
//    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
//
//    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
//
//    override fun afterTextChanged(s: Editable?) {
//        listener.onTextChanged()
//        val sphonetic = s?.let { getPhoneticMetadata(it) }
//        sphonetic?.let {
//            // phonetic data is in sphonetic - use it as you like
//            listener.onTextFuriganaChanged(it)
//        } ?: run {// no phonetic data}
//        }
//    }
//
//    private fun getPhoneticMetadata(s: CharSequence): String? {
//        var phonetic: String? = null
//        if (s is SpannableStringBuilder) {
//            val textAsSpan = s
//            val allSpans = textAsSpan.getSpans(0, s.length, TtsSpan::class.java)
//            if (allSpans.size == 1 && allSpans[0].type == TtsSpan.TYPE_TEXT) {
//                // log shows where the span is in the text
//                Log.d("DebugTest", "textwatcher:: " +
//                        s.toString() + " [" + textAsSpan.length + "] start:" +
//                        textAsSpan.getSpanStart(allSpans[0]) + " end:" +
//                        textAsSpan.getSpanEnd(allSpans[0]))
//                phonetic = allSpans[0].args.getString(TtsSpan.ARG_TEXT)
//                textAsSpan.removeSpan(allSpans[0]) // avoid consuming again
//            }
//        }
//        return phonetic
//    }
//
//    interface Listener {
//        abstract fun onTextChanged()
//        abstract fun onTextFuriganaChanged(furigana: String)
//    }
//}
