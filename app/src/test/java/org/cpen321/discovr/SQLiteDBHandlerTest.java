package org.cpen321.discovr;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

/**
 * Created by lerir on 2016-11-21.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=23)
public class SQLiteDBHandlerTest {

    SQLiteDBHandler dbh;

    @Before
    public void setUp(){
        dbh = new SQLiteDBHandler(RuntimeEnvironment.application);
    }

    @Test
    public void testExistence(){
        assertNotNull(dbh);
    }


    @Test
    public void testAddEvent(){
        assertEquals(0, dbh.getEventCount());
    }


}