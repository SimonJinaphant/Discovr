package org.cpen321.discovr;

import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.EditText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by valerian on 11/9/16.
 */

@RunWith(AndroidJUnit4.class)
public class MainActivityTest{

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testDrawerOpen(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        pressBack();
    }


    //TODO: Create test opening all fragment elements

    // 1. Open up Events Nearby

    // 2. Open up Subscribed Events

    // 3. Open up all events

    // 4. Open up course list

    // TODO: Testing fragment switching
    // 5. Open up Subscribed Events Fragment, then open up view map fragment


    // TODO: Testing proper back button behaviour
    // 6. Open up subscribed events, pressing back
    //      - assert we are back in the map view page

    @Test
    public void testSearchBarExistance(){
        onView(withId(R.id.action_search)).check(matches(isDisplayed()));
        onView(withId(R.id.action_search)).check(matches(isClickable()));
        onView(withId(R.id.action_search)).check(matches(isFocusable()));
        onView(withId(R.id.action_search)).perform(click());
        pressBack();
    }


    @Test
    public void testSearchBarType(){
        onView(withId(R.id.action_search)).perform(click());
        String testText = "This is a test";
        onView(isAssignableFrom(EditText.class)).perform(typeText(testText));
        onView(withText(testText)).check(matches(isDisplayed()));
    }

    @Test
    public void testSearchQuery(){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        onView(withId(R.id.action_search)).perform(click());
        String location = "The Nest";
        onView(isAssignableFrom(EditText.class)).perform(typeText(location), pressImeActionButton());
        ViewActions.closeSoftKeyboard();
        //Fix this with a more appropriate delay class
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}