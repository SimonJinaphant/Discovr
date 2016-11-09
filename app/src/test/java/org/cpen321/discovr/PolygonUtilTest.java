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
            new LatLng(-123.252354,49.269207)
            ,new LatLng(-123.252226,49.269062)
            ,new LatLng(-123.252461,49.26898)
            ,new LatLng(-123.252332,49.268822)
            ,new LatLng(-123.252358,49.268812)
            ,new LatLng(-123.252381,49.268834)
            ,new LatLng(-123.252431,49.26882)
            ,new LatLng(-123.252543,49.268951)
            ,new LatLng(-123.252685,49.268899)
            ,new LatLng(-123.252807,49.269045)

    };

    @Test
    public void inPolygon() throws Exception {
        LatLng p1 = new LatLng(-123.252498, 49.269066);
        assertEquals(true, PolygonUtil.pointInPolygon(p1, polygon));

        LatLng p2 = new LatLng(-123.252367, 49.269171);
        assertEquals(true, PolygonUtil.pointInPolygon(p2, polygon));
    }

    @Test
    public void notInPolygon(){
        LatLng p1 = new LatLng(-123.252933, 49.266229);
        assertEquals(false, PolygonUtil.pointInPolygon(p1, polygon));

        LatLng p2 = new LatLng(-123.252707, 49.266901);
        assertEquals(false, PolygonUtil.pointInPolygon(p2, polygon));
    }
}
