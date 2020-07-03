package ca.nait.dmit2504.soundboardapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<AudioClip>> audioClips = new MutableLiveData<List<AudioClip>>();

    public LiveData<List<AudioClip>> getAudioClips() {
        return audioClips;
    }

    public void setListOfClips(List<AudioClip> listOfClips) {
        if (listOfClips.size() > 0) {
            audioClips.setValue(listOfClips);
        }
    }
}
