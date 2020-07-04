package ca.nait.dmit2504.soundboardapp;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MainActivityViewModel extends ViewModel {

    private MutableLiveData<List<Pad>> padsList = new MutableLiveData<List<Pad>>();

    public LiveData<List<Pad>> getPads() {
        return padsList;
    }

    public void setPads(List<Pad> listOfPads) {
        padsList.setValue(listOfPads);
    }
}
