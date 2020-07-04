package ca.nait.dmit2504.soundboardapp;

import android.media.MediaPlayer;

import java.io.File;

public class Pad {

    private File audioFile;
    private String audioFilePath;
    private String padIdentifier;
    private MediaPlayer padPlayer;

    public File getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(File audioFile) {
        this.audioFile = audioFile;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }

    public String getPadIdentifier() {
        return padIdentifier;
    }

    public void setPadIdentifier(String padIdentifier) {
        this.padIdentifier = padIdentifier;
    }

    public MediaPlayer getPadPlayer() {
        return padPlayer;
    }

    public void setPadPlayer(MediaPlayer padPlayer) {
        this.padPlayer = padPlayer;
    }

}
