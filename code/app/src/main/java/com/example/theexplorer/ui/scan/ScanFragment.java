/**
 * A Fragment that handles the scanning functionality of the application.
 */

package com.example.theexplorer.ui.scan;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.theexplorer.databinding.FragmentScanBinding;

/**
 * A fragment that handles the scanning functionality of the app. It uses the ZXing library
 * to perform barcode and QR code scanning.
 */
public class ScanFragment extends Fragment {

    private FragmentScanBinding binding;

    /**
     * Initializes the view for the scan fragment and sets up the start scan button.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment.
     * @param container The parent view that this fragment's UI should be attached to.
     * @param savedInstanceState This fragment's previously saved state, if any.
     * @return The View for the fragment UI.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ScanViewModel scanViewModel = new ViewModelProvider(this).get(ScanViewModel.class);

        binding = FragmentScanBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textScan;
        scanViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        Button startScanButton = binding.buttonStartScan;
        startScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ZXingScannerScan.class);
                startActivity(intent);
            }
        });
        return root;
    }

    /**
     * Cleans up the binding when the fragment's view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}