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
import com.example.theexplorer.services.NewUserService;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class LeaderboardAdapter extends ArrayAdapter<RankingTuple> {

    private Context context;
    public LeaderboardAdapter(@NonNull Context context, ArrayList<RankingTuple> content) {
        super(context, 0, content);

        //this.truncatedUsers = content;
        //this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_leaderboard_content, parent,false);
        }else{
            view = convertView;
        }

        TextView ranking = view.findViewById(R.id.leaderboard_ranking);
        TextView userName = view.findViewById(R.id.leaderboard_username);

        NewUserService userService = new NewUserService();
        userService.getNameFromEmail(getItem(position).getId()).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                ranking.setText(String.valueOf(getItem(position).getValue()));
                userName.setText(s);
            }
        });
        return view;
    }
}
