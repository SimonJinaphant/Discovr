package org.cpen321.discovr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.exceptions.InvalidAccessTokenException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;

import org.cpen321.discovr.parser.CalendarFileParser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Made these global as per tutorial, can be made local (?)
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    SupportMapFragment mapFragment;


    private static final int REQUEST_ALL_MAPBOX_PERMISSIONS = 3211;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Refactor permission code
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        for (int i = 0; i < permissions.length; i++) {
            int hasFineLocation = ActivityCompat.checkSelfPermission(this, permissions[i]);
            if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, REQUEST_ALL_MAPBOX_PERMISSIONS);
            }
        }

        //Initialize mapbox variables
        try {

            // Only this method throws an exception for invalid token
            MapboxAccountManager.validateAccessToken(getString(R.string.mapbox_key));

            // Must present API key BEFORE calling setContentView() on any view containing MapView
            // This should save you 3+ hours of debugging why your valid API key isn't working...
            MapboxAccountManager.start(this, getString(R.string.mapbox_key));
        } catch (InvalidAccessTokenException e) {
            System.err.println("Invalid access token: " + e);
        }



        if (savedInstanceState == null) {

            // Create fragment
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            LatLng patagonia = new LatLng(49.262330, -123.248738);

            // Build mapboxMap
            MapboxMapOptions options = new MapboxMapOptions();
            options.styleUrl(Style.MAPBOX_STREETS);
            options.camera(new CameraPosition.Builder()
                    .target(patagonia)
                    .zoom(9)
                    .build());

            // Create map fragment
            mapFragment = SupportMapFragment.newInstance(options);

            // Add map fragment to parent container
            transaction.add(R.id.fragment_container, mapFragment, "com.mapbox.map");
            transaction.commit();
        } else {
            mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentByTag("com.mapbox.map");
        }


        mapFragment.getMapAsync(new OnMapReadyCallback() {
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

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    /**
     * Overriden to handle drawer opening and closing as well as handling
     * navigation item selection on backpress
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager manager = getSupportFragmentManager();
            if (manager.getBackStackEntryCount() > 0){
                Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
                if (currentFragment instanceof SupportMapFragment){
                    navigationView.getMenu().getItem(0).setChecked(true);
                } else if (currentFragment instanceof EventsSubscribedFragment){
                    navigationView.getMenu().getItem(2).setChecked(true);
                } else if (currentFragment instanceof EventCreateFragment) {
                    navigationView.getMenu().getItem(4).setChecked(true);
                }
                manager.popBackStack();
            } else {
                super.onBackPressed();
            }

        }
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

    /**
     * Prints the tag of all fragments in this list
     * List might contain null elements
     * @param fraglist the list containing the fragments
     */
    public void printFragmentNames(List<Fragment> fraglist){
        ListIterator<Fragment> list_it = fraglist.listIterator();
        while (list_it.hasNext()){
            Fragment curr_frag = list_it.next();
            if (curr_frag != null) {
                String tag = curr_frag.getTag();
                if (tag != null)
                    Log.d("event_frag_list", tag);
                else
                    Log.d("event_frag_list", "null tag of frag: " + curr_frag.toString());
            }
        }
    }

    private void fragmentDisplayManager(List<Fragment> fragList, Fragment shownFragment, FragmentTransaction ft){
        ListIterator<Fragment> li = fragList.listIterator();
        while (li.hasNext()){
            Fragment currFrag = li.next();
            if ((currFrag != null) && (!currFrag.equals(shownFragment))){
                ft.hide(currFrag);
            }
        }
        ft.show(shownFragment);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.map_view) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> all_frag = fm.getFragments();
            ListIterator<Fragment> li = all_frag.listIterator();
            while (li.hasNext()){
                Fragment currFrag = li.next();
                if ((currFrag != null) && (!currFrag.equals(mapFragment))){
                    ft.remove(currFrag);
                }
            }
            //ft.addToBackStack(null);
            ft.commit();

        } else if (id == R.id.events_subscribed) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> all_frag = fm.getFragments();
            ListIterator<Fragment> li = all_frag.listIterator();
            while (li.hasNext()){
                Fragment currFrag = li.next();
                if ((currFrag != null) && (!currFrag.equals(mapFragment))){
                    ft.remove(currFrag);
                }
            }
            ft.add(R.id.fragment_container, new EventsSubscribedFragment(), getResources().getString(R.string.events_sub_tag));
            ft.commit();
        } else if (id == R.id.events_nearby) {

        } else if (id == R.id.events_all) {

        } else if (id == R.id.events_create) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            List<Fragment> all_frag = fm.getFragments();
            ListIterator<Fragment> li = all_frag.listIterator();
            while (li.hasNext()){
                Fragment currFrag = li.next();
                if ((currFrag != null) && (!currFrag.equals(mapFragment))){
                    ft.remove(currFrag);
                }
            }
            ft.add(R.id.fragment_container, new EventCreateFragment(), getResources().getString(R.string.events_create_tag));
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}