package org.cpen321.discovr;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.DrawerActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.openDrawer;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isFocusable;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.allOf;

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
    // Current method of testing fragment existence is by
    // checking the title of the toolbar since there is no
    // actual way to check for the fragment within the view
    // :(


    // Title checking code credit: http://blog.sqisland.com/2015/05/espresso-match-toolbar-title.html
    private static ViewInteraction matchToolbarTitle(
            CharSequence title) {
        return onView(isAssignableFrom(Toolbar.class))
                .check(matches(withToolbarTitle(is(title))));
    }

    private static Matcher<Object> withToolbarTitle(
            final Matcher<CharSequence> textMatcher) {
        return new BoundedMatcher<Object, Toolbar>(Toolbar.class) {
            @Override public boolean matchesSafely(Toolbar toolbar) {
                return textMatcher.matches(toolbar.getTitle());
            }
            @Override public void describeTo(Description description) {
                description.appendText("with toolbar title: ");
                textMatcher.describeTo(description);
            }
        };
    }


    // 1. Open up Events Nearby
    @Test
    public void testEventsNearby(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.events_upcoming)).perform(click());
        //Check whether the right title shows up
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.events_upcoming);
        matchToolbarTitle(title);
        //TODO: Check whether the proper actions occur on the map
    }

    // 2. Open up Subscribed Events
    @Test
    public void testSubscribedEvents(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.events_subscribed)).perform(click());
        //Check whether the right title shows up
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.events_subscribed);
        matchToolbarTitle(title);
        //TODO: Check for fragment existence
    }


    // 3. Open up all events
    @Test
    public void testAllEvents(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.events_all)).perform(click());
        //Check whether the right title shows up
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.events_all);
        matchToolbarTitle(title);
        //TODO: Check for fragment existence
    }

    // 4. Open up course list
    @Test
    public void testCourseList(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.courses)).perform(click());
        //Check whether the right title shows up
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.courses);
        matchToolbarTitle(title);
        //TODO: Check for fragment existence
    }



    // TODO: Testing fragment switching
    // 5. Open up Subscribed Events Fragment, then open up course list
    @Test
    public void testFragmentSwitch(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.events_subscribed)).perform(click());
        //Check whether the right title shows up
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.events_subscribed);
        matchToolbarTitle(title);
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.courses)).perform(click());
        //Check whether the right title shows up
        CharSequence title2 = InstrumentationRegistry.getTargetContext().getString(R.string.courses);
        matchToolbarTitle(title2);
    }


    // TODO: Testing proper back button behaviour
    // 6. Open up subscribed events, pressing back
    //      - assert we are back in the map view page
    @Test
    public void backFragmentBackNavigation(){
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open());
        onView(withText(R.string.events_subscribed)).perform(click());
        //Check whether the right title shows up
        CharSequence title = InstrumentationRegistry.getTargetContext().getString(R.string.events_subscribed);
        matchToolbarTitle(title);
        pressBack();
        //Check whether the right title shows up
        CharSequence title2 = InstrumentationRegistry.getTargetContext().getString(R.string.app_name);
        matchToolbarTitle(title2);
    }

    // TODO: Determine way to organize tests

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