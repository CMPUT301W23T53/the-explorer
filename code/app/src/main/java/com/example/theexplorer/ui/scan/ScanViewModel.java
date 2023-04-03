package com.example.theexplorer.ui.scan;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * A ViewModel class for managing the data used by the ScanFragment.
 * It extends ViewModel to make use of Android's architecture components
 * for better lifecycle management.
 */
public class ScanViewModel extends ViewModel {

    // MutableLiveData holding the text to be displayed in the ScanFragment
    private final MutableLiveData<String> mText;

    /**
     * Constructor for the ScanViewModel. Initializes the MutableLiveData with a default value.
     */
    public ScanViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is a Scan fragment");
    }

    /**
     * Returns a LiveData reference to the text to be displayed in the ScanFragment.
     *
     * @return A LiveData<String> reference containing the text.
     */
    public LiveData<String> getText() {
        return mText;
    }
}
