package ca.nait.dmit2504.soundboardapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SetupFragment extends Fragment {

    private static final int REQUEST_AUDIO_FILES_LT19 = 1;
    private static final int REQUEST_AUDIO_FILES_GT19 = 2;
    private static final int RESULT_OK = -1;
    private static final int REQUEST_READ_EXTERNAL_STORAGE_PERMISSION = 21;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 22;
    private static final String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final String RECORD_AUDIO_PERMISSION = Manifest.permission.RECORD_AUDIO;
    private static final String RECORDER_OUTPUT_BASE_FILENAME = "recording";
    private static final String RECORDER_OUTPUT_EXTENSION = ".3gp";
    private MainActivityViewModel mMainActivityViewModel;
    private View mView;
    private Spinner mPadSpinner;
    private Spinner mAudioSpinner;
    private Button mSaveButton;
    private Button mUploadButton;
    private ImageButton mRecordImageButton;
    private Intent mUploadFileIntent;
    private MediaRecorder mMediaRecorder;
    private List<Pad> mPadList;
    private List<Audio> mAudioList;
    private File mOutputFile;
    private String mOutputFilename;
    private boolean mIsRecording;
    private int mRecordingOutputFilenameIndex = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_setup, container, false);

        // Views and Init
        mPadSpinner = mView.findViewById(R.id.setup_padIdentifier_spinner);
        mAudioSpinner = mView.findViewById(R.id.setup_audio_spinner);
        mSaveButton = mView.findViewById(R.id.setup_save_button);
        mUploadButton = mView.findViewById(R.id.setup_upload_button);
        mRecordImageButton = mView.findViewById(R.id.setup_record_imageButton);

        // Setup list of pad to have 12 empty pads
        if (mPadList == null) {
            mPadList = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                Pad emptyPad = new Pad();
                mPadList.add(emptyPad);
            }
        }
        if (mAudioList == null) {
            mAudioList = new ArrayList<>();
        }

        // Bind data to spinners
        bindDataToPadSpinner();

        // onClick for pad + audio bind
        mSaveButton.setOnClickListener(v -> {
            int padPosition = mPadSpinner.getSelectedItemPosition();

            // If audio selected is not empty selection, setup Pad, otherwise set to empty pad (initial values)
            Pad pad = new Pad();
            if (mAudioSpinner.getSelectedItem() != null && mAudioSpinner.getSelectedItem() != "") {
                int audioPosition = mAudioSpinner.getSelectedItemPosition();
                pad.setActive(true);
                pad.setPadPlayerUri(mAudioList.get(audioPosition).getAudioPlayerUri());
            }
            // By replacing the pad in the List<Pad>, we don't need to worry about if it contained default audio values
            mPadList.set(padPosition, pad);
            Toast.makeText(getActivity(), getText(R.string.audio_and_pad_bound), Toast.LENGTH_SHORT).show();
            mMainActivityViewModel.setPads(mPadList);
        });

        // onClick for file upload
        mUploadButton.setOnClickListener(v -> {
            if (checkReadExternalStoragePermission()) {
                // uri returned is different after android 4.4
                if (Build.VERSION.SDK_INT < 19) {
                    mUploadFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    mUploadFileIntent.setType("audio/*");
                    startActivityForResult(mUploadFileIntent, REQUEST_AUDIO_FILES_LT19);
                } else {
                    mUploadFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    mUploadFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                    mUploadFileIntent.setType("audio/*");
                    startActivityForResult(Intent.createChooser(mUploadFileIntent, getText(R.string.upload_audio_file)), REQUEST_AUDIO_FILES_GT19);
                }
            }
        });

        // onClick for recording
        mRecordImageButton.setOnClickListener(v -> {
            if (mIsRecording) {
                // Change image
                mRecordImageButton.setImageDrawable(getResources().getDrawable(R.drawable.not_recording, null));
                mRecordImageButton.setBackgroundColor(getResources().getColor(R.color.not_recording_background));
                mIsRecording = false;

                // Stop recording and clear resources
                mMediaRecorder.stop();
                mMediaRecorder.release();
                mMediaRecorder = null;

                // Get file uri
                Audio audio = new Audio();
                Uri uri;

                // Uri.fromFile() only works on api < 26
                if (Build.VERSION.SDK_INT < 26) {
                    uri = Uri.fromFile(mOutputFile);
                    audio.setName(RECORDER_OUTPUT_BASE_FILENAME + mRecordingOutputFilenameIndex);
                } else {
                    // getUriForFile uses FileProvider to get content uri instead of file uri
                    uri = FileProvider.getUriForFile(getActivity(), "ca.nait.dmit2504.soundboardapp.fileprovider", mOutputFile);
                    audio.setName(RECORDER_OUTPUT_BASE_FILENAME + mRecordingOutputFilenameIndex);
                }
                audio.setAudioPlayerUri(uri);
                mAudioList.add(audio);
                bindDataToAudioSpinner();
            } else {
                // Check permission
                if (checkRecordAudioPermission()) {
                    // Change image back
                    mRecordImageButton.setImageDrawable(getResources().getDrawable(R.drawable.recording, null));
                    mRecordImageButton.setBackgroundColor(getResources().getColor(R.color.recording_background));
                    mIsRecording = true;

                    // Start recording
                    // getFilesDir() for root of internal storage (does not req permission)
                    String outputPath = getActivity().getFilesDir().getAbsolutePath();
                    mOutputFilename = RECORDER_OUTPUT_BASE_FILENAME + mRecordingOutputFilenameIndex + RECORDER_OUTPUT_EXTENSION;
                    mOutputFile =  new File(outputPath + "/" + mOutputFilename);
                    mMediaRecorder = new MediaRecorder();

                    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    // Create unique file name for each recording
                    while (mOutputFile.exists()) {
                        mRecordingOutputFilenameIndex++;
                        mOutputFilename = RECORDER_OUTPUT_BASE_FILENAME + mRecordingOutputFilenameIndex + RECORDER_OUTPUT_EXTENSION;
                        mOutputFile = new File(outputPath + "/" + mOutputFilename);
                    }
                    mMediaRecorder.setOutputFile(outputPath + "/" + mOutputFilename);

                    // Prepare MediaRecorder
                    try {
                        mMediaRecorder.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mMediaRecorder.start();
                }
            }
        });
        return mView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMainActivityViewModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        // getViewLifecycleOwner instead of this so that the observer is not bound to the instance of the fragment, allowing it to be unbound when fragment view is destroyed
        mMainActivityViewModel.getPads().observe(getViewLifecycleOwner(), pads -> {
            mPadList = pads;
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Audio audio = new Audio();
            String path;
            String name = "";
            Uri uri = data.getData();

            switch (requestCode) {
                case REQUEST_AUDIO_FILES_LT19:
                    // Get name
                    path = uriToFilePath(uri);
                    File file = new File(path);
                    name = trimExtension(file.getName());
                    break;
                case REQUEST_AUDIO_FILES_GT19:
                    // Get name
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                    break;
            }
            // Trim extension from name
            name = trimExtension(name);

            // Set audio properties then save audio to list
            trySetAudioToList(audio, name, uri);

            // Bind new audio file to spinner
            bindDataToAudioSpinner();
        }
    }

    private void trySetAudioToList(Audio audio, String name, Uri uri) {
        boolean existingAudioCheck = false;

        // Check if audio already exists
        for (Audio audioFile : mAudioList) {
            if (audioFile.getName().equals(name)) {
                existingAudioCheck = true;
            }
        }
        if (!existingAudioCheck) {
            // Check if values are valid
            if (name != null && name != "" && uri != null) {
                audio.setName(name);
                audio.setAudioPlayerUri(uri);
                Toast.makeText(getActivity(), getText(R.string.file_successfully_uploaded), Toast.LENGTH_SHORT).show();
                mAudioList.add(audio);
            } else {
                Toast.makeText(getActivity(), getText(R.string.unable_to_read_file), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(), getText(R.string.this_file_already_exists), Toast.LENGTH_SHORT).show();
        }
    }

    private String trimExtension(String name) {
        if (name.contains(".mp3") || name.contains(".wav") || name.contains(".3gp")) {
            return name.substring(0, name.length() - 4);
        } else {
            return name;
        }
    }

    private String uriToFilePath(Uri uri) {
        String path;
        String[] dataArray = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(getActivity(), uri, dataArray, null, null, null);
        Cursor cursor = loader.loadInBackground();
        try {
            int column_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
        } finally {
            cursor.close();
        }
        return path;
    }

    private boolean checkReadExternalStoragePermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE_PERMISSION}, REQUEST_READ_EXTERNAL_STORAGE_PERMISSION);
            return false;
        }
    }

    private boolean checkRecordAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), RECORD_AUDIO_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{RECORD_AUDIO_PERMISSION}, REQUEST_RECORD_AUDIO_PERMISSION);
            return false;
        }
    }

    private void bindDataToPadSpinner() {
        List<String> padSpinnerList = new ArrayList<>();
        padSpinnerList.add(getText(R.string.a1).toString());
        padSpinnerList.add(getText(R.string.a2).toString());
        padSpinnerList.add(getText(R.string.a3).toString());
        padSpinnerList.add(getText(R.string.b1).toString());
        padSpinnerList.add(getText(R.string.b2).toString());
        padSpinnerList.add(getText(R.string.b3).toString());
        padSpinnerList.add(getText(R.string.c1).toString());
        padSpinnerList.add(getText(R.string.c2).toString());
        padSpinnerList.add(getText(R.string.c3).toString());
        padSpinnerList.add(getText(R.string.d1).toString());
        padSpinnerList.add(getText(R.string.d2).toString());
        padSpinnerList.add(getText(R.string.d3).toString());
        ArrayAdapter<String> padSpinnerAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                padSpinnerList
        );
        padSpinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mPadSpinner.setAdapter(padSpinnerAdapter);
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