package ca.nait.dmit2504.soundboardapp;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "HomeFragment";
    private ImageButton mA1ImageButton;
    private ImageButton mA2ImageButton;
    private ImageButton mA3ImageButton;
    private ImageButton mB1ImageButton;
    private ImageButton mB2ImageButton;
    private ImageButton mB3ImageButton;
    private ImageButton mC1ImageButton;
    private ImageButton mC2ImageButton;
    private ImageButton mC3ImageButton;
    private ImageButton mD1ImageButton;
    private ImageButton mD2ImageButton;
    private ImageButton mD3ImageButton;
    private MainActivityViewModel mMainActivityViewModel;
    private View mView;
    private Pad mPad;
    private List<Pad> mPadList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_home, container, false);

        // Views/Initialization
        mA1ImageButton = mView.findViewById(R.id.launchpad_A1_imageButton);
        mA2ImageButton = mView.findViewById(R.id.launchpad_A2_imageButton);
        mA3ImageButton = mView.findViewById(R.id.launchpad_A3_imageButton);
        mB1ImageButton = mView.findViewById(R.id.launchpad_B1_imageButton);
        mB2ImageButton = mView.findViewById(R.id.launchpad_B2_imageButton);
        mB3ImageButton = mView.findViewById(R.id.launchpad_B3_imageButton);
        mC1ImageButton = mView.findViewById(R.id.launchpad_C1_imageButton);
        mC2ImageButton = mView.findViewById(R.id.launchpad_C2_imageButton);
        mC3ImageButton = mView.findViewById(R.id.launchpad_C3_imageButton);
        mD1ImageButton = mView.findViewById(R.id.launchpad_D1_imageButton);
        mD2ImageButton = mView.findViewById(R.id.launchpad_D2_imageButton);
        mD3ImageButton = mView.findViewById(R.id.launchpad_D3_imageButton);
        mPad = new Pad();

        // Setup list of pad to have 12 empty pads
        if (mPadList == null) {
            mPadList = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Pad emptyPad = new Pad();
                emptyPad.setActive(false);
                mPadList.add(emptyPad);
            }
        }

        // Set onClickListeners using method reference operators (::)
        mA1ImageButton.setOnClickListener(this::onClick);
        mA2ImageButton.setOnClickListener(this::onClick);
        mA3ImageButton.setOnClickListener(this::onClick);
        mB1ImageButton.setOnClickListener(this::onClick);
        mB2ImageButton.setOnClickListener(this::onClick);
        mB3ImageButton.setOnClickListener(this::onClick);
        mC1ImageButton.setOnClickListener(this::onClick);
        mC2ImageButton.setOnClickListener(this::onClick);
        mC3ImageButton.setOnClickListener(this::onClick);
        mD1ImageButton.setOnClickListener(this::onClick);
        mD2ImageButton.setOnClickListener(this::onClick);
        mD3ImageButton.setOnClickListener(this::onClick);

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        // Load default audio clips to launchpad
        getDefaultAudio();

        // getViewLifecycleOwner instead of this so that the observer is not bound to the instance of the fragment, allowing it to be unbound when fragment view is destroyed
        mMainActivityViewModel.getPads().observe(getViewLifecycleOwner(), pads -> {
            mPadList = pads;
            for (Pad pad : mPadList) {
                // If pad has selected audio, setup MediaPlayer for pad
                if (pad.isActive()) {
                    MediaPlayer mediaPlayer = new MediaPlayer();

                    // If default audio, setup using AssetFileDescriptor, else setup with uri
                    if (pad.isDefaultAudio()) {
                        AssetFileDescriptor assetFileDescriptor;
                        assetFileDescriptor = pad.getDefaultAudioAssetFileDescriptor();
                        try {
                            mediaPlayer.setDataSource(
                                    assetFileDescriptor.getFileDescriptor(),
                                    assetFileDescriptor.getStartOffset(),
                                    assetFileDescriptor.getLength());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Uri uri;
                        uri = pad.getPadPlayerUri();
                        try {
                            mediaPlayer.setDataSource(getActivity(), uri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    mediaPlayer.prepareAsync();
                    pad.setPadPlayer(mediaPlayer);
                } else {
                    // else set MediaPlayer to null
                    pad.setPadPlayer(null);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        // Send List<Pad> to ModelView to maintain items
        mMainActivityViewModel.setPads(mPadList);

        // Release resources when switching fragments
        for (Pad pad : mPadList){
            if (pad.getPadPlayer() != null) {
                pad.getPadPlayer().release();
                pad.setPadPlayer(null);
            }
        }
    }

    public void getDefaultAudio() {
        try {
            List<String> defaultAudioPaths = new ArrayList<>();
            defaultAudioPaths = getAssetFiles("");

            // Set to < 4 because there are currently only 3 default audio files in assets
            if (defaultAudioPaths.size() > 0 && defaultAudioPaths.size() < 4){
                int index = 0;
                for (String defaultAudioPath : defaultAudioPaths){
                    // Only set default audio MediaPlayers to empty pads
                    if (!mPadList.get(index).isActive()) {
                        MediaPlayer mediaPlayer = new MediaPlayer();
                        // getActivity() gets Activity context for getAssets to work
                        AssetFileDescriptor assetFileDescriptor = getActivity().getAssets().openFd(defaultAudioPath);

                        mPad = new Pad();
                        mediaPlayer = new MediaPlayer();
                        mediaPlayer.setDataSource(
                                assetFileDescriptor.getFileDescriptor(),
                                assetFileDescriptor.getStartOffset(),
                                assetFileDescriptor.getLength());
                        mediaPlayer.prepareAsync();
                        mPad.setPadPlayer(mediaPlayer);
                        mPad.setActive(true);
                        mPad.setDefaultAudio(true);
                        mPad.setDefaultAudioAssetFileDescriptor(assetFileDescriptor);

                        mPadList.set(index, mPad);
                    }
                    index++;
                }
            } else {
                Log.d(TAG, getString(R.string.no_default_audio_found));
            }
        } catch (IOException ioe) {
            Log.d(TAG, getString(R.string.ioexception), ioe);
        }
    }

    private List<String> getAssetFiles(String path) {
        List<String> fileNameList = new ArrayList<>();
        String[] files;
        try {
            files = getActivity().getAssets().list(path);
            if (files.length > 0) {
                for (String file : files) {
                    // Quick check for some valid file extensions
                    if (file.contains(".mp3") || file.contains(".wav") || file.contains(".3gp")){
                        fileNameList.add(file);
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return fileNameList;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.launchpad_A1_imageButton:
                // Start/Stop MediaPlayer
                startStopMediaPlayer(mPadList.get(0).getPadPlayer(), mA1ImageButton);
//                mPadList.get(0).getPadPlayer().setOnCompletionListener(mp -> {
//                    mA1ImageButton.setImageDrawable(getResources().getDrawable(R.drawable.launchpad_button_black_a1));
//                    mA1ImageButton.setBackgroundColor(getResources().getColor(R.color.background_black));
//                });
                break;
            case R.id.launchpad_A2_imageButton:
                startStopMediaPlayer(mPadList.get(1).getPadPlayer(), mA2ImageButton);
                break;
            case R.id.launchpad_A3_imageButton:
                startStopMediaPlayer(mPadList.get(2).getPadPlayer(), mA3ImageButton);
                break;
            case R.id.launchpad_B1_imageButton:
                startStopMediaPlayer(mPadList.get(3).getPadPlayer(), mB1ImageButton);
                break;
            case R.id.launchpad_B2_imageButton:
                startStopMediaPlayer(mPadList.get(4).getPadPlayer(), mB2ImageButton);
                break;
            case R.id.launchpad_B3_imageButton:
                startStopMediaPlayer(mPadList.get(5).getPadPlayer(), mB3ImageButton);
                break;
            case R.id.launchpad_C1_imageButton:
                startStopMediaPlayer(mPadList.get(6).getPadPlayer(), mC1ImageButton);
                break;
            case R.id.launchpad_C2_imageButton:
                startStopMediaPlayer(mPadList.get(7).getPadPlayer(), mC2ImageButton);
                break;
            case R.id.launchpad_C3_imageButton:
                startStopMediaPlayer(mPadList.get(8).getPadPlayer(), mC3ImageButton);
                break;
            case R.id.launchpad_D1_imageButton:
                startStopMediaPlayer(mPadList.get(9).getPadPlayer(), mD1ImageButton);
                break;
            case R.id.launchpad_D2_imageButton:
                startStopMediaPlayer(mPadList.get(10).getPadPlayer(), mD2ImageButton);
                break;
            case R.id.launchpad_D3_imageButton:
                startStopMediaPlayer(mPadList.get(11).getPadPlayer(), mD3ImageButton);
                break;
            default:
                break;
        }
    }

    private void startStopMediaPlayer(MediaPlayer padPlayer, ImageButton imageButton) {
        // Check if pad has MediaPlayer (Prevents MediaPlayer.isPlaying() on null object)
        if (padPlayer != null) {
            if (padPlayer.isPlaying()) {
                padPlayer.stop();
                padPlayer.prepareAsync();
            } else {
                padPlayer.start();
//                // Change button image and background
//                imageButton.setImageDrawable(getResources().getDrawable(R.drawable.launchpad_button_black_green_clicked));
//                imageButton.setBackgroundColor(getResources().getColor(R.color.background_black));
            }
        }
    }
}