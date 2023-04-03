package com.example.theexplorer.ui.leaderboard;

import android.content.Context;
import android.util.Log;
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

public class LeaderboardAdapter extends ArrayAdapter<RankingData> {

    private Context context;
    public LeaderboardAdapter(@NonNull Context context, ArrayList<RankingData> content) {
        super(context, 0, content);

        //this.truncatedUsers = content;
        //this.context = context;
    }

    /**
     * Returns a View that displays the data at the specified position in the list.
     *
     * @param position    the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent that this view will eventually be attached to
     * @return            a View that displays the data at the specified position in the list
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(getContext()).inflate(R.layout.adapter_leaderboard_content, parent,false);
        }else{
            view = convertView;
        }

        RankingData data = getItem(position);
        TextView ranking = view.findViewById(R.id.leaderboard_ranking);
        TextView title = view.findViewById(R.id.leaderboard_title);
        TextView subtitle = view.findViewById(R.id.leaderboard_subtitle);

        ranking.setText(String.valueOf(data.getRanking()));


        NewUserService userService = new NewUserService();
        userService.getNameFromEmail(data.getUserID()).addOnSuccessListener(s -> {
            String fin = s;
            if(data.getIsQRCode()){
                fin += System.lineSeparator() + "Score: " + data.getValue();
            }
            title.setText(fin);
            subtitle.setText(data.getSubtitle());
        });
        return view;
    }
}
