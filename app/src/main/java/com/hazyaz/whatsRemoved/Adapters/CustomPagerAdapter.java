package com.hazyaz.whatsRemoved.Adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.hazyaz.whatsRemoved.Fragments.AudioFragment;
import com.hazyaz.whatsRemoved.Fragments.ImageFragment;
import com.hazyaz.whatsRemoved.Fragments.StorySaverFragment;
import com.hazyaz.whatsRemoved.Fragments.UserNameFragment;
import com.hazyaz.whatsRemoved.Fragments.VideoFragment;
import com.hazyaz.whatsRemoved.Fragments.VoiceNotesFragment;


public class CustomPagerAdapter extends FragmentPagerAdapter {


    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public int getCount() {
        return 6;
    }


    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new UserNameFragment();
            case 1:
                return new ImageFragment();
            case 2:
                return new VideoFragment();
            case 3:
                return new VoiceNotesFragment();
            case 4:
                return new AudioFragment();
            case 5:
                return new StorySaverFragment();
            default:
                return null;
        }

    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0:
                return "Messages";
            case 1:
                return "Images";
            case 2:
                return "Videos";
            case 3:
                return "Voice Notes";
            case 4:
                return "Audio";
            case 5:
                return "Stories";
            default:
                return null;
        }

    }
}