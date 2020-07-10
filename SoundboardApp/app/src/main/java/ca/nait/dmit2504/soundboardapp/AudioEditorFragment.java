package ca.nait.dmit2504.soundboardapp;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AudioEditorFragment extends Fragment {

    private MainActivityViewModel mMainActivityViewModel;
    private Spinner mAudioSpinner;
    private SeekBar mProgressSeekBar;
    private SeekBar mSpeedSeekBar;
    private SeekBar mPitchSeekBar;
    private TextView mAudioNameTextView;
    private TextView mElapsedTimeTextView;
    private TextView mRemainingTimeTextView;
    private Button mPlayPauseButton;
    private Button mSaveButton;
    private List<Audio> mAudioList;
    private View mView;
    private MediaPlayer mMediaPlayer;
    private int mAudioListPosition;
    private int mTotalPlaytime;
    private float mSpeedMod;
    private float mPitchMod;

    public static AudioEditorFragment newInstance() {
        return new AudioEditorFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_audio_editor, container, false);

        mAudioSpinner = mView.findViewById(R.id.editor_audio_spinner);
        mProgressSeekBar = mView.findViewById(R.id.editor_progress_seekbar);
        mSpeedSeekBar = mView.findViewById(R.id.editor_speed_seekbar);
        mPitchSeekBar = mView.findViewById(R.id.editor_pitch_seekbar);
        mAudioNameTextView = mView.findViewById(R.id.editor_audioName_textview);
        mElapsedTimeTextView = mView.findViewById(R.id.editor_elapsedTime_textview);
        mRemainingTimeTextView = mView.findViewById(R.id.editor_remainingTime_textview);
        mPlayPauseButton = mView.findViewById(R.id.editor_playPause_button);
        mSaveButton = mView.findViewById(R.id.editor_save_button);

        mMediaPlayer = new MediaPlayer();

        if (mAudioList == null) {
            mAudioList = new ArrayList<>();
        }

        // Audio Spinner onItemSelected
        mAudioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAudioListPosition = position;
                mAudioNameTextView.setText(mAudioList.get(position).getName());
                if (mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                    mMediaPlayer.reset();
                }
                try {
                    mMediaPlayer.setDataSource(getActivity(), mAudioList.get(position).getAudioPlayerUri());
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(mp -> {
                        mTotalPlaytime = mMediaPlayer.getDuration();
                        mProgressSeekBar.setMax(mTotalPlaytime);
                        mMediaPlayer.start();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Play/Pause button onClick
        mPlayPauseButton.setOnClickListener(v -> {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            } else {
               mMediaPlayer.start();
            }
        });

        // Progress bar onChange
        mProgressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mMediaPlayer.seekTo(progress);
                    mProgressSeekBar.setProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Speed bar onChange
        mSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSpeedMod = progress * 0.15f;
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setSpeed(progress * 0.15f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Pitch bar onChange
        mPitchSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPitchMod = progress * 0.15f;
                mMediaPlayer.setPlaybackParams(mMediaPlayer.getPlaybackParams().setPitch(progress * 0.15f));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Save button onClick
        mSaveButton.setOnClickListener(v -> {
            mAudioList.get(mAudioListPosition).setSpeedMod(mSpeedMod);
            mAudioList.get(mAudioListPosition).setPitchMod(mPitchMod);
            Toast.makeText(getActivity(), getText(R.string.successfully_saved_please_rebind_to_pad), Toast.LENGTH_SHORT).show();
        });

        // Thread (Update positionBar & timeLabel)
        new Thread(() -> {
            while (mMediaPlayer != null) {
                try {
                    Message msg = new Message();
                    msg.what = mMediaPlayer.getCurrentPosition();
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        }).start();

        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        mMainActivityViewModel.getAudioList().observe(getViewLifecycleOwner(), audioList -> {
            mAudioList = audioList;
            bindDataToAudioSpinner();
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mMainActivityViewModel.setAudioList(mAudioList);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update progress bar.
            mProgressSeekBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            mElapsedTimeTextView.setText(elapsedTime);

            String remainingTime = createTimeLabel(mTotalPlaytime - currentPosition);
            mRemainingTimeTextView.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    private void bindDataToAudioSpinner() {
        Collections.sort(mAudioList, (o1, o2) -> o1.getName().compareTo(o2.getName()));
        AudioSpinnerAdapter audioSpinnerAdapter = new AudioSpinnerAdapter(
                getActivity(),
                android.R.layout.simple_spinner_item,
                mAudioList
        );
        mAudioSpinner.setAdapter(audioSpinnerAdapter);
    }
}