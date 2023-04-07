package com.hazyaz.whatsRemoved.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.hazyaz.whatsRemoved.MainActivity
import com.hazyaz.whatsRemoved.R
import com.hazyaz.whatsRemoved.service.CounterService
import java.io.*
import java.util.*

class ActiveService : Service() {
    var imageObserver: FileObserver? = null
    var videoObserver: FileObserver? = null
    var audioObserver: FileObserver? = null
    var voiceNotesObserver: FileObserver? = null

    private var handler: Handler? = null
    private lateinit var timeoutRunnable: Runnable


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("obService", "started")
        // This code is for to make a channel and notification for the Foreground Service
        notificationChannel()
        val intent1 = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0)

        val notification = NotificationCompat.Builder(this, "1")
            .setOngoing(true)
            .setAutoCancel(false)
            .setContentText("WhatsRemoved will save deleted whatsapp message")
            .setSmallIcon(R.drawable.start)
            .setContentIntent(pendingIntent).build()

        startForeground(1, notification)


//        startCounter()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ObserverWhatsappImages11()
            ObserverWhatsappVideos11()
            ObserverWhatsappAudio11()
            ObserverWhatsappVoiceNotes11()
        } else {
            ObserverWhatsappImages()
            ObserverWhatsappVideos()
            ObserverWhatsappAudio()
            ObserverWhatsappVoiceNotes()
        }
        onTaskRemoved(intent)
        return START_NOT_STICKY
    }

    private fun startCounter() {
        Log.d("Android11_counter", "Starting the counter...")
        val looper: Looper? = Looper.myLooper()
        looper.let {
            handler = Handler(looper!!)
            timeoutRunnable = Runnable {


                CounterService.counter.value = CounterService.counter.value!! + 1

                Toast.makeText(
                    applicationContext,
                    "Counter ${CounterService.counter.value}",
                    Toast.LENGTH_SHORT
                ).show()


                handler?.postDelayed(timeoutRunnable, 3000)
            }
            handler?.post(timeoutRunnable)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        startService(Intent(applicationContext, NotificationListener::class.java))
        throw java.lang.UnsupportedOperationException("Not yet implemented")
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        super.onTaskRemoved(rootIntent)
    }


    override fun onDestroy() {
        stopForeground(false)
        val broadcastIntent = Intent(this, BroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        Toast.makeText(this, "service destoyed", Toast.LENGTH_LONG).show()
        super.onDestroy()
    }

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("1", "foreground", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }

    // Android 11 Observer
    private fun ObserverWhatsappImages11() {
        Log.d("Android11Service__", "Service Started for Images")
        val path = Environment.getExternalStorageDirectory()
            .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images"
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android11Service__", "Whatsapp Path Exist - " + dir.absolutePath)
        } else {
            Log.d("Android11Service__", "Whatsapp Path Not Exist - " + dir.absolutePath)
        }
        imageObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android11Service__", "EVENTS - $event")
                Log.d("Android11Service__", "File Name - $file")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service__", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder11("WhatsRemoved Image")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android11Service__", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/Deleted Images/nomedia/"
                        )
                        createFolderDelete11("Deleted Images")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        imageObserver!!.startWatching()
    }

    private fun ObserverWhatsappVideos11() {
        Log.d("Android11Service__", "Service Started for Videos")
        val path = Environment.getExternalStorageDirectory()
            .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video"
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        videoObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android11Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder11("WhatsRemoved Video")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android11Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/Deleted Video/nomedia/"
                        )
                        createFolderDelete11("Deleted Video")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        videoObserver!!.startWatching()
    }

    private fun ObserverWhatsappAudio11() {
        Log.d("Android11Service__", "Service Started for Audio")
        val path = Environment.getExternalStorageDirectory()
            .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Audio"
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        audioObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android11Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder11("WhatsRemoved Audio")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android11Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/Deleted Audio/nomedia/"
                        )
                        createFolderDelete11("Deleted Audio")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        audioObserver!!.startWatching()
    }

    private fun ObserverWhatsappVoiceNotes11() {
        Log.d("Android11Service__", "Service Started for Voice Notes")

        val date = Date()
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val week = getWeek(calendar.get(Calendar.WEEK_OF_YEAR))
        val suffix = year.toString() + week.toString()

        val path = Environment.getExternalStorageDirectory()
            .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes/" + suffix
        Log.d("Android11Service__S", "Suffix - $path")
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        voiceNotesObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android11Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder11("WhatsRemoved Voice Notes")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android11Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/Download/WhatsRemoved/recent/Deleted Voice Notes/nomedia/"
                        )
                        createFolderDelete11("Deleted Voice Notes")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        voiceNotesObserver!!.startWatching()
    }

    private fun createFolder11(name: String) {
        val folder = commonDocumentDirPath("WhatsRemoved/recent/$name/nomedia")
        if (folder.exists()) {
            Log.d("Android11Service__", "No Media folder exist " + folder.absolutePath)
        } else {
            Log.d("Android11Service__", " No Media Not Exist")
            if (folder.mkdirs()) {
                Log.d("Android11Service__", "No Media Created")
            }
        }
    }

    private fun createFolderDelete11(name: String) {
        val folder = commonDocumentDirPath("WhatsRemoved/recent/$name/nomedia")
        if (folder.exists()) {
            Log.d("Android11Service__", "Deleted Folder Exist - " + folder.absolutePath)
        } else {
            Log.d("Android11Service__", "Delete Not Exist")
            if (folder.mkdirs()) {
                Log.d("Android11Service__", "Delete Created")
            }
        }
    }

    private fun commonDocumentDirPath(folderName: String): File {
        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("Android11Service__", "Android 11")
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .toString() + "/" + folderName
            )
        } else {
            Log.d("Android11Service__", "Android 10")
            File(Environment.getExternalStorageDirectory().toString() + "/" + folderName)
        }
        return dir
    }

    fun moveFiles(inputPath: String, inputFile: String, outputPath: String) {
        val `in`: InputStream
        val out: OutputStream
        try {
            val dir = File(outputPath)
            val dirs = File(inputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream("$inputPath/$inputFile")
            out = FileOutputStream("$outputPath/$inputFile")
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()

            // write the output file
            out.flush()
            out.close()

            // delete the original file
            val lastFile = File("$inputPath/$inputFile")
            Log.d("Android11Service__", "Last File Path - " + lastFile.absolutePath)
        } catch (e: java.lang.Exception) {
            Log.e("Android11Service__", "Error - " + e.message)
        }
    }

    fun moveFilesWithDelete(inputPath: String, inputFile: String, outputPath: String) {
        val `in`: InputStream
        val out: OutputStream
        try {
            val dir = File(outputPath)
            val dirs = File(inputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            `in` = FileInputStream("$inputPath/$inputFile")
            out = FileOutputStream("$outputPath/$inputFile")
            val buffer = ByteArray(1024)
            var read: Int
            while (`in`.read(buffer).also { read = it } != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()

            // write the output file
            out.flush()
            out.close()

            // delete the original file
            val lastFile = File("$inputPath/$inputFile")
            Log.d("Android11Service", "Last File Path - " + lastFile.absolutePath)
            if (lastFile.exists()) {
                Log.d("Android11Service", "Last File Exist")
            } else {
                Log.d("Android11Service", "Last File Not Exist")
            }
            lastFile.delete()
        } catch (e: java.lang.Exception) {
            Log.e("Android11Service", "Error - " + e.message)
        }
    }

    // Android 10 Observer

    // Android 10 Observer
    private fun ObserverWhatsappImages() {
        Log.d("Android10Service", "Service Started")
        val path =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/WhatsApp Images"
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        imageObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android10Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder("WhatsRemoved Image")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android10Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/Deleted Images/nomedia/"
                        )
                        createFolderDelete("Deleted Images")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        imageObserver!!.startWatching()
    }

    private fun ObserverWhatsappVideos() {
        Log.d("Android10Service", "Service Started")
        val path =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/WhatsApp Video"
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        videoObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android10Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder("WhatsRemoved Video")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android10Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/Deleted Video/nomedia/"
                        )
                        createFolderDelete("Deleted Video")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        videoObserver!!.startWatching()
    }

    private fun ObserverWhatsappAudio() {
        Log.d("Android10Service", "Service Started")
        val path =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/WhatsApp Audio"
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        audioObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android10Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder("WhatsRemoved Audio")
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString())
                        }
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android10Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/Deleted Audio/nomedia/"
                        )
                        createFolderDelete("Deleted Audio")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        audioObserver!!.startWatching()
    }

    private fun ObserverWhatsappVoiceNotes() {
        Log.d("Android10Service", "Voice Note Service Started")

        val date = Date()
        val calendar: Calendar = Calendar.getInstance()
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val week = getWeek(calendar.get(Calendar.WEEK_OF_YEAR))
        val suffix = year.toString() + week.toString()
        val path =
            Environment.getExternalStorageDirectory().absolutePath + "/WhatsApp/Media/WhatsApp Voice Notes/" + suffix
        Log.d("Android10Service", suffix + "Service - " + path)
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.absolutePath)
        }
        voiceNotesObserver = object : FileObserver(path) {
            override fun onEvent(event: Int, file: String?) {
                Log.d("Android10Service", "EVENTS - $event")
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - $file")
                    Thread {
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        )
                        val whatsappImages = File(path)
                        createFolder("WhatsRemoved Voice Notes")
                        if (file != null) moveFiles(
                            whatsappImages.toString(),
                            file,
                            output.toString()
                        )
                    }.start()
                }
                if (event == DELETE) {
                    Log.d("Android10Service", "$event---$file")
                    Thread {
                        val input = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        )
                        val output = File(
                            Environment.getExternalStorageDirectory().absolutePath,
                            "/WhatsRemoved/recent/Deleted Voice Notes/nomedia/"
                        )
                        createFolderDelete("Deleted Voice Notes")
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString())
                        }
                    }.start()
                }
            }
        }
        voiceNotesObserver!!.startWatching()
    }

    private fun getWeek(get: Int): Any {
        when (get) {
            1 -> {
                return "01"
            }
            2 -> {
                return "02"
            }
            3 -> {
                return "03"
            }
            4 -> {
                return "04"
            }
            5 -> {
                return "05"
            }
            6 -> {
                return "06"
            }
            7 -> {
                return "07"
            }
            8 -> {
                return "08"
            }
            9 -> {
                return "09"
            }
            10 -> {
                return "10"
            }
            11 -> {
                return "11"
            }
            12 -> {
                return "12"
            }
            else -> {
                return "01"
            }
        }
    }

    private fun createFolder(name: String) {
        val folder = commonDocumentDirPath("WhatsRemoved/recent/$name/nomedia")
        if (folder.exists()) {
            Log.d("Android10Service", "NoMedia folder exist " + folder.absolutePath)
        } else {
            Log.d("Android10Service", " No Media Not Exist")
            if (folder.mkdirs()) Log.d("Android10Service", "No Media Created")
        }
    }

    private fun createFolderDelete(name: String) {
        val folder = commonDocumentDirPath("WhatsRemoved/recent/$name/nomedia")
        if (folder.exists()) {
            Log.d("Android10Service", "Deleted Folder Exist - " + folder.absolutePath)
        } else {
            Log.d("Android10Service", "Delete Not Exist")
            if (folder.mkdirs()) Log.d("Android10Service", "Delete Created")
        }
    }
}