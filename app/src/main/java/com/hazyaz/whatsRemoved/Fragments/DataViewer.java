package com.hazyaz.whatsRemoved.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.hazyaz.whatsRemoved.R;
import com.hazyaz.whatsRemoved.databinding.ActivityImageViewerBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DataViewer extends AppCompatActivity {

    private ActivityImageViewerBinding binding;

    Toolbar mToolbar;
    ImageView imageView;
    ImageButton mDownloadButton;
    ImageButton mShareButton;
    private InterstitialAd mInterstitialAd;

    File file;
    String imageUrl;
    ImageButton mPlayButton;
    private ProgressDialog mProgressBar;
    private final String TAG = "MainActivity__";


    private static void refreshGallery(String mCurrentPhotoPath, Context context) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityImageViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MobileAds.initialize(this, initializationStatus -> {
        });
        setAdds();
        mToolbar = binding.mainPageToolbar.mainAppBar;
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        imageUrl = intent.getStringExtra("IMAGEURL");

        mProgressBar = new ProgressDialog(DataViewer.this);
        mToolbar.setNavigationOnClickListener(v -> finish());
        imageView = binding.imageViewer;
        mDownloadButton = binding.imageDownload;
        mShareButton = binding.share;
        mPlayButton = binding.playButton;

        if (!imageUrl.endsWith(".mp4")) {
            mPlayButton.setVisibility(View.GONE);
            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);
        } else {
            mPlayButton.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(imageUrl).into(imageView);

            mPlayButton.setOnClickListener(view -> {
                Intent intent1 = new Intent(Intent.ACTION_VIEW);
                intent1.setDataAndType(Uri.parse(imageUrl), "video/mp4");
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent1);
            });
        }

        mDownloadButton.setOnClickListener(view -> {
            showAdd();
        });


        mShareButton.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_SHORT).show();
            try {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                Uri screenshotUri = Uri.parse(imageUrl);
                sharingIntent.setType("image/png");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                startActivity(Intent.createChooser(sharingIntent, "Share image using"));
            } catch (Exception e) {
            }
        });

    }

    private void setAdds() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-2675887677224394/6616163679", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


    }

    private void copyFile(String inputPath, String filename, String outputPath) {

        InputStream in;
        OutputStream out;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath + "/" + filename);
            out = new FileOutputStream(outputPath + "/" + filename);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();

        } catch (Exception e) {

        }

    }

    public void showAdd() {

        if (mInterstitialAd != null) {
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    mInterstitialAd = null;
                    downloadImg();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
            mInterstitialAd.show(DataViewer.this);
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.");
        }
    }

    private void downloadImg() {
        mProgressBar.setTitle("Downloading File");
        mProgressBar.setMessage("Your file in being downloaded...");
        mProgressBar.setCanceledOnTouchOutside(false);
        mProgressBar.show();

        file = new File(imageUrl);
        final String mDirpath = file.getParent();
        final String mFileName = file.getName();
        final File mOutFile = new File(Environment.getExternalStorageDirectory(), "/DCIM/WhatsRemoved Images/");
        new Thread(() -> copyFile(mDirpath, mFileName, mOutFile.toString())).start();

        Handler mHand = new Handler();
        mHand.postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.dismiss();
                refreshGallery(mOutFile + "/" + mFileName, getApplicationContext());
                Toast.makeText(getApplicationContext(), "File Downloaded", Toast.LENGTH_SHORT).show();
            }
        }, 3000);

    }

}