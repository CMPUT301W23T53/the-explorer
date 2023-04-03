/**
 * The HomeViewModel class is responsible for managing the data and business logic for the HomeFragment.
 * It extends the ViewModel class from the Android Architecture Components library and provides a LiveData
 * object that contains a string representing the text displayed in the HomeFragment.
 */

package com.example.theexplorer.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Constructor for the HomeViewModel class. It initializes the MutableLiveData object with the default text.
 */
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    /**
     * Getter method for the LiveData object containing the text displayed in the HomeFragment.
     * @return The LiveData object containing the text displayed in the HomeFragment.
     */
    public LiveData<String> getText() {
        return mText;
    }
}