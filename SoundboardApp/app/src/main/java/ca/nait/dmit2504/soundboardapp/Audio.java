package ca.nait.dmit2504.soundboardapp;

import android.media.MediaPlayer;

public class Audio {

    private String name;
    private String audioFilePath;

    public MediaPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void setAudioPlayer(MediaPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }

    private MediaPlayer audioPlayer;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }

    public void setAudioFilePath(String audioFilePath) {
        this.audioFilePath = audioFilePath;
    }
}
