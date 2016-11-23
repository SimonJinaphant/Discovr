package org.cpen321.discovr;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * This throws an error, but the template is complete (ish)
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = "app/src/main/AndroidManifest.xml")
public class EventsFragmentTest {

    @Before
    public void setUp() {

    }

    @Test
    public void testOpenAllEventsFragment() {
        //EventsFragment frag = new EventsFragment();
        //SupportFragmentTestUtil.startFragment(frag, MainActivity.class);
    }
}