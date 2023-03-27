package com.example.theexplorer.ui.leaderboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.theexplorer.R;
import com.example.theexplorer.services.User;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardAdapter extends ArrayAdapter<User> {

    private ArrayList<User> truncatedUsers;
    private Context context;
    public LeaderboardAdapter(@NonNull Context context, ArrayList<User> content) {
        super(context, 0,content);
        this.truncatedUsers = content;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.adapter_leaderboard_content, parent,false);
        }
        User user = truncatedUsers.get(position);
        TextView ranking = view.findViewById(R.id.leaderboard_ranking);
        TextView userName = view.findViewById(R.id.leaderboard_username);

        ranking.setText(String.valueOf(position));
        userName.setText(user.getUserId());
        return view;

    }
}
