package org.cpen321.discovr;

import com.mapbox.mapboxsdk.geometry.LatLng;

import org.cpen321.discovr.utility.PolygonUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Simon Jinaphant on 09-Nov-2016.
 */

public class PolygonUtilTest {

    LatLng[] polygon = {
            new LatLng(49.269207, -123.252354),
            new LatLng(49.269062, -123.252226),
            new LatLng(49.26898, -123.252461),
            new LatLng(49.268822, -123.252332),
            new LatLng(49.268812, -123.252358),
            new LatLng(49.268834, -123.252381),
            new LatLng(49.26882, -123.252431),
            new LatLng(49.268951, -123.252543),
            new LatLng(49.268899, -123.252685),
            new LatLng(49.269045, -123.252807)
    };

    @Test
    public void inPolygon() throws Exception {
        LatLng p1 = new LatLng(49.269066, -123.252498);
        assertEquals(true, PolygonUtil.pointInPolygon(p1, polygon));

        LatLng p2 = new LatLng(49.269171, -123.252367);
        assertEquals(true, PolygonUtil.pointInPolygon(p2, polygon));
    }

    @Test
    public void notInPolygon() {
        LatLng p1 = new LatLng(49.266229, -123.252933);
        assertEquals(false, PolygonUtil.pointInPolygon(p1, polygon));

        LatLng p2 = new LatLng(49.266901, -123.252707);
        assertEquals(false, PolygonUtil.pointInPolygon(p2, polygon));
    }
}
