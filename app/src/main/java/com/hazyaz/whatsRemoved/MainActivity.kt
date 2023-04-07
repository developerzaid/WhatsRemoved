package com.hazyaz.whatsRemoved

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.documentfile.provider.DocumentFile
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.*
import com.hazyaz.whatsRemoved.Adapters.CustomPagerAdapter
import com.hazyaz.whatsRemoved.Notification.ActiveService
import com.hazyaz.whatsRemoved.Notification.BroadCastReciever
import com.hazyaz.whatsRemoved.Notification.NotificationListener
import com.hazyaz.whatsRemoved.Utils.DepthPageTransformer
import com.hazyaz.whatsRemoved.Utils.SharePreferencesManager
import com.hazyaz.whatsRemoved.databinding.ActivityMainBinding
import com.hazyaz.whatsRemoved.permission.PermissionKotlin


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var binding: ActivityMainBinding? = null
    var mToolbar: Toolbar? = null
    var mViewPager: ViewPager? = null
    var mTabLayout: TabLayout? = null
    var mCustomPagerAdapter: CustomPagerAdapter? = null
    var mServiceIntent: Intent? = null
    private var mInterstitialAd: InterstitialAd? = null
    private val mInterstitialAd2: InterstitialAd? = null
    private var sharedPreferences: SharePreferencesManager? = null

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setAdds()
        initAds()
          initViews()
    }//permission is automatically granted on sdk<23 upon installation


    override fun onResume() {
        super.onResume()
        Log.d("isServiceRunning?", isMyServiceRunning().toString())
    }

    private fun isMyServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (ActiveService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }


    private fun getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
                )
            }
        }
    }

    // For Android 11
    private val isReadStoragePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11
            if (Environment.isExternalStorageManager()) {
                true
            } else if (sharedPreferences?.statusFolder.equals("status")) {
                true
            } else {
                try {
                    Log.d("PRINT___", "TWO")
                    openFolder()
                } catch (exception: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Pls Select Whatsapp folder",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    }
                    startActivityForResult(intent, OPEN_FOLDER_REQUEST_CODE)
                }
                false
            }


        } else if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Log.v(TAG, "Permission is granted1")
                true
            } else {
                Log.v(TAG, "Permission is revoked1")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_FOR_ANDROID_BELOW_11
                )
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted1")
            true
        }

    private fun openFolder() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
        }
        val wa_status_uri =
            Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri)
        }
        startActivityForResult(intent, OPEN_FOLDER_REQUEST_CODE)
    }

    override fun onStart() {
        super.onStart()
        val isNotificationServiceRunning: Boolean = isNotificationServiceRunning
        if (!isNotificationServiceRunning) {
            val intent = Intent(this@MainActivity, PermissionKotlin::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        startService(Intent(this, NotificationListener::class.java))
    }

    private val isNotificationServiceRunning: Boolean
        get() {
            val contentResolver = contentResolver
            val enabledNotificationListeners =
                Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
            val packageName = packageName
            return enabledNotificationListeners != null && enabledNotificationListeners.contains(
                packageName
            )
        }

    @SuppressLint("NonConstantResourceId")
    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.more_apps -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/developer?id=Hazyaz++Technologies&hl=en_IN")
                )
                startActivity(intent)
            }
            R.id.story_saver -> mViewPager!!.currentItem = 5
            R.id.voice_notes -> mViewPager!!.currentItem = 3
            R.id.audio -> mViewPager!!.currentItem = 4
            R.id.videos -> mViewPager!!.currentItem = 2
            R.id.images -> mViewPager!!.currentItem = 1
            R.id.Share -> try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Whats Removed | Recover WhatsApp Messages"
                )
                var shareMessage =
                    "\nDownload this application to get deleted WhatsApp messages\n\n"
                shareMessage = """
                    ${shareMessage}https://play.google.com/store/apps/details?id=com.hazyaz.whatsRemoved
                    
                    
                    """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to share", Toast.LENGTH_SHORT).show()
            }
            R.id.privacy_policy -> {
                val browserIntent98 = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.hazyaztechnologies.in/apps-privacy-policy/")
                )
                startActivity(browserIntent98)
            }
            R.id.contact_us -> {
                val browserIntent99 = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.hazyaztechnologies.in/contact/")
                )
                startActivity(browserIntent99)
            }
        }
        val drawer = binding!!.drawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

