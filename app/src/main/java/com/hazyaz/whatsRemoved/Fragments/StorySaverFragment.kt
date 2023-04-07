package com.hazyaz.whatsRemoved.Fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
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
import androidx.annotation.RequiresApi
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import com.hazyaz.whatsRemoved.Adapters.CustomStoryAdapter
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.Utils.SharePreferencesManager
import com.hazyaz.whatsRemoved.databinding.StorysaverFragmentBinding
import java.io.File
import java.util.*

class StorySaverFragment : Fragment() {
    private var binding: StorysaverFragmentBinding? = null
    var mGridview: GridView? = null
    var files: Array<String?>? = null
    private var Lfiles: Array<File>? = null
    private var mNoData: LinearLayout? = null
    private var mBroadcastReceiver: BroadcastReceiver? = null
    private var sharedPreferences: SharePreferencesManager? = null
    private var mGrantStatusFolderAccess: Button? = null

    private fun checkButtonVisibility() {
        if (sharedPreferences!!.statusFolder == null) {
            binding!!.grantButton6.visibility = View.VISIBLE
            binding!!.titleOne.visibility = View.VISIBLE
        } else if (sharedPreferences!!.statusFolder == "status") {
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
                    "status" -> {
                        checkButtonVisibility()
                    }
                }
            }
        }

        val filter = IntentFilter("status")
        requireContext().registerReceiver(mBroadcastReceiver, filter)
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = StorysaverFragmentBinding.inflate(inflater)
        sharedPreferences = SharePreferencesManager(requireContext())
        mGridview = binding!!.gridview
        mNoData = binding!!.NoDataRecyclerView
        mGrantStatusFolderAccess = binding!!.grantButton6
        checkButtonVisibility()


        if (sharedPreferences!!.statusFolder == "status") {
            binding!!.grantButton6.visibility = View.GONE
            binding!!.titleOne.visibility = View.GONE
            showFiles()
        } else {
            mNoData!!.visibility = View.VISIBLE
        }


        mGridview!!.onItemLongClickListener =
            OnItemLongClickListener { adapterView: AdapterView<*>?, view: View?, i: Int, l: Long ->
                Toast.makeText(
                    context, "You are Awesome and we love you", Toast.LENGTH_SHORT
                ).show()
                false
            }

        mGrantStatusFolderAccess?.setOnClickListener {
            (requireActivity() as MainActivity).openFolderForStatus()
        }

        // Inflate the layout for this fragment
        return binding!!.root
    }

    private fun showFiles() {
        filesByVersion
        if (files != null) {
            mNoData!!.visibility = View.GONE
            mGridview!!.visibility = View.VISIBLE
            mGridview!!.adapter = CustomStoryAdapter(context, files)
            mGridview!!.onItemClickListener =
                OnItemClickListener { parent: AdapterView<*>?, view: View?, position: Int, id: Long ->
                    val intent = Intent(
                        activity, DataViewer::class.java
                    )
                    intent.putExtra("IMAGEURL", files!![position])
                    startActivity(intent)
                }
        } else {
            mNoData!!.visibility = View.VISIBLE
            mGridview!!.visibility = View.GONE
        }
    }


    private val filesByVersion: Unit
        get() {
            if (Build.VERSION.SDK_INT < 30) {
                val dir = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
                )
                Lfiles = if (dir.exists() && dir.isDirectory) {
                    dir.listFiles()
                } else {
                    val recent =
                        File(Environment.getExternalStorageDirectory(), "/WhatsApp/Media/.Statuses")
                    recent.listFiles()
                }
                if (Lfiles != null) {
                    for (f in Lfiles!!) {
                        f.name
                        if (f.name.trim { it <= ' ' }.equals(".nomedia", ignoreCase = true)) {
                            f.delete()
                            continue
                        }
                    }
                }
                val dire = File(
                    Environment.getExternalStorageDirectory()
                        .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
                )
                Lfiles = if (dire.exists() && dire.isDirectory) {
                    val recentnew = File(
                        Environment.getExternalStorageDirectory(),
                        "/Android/media/com.whatsapp/WhatsApp/Media/.Statuses"
                    )
                    recentnew.listFiles()
                } else {
                    val recentnew =
                        File(Environment.getExternalStorageDirectory(), "/WhatsApp/Media/.Statuses")
                    recentnew.listFiles()
                }
                if (Lfiles != null) {
                    Arrays.sort(Lfiles) { o1, o2 ->
                        if ((o1 as File).lastModified() > (o2 as File).lastModified()) {
                            -1
                        } else if (o1.lastModified() < o2.lastModified()) {
                            +1
                        } else {
                            0
                        }
                    }
                    files = arrayOfNulls(Lfiles!!.size)
                    var i = 0
                    for (f in Lfiles!!) {
                        files!![i] = f.absolutePath
                        i++
                    }
                } else {
                    files = null
                }
            } else {

                val directoryUri =
                    Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses")

                Log.d("DATA____1", directoryUri.toString())

                //Taking permission to retain access
                requireActivity().contentResolver.takePersistableUriPermission(
                    directoryUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                //Now you have access to the folder, you can easily view the content or do whatever you want.
                val documentsTree =
                    DocumentFile.fromTreeUri(requireContext(), directoryUri) ?: return
                val childDocuments = documentsTree.listFiles().asList()
                Log.d("DATA____2", documentsTree.name.toString())
                Log.d("DATA____222", "Size- ${childDocuments.size}")

                var x = 0
                files = arrayOfNulls(childDocuments.size)
                for (i in childDocuments.indices) {
                    files!![x] = childDocuments[i].uri.toString()
                    x++
                    Log.d("DATA____3", childDocuments[i].uri.toString())
                    Log.d("DATA____3", childDocuments[i].name.toString())
                }
                Log.d("DATA____4", files!!.size.toString())

            }
        }


//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            Log.d("MainActivity__", "Story Frag Visible")
//            (requireActivity() as MainActivity).showAdd()
//        } else {
//            Log.d("MainActivity__", "Story Frag Not Visible")
//        }
//    }

}