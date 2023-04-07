package com.hazyaz.whatsRemoved.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
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
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.hazyaz.whatsRemoved.Adapters.CustomImageAdapter
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.R
import com.hazyaz.whatsRemoved.Utils.SharePreferencesManager
import com.hazyaz.whatsRemoved.databinding.ImageFragmentBinding
import java.io.File
import java.util.*


class ImageFragment : Fragment() {
    private var binding: ImageFragmentBinding? = null
    var mGridview: GridView? = null
    var files: Array<File>? = null
    private var mNoData: LinearLayout? = null
    private var mGrantImageFolderAccess: Button? = null
    private var sharedPreferences: SharePreferencesManager? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null


    override fun onResume() {
        super.onResume()
        mBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.action) {
                    "image" -> {
                        checkButtonVisibility()
                    }
                }
            }
        }

        val filter = IntentFilter("image")
        requireContext().registerReceiver(mBroadcastReceiver, filter)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ImageFragmentBinding.inflate(inflater)
        sharedPreferences = SharePreferencesManager(requireContext())
        mNoData = binding!!.NoDataRecyclerView
        mGridview = binding!!.gridview
        mGrantImageFolderAccess = binding!!.grantButton6
        checkButtonVisibility()
        files = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("ImageFragment__", "Android 11")
            val deletedFiles = File(
                Environment.getExternalStorageDirectory().absolutePath,
                "/Download/WhatsRemoved/recent/Deleted Images/nomedia/"
            )
            deletedFiles.listFiles()
        } else {
            Log.d("ImageFragment__", "Android 10")
            val deletedFiles = File(
                Environment.getExternalStorageDirectory(),
                "/WhatsRemoved/recent/Deleted Images/nomedia"
            )
            deletedFiles.listFiles()
        }
        Thread {
            if (files != null) {
                files?.let {
                    Arrays.sort(it) { o1, o2 ->
                        if ((o1 as File).lastModified() > (o2 as File).lastModified()) {
                            return@sort -1
                        } else if (o1.lastModified() < o2.lastModified()) {
                            return@sort +1
                        } else {
                            return@sort 0
                        }
                    }
                }
            }
        }.start()
        if (sharedPreferences!!.imageFolder == "image") {
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
                    context, "You are Awesome and we love you", Toast.LENGTH_LONG
                ).show()
                false
            }

        mGrantImageFolderAccess?.setOnClickListener {
            (requireActivity() as MainActivity).openFolderForImages()
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

    private fun checkButtonVisibility() {
        if (sharedPreferences!!.imageFolder == null) {
            binding!!.grantButton6.visibility = View.VISIBLE
            binding!!.titleOne.visibility = View.VISIBLE
        } else if (sharedPreferences!!.imageFolder == "image") {
            binding!!.grantButton6.visibility = View.GONE
            binding!!.titleOne.visibility = View.GONE
            showFiles()
        }
    }

}