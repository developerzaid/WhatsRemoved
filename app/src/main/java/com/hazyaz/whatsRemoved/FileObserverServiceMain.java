package com.hazyaz.whatsRemoved;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

public class FileObserverServiceMain extends Service {


    FileObserver imageObserver = null;
    FileObserver videoObserver = null;
    FileObserver audioObserver = null;
    FileObserver voiceNotesObserver = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("obService", "started");
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("obService", "onCreate");

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ObserverWhatsappImages11();
                ObserverWhatsappVideos11();
                ObserverWhatsappAudio11();
                ObserverWhatsappVoiceNotes11();
                Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
            } else {
                ObserverWhatsappImages();
                ObserverWhatsappVideos();
                ObserverWhatsappAudio();
                ObserverWhatsappVoiceNotes();
                Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.d("Android11Service__ERROR", e.getMessage());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // Android 11 Observer

    private void ObserverWhatsappImages11() {
        Log.d("Android11Service__", "Service Started for Images");

        String path = Environment.getExternalStorageDirectory()
                .toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Images";

        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        imageObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android11Service", "EVENTS - " + event);
                Log.d("Android11Service", "File Name - " + file);
                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder11("WhatsRemoved Image");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android11Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/Deleted Images/nomedia/"
                        );
                        createFolderDelete11("Deleted Images");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        imageObserver.startWatching();
    }

    private void ObserverWhatsappVideos11() {
        Log.d("Android11Service__", "Service Started for Videos");
        String path =
                Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Video";
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        videoObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android11Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder11("WhatsRemoved Video");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android11Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/Deleted Video/nomedia/"
                        );
                        createFolderDelete11("Deleted Video");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        videoObserver.startWatching();
    }

    private void ObserverWhatsappAudio11() {
        Log.d("Android11Service__", "Service Started for Audio");
        String path =
                Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Audio";
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        audioObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android11Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder11("WhatsRemoved Audio");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android11Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/Download/WhatsRemoved/recent/Deleted Audio/nomedia/"
                        );
                        createFolderDelete11("Deleted Audio");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        audioObserver.startWatching();
    }

    private void ObserverWhatsappVoiceNotes11() {
        Log.d("Android11Service__", "Service Started for Voice Notes");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR);

        String suffix = String.valueOf(year) + week;

        String path =
                Environment.getExternalStorageDirectory().toString() + "/Android/media/com.whatsapp/WhatsApp/Media/WhatsApp Voice Notes/" + suffix;
        Log.d("Android11Service__S", "Suffix - " + path);
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android11Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        voiceNotesObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android11Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android11Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder11("WhatsRemoved Voice Notes");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android11Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/Deleted Voice Notes/nomedia/"
                        );
                        createFolderDelete11("Deleted Voice Notes");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        voiceNotesObserver.startWatching();
    }

    private void createFolder11(String name) {
        File folder = commonDocumentDirPath("WhatsRemoved/recent/" + name + "/nomedia");
        if (folder.exists()) {
            Log.d("Android11Service", "No Media folder exist " + folder.getAbsolutePath());
        } else {
            Log.d("Android11Service", " No Media Not Exist");
            if (folder.mkdirs())
                Log.d("Android11Service", "No Media Created");
        }
    }

    private void createFolderDelete11(String name) {
        File folder = commonDocumentDirPath("WhatsRemoved/recent/" + name + "/nomedia");
        if (folder.exists()) {
            Log.d("Android11Service", "Deleted Folder Exist - " + folder.getAbsolutePath());
        } else {
            Log.d("Android11Service", "Delete Not Exist");
            if (folder.mkdirs())
                Log.d("Android11Service", "Delete Created");
        }
    }

    private File commonDocumentDirPath(String folderName) {
        File dir = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("Android11Service", "Android 11");

            dir = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .toString() + "/" + folderName
            );

        } else {
            Log.d("Android11Service", "Android 10");
            dir = new
                    File(Environment.getExternalStorageDirectory().toString() + "/" + folderName);
        }
        return dir;
    }

    public void moveFiles(String inputPath, String inputFile, String outputPath) {

        InputStream in;
        OutputStream out;
        try {
            File dir = new File(outputPath);
            File dirs = new File(inputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + "/" + inputFile);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file
            out.flush();
            out.close();

            // delete the original file
            File lastFile = new File(inputPath + "/" + inputFile);
            Log.d("Android11Service", "Last File Path - " + lastFile.getAbsolutePath());

        } catch (Exception e) {
            Log.e("Android11Service", "Error - " + e.getMessage());
        }


    }

    public void moveFilesWithDelete(String inputPath, String inputFile, String outputPath) {

        InputStream in;
        OutputStream out;
        try {
            File dir = new File(outputPath);
            File dirs = new File(inputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            in = new FileInputStream(inputPath + "/" + inputFile);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();

            // write the output file
            out.flush();
            out.close();

            // delete the original file
            File lastFile = new File(inputPath + "/" + inputFile);
            Log.d("Android11Service", "Last File Path - " + lastFile.getAbsolutePath());
            if (lastFile.exists()) {
                Log.d("Android11Service", "Last File Exist");
            } else {
                Log.d("Android11Service", "Last File Not Exist");
            }
            lastFile.delete();
        } catch (Exception e) {
            Log.e("Android11Service", "Error - " + e.getMessage());
        }


    }

    // Android 10 Observer

    private void ObserverWhatsappImages() {
        Log.d("Android10Service", "Sevrice Started");

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/WhatsApp Images";

        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        imageObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android10Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder("WhatsRemoved Image");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android10Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Image/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/Deleted Images/nomedia/"
                        );
                        createFolderDelete("Deleted Images");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        imageObserver.startWatching();
    }

    private void ObserverWhatsappVideos() {
        Log.d("Android10Service", "Sevrice Started");
        String path =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/WhatsApp Video";
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        videoObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android10Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder("WhatsRemoved Video");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android10Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Video/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/Deleted Video/nomedia/"
                        );
                        createFolderDelete("Deleted Video");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        videoObserver.startWatching();
    }

    private void ObserverWhatsappAudio() {
        Log.d("Android10Service", "Sevrice Started");
        String path =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/WhatsApp Audio";
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        audioObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android10Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder("WhatsRemoved Audio");
                        if (file != null) {
                            moveFiles(whatsappImages.toString(), file, output.toString());
                        }
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android10Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Audio/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/Deleted Audio/nomedia/"
                        );
                        createFolderDelete("Deleted Audio");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        audioObserver.startWatching();
    }

    private void ObserverWhatsappVoiceNotes() {
        Log.d("Android10Service", "Sevrice Started");
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int week = Calendar.getInstance().get(Calendar.WEEK_OF_YEAR) + 1;
        String suffix = String.valueOf(year) + week;
        String path =
                Environment.getExternalStorageDirectory().getAbsolutePath() + "/WhatsApp/Media/WhatsApp Voice Notes/" + suffix;

        Log.d("Android10Service", suffix + "Service - " + path);

        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            Log.d("Android10Service", "Whatsapp Path Exist - " + dir.getAbsolutePath());
        }

        voiceNotesObserver = new FileObserver(path) {

            @Override
            public void onEvent(int event, final String file) {
                Log.d("Android10Service", "EVENTS - " + event);

                if (event == MOVED_TO || event == CREATE) {
                    Log.d("Android10Service", "File - " + file);
                    new Thread(() -> {
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        );
                        File whatsappImages = new File(path);
                        createFolder("WhatsRemoved Voice Notes");
                        if (file != null)
                            moveFiles(whatsappImages.toString(), file, output.toString());
                    }).start();
                }


                if (event == DELETE) {
                    Log.d("Android10Service", event + "---" + file);
                    new Thread(() -> {
                        File input = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/WhatsRemoved Voice Notes/nomedia/"
                        );
                        File output = new File(
                                Environment.getExternalStorageDirectory().getAbsolutePath(),
                                "/WhatsRemoved/recent/Deleted Voice Notes/nomedia/"
                        );
                        createFolderDelete("Deleted Voice Notes");
                        if (file != null) {
                            moveFilesWithDelete(input.toString(), file, output.toString());
                        }
                    }).start();
                }

            }


        };
        voiceNotesObserver.startWatching();
    }

    private void createFolder(String name) {
        File folder = commonDocumentDirPath("WhatsRemoved/recent/" + name + "/nomedia");
        if (folder.exists()) {
            Log.d("Android10Service", "NoMedia folder exist " + folder.getAbsolutePath());
        } else {
            Log.d("Android10Service", " No Media Not Exist");
            if (folder.mkdirs())
                Log.d("Android10Service", "No Media Created");
        }
    }

    private void createFolderDelete(String name) {
        File folder = commonDocumentDirPath("WhatsRemoved/recent/" + name + "/nomedia");
        if (folder.exists()) {
            Log.d("Android10Service", "Deleted Folder Exist - " + folder.getAbsolutePath());
        } else {
            Log.d("Android10Service", "Delete Not Exist");
            if (folder.mkdirs())
                Log.d("Android10Service", "Delete Created");
        }
    }
}
