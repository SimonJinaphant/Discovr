package org.cpen321.discovr;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.exceptions.InvalidAccessTokenException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.models.Position;

import org.cpen321.discovr.model.Building;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    final int ALLEVENTS = 0;
    final int SUBSCRIBEDEVENTS = 1;

    //Made these global as per tutorial, can be made local (?)
    NavigationView navigationView = null;
    Toolbar toolbar = null;
    MapViewFragment mapFragment;

    //Building information JSON inputstream for searching
    InputStream is;

    private List<EventInfo> AllEventsList = new ArrayList<EventInfo>();
    private static final int REQUEST_ALL_MAPBOX_PERMISSIONS = 3211;

    @Override
    protected void onCreate(Bundle savedInstanceState) {// Get the SearchView and set the searchable configuration
        super.onCreate(savedInstanceState);

        //Inflate the container
        setContentView(R.layout.activity_main);

        //Preparing app functionalities
        setUpEventsClient();
        obtainPermissions();



        if (savedInstanceState == null) {
            // Create fragment
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mapFragment = new MapViewFragment();
            initializeBuildingPolygons();
            // Add map fragment to parent container
            transaction.add(R.id.fragment_container, mapFragment, "com.mapbox.map");
            transaction.commit();
        } else {
            mapFragment = (MapViewFragment) getSupportFragmentManager().findFragmentByTag("com.mapbox.map");
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create navigation drawer
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }


        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Search handler to exist on onCreate
        handleIntent(getIntent());

        //Access the building JSON file and initialize input stream
        initInputStream();


    }

    /**
     * Initializes the input stream for searching
     */
    void initInputStream(){
        AssetManager am = getAssets();
        try {
            is = am.open("buildings.geojson");
        } catch (IOException e){
            e.printStackTrace();
            Log.d("buildings", "Cannot open file properly");
        }

    }

    /**
     * Obtains necessary permission for mapbox
     */
    void obtainPermissions(){
        // TODO: Refactor permission code
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
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

    }

    /**
     * Populates the map with the polygons
     */
    private void initializeBuildingPolygons() {
        // Load the buildings from the buildings.geojson file and draw a polygon outline for each building.
        try {
            List<Building> buildings = GeoJsonParser.parseBuildings(getResources().getAssets().open("buildings.geojson"));
            mapFragment.setBuildings(buildings);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    /**
     * Sets up the client for getting event information from the events database
     */
    void setUpEventsClient(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://discovrweb.azurewebsites.net/api/Events", new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                // called before request is started
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                String r = new String(response);
                try {
                    JSONArray json = new JSONArray(r);
                    for(int i = 0; i < json.length(); i++){
                        JSONObject o = json.getJSONObject(i);
                        AllEventsList.add(new EventInfo(o.getInt("Id"),
                                o.getString("Name"),
                                o.getString("Host"),
                                o.getString("Location"),
                                o.getString("StartTime"),
                                o.getString("EndTime"),
                                "",
                                o.getString("Description")));
                    }
                }
                catch (JSONException e){
                    throw new RuntimeException(e);
                }
                System.out.println(r);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                System.out.println(":(");
            }
            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
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

        //Creates the searchbar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
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
                        //Search is submitted
                        Log.d("search", "Text submitted: " + query);
                        try {
                            //Workaround the "refresh" the input stream
                            is.mark(Integer.MAX_VALUE);
                            double[] coords = GeoJsonParser.getCoordinates(query, is); //obtains coordinates from query
                            is.reset();

                            //Failed to return values
                            if (coords.length < 1){
                                return false;
                            }

                            Log.d("search", "coords size: " + coords.length + " latlng: = " + coords[0] + " " +coords[1]);
                            LatLng loc = new LatLng(coords[1], coords[0]);

                            //Creates a marker on the queried location
                            mapFragment.movePointOfInterestMarker(loc);

                            //Moves the camera to focus on queried location
                            mapFragment.moveMapToLocation(loc);

                            //Determines a route from user position to the current location
                            Position destination = Position.fromCoordinates(loc.getLongitude(), loc.getLatitude());

                            Location userLoc = mapFragment.getUserLocation();
                            if (userLoc == null){
                                Log.d("search", "User location not found, route not calculated");
                                return false;
                            }
                            Position origin = Position.fromCoordinates(userLoc.getLongitude(), userLoc.getLatitude());
                            try{
                                mapFragment.getRoute(origin, destination);
                            } catch (ServicesException servicesException) {
                                servicesException.printStackTrace();
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                            return false;
                        }

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
     * Overriden to handle drawer opening and closing as well as handling
     * navigation item selection on backpress
     */
    @Override
    public void onBackPressed() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment currentFragment = manager.findFragmentById(R.id.fragment_container);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        drawerFragmentManager(id);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Takes care of hiding and switching of fragments
     * @param fragmentID the ID of the fragment selected
     */
    private void drawerFragmentManager(int fragmentID){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> all_frag = fm.getFragments();

        //Remove all the fragments but the map fragment
        ListIterator<Fragment> li = all_frag.listIterator();
        while (li.hasNext()){
            Fragment currFrag = li.next();
            if ((currFrag != null) && (!currFrag.equals(mapFragment))){
                ft.remove(currFrag);
            }
        }
        Log.d("backstack", "Destroying backstack of size: " + fm.getBackStackEntryCount());
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        Log.d("backstack", "Backstack size: " + fm.getBackStackEntryCount());

        //Adds a fragment to the container and changes the toolbar title correspondingly
        switch (fragmentID) {
            case R.id.map_view:
                getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                break;
            case R.id.events_subscribed:
                ft.add(R.id.fragment_container, new EventsSubscribedFragment(), getResources().getString(R.string.events_sub_tag));
                getSupportActionBar().setTitle(getResources().getString(R.string.events_subscribed));
                break;
            case R.id.events_nearby:
                getSupportActionBar().setTitle(getResources().getString(R.string.events_nearby));
                break;
            case R.id.events_all:
                ft.add(R.id.fragment_container, new AllEventsFragment(), getResources().getString(R.string.all_events_tag));
                getSupportActionBar().setTitle(getResources().getString((R.string.events_all)));
                break;
            case R.id.test_frag:
                ft.add(R.id.fragment_container, new BlankFragment(), "test fragment");
                getSupportActionBar().setTitle("Testing Fragment");
                break;
            case R.id.courses_frag:
                ft.add(R.id.fragment_container, new CoursesFragment(), getResources().getString(R.string.courses));
                getSupportActionBar().setTitle(getResources().getString(R.string.courses));
                break;
            default:
                break;
        }

        ft.commit();
    }

    List<EventInfo> getAllEvents(){
        return this.AllEventsList;
    }
}