//    private val isMyServiceRunning: Boolean
//        get() {
//            val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
//            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
//                if (ActiveService::class.java.name == service.service.className) {
//                    return true
//                }
//            }
//            return false
//        }

    private fun initAds() {
        MobileAds.initialize(this) { }
        val mAdView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        val Inter1 = AdRequest.Builder().build()
        InterstitialAd.load(this, "ca-app-pub-2675887677224394/2407124224", Inter1,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.i(TAG, loadAdError.message)
                    mInterstitialAd = null
                }
            })
    }

    private fun initViews() {
//        RestarterBroadcastReceiver.startWorker(applicationContext)

        sharedPreferences = SharePreferencesManager(applicationContext)

        mToolbar = binding!!.mainPageToolbar.mainAppBar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "WHATS REMOVED"
        mViewPager = binding!!.viewPagerMain

//        mCustomPagerAdapter = new CustomPagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(mCustomPagerAdapter);
        mViewPager!!.setPageTransformer(true, DepthPageTransformer())
        mViewPager!!.offscreenPageLimit = 2
        mTabLayout = binding!!.tabBarMain
        mTabLayout!!.setupWithViewPager(mViewPager)
        val drawer = binding!!.drawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            mToolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)


        startService(Intent(this, NotificationListener::class.java))

//      Starting services one app starts for first time after that services will run independantly


//      Starting services one app starts for first time after that services will run independantly
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(baseContext, ActiveService::class.java))
            } else {
                startService(Intent(baseContext, ActiveService::class.java))
            }
            startService(Intent(this, NotificationListener::class.java))
            val `in` = IntentFilter()
            `in`.addAction(".Notification.BroadCastReciever")
            registerReceiver(BroadCastReciever(), `in`)
        } catch (e: java.lang.Exception) {
            Toast.makeText(this, "Cannot Start Service", Toast.LENGTH_SHORT).show()
        }


//      For showing Interstial Ads while swiping between tabs and banner ad below screen
        val pagerAdapter = CustomPagerAdapter(supportFragmentManager)
        mViewPager!!.adapter = pagerAdapter
        //change Tab selection when swipe ViewPager
        mViewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(mTabLayout))
        //change ViewPager page when tab selected
        mTabLayout!!.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab) {
                if (tab.position == 3 && mInterstitialAd != null) {
                    mInterstitialAd!!.show(this@MainActivity)
                }
                mViewPager!!.currentItem = tab.position
            }

            override fun onTabUnselected(tab: Tab) {}
            override fun onTabReselected(tab: Tab) {}
        })
