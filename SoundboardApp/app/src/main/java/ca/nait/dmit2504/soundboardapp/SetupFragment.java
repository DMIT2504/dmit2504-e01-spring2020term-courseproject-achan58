package ca.nait.dmit2504.soundboardapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetupFragment extends Fragment {

    private static final int REQUEST_AUDIO_FILES = 1;
    private static final int RESULT_OK = -1;
    private static final int REQUEST_PERMISSION = 21;
    private static final String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;
    private MainActivityViewModel mMainActivityViewModel;
    private View mView;
    private Button mUploadButton;
    private Intent mUploadFileIntent;
    private List<Pad> mPadList;
    private List<Audio> mAudioList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mView = inflater.inflate(R.layout.fragment_setup, container, false);

        mUploadButton = mView.findViewById(R.id.setup_upload_button);

        // onClick for file upload
        mUploadButton.setOnClickListener(v -> {
            if (checkPermission()) {
                mUploadFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                mUploadFileIntent.setType("audio/*");
                startActivityForResult(mUploadFileIntent, REQUEST_AUDIO_FILES);
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
        switch (requestCode) {
            case REQUEST_AUDIO_FILES:
                if (resultCode == RESULT_OK) {
                    Audio audio = new Audio();
                    String path;
                    String name;

                    Uri uri = data.getData();
                    path = uriToFilePath(uri);
                    File file = new File(path);
                    name = file.getName();

                    audio.setName(name);
                    audio.setAudioFilePath(path);
                    mAudioList.add(audio);
//                    Uri uri = data.getData();
//                    MediaPlayer mp = new MediaPlayer();
//                    try {
//                        mp.setDataSource(new FileInputStream(new File(uri.getPath())).getFD());
//                        mp.prepareAsync();
//                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                            @Override
//                            public void onPrepared(MediaPlayer mp) {
//                                mp.start();
//                            }
//                        });
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
                break;
        }
    }

    private String uriToFilePath(Uri uri) {
        String path;
        String[] dataArray = {MediaStore.Audio.Media.DATA};
        CursorLoader loader = new CursorLoader(getActivity(), uri, dataArray, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        path = cursor.getString(column_index);
        cursor.close();
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