package ca.nait.dmit2504.soundboardapp;

import android.media.MediaPlayer;

import java.io.File;

public class Pad {

    private String audioFilePath;
    private MediaPlayer padPlayer;

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public MediaPlayer getPadPlayer() {
        return padPlayer;
    }

    public void setPadPlayer(MediaPlayer padPlayer) {
        this.padPlayer = padPlayer;
    }

}
