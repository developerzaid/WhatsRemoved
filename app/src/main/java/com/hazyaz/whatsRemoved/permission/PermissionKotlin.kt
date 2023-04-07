package com.hazyaz.whatsRemoved.permission

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.documentfile.provider.DocumentFile
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetView
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.R
import com.hazyaz.whatsRemoved.Utils.SharePreferencesManager
import com.hazyaz.whatsRemoved.databinding.ActivityPermissonBinding


class PermissionKotlin : AppCompatActivity() {


    private var binding: ActivityPermissonBinding? = null

    var mGrantNotificationAccess: RelativeLayout? = null
    var mGrantStorageAccess: RelativeLayout? = null
    var mGrantAutoStartAccess: RelativeLayout? = null
    var mGrantAutoDownloadAccess: RelativeLayout? = null


    var mToolbar: Toolbar? = null
    private var sharedPreferences: SharePreferencesManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissonBinding.inflate(
            layoutInflater
        )
        setContentView(binding!!.root)
        mToolbar = binding!!.mainPageToolbar.mainAppBar
        setSupportActionBar(mToolbar)
        supportActionBar!!.title = "WHATS REMOVED"
        sharedPreferences = SharePreferencesManager(applicationContext)

        mGrantNotificationAccess = binding!!.layoutOne
        mGrantStorageAccess = binding!!.layoutTwo
        mGrantAutoStartAccess = binding!!.layoutThree
        mGrantAutoDownloadAccess = binding!!.layoutFour

        mGrantNotificationAccess!!.visibility = View.VISIBLE
        mGrantStorageAccess!!.visibility = View.GONE
        mGrantAutoStartAccess!!.visibility = View.GONE
        mGrantAutoDownloadAccess!!.visibility = View.GONE


