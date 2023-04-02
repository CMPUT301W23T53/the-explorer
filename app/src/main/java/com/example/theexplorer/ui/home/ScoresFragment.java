package com.example.theexplorer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.theexplorer.R;
import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.QRCode;
import com.example.theexplorer.services.User;
import com.example.theexplorer.services.UserService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Map;

/**
 * This activity allows users to view their highest and lowest scores, as well as the total number
 * of codes scanned, and the sum of all scores from each code scanned.
 */
public class ScoresFragment extends AppCompatActivity {

    final NewUserService newUserService = new NewUserService();
    final User[] user = {new User()};
    String userEmail1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userEmail1 = firebaseUser.getEmail();

        newUserService.getUser(userEmail1).addOnSuccessListener(new OnSuccessListener<User>() {
            @Override
            public void onSuccess(User fetchUser) {
                user[0] = fetchUser;

                if (user[0] != null) {
                    List<QRCode> arrayQRCode = user[0].getQRList();

                    TextView highestScoreTextView = (TextView) findViewById(R.id.highest_score);
                    if (getHighestQRScore(arrayQRCode) == -1) {
                        highestScoreTextView.setText("No codes scanned");
                    } else {
                        highestScoreTextView.setText(String.valueOf(getHighestQRScore(arrayQRCode)));
                        TextView highName = (TextView) findViewById(R.id.high_score_name);
                        highName.setText(getHighestQRScoreName(arrayQRCode));
                    }

                    TextView low = findViewById(R.id.lowest_score);
                    if (getLowestQRScore(arrayQRCode) == Integer.MAX_VALUE) {
                        low.setText("No codes scanned");
                    } else {
                        low.setText(String.valueOf(getLowestQRScore(arrayQRCode)));
                        TextView lowName = findViewById(R.id.low_score_name);
                        lowName.setText(getLowestQRScoreName(arrayQRCode));
                    }

                    TextView total = findViewById(R.id.total_scanned);
                    total.setText(String.valueOf(arrayQRCode.size()));

                    TextView sumValue = findViewById(R.id.score_sum);
                    sumValue.setText(String.valueOf(getSumQRScores(arrayQRCode)));

                    TextView playerRanking = findViewById(R.id.player_ranking);
                    newUserService.getRankOfUser(user[0]).addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer rank) {
                            playerRanking.setText("Code Ranking: " + String.valueOf(rank));
                            Log.d("RANK", String.valueOf(rank));
                        }
                    });

                }


            }
        });

    }

    /**
     * Returns the highest QR score among all QR codes associated with the user.
     *
     * @return the highest QR score
     */
    public long getHighestQRScore(List<QRCode> arrayQRCode) {
        long maxScore = -1;

        for (int i = 0; i < arrayQRCode.size(); i++) {
            Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(i);

            long score = (long) qrCode.get("qrscore");
            if (score >= maxScore) {
                maxScore = score;
            }
        }
        return maxScore;
    }

    /**
     * Returns the name of the highest QR score among all QR codes associated with the user.
     *
     * @return the name of the highest QR score
     */
    public String getHighestQRScoreName(List<QRCode> arrayQRCode) {
        long maxScore = 0;
        String maxScoreName = null;

        for (int i = 0; i < arrayQRCode.size(); i++) {
            Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(i);

            long score = (long) qrCode.get("qrscore");
            if (score >= maxScore) {
                maxScore = score;
                maxScoreName = (String) qrCode.get("qrname");
            }
        }

        return maxScoreName;
    }

    /**
     * Returns the lowest QR score among all QR codes associated with the user.
     *
     * @return the lowest QR score
     */
    public long getLowestQRScore(List<QRCode> arrayQRCode) {
        long minScore = Integer.MAX_VALUE;

        for (int i = 0; i < arrayQRCode.size(); i++) {
            Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(i);

            long score = (long) qrCode.get("qrscore");
            if (score < minScore) {
                minScore = score;
            }
        }

        return minScore;
    }

    /**
     * Returns the name of the lowest QR score among all QR codes associated with the user.
     *
     * @return the name of the lowest QR score
     */
    public String getLowestQRScoreName(List<QRCode> arrayQRCode) {
        long minScore = Integer.MAX_VALUE;
        String minScoreName = null;

        for (int i = 0; i < arrayQRCode.size(); i++) {
            Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(i);

            long score = (long) qrCode.get("qrscore");
            if (score < minScore) {
                minScore = score;
                minScoreName = (String) qrCode.get("qrname");
            }
        }
        return minScoreName;
    }

    /**
     * Returns the sum of all QR scores associated with the user.
     *
     * @return the sum of all QR scores
     */
    public int getSumQRScores(List<QRCode> arrayQRCode) {
        int sumScore = 0;

        for (int i = 0; i < arrayQRCode.size(); i++) {
            Map<String, Object> qrCode = (Map<String, Object>) arrayQRCode.get(i);

            long score = (long) qrCode.get("qrscore");
            sumScore += score;
        }

        return sumScore;
    }


}
