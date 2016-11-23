package org.cpen321.discovr.utility;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.random;

/**
 * Created by Simon Jinaphant on 09-Nov-2016.
 */

public class PolygonUtil {

    /**
     * Fuzzes the latlong to a nearby point
     *
     * @param input
     * @return
     */
    public static LatLng fuzzLatLng(LatLng input) {
        double lat = input.getLatitude();
        double lon = input.getLongitude();
        lat += random() * 0.0001;
        lon += random() * 0.0001;
        return new LatLng(lat, lon);
    }

    private static boolean isZero(double value, double threshold) {
        return value >= -threshold && value <= threshold;
    }

    private static int orientation(LatLng p1, LatLng p2, LatLng q1) {
        double val = (p2.getLongitude() - p1.getLongitude()) * (q1.getLatitude() - p2.getLatitude())
                - (q1.getLongitude() - p2.getLongitude()) * (p2.getLatitude() - p1.getLatitude());
        if (isZero(val, 0.0000001))
            return 0;
        else
            return (val < 0) ? -1 : 1;
    }

    private static boolean onSegment(LatLng p1, LatLng p2, LatLng q) {
        if (min(p1.getLatitude(), p2.getLatitude()) <= q.getLatitude()
                && q.getLatitude() <= max(p1.getLatitude(), p2.getLatitude())
                && min(p1.getLongitude(), p2.getLongitude()) <= q.getLongitude()
                && q.getLongitude() <= max(p1.getLongitude(), p2.getLongitude()))
            return true;
        else
            return false;
    }

    private static boolean intersectionTest(LatLng p1, LatLng p2, LatLng p3, LatLng p4) {
        int o1 = orientation(p1, p2, p3);
        int o2 = orientation(p1, p2, p4);
        int o3 = orientation(p3, p4, p1);
        int o4 = orientation(p3, p4, p2);

        // General case
        if (o1 != o2 && o3 != o4)
            return true;

        // Special cases
        if (o1 == 0 && onSegment(p1, p2, p3))
            return true;
        if (o2 == 0 && onSegment(p1, p2, p4))
            return true;
        if (o3 == 0 && onSegment(p3, p4, p1))
            return true;
        if (o4 == 0 && onSegment(p3, p4, p2))
            return true;

        return false;
    }

    public static boolean pointInPolygon(LatLng p, LatLng[] polygon) {

        if (polygon.length < 3) {
            return false; // Flawed polygon
        }
        LatLng PtoInfinity = new LatLng(Long.MAX_VALUE, p.getLongitude());

        int intersectionsCount = 0;
        int i = 0, j = i + 1;
        // Same Y coordinate points have to be counted once
        Set<LatLng> sameYcoordPoints = new HashSet<>();
        do {

            if (intersectionTest(p, PtoInfinity, polygon[i], polygon[j]) == true) {

                boolean invalidIntersection = false;
                if (p.getLongitude() == polygon[i].getLongitude() || p.getLongitude() == polygon[j].getLongitude()) {

                    boolean res = sameYcoordPoints.contains(polygon[i]);
                    // Possible collision
                    if (res) {
                        invalidIntersection = true;
                    }
                    res = sameYcoordPoints.contains(polygon[j]);
                    // Possible collision
                    if (res) {
                        invalidIntersection = true;
                    }
                    if (p.getLongitude() == polygon[i].getLongitude())
                        sameYcoordPoints.add(polygon[i]);
                    else if (p.getLongitude() == polygon[j].getLongitude())
                        sameYcoordPoints.add(polygon[j]);
                }

                if (!invalidIntersection) {

                    ++intersectionsCount;

                    if (orientation(polygon[i], polygon[j], p) == 0) { // Collinear
                        if (onSegment(polygon[i], polygon[j], p) == true)
                            return true;
                        else {
                            // Exception case when point is collinear but not on segment
                            // e.g.
                            //           *  ************
                            //             /            \
                            //            k              w
                            // The collinear segment is worth 0 if k and w have the same
                            // vertical direction

                            int k = (((i - 1) >= 0) ? // Negative wraparound
                                    (i - 1) % (polygon.length) : (polygon.length) + (i - 1));
                            int w = ((j + 1) % polygon.length);

                            if ((polygon[k].getLongitude() <= polygon[i].getLongitude() && polygon[w].getLongitude() <= polygon[j].getLongitude())
                                    || (polygon[k].getLongitude() >= polygon[i].getLongitude() && polygon[w].getLongitude() >= polygon[j].getLongitude()))
                                --intersectionsCount;
                        }
                    }
                }
            }

            i = ((i + 1) % polygon.length);
            j = ((j + 1) % polygon.length);

        } while (i != 0);

        return (intersectionsCount % 2 != 0);
    }

}
