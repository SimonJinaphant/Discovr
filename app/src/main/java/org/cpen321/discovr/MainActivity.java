package org.cpen321.discovr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.exceptions.InvalidAccessTokenException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private static final int REQUEST_ALL_MAPBOX_PERMISSIONS = 3211;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Refactor permissions code
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
        };

        for (int i = 0; i < permissions.length; i++) {
            int hasFineLocation = ActivityCompat.checkSelfPermission(this, permissions[i]);
            if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, REQUEST_ALL_MAPBOX_PERMISSIONS);
            }
        }

        try {
            // Only this method throws an exception for invalid token
            MapboxAccountManager.validateAccessToken(getString(R.string.mapbox_key));

            // Must present API key BEFORE calling setContentView() on any view containing MapView
            // This should save you 3+ hours of debugging why your valid API key isn't working...
            MapboxAccountManager.start(this, getString(R.string.mapbox_key));

        } catch (InvalidAccessTokenException e) {
            System.err.println("Invalid access token: " + e);
        }

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Load up zoom-in animation for the central UBC Vancouver campus
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mapboxMap.getCameraPosition().target)
                        .bearing(mapboxMap.getCameraPosition().bearing)
                        .tilt(50)
                        .zoom(16)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
            }

        });
    }

    /**
     * Generate a polygon outline of the all the engineering buildings.
     * @param map Map to draw upon.
     */
    void outlineEngineeringLocations(MapboxMap map){
        ArrayList<LatLng> points = new ArrayList<>();
        points.add(new LatLng(49.262667, -123.250605));
        points.add(new LatLng(49.262828, -123.250047));
        points.add(new LatLng(49.262520, -123.249789));
        points.add(new LatLng(49.262975, -123.248416));
        points.add(new LatLng(49.262530, -123.248067));
        points.add(new LatLng(49.262855, -123.246844));
        points.add(new LatLng(49.262383, -123.246490));
        points.add(new LatLng(49.262064, -123.247418));
        points.add(new LatLng(49.262288, -123.247628));
        points.add(new LatLng(49.261851, -123.248910));
        points.add(new LatLng(49.261690, -123.248808));
        points.add(new LatLng(49.261420, -123.249580));

        map.addPolygon(new PolygonOptions()
                .addAll(points)
                .alpha(0.35f)
                .strokeColor(Color.parseColor("#000000"))
                .fillColor(Color.parseColor("#3bb2d0"))
        );

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(49.262330, -123.248738))
                .bearing(0)
                .tilt(50)
                .zoom(16)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
    }

    /**
     * Add a custom pin to the Tim Hortons at Forestry.
     * @param map Map to draw upon.
     */
    void locateTimHortons(MapboxMap map){
        MarkerViewOptions timHortons = new MarkerViewOptions()
                .position(new LatLng(49.260131, -123.248534))
                .title("Forestry Tim Hortons")
                .snippet("Where the line-up never gets short :(");

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(timHortons.getPosition())
                .bearing(270)
                .tilt(50)
                .zoom(17)
                .build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);
        map.addMarker(timHortons);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case R.id.polygon_action:
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {
                        outlineEngineeringLocations(mapboxMap);
                    }
                });
                break;
            case R.id.locate_action:
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {
                        locateTimHortons(mapboxMap);
                    }
                });
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
