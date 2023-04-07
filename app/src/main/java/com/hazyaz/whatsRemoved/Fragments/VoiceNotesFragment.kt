package com.hazyaz.whatsRemoved.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hazyaz.whatsRemoved.Adapters.CustomAudioAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.hazyaz.whatsRemoved.Adapters.CustomImageAdapter
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.Utils.SharePreferencesManager
import com.hazyaz.whatsRemoved.databinding.VoiceNotesFragmentBinding
import java.io.File
import java.util.*

class VoiceNotesFragment : Fragment() {
    private var binding: VoiceNotesFragmentBinding? = null
    var mRecyclerView: RecyclerView? = null
    var files: Array<File>? = null
    private var mNoData: LinearLayout? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var sharedPreferences: SharePreferencesManager? = null
    private var mGrantVoiceNoteAccess: Button? = null

    private fun checkButtonVisibility() {
        if (sharedPreferences!!.voiceNoteFolder == null) {
            binding!!.grantButton6.visibility = View.VISIBLE
            binding!!.titleOne.visibility = View.GONE
        } else if (sharedPreferences!!.voiceNoteFolder == "voice") {
            binding!!.grantButton6.visibility = View.GONE
            binding!!.titleOne.visibility = View.GONE
            showFiles()
        }
    }

    override fun onResume() {
        super.onResume()
//        (requireActivity() as MainActivity).showAdd()
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    "voiceNote" -> {
                        checkButtonVisibility()
                    }
                }
            }
        }

        val filter = IntentFilter("voiceNote")
        requireContext().registerReceiver(mBroadcastReceiver, filter)
    }

    //Voice Notes Not working rest everything is ready for update
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VoiceNotesFragmentBinding.inflate(inflater)
        sharedPreferences = SharePreferencesManager(requireContext())
        mRecyclerView = binding!!.convListyt
        mNoData = binding!!.NoDataRecyclerView
        mGrantVoiceNoteAccess = binding!!.grantButton6
        checkButtonVisibility()

        files = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("NotesFragment__", "Android 11")
            val deletedFiles = File(
                Environment.getExternalStorageDirectory().absolutePath,
                "/Download/WhatsRemoved/recent/Deleted Voice Notes/nomedia/"
            )
            deletedFiles.listFiles()
        } else {
            val deletedFiles = File(
                Environment.getExternalStorageDirectory(),
                "/WhatsRemoved/recent/Deleted Voice Notes/nomedia/"
            )
            deletedFiles.listFiles()
        }
        if (files != null) {
            Thread {
                Arrays.sort(files) { o1, o2 ->
                    if ((o1 as File).lastModified() > (o2 as File).lastModified()) {
                        return@sort -1
                    } else if (o1.lastModified() < o2.lastModified()) {
                        return@sort +1
                    } else {
                        return@sort 0
                    }
                }
            }.start()
        }

        if (sharedPreferences!!.voiceNoteFolder == "voice") {
            binding!!.grantButton6.visibility = View.GONE
            binding!!.titleOne.visibility = View.GONE
            showFiles()
        } else {
            mNoData!!.visibility = View.VISIBLE
        }

        mGrantVoiceNoteAccess?.setOnClickListener {
            (requireActivity() as MainActivity).openFolderForVoiceNotes()
        }


        return binding!!.root
    }

    private fun showFiles() {
        if (files == null) {
            mNoData!!.visibility = View.VISIBLE
            mRecyclerView!!.visibility = View.GONE
        } else {
            mNoData!!.visibility = View.GONE
            mRecyclerView!!.visibility = View.VISIBLE
            val mAdapter = CustomAudioAdapter(context, files)
            mRecyclerView!!.adapter = mAdapter
            val linearLayoutManager = LinearLayoutManager(context)
            mRecyclerView!!.setHasFixedSize(true)
            mRecyclerView!!.layoutManager = linearLayoutManager
            mRecyclerView!!.addItemDecoration(
                DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    fun getListFiles(parentDir: File): List<File> {
        val inFiles = ArrayList<File>()
        val files = parentDir.listFiles()
        if (files != null) {
            for (file in files) {
                if (file.isDirectory) {
                    inFiles.addAll(getListFiles(file))
                } else {
                    inFiles.add(file)
                }
            }
        }
        return inFiles
    }
}