package com.example.theexplorer.ui.leaderboard;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.theexplorer.services.User;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter<Leaderboard> {
    public LeaderboardAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        // TODO Add resource file for adapter.
    }
}
