package ca.nait.dmit2504.soundboardapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Pad>> padsList;
    private MutableLiveData<List<Audio>> audioList;

    public LiveData<List<Pad>> getPads() {
        if (padsList == null) {
            padsList = new MutableLiveData<List<Pad>>();
        }
        return padsList;
    }

    public void setPads(List<Pad> listOfPads) {
        padsList.setValue(listOfPads);
    }

    public LiveData<List<Audio>> getAudioList() {
        if (audioList == null) {
            audioList = new MutableLiveData<List<Audio>>();
        }
        return audioList;
    }

    public void setAudioList(List<Audio> listOfAudio) { audioList.setValue(listOfAudio); }
}
