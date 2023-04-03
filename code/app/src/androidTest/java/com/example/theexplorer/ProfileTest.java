package com.example.theexplorer;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import static junit.framework.TestCase.assertTrue;

/**
 * US 01.07.01, US 05.01.01, US 02.03.01
 */
public class ProfileTest {
    private Solo solo;


    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    /**
     * Gets the Activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * US 01.07.01: As a player, I want to see other player’s profiles.
     * US 05.01.01: As a player, I want to search for other players by username.
     */
    @Test
    public void checkViewPlayers() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.toolbar_search)); // click search button and show and show activity_search

        EditText searchEditText = (EditText) solo.getView(R.id.etSearch);
        solo.enterText(searchEditText, "jontymagansashreek");

        solo.clickOnView(solo.getView(R.id.ivSearch));

        TextView profileNameTextView = (TextView) solo.getView(R.id.etUserName);

        String text = profileNameTextView.getText().toString();
        assertTrue("PROFILE VIEWED :)", text.matches("jontymagansashreek"));

    }

    /**
     * US 02.03.01: As a player, I want to be able to browse QR codes that other players have
     * scanned.
     */
    @Test
    public void checkPlayerCodes() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);

        solo.clickOnView(solo.getView(R.id.toolbar_search)); // click search button and show and show activity_search

        EditText searchEditText = (EditText) solo.getView(R.id.etSearch);
        solo.enterText(searchEditText, "jontymagansashreek");

        solo.clickOnView(solo.getView(R.id.ivSearch));

        TextView qrCodeListViewTitle = (TextView) solo.getView(R.id.qr_code_list_title);

        String text = qrCodeListViewTitle.getText().toString();
        assertTrue("qrCodeListViewTitle VIEWED :)", text.matches("QR Codes :"));

    }
}

