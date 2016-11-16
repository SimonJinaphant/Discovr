package org.cpen321.discovr;


import android.os.Bundle;
import android.os.PersistableBundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

/**
 * Created by valerian on 11/15/16.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = "app/src/main/AndroidManifest.xml")
public class AllEventsFragmentTest {

    @Before
    public void setUp(){

    }

    @Test
    public void testOpenAllEventsFragment() {
        AllEventsFragment frag = new AllEventsFragment();
        SupportFragmentTestUtil.startFragment(frag, MainActivity.class);
    }
}