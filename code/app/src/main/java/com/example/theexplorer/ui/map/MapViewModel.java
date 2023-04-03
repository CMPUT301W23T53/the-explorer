package com.example.theexplorer.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * MapViewModel is a class that extends ViewModel and is responsible for holding
 * and managing UI-related data in a lifecycle conscious way.
 * This class allows data to survive configuration changes such as screen rotations.
 */
public class MapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    /**
     * Initializes the MapViewModel with default text for the Maps fragment.
     */
    public MapViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is the Maps fragment");
    }

    /**
     * Returns a LiveData object containing the text for the Maps fragment.
     *
     * @return LiveData object containing the text for the Maps fragment.
     */
    public LiveData<String> getText() {
        return mText;
    }
}
