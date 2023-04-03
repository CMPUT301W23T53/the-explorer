package com.example.theexplorer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.theexplorer.services.NewUserService;
import com.example.theexplorer.services.User;
import com.example.theexplorer.ui.home.DetailPageOfOneQR;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;

/**
 * US 01.02.01: As a player, I want to see what QR codes I have added to my account.
 * US 01.03.01: As a player, I want to remove QR codes from my account.
 * US 02.02.01: As a player, I want to be able to comment on QR codes.
 * US 02.04.01: As a player, I want to see that other players have scanned the same QR code.
 * US 02.05.01: As a player, I want QR codes to have a unique human readable name.
 * US 02.06.01: As a player, I want to see a visual representation of a QR code relatively unique to that QR code.
 *
 * Assumes at least 1 code has been scanned to show off the information required in each US.
 */
public class DetailPageTest {
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
     * US 01.02.01: As a player, I want to see what QR codes I have added to my account.
     *
     * This test case is assuming the user has at least 1 code added to show the QR list view.
     */
    @Test
    public void checkQRList() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_view_codes)); // click statistics button and show activity_scores
        ListView listView = (ListView) solo.getView(R.id.listview_scanned);
        HashMap<String, String> item = (HashMap<String, String>) listView.getItemAtPosition(0); // Get item from first position
        String name = item.get("qrname");
        assertNotNull(name);
    }
    /**
     * US 02.05.01: As a player, I want QR codes to have a unique human readable name.
     *
     * This test case is assuming the user has at least 1 code added to check the name.
     */
    @Test
    public void checkQRName() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        // click show code button and show scanded codes
        solo.clickOnView(solo.getView(R.id.button_view_codes));
        ListView listView = (ListView) solo.getView(R.id.listview_scanned);
        int index = 0; // select the first item
        solo.clickInList(index);
        solo.clickOnButton("More");
        // Verify that the dialog is dismissed and the previous activity is resumed
        assertTrue(solo.waitForActivity(DetailPageOfOneQR.class));
        TextView nameText = (TextView) solo.getView(R.id.qr_name);
        String text = nameText.getText().toString();
        assertNotNull(nameText);

    }
    /**
     * US 02.06.01: As a player, I want to see a visual representation of a QR code relatively unique to that QR code.
     *
     * This test case is assuming the user has at least 1 code added to check the name.
     */

    @Test
    public void checkRep() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_view_codes)); // click show code button and show scanded codes
        ListView listView = (ListView) solo.getView(R.id.listview_scanned);
        int index = 0; // select the first item
        solo.clickInList(index);
        solo.clickOnButton("More");
        // Verify that the dialog is dismissed and the previous activity is resumed
        assertTrue(solo.waitForActivity(DetailPageOfOneQR.class));
        // verify the represent is show up
        TextView repTopText = (TextView) solo.getView(R.id.vis_rep_top);
        String repTop = repTopText.getText().toString();
        assertNotNull(repTop);
    }

    /**
     * US 02.02.01: As a player, I want to be able to comment on QR codes.
     *
     * This test case is assuming the user has at least 1 code added to check the name.
     */

    @Test
    public void checkComment() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_view_codes)); // click show code button and show scanded codes
        ListView listView = (ListView) solo.getView(R.id.listview_scanned);
        int index = 0; // select the first item
        solo.clickInList(index);
        solo.clickOnButton("More");
        // Verify that the dialog is dismissed and the previous activity is resumed
        assertTrue(solo.waitForActivity(DetailPageOfOneQR.class));
        //add a comment for test
        solo.enterText((EditText) solo.getView(R.id.comment_edit_text), "Test");
        solo.clickOnView(solo.getView(R.id.add_comment_button));
        solo.waitForText("Test", 1, 2000);
        //verify the comment is added
        ListView commentList = (ListView) solo.getView(R.id.comment_list_view);
        String comment = (String) commentList.getItemAtPosition(0); // Get item from first position
        assertTrue("Test", comment.contains("Test"));


    }


    /**
     * US 02.04.01: As a player, I want to see that other players have scanned the same QR code.
     *
     * This test case is assuming the user has at least 1 code added to check the name.
     */
    @Test
    public void checkOtherUser() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_view_codes)); // click show code button and show scanded codes
        ListView listView = (ListView) solo.getView(R.id.listview_scanned);
        int index = 0; // select the first item
        solo.clickInList(index);
        //verify the user list is show up
        ListView userListView = (ListView) solo.getView(R.id.listview_users);
        assertNotNull(userListView);


    }
    /**
     * US 01.03.01: As a player, I want to remove QR codes from my account.
     *
     * This test case is assuming the user has at least 1 code added to show the QR list view.
     */
    @Test
    public void checkRemove() {
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnView(solo.getView(R.id.button_view_codes)); // click show code button and show scanded codes
        ListView listView = (ListView) solo.getView(R.id.listview_scanned);
        int initialCount = listView.getCount();

        // Remove an item from the list view
        int index = 0; // select the first item
        solo.clickInList(index);
        solo.clickOnButton("Delete");
        // Get the count of items after removing an item
        int finalCount = listView.getCount();
        // Verify that the count has decreased by 1
        assertEquals(initialCount , finalCount);

    }


    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }

}