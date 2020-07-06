package ca.nait.dmit2504.soundboardapp;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;

public class Pad {

    private boolean active;
    private Uri padPlayerUri;
    private MediaPlayer padPlayer;
    private boolean defaultAudio;
    private AssetFileDescriptor defaultAudioAssetFileDescriptor;

    public boolean isActive() { return active; }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Uri getPadPlayerUri() {
        return padPlayerUri;
    }

    public void setPadPlayerUri(Uri uri) {
        this.padPlayerUri = uri;
    }

    public MediaPlayer getPadPlayer() {
        return padPlayer;
    }

    public void setPadPlayer(MediaPlayer padPlayer) {
        this.padPlayer = padPlayer;
    }

    public boolean isDefaultAudio() {
        return defaultAudio;
    }

    public void setDefaultAudio(boolean defaultAudio) {
        this.defaultAudio = defaultAudio;
    }

    public AssetFileDescriptor getDefaultAudioAssetFileDescriptor() {
        return defaultAudioAssetFileDescriptor;
    }

    public void setDefaultAudioAssetFileDescriptor(AssetFileDescriptor defaultAudioAssetFileDescriptor) {
        this.defaultAudioAssetFileDescriptor = defaultAudioAssetFileDescriptor;
    }
}
