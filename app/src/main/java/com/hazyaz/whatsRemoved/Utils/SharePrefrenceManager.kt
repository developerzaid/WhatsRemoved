package com.hazyaz.whatsRemoved.Utils

import android.content.Context
import android.content.SharedPreferences

class SharePreferencesManager constructor(context: Context) {

    companion object {
        private const val SHARED_PREF_NAME = "whatRemoved"
        private const val STATUS_FOLDER = "statusFolder"
        private const val IMAGES_FOLDER = "imagesFolder"
        private const val VIDEO_FOLDER = "videoFolder"
        private const val AUDIO_FOLDER = "audioFolder"
        private const val VOICE_NOTES_FOLDER = "voiceNoteFolder"
    }

    private val sharedPreferences by lazy(LazyThreadSafetyMode.NONE) {
        context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.put(body: SharedPreferences.Editor.() -> Unit) {
        val editor = edit()
        editor.body()
        editor.apply()
    }

    fun clearData() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }

    var statusFolder: String?
        get() = sharedPreferences.getString(STATUS_FOLDER, "")
        set(value) = sharedPreferences.put { putString(STATUS_FOLDER, value) }

    var imageFolder: String?
        get() = sharedPreferences.getString(IMAGES_FOLDER, null)
        set(value) = sharedPreferences.put { putString(IMAGES_FOLDER, value) }

    var videoFolder: String?
        get() = sharedPreferences.getString(VIDEO_FOLDER, null)
        set(value) = sharedPreferences.put { putString(VIDEO_FOLDER, value) }

    var audioFolder: String?
        get() = sharedPreferences.getString(AUDIO_FOLDER, null)
        set(value) = sharedPreferences.put { putString(AUDIO_FOLDER, value) }

    var voiceNoteFolder: String?
        get() = sharedPreferences.getString(VOICE_NOTES_FOLDER, null)
        set(value) = sharedPreferences.put { putString(VOICE_NOTES_FOLDER, value) }


}

