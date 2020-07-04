package ca.nait.dmit2504.soundboardapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;

import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SetupFragment extends Fragment {

    private static final int REQUEST_AUDIO_FILES_LT19 = 1;
    private static final int REQUEST_AUDIO_FILES_GT19 = 2;
    private static final int RESULT_OK = -1;
    private static final int REQUEST_PERMISSION = 21;
    private static final String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private MainActivityViewModel mMainActivityViewModel;
    private View mView;
    private Button mUploadButton;
    private Intent mUploadFileIntent;
    private List<Pad> mPadList;
    private List<Audio> mAudioList;

//    private TextView tv;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_setup, container, false);

        // Views and Init
        mUploadButton = mView.findViewById(R.id.setup_upload_button);
//        tv = mView.findViewById(R.id.textView);
        mAudioList = new ArrayList<>();

        // onClick for file upload
        mUploadButton.setOnClickListener(v -> {
            if (checkPermission()) {
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
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            Audio audio = new Audio();
            MediaPlayer mediaPlayer = new MediaPlayer();
            String path;
            String name = "";
            Uri uri;
            InputStream inputStream;

            switch (requestCode) {
                case REQUEST_AUDIO_FILES_LT19:
                    uri = data.getData();

                    // Get name
                    path = uriToFilePath(uri);
                    File file = new File(path);
                    name = trimExtension(file.getName());

                    // Set MediaPlayer with uri
                    setMediaPlayerWithUri(getActivity(), mediaPlayer, uri);

                    name = trimExtension(name);
                    trySetAudioToList(audio, name, mediaPlayer);
                    break;
                case REQUEST_AUDIO_FILES_GT19:
                    uri = data.getData();

                    // Get name
                    Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                    try {
                        if (cursor != null && cursor.moveToFirst()) {
                            name = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }

                    // Get name alt (if cursor comes back null)
                    if (name == null && name == "") {
                        name = uri.getPath();
                        int cut = name.lastIndexOf('/');
                        if (cut != -1) {
                            name = name.substring(cut + 1);
                        }
                    }

                    // Set MediaPlayer on uri
                    setMediaPlayerWithUri(getActivity(), mediaPlayer, uri);

                    name = trimExtension(name);
                    trySetAudioToList(audio, name, mediaPlayer);
                    break;
            }
        }
    }

    private void setMediaPlayerWithUri(FragmentActivity activity, MediaPlayer mediaPlayer, Uri uri) {
        try {
            mediaPlayer.setDataSource(activity, uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void trySetAudioToList(Audio audio, String name, MediaPlayer mediaPlayer) {
        if (name != null && name != "" && mediaPlayer != null) {
            audio.setName(name);
            audio.setAudioPlayer(mediaPlayer);
            Toast.makeText(getActivity(), getText(R.string.file_successfully_uploaded), Toast.LENGTH_SHORT).show();
            mAudioList.add(audio);
        } else {
            Toast.makeText(getActivity(), getText(R.string.unable_to_read_file), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Release MediaPlayer on fragment stop
        for (Audio audio : mAudioList) {
            audio.getAudioPlayer().release();
        }
    }

    private String trimExtension(String name) {
        if (name.contains(".mp3") || name.contains(".wav") || name.contains("m4a")) {
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

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(getContext(), READ_EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE_PERMISSION}, REQUEST_PERMISSION);
            return false;
        }
    }

//    public void setPadIdentifier() {
//        int padIndex = 0;
//        String padPrefix;
//        String padIdentifier;
//        for (int indexAlpha = 0; indexAlpha < 4; indexAlpha++) {
//            for (int indexNumeric = 0; indexNumeric < 3; indexNumeric++) {
//                switch (indexAlpha) {
//                    case 0:
//                        padPrefix = "A";
//                        break;
//                    case 1:
//                        padPrefix = "B";
//                        break;
//                    case 2:
//                        padPrefix = "C";
//                        break;
//                    case 3:
//                        padPrefix = "D";
//                        break;
//                    default:
//                        padPrefix = "";
//                        break;
//                }
//                padIdentifier = padPrefix + (indexNumeric + 1);
//                mPadList.get(padIndex).setPadIdentifier(padIdentifier);
//                padIndex++;
//            }
//        }
//    }
}