        initClicks()
//        TapTargetView.showFor(this,
//            TapTarget.forView(
//                binding!!.grantButton,
//                "Step 1",
//                "In Order to Work Properly Please Grant Notification Access to WhatsRemoved"
//            )
//                .outerCircleColor(R.color.outerCircle) // Specify a color for the outer circle
//                .outerCircleAlpha(0.56f) // Specify the alpha amount for the outer circle
//                .targetCircleColor(R.color.colorPrimaryDark) // Specify a color for the target circle
//                .titleTextSize(25) // Specify the size (in sp) of the title text
//                .titleTextColor(R.color.black) // Specify the color of the title text
//                .descriptionTextSize(17) // Specify the size (in sp) of the description text
//                .descriptionTextColor(R.color.black) // Specify the color of the description text
//                .textColor(R.color.black) // Specify a color for both the title and description text
//                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
//                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
//                .drawShadow(true) // Whether to draw a drop shadow or not
//                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
//                .tintTarget(true) // Whether to tint the target view's color
//                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath)
//                .targetRadius(100),
//            object : TapTargetView.Listener() {
//                override fun onTargetClick(view: TapTargetView) {
//                    super.onTargetClick(view)
//                    notificationDialog()
//                    val handler = Handler()
//                    handler.postDelayed({ //                                mAcessText.setText(R.string.perm_stor);
//                        mGrantNotificationAccess!!.visibility = View.GONE
//
//                    }, 700)
//
//                    mGrantStorageAccess!!.visibility = View.VISIBLE
//                    mGrantAutoStartAccess!!.visibility = View.GONE
//                    mGrantAutoDownloadAccess!!.visibility = View.GONE
//                    permissionStorage()
//                }
//            }
//        )


    }

    private fun initClicks() {
        binding!!.grantButton.setOnClickListener {
            notificationDialog()
            val handler = Handler()
            handler.postDelayed({ //                                mAcessText.setText(R.string.perm_stor);
                mGrantNotificationAccess!!.visibility = View.GONE

            }, 2000)

            mGrantStorageAccess!!.visibility = View.VISIBLE
            mGrantAutoStartAccess!!.visibility = View.GONE
            mGrantAutoDownloadAccess!!.visibility = View.GONE
        }
        binding!!.grantButton2.setOnClickListener {
            if (isStoragePermissionGranted) {
                startNext()
            }
        }
        binding!!.grantButton3.setOnClickListener {
            fGrantMiAccess()
            val handler = Handler()
            handler.postDelayed({
                mGrantAutoStartAccess!!.visibility = View.GONE
                mGrantAutoDownloadAccess!!.visibility = View.VISIBLE
                mGrantNotificationAccess!!.visibility = View.GONE
                mGrantStorageAccess!!.visibility = View.GONE
            }, 500)
//            permissionAutoDownload("4")
        }
        binding!!.grantButton4.setOnClickListener {
            val intent = Intent(this@PermissionKotlin, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

    }

//    private fun permissionStorage() {
////        TapTargetView.showFor(this,
////            TapTarget.forView(
////                mGrantStorageAccess,
////                "Step 2",
////                "In Order to Work Properly Please Grant Storage Access to WhatsRemoved"
////            )
////                .outerCircleColor(R.color.outerCircle) // Specify a color for the outer circle
////                .outerCircleAlpha(0.56f) // Specify the alpha amount for the outer circle
////                .targetCircleColor(R.color.colorPrimaryDark) // Specify a color for the target circle
////                .titleTextSize(25) // Specify the size (in sp) of the title text
////                .titleTextColor(R.color.black) // Specify the color of the title text
////                .descriptionTextSize(17) // Specify the size (in sp) of the description text
////                .descriptionTextColor(R.color.black) // Specify the color of the description text
////                .textColor(R.color.black) // Specify a color for both the title and description text
////                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
////                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
////                .drawShadow(true) // Whether to draw a drop shadow or not
////                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
////                .tintTarget(true) // Whether to tint the target view's color
////                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath)
////                .targetRadius(100),
////            object : TapTargetView.Listener() {
////                override fun onTargetClick(view: TapTargetView) {
////                    super.onTargetClick(view)
////                    if (isStoragePermissionGranted) {
////                        startNext()
////                    }
////                }
////            }
////        )
//    }
//
//    // Closed
//
//    private fun permissionAutoStart(stepString: String) {
////        TapTargetView.showFor(this,
////            TapTarget.forView(
////                mGrantAutoStartAccess,
////                "Step $stepString",
////                "Enable Auto Start from setting for app work properly"
////            )
////                .outerCircleColor(R.color.outerCircle) // Specify a color for the outer circle
////                .outerCircleAlpha(0.56f) // Specify the alpha amount for the outer circle
////                .targetCircleColor(R.color.colorPrimaryDark) // Specify a color for the target circle
////                .titleTextSize(25) // Specify the size (in sp) of the title text
////                .titleTextColor(R.color.black) // Specify the color of the title text
////                .descriptionTextSize(17) // Specify the size (in sp) of the description text
////                .descriptionTextColor(R.color.black) // Specify the color of the description text
////                .textColor(R.color.black) // Specify a color for both the title and description text
////                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
////                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
////                .drawShadow(true) // Whether to draw a drop shadow or not
////                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
////                .tintTarget(true) // Whether to tint the target view's color
////                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath)
////                .targetRadius(80),
////            object : TapTargetView.Listener() {
////                override fun onTargetClick(view: TapTargetView) {
////                    super.onTargetClick(view)
////                    fGrantMiAccess()
////                    val handler = Handler()
////                    handler.postDelayed({
////                        mGrantAutoStartAccess!!.visibility = View.GONE
////                        mGrantAutoDownloadAccess!!.visibility = View.VISIBLE
////                        mGrantNotificationAccess!!.visibility = View.GONE
////                        mGrantStorageAccess!!.visibility = View.GONE
////                    }, 500)
////                    permissionAutoDownload("4")
////                }
////            }
////        )
//    }
//
//    private fun permissionAutoDownload(fourth: String) {
//        TapTargetView.showFor(this,
//            TapTarget.forView(
//                mGrantAutoDownloadAccess,
//                "Step $fourth",
//                "Enable Auto media download from whatsapp for app to work properly"
//            )
//                .outerCircleColor(R.color.outerCircle) // Specify a color for the outer circle
//                .outerCircleAlpha(0.56f) // Specify the alpha amount for the outer circle
//                .targetCircleColor(R.color.colorPrimaryDark) // Specify a color for the target circle
//                .titleTextSize(25) // Specify the size (in sp) of the title text
//                .titleTextColor(R.color.black) // Specify the color of the title text
//                .descriptionTextSize(17) // Specify the size (in sp) of the description text
//                .descriptionTextColor(R.color.black) // Specify the color of the description text
//                .textColor(R.color.black) // Specify a color for both the title and description text
//                .textTypeface(Typeface.SANS_SERIF) // Specify a typeface for the text
//                .dimColor(R.color.black) // If set, will dim behind the view with 30% opacity of the given color
//                .drawShadow(true) // Whether to draw a drop shadow or not
//                .cancelable(false) // Whether tapping outside the outer circle dismisses the view
//                .tintTarget(true) // Whether to tint the target view's color
//                .transparentTarget(true) // Specify whether the target is transparent (displays the content underneath)
//                .targetRadius(60),
//            object : TapTargetView.Listener() {
//                private
//                val MY_IGNORE_OPTIMIZATION_REQUEST = 0
//                override fun onTargetClick(view: TapTargetView) {
//                    super.onTargetClick(view)
//
//                    val intent = Intent(this@PermissionKotlin, MainActivity::class.java)
//                    intent.flags =
//                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
//                    startActivity(intent)
//                }
//            }
//        )
//    }//permission is automatically granted on sdk<23 upon installation//

    // For Android 11
    val isStoragePermissionGranted: Boolean
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this@PermissionKotlin,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    PERMISSION_FOR_ANDROID_BELOW_11
                )
                startNext()
                false
            }
        } else if (Build.VERSION.SDK_INT >= 23) {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                ActivityCompat.requestPermissions(
                    this@PermissionKotlin,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    PERMISSION_FOR_ANDROID_BELOW_11
                )
                startNext()
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            true
        }


    private fun notificationDialog() {
        val isNotificationServiceRunning = isNotificationServiceRunning
        if (!isNotificationServiceRunning) {
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
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

    fun fGrantMiAccess() {
        val manufacturer = Build.MANUFACTURER
        Log.d("sdfdfdf", "" + manufacturer)
        try {
            val intent = Intent()
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            } else if ("asus".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.asus.mobilemanager",
                    "com.asus.mobilemanager.powersaver.PowerSaverSettings"
                )
            } else if ("huawei".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            } else if ("nokia".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.evenwell.powersaving.g3",
                    "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity"
                )
            } else if ("samsung".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.samsung.android.lool",
                    "com.samsung.android.sm.ui.battery.BatteryActivity"
                )
            } else if ("oneplus".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.oneplus.security",
                    "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity"
                )
            }
            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Auto Start cannot open, Kindly grant permission manually ",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val PERMISSION_FOR_ANDROID_BELOW_11 = 10
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("TAG", "User denied READ_PERMISSION permission.$requestCode")

        when (requestCode) {
            PERMISSION_FOR_ANDROID_BELOW_11 -> {
                if (grantResults.isEmpty() ||
                    grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i("TAG", "User denied READ_PERMISSION permission.")
                } else {
                    startNext()
                    Log.i("TAG", "User granted WRITE_PERMISSION permission.")
                }
            }

        }
    }

    private fun startNext() {
        val handler = Handler()
        handler.postDelayed({
            mGrantAutoStartAccess!!.visibility = View.VISIBLE
            mGrantAutoDownloadAccess!!.visibility = View.GONE
            mGrantNotificationAccess!!.visibility = View.GONE
            mGrantStorageAccess!!.visibility = View.GONE
        }, 1000)
//        permissionAutoStart("3")
    }
}