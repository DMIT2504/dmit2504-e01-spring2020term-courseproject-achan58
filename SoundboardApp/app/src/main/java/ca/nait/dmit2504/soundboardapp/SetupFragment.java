package ca.nait.dmit2504.soundboardapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class SetupFragment extends Fragment {

    private MainActivityViewModel mMainActivityViewModel;
    private List<Pad> mPadList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setup, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mMainActivityViewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);

    }

//    public void setPadIdentifier() {
//        int padIndex = 0;
//        String padPrefix;
//        String padIdentifier;
//        for (int indexAlpha = 0; indexAlpha < 4; indexAlpha++) {
//            for (int indexNumeric = 0; indexNumeric < 3; indexNumeric++) {
//                switch (indexAlpha) {
//                    case 0:
//                        padPrefix = "A";
//                        break;
//                    case 1:
//                        padPrefix = "B";
//                        break;
//                    case 2:
//                        padPrefix = "C";
//                        break;
//                    case 3:
//                        padPrefix = "D";
//                        break;
//                    default:
//                        padPrefix = "";
//                        break;
//                }
//                padIdentifier = padPrefix + (indexNumeric + 1);
//                mPadList.get(padIndex).setPadIdentifier(padIdentifier);
//                padIndex++;
//            }
//        }
//    }
}