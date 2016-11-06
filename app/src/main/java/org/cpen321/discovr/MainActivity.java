package org.cpen321.discovr;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import com.mapbox.mapboxsdk.MapboxAccountManager;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.exceptions.InvalidAccessTokenException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.SupportMapFragment;
import com.mapbox.services.android.geocoder.ui.GeocoderAutoCompleteView;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.geocoding.v5.GeocodingCriteria;
import com.mapbox.services.geocoding.v5.MapboxGeocoding;
import com.mapbox.services.geocoding.v5.models.CarmenFeature;
import com.mapbox.services.geocoding.v5.models.GeocodingResponse;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //Made these global as per tutorial, can be made local (?)
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    SupportMapFragment mapFragment;

    //Location Variables
    Location userLocation;
    LocationServices locationServices;
    FloatingActionButton fab;

    //Reference to map
    MapboxMap map;

    Marker userPositionMarker;
    Marker pointOfInterestMarker;

    private static final int REQUEST_ALL_MAPBOX_PERMISSIONS = 3211;

    @Override
    protected void onCreate(Bundle savedInstanceState) {// Get the SearchView and set the searchable configuration
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: Refactor permission code
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
        };

        for (int i = 0; i < permissions.length; i++) {
            int hasFineLocation = ActivityCompat.checkSelfPermission(this, permissions[i]);
            if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{permissions[i]}, REQUEST_ALL_MAPBOX_PERMISSIONS);
            }
        }
        locationServices = LocationServices.getLocationServices(this);

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
            options.styleUrl("mapbox://styles/sansnickel/ciuhw415o001k2iqo1mnjtjj2");
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
                map = mapboxMap;

                //Initial map location on campus
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mapboxMap.getCameraPosition().target)
                        .bearing(mapboxMap.getCameraPosition().bearing)
                        .tilt(50)
                        .zoom(16)
                        .build();

                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000, null);


                //Get user location and [enable user location layer (BUGGED)]
                userLocation = locationServices.getLastLocation();
                Log.d("location", "location enabled is being set to true");
                map.setMyLocationEnabled(true); //TODO: find out why this function won't work
                map.getMyLocationViewSettings().setForegroundTintColor(Color.parseColor("#FFB6C1"));
                locationServices.addLocationListener(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        if (location != null) {
                            Log.d("location", "User location has changed to: " + location.toString());
                            userLocation = location;
                        }
                    }
                });

            }
        });



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Button to fucus on user locatoin
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Log.d("location fab", "fab clicked");
                                       moveMapToLocation(new LatLng(userLocation));
                                   }
                               });

        //Create navigation drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Search handler to exist on onCreate
        handleIntent(getIntent());



    }

    /**
     * Moves the map to user location as well as adding a marker on that position
     */
    private void cameraToUser(){
        if (userPositionMarker == null){
            MarkerViewOptions marker = new MarkerViewOptions().position(new LatLng(userLocation));
            userPositionMarker = map.addMarker(marker);
        } else {
            userPositionMarker.setPosition(new LatLng(userLocation));
        }
        CameraPosition position = new CameraPosition.Builder().target(new LatLng(userLocation)).zoom(17).tilt(30).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Overriden to create a search bar on the top toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setIconifiedByDefault(false);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText){
                        Log.d("search", "Text changed to: " + newText);
                        //Try and perform autocomplete
                        return true;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query){
                        Log.d("search", "Text submitted: " + query);
                        /*
                        Geocoder gc = new Geocoder(getBaseContext(), Locale.getDefault());

                        try {
                            List<Address> add = gc.getFromLocationName(query, 5);
                            if (!add.isEmpty()){
                                Log.d("search", "Size of results: " + add.size());
                                for (int i = 0; i < add.size(); i++)
                                    Log.d("search", add.get(i).getCountryName());
                                Address a = add.get(0);
                                if (pointOfInterestMarker == null) {
                                    if (a.hasLatitude() && a.hasLongitude()) {
                                        Log.d("search", "search contains lat and long");
                                        MarkerViewOptions marker = new MarkerViewOptions().position(new LatLng(a.getLatitude(), a.getLongitude()));
                                        pointOfInterestMarker = map.addMarker(marker);
                                    }
                                } else {
                                    pointOfInterestMarker.setPosition(new LatLng(a.getLatitude(), a.getLongitude()));
                                }
                                CameraPosition position = new CameraPosition.Builder().target(new LatLng(a.getLatitude(), a.getLongitude())).zoom(17).tilt(30).build();
                                Log.d("location", position.toString());
                                map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);


                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        */
                        return true;
                    }
                });
        return true;
    }


    /*
        We might not need onNewIntent or handleIntent if we can handle
        the map search within the onQueryTexListener class on the
        onCreateOptionsMenu() method. If we decide to go down that path delete
        the onNewIntent() and handleIntent() methods below as well as the call
        to handleIntent() within the method onCreate()
     */
    @Override
    protected void onNewIntent(Intent intent){
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent){
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("search", "Search intent with: " + query);
            //Do something with query such as searching for it in database
        }
    }



    /**
     * Moves the map to user location as well as adding a marker on that position
     */
    private void moveMapToLocation(LatLng loc){
        Log.d("location", "moving map");
        if (userLocation != null) {
            if (userPositionMarker == null) {
                MarkerViewOptions marker = new MarkerViewOptions().position(loc);
                userPositionMarker = map.addMarker(marker);
            } else {
                userPositionMarker.setPosition(loc);
            }
            CameraPosition position = new CameraPosition.Builder().target(loc).zoom(17).tilt(30).build();
            Log.d("location", position.toString());
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
        }
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
            getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
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
            Log.d("events_sub", "commited the fragment");
            getSupportActionBar().setTitle(getResources().getString(R.string.events_subscribed));

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
            Log.d("events_create", "commited the fragment");
            getSupportActionBar().setTitle(getResources().getString((R.string.events_create)));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}