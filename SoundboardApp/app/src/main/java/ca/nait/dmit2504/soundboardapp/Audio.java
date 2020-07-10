package ca.nait.dmit2504.soundboardapp;

import android.media.MediaPlayer;
import android.net.Uri;

public class Audio {

    private String name;
    private Uri audioPlayerUri;
    private float speedMod;
    private float pitchMod;

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

    public float getSpeedMod() {
        if (speedMod == 0.0f) {
            speedMod = 1.0f;
        }
        return speedMod;
    }

    public void setSpeedMod(float speedMod) {
        this.speedMod = speedMod;
    }

    public float getPitchMod() {
        if (pitchMod == 0.0f) {
            pitchMod = 1.0f;
        }
        return pitchMod;
    }

    public void setPitchMod(float pitchMod) {
        this.pitchMod = pitchMod;
    }
}
