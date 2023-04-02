/**
 * A fragment representing the home screen of the app, which allows users to view their scores,
 * view their scanned codes, and view the leaderboard.
 */

package com.example.theexplorer.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.theexplorer.databinding.FragmentHomeBinding;
import com.example.theexplorer.ui.leaderboard.LeaderboardActivity;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;


    /**
     * Inflates the layout for this fragment, sets up the button click listeners, and returns the root view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment
     * @param container The parent view that the fragment UI should be attached to
     * @param savedInstanceState The previously saved state of the fragment, or null if this is a new instance
     * @return The root view of the inflated layout
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Button seeScoresButton = binding.buttonSeeScores;
        seeScoresButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScoresFragment.class);
                startActivity(intent);
            }
        });

        Button viewCodesButton = binding.buttonViewCodes;
        viewCodesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScannedFragment.class);
                startActivity(intent);
            }
        });

        Button leaderboardButton = binding.buttonSeeLeaderboard;
        leaderboardButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Bundle bundle = new Bundle();

                Intent intent = new Intent(getActivity(), LeaderboardActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

    /**
     * Nullifies the binding instance when the fragment is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}