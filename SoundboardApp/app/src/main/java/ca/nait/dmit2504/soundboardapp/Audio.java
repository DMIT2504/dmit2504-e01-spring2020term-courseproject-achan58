package ca.nait.dmit2504.soundboardapp;

import android.media.MediaPlayer;
import android.net.Uri;

public class Audio {

    private String name;
    private Uri audioPlayerUri;


    public Uri getAudioPlayerUri() { return audioPlayerUri; }

    public void setAudioPlayerUri(Uri uri) {
        this.audioPlayerUri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