//        isReadStoragePermissionGranted
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (mInterstitialAd != null) {
            mInterstitialAd!!.show(this@MainActivity)
            finish()
        } else {
            finish()
        }
    }

    companion object {
        private const val TAG = "MainActivity__"
        private const val OPEN_FOLDER_REQUEST_CODE = 2
        private const val PERMISSION_FOR_ANDROID_BELOW_11 = 3
        private const val OPEN_STATUS_FOLDER_REQUEST_CODE = 2
        private const val OPEN_IMAGES_FOLDER_REQUEST_CODE = 3
        private const val OPEN_VIDEO_FOLDER_REQUEST_CODE = 4
        private const val OPEN_AUDIO_FOLDER_REQUEST_CODE = 5
        private const val OPEN_VOICE_NOTE_FOLDER_REQUEST_CODE = 6

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                OPEN_STATUS_FOLDER_REQUEST_CODE -> {
                    val directoryUri = data?.data ?: return
                    Log.d("DATA____", directoryUri.toString())
                    contentResolver.takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    val documentsTree =
                        DocumentFile.fromTreeUri(application, directoryUri) ?: return
                    val childDocuments = documentsTree.listFiles().asList()
                    Log.d("DATA____", documentsTree.name.toString())
                    Log.d("DATA____", childDocuments.toString())
                    Log.d("DATA____", childDocuments.size.toString())

                    if (directoryUri.toString() == "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
                    ) {
                        sharedPreferences?.statusFolder = "status"
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendBroadcastMsg("status")
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Pls Grant Access to Whatsapp Status Folder",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                OPEN_IMAGES_FOLDER_REQUEST_CODE -> {
                    val directoryUri = data?.data ?: return
                    Log.d("DATA____", directoryUri.toString())
                    //Taking permission to retain access
                    contentResolver.takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    //Now you have access to the folder, you can easily view the content or do whatever you want.
                    val documentsTree =
                        DocumentFile.fromTreeUri(application, directoryUri) ?: return
                    val childDocuments = documentsTree.listFiles().asList()
                    Log.d("DATA____", documentsTree.name.toString())
                    Log.d("DATA____", childDocuments.toString())
                    Log.d("DATA____", childDocuments.size.toString())

                    if (directoryUri.toString() == "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Images") {
                        sharedPreferences?.imageFolder = "image"
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendBroadcastMsg("image")
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Pls Grant Access to Whatsapp Image Folder",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                OPEN_VIDEO_FOLDER_REQUEST_CODE -> {
                    val directoryUri = data?.data ?: return
                    Log.d("DATA____", directoryUri.toString())
                    //Taking permission to retain access
                    contentResolver.takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    //Now you have access to the folder, you can easily view the content or do whatever you want.
                    val documentsTree =
                        DocumentFile.fromTreeUri(application, directoryUri) ?: return
                    val childDocuments = documentsTree.listFiles().asList()
                    Log.d("DATA____", documentsTree.name.toString())
                    Log.d("DATA____", childDocuments.toString())
                    Log.d("DATA____", childDocuments.size.toString())

                    if (directoryUri.toString() == "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Video") {
                        sharedPreferences?.videoFolder = "video"
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendBroadcastMsg("video")
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Pls Grant Access to Whatsapp Video Folder",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
                OPEN_AUDIO_FOLDER_REQUEST_CODE -> {
                    val directoryUri = data?.data ?: return
                    Log.d("DATA____", directoryUri.toString())
                    //Taking permission to retain access
                    contentResolver.takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    //Now you have access to the folder, you can easily view the content or do whatever you want.
                    val documentsTree =
                        DocumentFile.fromTreeUri(application, directoryUri) ?: return
                    val childDocuments = documentsTree.listFiles().asList()
                    Log.d("DATA____", documentsTree.name.toString())
                    Log.d("DATA____", childDocuments.toString())
                    Log.d("DATA____", childDocuments.size.toString())

                    if (directoryUri.toString() == "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Audio") {
                        sharedPreferences?.audioFolder = "audio"
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendBroadcastMsg("audio")
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Pls Grant Access to Whatsapp Audio Folder",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
                OPEN_VOICE_NOTE_FOLDER_REQUEST_CODE -> {
                    val directoryUri = data?.data ?: return
                    Log.d("DATA____", directoryUri.toString())
                    //Taking permission to retain access
                    contentResolver.takePersistableUriPermission(
                        directoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    //Now you have access to the folder, you can easily view the content or do whatever you want.
                    val documentsTree =
                        DocumentFile.fromTreeUri(application, directoryUri) ?: return

                    val childDocuments = documentsTree.listFiles().asList()
                    Log.d("DATA____", documentsTree.name.toString())
                    Log.d("DATA____", childDocuments.toString())
                    Log.d("DATA____", childDocuments.size.toString())

                    if (directoryUri.toString() == "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Voice%20Notes") {
                        sharedPreferences?.voiceNoteFolder = "voice"
                        Toast.makeText(
                            this@MainActivity,
                            "Permission granted",
                            Toast.LENGTH_SHORT
                        ).show()
                        sendBroadcastMsg("voiceNote")
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Pls Grant Access to Whatsapp Voice Note Folder",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }


        }
    }

    private fun sendBroadcastMsg(name: String) {
        val intent = Intent(name)
        intent.setPackage(packageName)
        intent.putExtra("message", name)
        applicationContext.sendBroadcast(intent)
    }


    private fun setAdds() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            this,
            "ca-app-pub-2675887677224394/2407124224",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.toString())
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                }
            })

    }


    fun showAdd() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.")
                }

                override fun onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    Log.d(TAG, "Ad dismissed fullscreen content.")
                    mInterstitialAd = null
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.")
                    mInterstitialAd = null
                }

                override fun onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.")
                }

                override fun onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.")
                }
            }
            mInterstitialAd?.show(this)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    // Permissions

    fun openFolderForStatus() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        val wa_status_uri =
            Uri.parse(
                "content://com.android.externalstorage.documents/tree/primary%3" +
                        "AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri)
        }
        startActivityForResult(intent, OPEN_STATUS_FOLDER_REQUEST_CODE)
    }

    fun openFolderForImages() {

        try {
            val uri =
                Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp Images")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            }
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            startActivityForResult(intent, OPEN_IMAGES_FOLDER_REQUEST_CODE)
        } catch (e: Exception) {
            print("Error ${e.message}")
        }
//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
//        }
//        val wa_status_uri =
//            Uri.parse(
//                "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Images"
//            )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri)
//        }
//        startActivityForResult(intent, OPEN_IMAGES_FOLDER_REQUEST_CODE)
    }

    fun openFolderForVideo() {


        try {
            val uri =
                Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp Video")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            }
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            startActivityForResult(intent, OPEN_VIDEO_FOLDER_REQUEST_CODE)
        } catch (e: Exception) {
            print("Error ${e.message}")
        }

//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
//        }
//        val wa_status_uri =
//            Uri.parse(
//                "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Video"
//            )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri)
//        }
//        startActivityForResult(intent, OPEN_VIDEO_FOLDER_REQUEST_CODE)
    }

    fun openFolderForAudio() {

        try {
            val uri =
                Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp Audio")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            }
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            startActivityForResult(intent, OPEN_AUDIO_FOLDER_REQUEST_CODE)
        } catch (e: Exception) {
            print("Error ${e.message}")
        }


//        val wa_status_uri =
//            Uri.parse(
//                "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Audio"
//            )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri)
//        }
//        startActivityForResult(intent, OPEN_AUDIO_FOLDER_REQUEST_CODE)
    }



    fun openFolderForVoiceNotes() {

        try {
            val uri =
                Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia/document/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp Voice Notes")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            }
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            startActivityForResult(intent, OPEN_VOICE_NOTE_FOLDER_REQUEST_CODE)
        } catch (e: Exception) {
            print("Error ${e.message}")
        }

//        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
//            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
//        }
//        val wa_status_uri =
//            Uri.parse(
//                "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2FWhatsApp%20Voice%20Notes"
//            )
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, wa_status_uri)
//        }
//        startActivityForResult(intent, OPEN_VOICE_NOTE_FOLDER_REQUEST_CODE)
    }

    fun checkPagesPermissions() {
        if (sharedPreferences?.imageFolder == null) {
            mViewPager!!.currentItem = 1
            return
        }
        if (sharedPreferences?.videoFolder == null) {
            mViewPager!!.currentItem = 2
            return
        }
        if (sharedPreferences?.audioFolder == null) {
            mViewPager!!.currentItem = 4
            return
        }
        if (sharedPreferences?.voiceNoteFolder == null) {
            mViewPager!!.currentItem = 3
            return
        }
        if (sharedPreferences?.statusFolder == null) {
            mViewPager!!.currentItem = 5
            return
        }

    }


}