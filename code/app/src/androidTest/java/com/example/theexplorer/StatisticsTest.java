package com.example.theexplorer;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

/**
 * US 01.04.01: As a player, I want to see my highest and lowest scoring QR codes.
 * US 01.05.01: As a player, I want to see the sum of scores of QR codes that I have scanned.
 * US 01.06.01: As a player, I want to see the total number of QR codes that I have scanned.
 *
 * Assumes at least 1 code has been scanned to show off the information required in each US.
 */
public class StatisticsTest {
    private Solo solo;


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    /**
     * US 01.04.01: As a player, I want to see my highest and lowest scoring QR codes.
     *
     * This test case is assuming the user has at least 1 code added to show as the HIGHEST scoring
     * QR code in the statistics page.
     */
    @Test
    public void checkHighest() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_see_scores)); // click statistics button and show activity_scores

        TextView highestScoreText = (TextView) solo.getView(R.id.highest_score);

        String text = highestScoreText.getText().toString();
        assertTrue("highestScoreText displays a score :)", text.matches("Score: " + "\\d+")); // checks for proper formatting Score: SCORENUMBER
    }

    /**
     * US 01.04.01: As a player, I want to see my highest and lowest scoring QR codes.
     *
     * This test case is assuming the user has at least 1 code added to show as the LOWEST scoring
     * QR code in the statistics page.
     */
    @Test
    public void checkLowest() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_see_scores)); // click statistics button and show activity_scores

        TextView lowestScoreText = (TextView) solo.getView(R.id.lowest_score);

        String text = lowestScoreText.getText().toString();
        assertTrue("lowestScoreText displays a score :)", text.matches("Score: " + "\\d+")); // checks for proper formatting Score: SCORENUMBER
    }

    /**
     * US 01.05.01: As a player, I want to see the sum of scores of QR codes that I have scanned.
     *
     * This test case is assuming the user has at least 1 code added to show as the sum of score(s).
     */
    @Test
    public void checkSum() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_see_scores)); // click statistics button and show activity_scores

        TextView sumScoreText = (TextView) solo.getView(R.id.score_sum);

        String text = sumScoreText.getText().toString();
        assertTrue("sumScoreText displays a score :)", text.matches("\\d+")); // checks for proper formatting SUMNUMBER
    }

    /**
     * US 01.06.01: As a player, I want to see the total number of QR codes that I have scanned.
     *
     * This test case is assuming the user has at least 1 code added to show as the TOTAL number of
     * QR codes scanned in statistics page.
     */
    @Test
    public void checkTotal() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.button_see_scores)); // click statistics button and show activity_scores

        TextView totalScanText = (TextView) solo.getView(R.id.total_scanned);

        String text = totalScanText.getText().toString();
        assertTrue("totalScanText displays a score :)", text.matches("\\d+")); // checks for proper formatting TOTALNUMBER
    }

}

