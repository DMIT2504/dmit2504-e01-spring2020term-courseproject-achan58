package ca.nait.dmit2504.simplemediaplayerdemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int UPLOAD_REQUEST_CODE = 1;
    private Uri mUri;
    private MediaPlayer mMediaPlayer = new MediaPlayer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void uploadOnClick(View view) {
        Intent mUploadFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        mUploadFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        mUploadFileIntent.setType("audio/*");
        startActivityForResult(Intent.createChooser(
                mUploadFileIntent,
                "Select an audio file."),
                UPLOAD_REQUEST_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case UPLOAD_REQUEST_CODE:
                mUri = data.getData();
                break;
            }
        }
    }

    public void playOnClick(View view) {
        if (!mMediaPlayer.isPlaying()) {
            try {
                mMediaPlayer.setDataSource(this, mUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } else {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
    }
}