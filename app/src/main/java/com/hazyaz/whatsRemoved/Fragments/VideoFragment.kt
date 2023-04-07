package com.hazyaz.whatsRemoved.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import androidx.fragment.app.Fragment
import com.hazyaz.whatsRemoved.Adapters.CustomImageAdapter
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.Utils.SharePreferencesManager
import com.hazyaz.whatsRemoved.databinding.VideoFragmentBinding
import java.io.File
import java.util.*

class VideoFragment : Fragment() {
    private var binding: VideoFragmentBinding? = null
    var mGridview: GridView? = null
    var files: Array<File>? = null
    private var mNoData: LinearLayout? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var sharedPreferences: SharePreferencesManager? = null
    private var mGrantVideoFolderAccess: Button? = null


    override fun onResume() {
        super.onResume()
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    "video" -> {
                        checkButtonVisibility()
                    }
                }
            }
        }

        val filter = IntentFilter("video")
        requireContext().registerReceiver(mBroadcastReceiver, filter)
    }

    private fun checkButtonVisibility() {
        if (sharedPreferences!!.videoFolder == null) {
            binding!!.grantButton6.visibility = View.VISIBLE
            binding!!.titleOne.visibility = View.VISIBLE
        } else if (sharedPreferences!!.videoFolder == "video") {
            binding!!.grantButton6.visibility = View.GONE
            binding!!.titleOne.visibility = View.GONE
            showFiles()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = VideoFragmentBinding.inflate(inflater)
        sharedPreferences = SharePreferencesManager(requireContext())
        mGridview = binding!!.gridview
        mNoData = binding!!.NoDataRecyclerView
        mGrantVideoFolderAccess = binding!!.grantButton6
        checkButtonVisibility()

        files = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("VideoFragment__", "Android 11")
            val deletedFiles = File(
                Environment.getExternalStorageDirectory().absolutePath,
                "/Download/WhatsRemoved/recent/Deleted Video/nomedia/"
            )
            deletedFiles.listFiles()
        } else {
            Log.d("VideoFragment__", "Android 10")
            val deletedFiles = File(
                Environment.getExternalStorageDirectory(),
                "/WhatsRemoved/recent/Deleted Video/nomedia"
            )
            deletedFiles.listFiles()
        }
        Thread {
            if (files != null) {
                Arrays.sort(files) { o1, o2 ->
                    if ((o1 as File).lastModified() > (o2 as File).lastModified()) {
                        return@sort -1
                    } else if (o1.lastModified() < o2.lastModified()) {
                        return@sort +1
                    } else {
                        return@sort 0
                    }
                }
            }
        }.start()
        if (sharedPreferences!!.videoFolder == "video") {
            binding!!.grantButton6.visibility = View.GONE
            binding!!.titleOne.visibility = View.GONE
            showFiles()
        } else {
            mNoData!!.visibility = View.VISIBLE
            mGridview!!.visibility = View.GONE
        }


        mGridview!!.onItemLongClickListener =
            OnItemLongClickListener { adapterView: AdapterView<*>?, view: View?, i: Int, l: Long ->
                Toast.makeText(
                    context, "You are Awesome...! Love from Hazyaz Technologies", Toast.LENGTH_LONG
                ).show()
                false
            }


        mGrantVideoFolderAccess?.setOnClickListener {
            (requireActivity() as MainActivity).openFolderForVideo()
        }
        // Inflate the layout for this fragment
        return binding!!.root
    }

    private fun showFiles() {
        if (files != null) {
            mNoData!!.visibility = View.GONE
            mGridview!!.visibility = View.VISIBLE
            mGridview!!.adapter = CustomImageAdapter(context, files)
            mGridview!!.onItemClickListener =
                OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                    val intent = Intent(
                        activity, DataViewer::class.java
                    )
                    intent.putExtra("IMAGEURL", files!![position].toString())
                    startActivity(intent)
                }
        } else {
            mNoData!!.visibility = View.VISIBLE
            mGridview!!.visibility = View.GONE
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            Log.d("MainActivity__", "Video Frag Visible")
//            (requireActivity() as MainActivity).showAdd()
        } else {
            Log.d("MainActivity__", "Video Frag Not Visible")
        }
    }
}