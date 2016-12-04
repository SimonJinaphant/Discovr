package org.cpen321.discovr;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.exceptions.InvalidAccessTokenException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.models.Position;

import org.cpen321.discovr.fragment.EventsFragment;
import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.Course;
import org.cpen321.discovr.model.EventInfo;
import org.cpen321.discovr.model.MapPolygon;
import org.cpen321.discovr.model.MapTransitStation;
import org.cpen321.discovr.fragment.CoursesFragment;
import org.cpen321.discovr.fragment.EventsSubscribedFragment;
import org.cpen321.discovr.fragment.MapViewFragment;
import org.cpen321.discovr.fragment.partial.BuildingPartialFragment;
import org.cpen321.discovr.parser.GeojsonFileParser;
import org.cpen321.discovr.utility.IconUtil;
import org.cpen321.discovr.utility.AlertUtil;
import org.cpen321.discovr.utility.PolygonUtil;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ListIterator;

import static org.cpen321.discovr.parser.CalendarFileParser.loadUserCourses;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_ALL_MAPBOX_PERMISSIONS = 3211;
    final int ALLEVENTS = 0;
    final int SUBSCRIBEDEVENTS = 1;
    SQLiteDBHandler dbh = new SQLiteDBHandler(this);
    MapViewFragment mapFragment;
    EventClientManager ecm;
    private List<EventInfo> AllEventsList = new ArrayList<EventInfo>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {// Get the SearchView and set the searchable configuration
        super.onCreate(savedInstanceState);

        //Inflate the container
        setContentView(R.layout.activity_main);

        //Preparing app functionalities
        obtainPermissions();

        //Setting up the client manager
        ecm = new EventClientManager();

        if (savedInstanceState == null) {
            // Create fragment
            final FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            mapFragment = new MapViewFragment();
            initializeBuildingPolygons();
            initializeTransitStation();
            initializeConstructionZones();
            // Add map fragment to parent container
            transaction.add(R.id.fragment_container, mapFragment, "com.mapbox.map");
            transaction.commit();
        } else {
            mapFragment = (MapViewFragment) getSupportFragmentManager().findFragmentByTag("com.mapbox.map");
        }

        //Create navigation drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.app_name, R.string.app_name) {
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //bring up safewalk notification
        safewalkNotifier();
        List<Course> cl = new ArrayList<>();
        List<Course> courseSelected = new ArrayList<>();

        //read from local ical file
        try {
            cl = readCourses();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //select courses for the current term
        courseSelected = courseSelector(cl);

        //bring up the course notification
        try {
            courseNotifier(courseSelected);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtains necessary permission for mapbox
     */
    void obtainPermissions() {
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
     * Wrapper function for the Event Client Manager
     *
     * @return
     */
    public List<EventInfo> getAllEvents() {
        ecm.updateEventsList();
        return ecm.getAllEvents();
    }

    /**
     * Populates the map with the polygons
     */
    private void initializeBuildingPolygons() {
        // Load the buildings from the buildings.geojson file and draw a polygon outline for each building.
        try {
            List<Building> buildings = GeojsonFileParser.parseBuildings(getResources().getAssets().open("simplebuildings.geojson"));
            mapFragment.setBuildings(buildings);
            if(dbh.getBuildingCount() == 0){
                for (Building bldg : buildings) {
                    dbh.addBuilding(bldg);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeTransitStation() {
        try {
            List<MapTransitStation> stations = GeojsonFileParser.parseTransitStations(getResources().getAssets().open("transitstations.geojson"));
            mapFragment.setTransitStation(stations);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeConstructionZones() {
        try {
            List<MapPolygon> constructions = GeojsonFileParser.parsePolygons(getResources().getAssets().open("construction.geojson"));
            mapFragment.setConstructionZones(constructions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check current time and show up the safewalk notification if necessary
     */
    private void safewalkNotifier() {
        Calendar c = Calendar.getInstance();
        //get current month: 0~11 -> Jan~Dec
        int month = c.get(Calendar.MONTH);
        //get current hour
        int hour = c.get(Calendar.HOUR_OF_DAY);
        //standard time
        if (month >= 10 || month < 2) {
            //after 21:00 pm
            if (hour >= 21) {
                new AlertDialog.Builder(this)
                        .setTitle("Don't walk alone after dark!")
                        .setMessage("Call Safewalk at (604) 822-5355")
                        .show();
            }

            //daylight saving time
        } else {
            //after 20:00 pm
            if (hour >= 20) {
                new AlertDialog.Builder(this)
                        .setTitle("Don't walk alone after dark!")
                        .setMessage("Call Safewalk at (604) 822-5355")
                        .show();
            }
        }
    }

    /**
     * Read from local ical files if no courses have ever been loaded to the local database
     */
    private List<Course> readCourses() throws ParseException {
        SQLiteDBHandler dbh = new SQLiteDBHandler(this);
        List<Course> courses = new ArrayList<>();

        try {
            //check if the ical file is already loaded to the app
            List<Course> flag = dbh.getAllCourses();
            //if nothing in it, then load ical file from the download folder
            if (flag.isEmpty()) {
                //load from local ical files
                List<Course> rawCourses = loadUserCourses();
                //Deal with the course duplicates here
                List<Course> myCourses = removeDuplicates(rawCourses);
                //add courses to the local database
                dbh.addCourses(myCourses);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        courses = dbh.getAllCourses();

        return courses;
    }

    /**
     * Remove the duplicates before storing courses to the local database
     */
    private List<Course> removeDuplicates(List<Course> rawCourses) {

        if (rawCourses == null) return null;

        List<Course> result = new ArrayList<>();

        for (int i = 0; i < rawCourses.size(); i++) {
            String rTitle = rawCourses.get(i).getCategory() + rawCourses.get(i).getNumber() + rawCourses.get(i).getSection();
            label:
            if (result.isEmpty()) {
                result.add(rawCourses.get(i));
                //myStrings.add(rTitle);
            } else {
                for (int j = 0; j < result.size(); j++) {
                    String nTitle = result.get(j).getCategory() + result.get(j).getNumber() + result.get(j).getSection();
                    if (rTitle.equals(nTitle)) {
                        result.get(j).setDayOfWeek(result.get(j).getDayOfWeek() + "/" + rawCourses.get(i).getDayOfWeek());
                        break label;
                    }
                }
                result.add(rawCourses.get(i));
            }
        }
        return result;
    }

    /**
     * Select courses for the current term
     */
    public static List<Course> courseSelector(List<Course> myList) {
        List<Course> result = new ArrayList<>();
        List<Course> courseTerm1 = new ArrayList<>();
        List<Course> courseTerm2 = new ArrayList<>();
        List<Course> courseTerm3 = new ArrayList<>();

        for(final Course course : myList){
            if(course.getEndDate().contains("Nov") || course.getEndDate().contains("Dec")) {
                courseTerm1.add(course);
            }else if(course.getEndDate().contains("Apr") || course.getEndDate().contains("May")){
                courseTerm2.add(course);
            }else{
                courseTerm3.add(course);
            }
        }

        Calendar c = Calendar.getInstance();
        int month = c.get(Calendar.MONTH);
        Log.d("Get Current Month:  ", String.valueOf(month));

        if ( 0 <= month && month <= 4){
            result = courseTerm2;
        }else if( 8 <= month || month <= 11 ){
            result = courseTerm1;
        }else{
            result = courseTerm3;
        }
        return result;
    }

    private void courseNotifier(List<Course> courseSelected) throws ParseException {
        //check if there is any class in 10 mins
        if (AlertUtil.courseAlert(courseSelected) != null) {
            //get the course if there is any
            Course currCourse = AlertUtil.courseAlert(courseSelected);
            //show the alert message
            new AlertDialog.Builder(this)
                    //set alert title with the course name
                    .setTitle(currCourse.getCategory() + " " + currCourse.getNumber() + " " + currCourse.getSection() + " will start in 10 mins")
                    //set the message with the location
                    .setMessage(currCourse.getBuilding() + " " + currCourse.getRoom())
                    //show the alert
                    .show();
        }
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
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);

        //Creates the searchbar
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        Log.d("search", "Text changed to: " + newText);
                        //Try and perform autocomplete
                        return true;
                    }


                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //Search is submitted

                        // Closes the keyboard
                        InputMethodManager inputMethodManager = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                        Log.d("search", "Text submitted: " + query);
                        try {
                            LatLng loc = GeojsonFileParser.getCoordinates(dbh.getBuildingByCode(query).getAllCoordinates()); //obtains coordinates from query

                            //Failed to return values
                            if (loc == null) {
                                return false;
                            }

                            Log.d("search", loc.toString());
                            moveMap(loc, IconUtil.MarkerType.BUILDING);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return false;
                        }

                        return true;
                    }
                });
        return true;
    }

    public void moveMap(LatLng loc, IconUtil.MarkerType type) {
        //Creates a marker on the queried location
        mapFragment.movePointOfInterestMarker(loc, type);

        //Moves the camera to focus on queried location
        mapFragment.moveMapToLocation(loc);

        //Determines a route from user position to the current location
        Position destination = Position.fromCoordinates(loc.getLongitude(), loc.getLatitude());

        Location userLoc = mapFragment.getUserLocation();
        if (userLoc == null) {
            Log.d("search", "User location not found, route not calculated");
            return;
        }
        Position origin = Position.fromCoordinates(userLoc.getLongitude(), userLoc.getLatitude());
        try {
            mapFragment.getRoute(origin, destination);
        } catch (ServicesException servicesException) {
            servicesException.printStackTrace();
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
        } else if ((currentFragment instanceof MapViewFragment) && mapFragment.isMapDirty()) {
            mapFragment.removeRoute();
            mapFragment.removeAllMarkers();
        } else if (manager.getBackStackEntryCount() > 1){
            // Destroy stacks of fragments
            Log.d("backstack", "Destroying backstack of size: " + manager.getBackStackEntryCount());
            manager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            Log.d("backstack", "Backstack size: " + manager.getBackStackEntryCount());
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
     * Plots upcoming events on the map
     */
    private void plotUpcomingEventsOnMap() {
        // TODO: Replace getRawEvents() with getUpcomingEvents()
        List<EventInfo> upcomingEvents = ecm.getRawEvents();
        ListIterator<EventInfo> li = upcomingEvents.listIterator();
        List<LatLng> markerLoc = new ArrayList<>();

        // Get icon type for events
        Icon icon = IconFactory.getInstance(this).fromDrawable(ContextCompat.getDrawable(this, R.drawable.event_icon));

        while (li.hasNext()) {
            EventInfo event = li.next();
            Building bldg = dbh.getBuildingByCode(event.getBuildingName());
            if (bldg != null) {
                LatLng loc = GeojsonFileParser.getCoordinates(bldg.getAllCoordinates());
                //Prevents marker overlapping directly on top of one another
                while (markerLoc.contains(loc)) {
                    loc = PolygonUtil.fuzzLatLng(loc);
                }
                markerLoc.add(loc);
                mapFragment.addMarker(loc, IconUtil.MarkerType.EVENT).setTitle(String.valueOf(event.getID()));
                //Pass the creation of the event fragment to mapFragment (possible refactor)
                mapFragment.getMap().setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (marker.getTitle() != null) {
                            mapFragment.createEventPanel(Integer.parseInt(marker.getTitle()));
                            return true;
                        }
                        return false;
                    }
                });

            }
        }
    }


    /**
     * Getter for the event client manager
     *
     * @return the event client manager
     */
    public EventClientManager getEventClientManager() {
        return ecm;
    }


    /**
     * Takes care of hiding and switching of fragments
     *
     * @param fragmentID the ID of the fragment selected
     */
    public void drawerFragmentManager(int fragmentID) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        List<Fragment> all_frag = fm.getFragments();

        //Remove all the fragments but the map fragment
        ListIterator<Fragment> li = all_frag.listIterator();
        while (li.hasNext()) {
            Fragment currFrag = li.next();
            if ((currFrag != null) && (!currFrag.equals(mapFragment))) {
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
                ft.addToBackStack(null);
                getSupportActionBar().setTitle(getResources().getString(R.string.events_subscribed));
                break;
            case R.id.events_upcoming:
                getSupportActionBar().setTitle(getResources().getString(R.string.events_upcoming));
                mapFragment.removeAllMarkers();
                plotUpcomingEventsOnMap();
                break;
            case R.id.events_all:
                ft.add(R.id.fragment_container, new EventsFragment(), getResources().getString(R.string.all_events_tag));
                ft.addToBackStack(null);
                getSupportActionBar().setTitle(getResources().getString((R.string.events_all)));
                break;
            case R.id.transit_display:
                mapFragment.removeAllMarkers();
                mapFragment.displayTransitStation();
                getSupportActionBar().setTitle(getResources().getString(R.string.transit));
                break;
            case R.id.courses_frag:
                ft.add(R.id.fragment_container, new CoursesFragment(), getResources().getString(R.string.courses));
                ft.addToBackStack(null);
                getSupportActionBar().setTitle(getResources().getString(R.string.courses));
                break;
            default:
                break;
        }

        ft.commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Handle intent when suggestion is selected from search
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            String[] s = uri.split("/");

            //find buildling being selected with the dataString passed
            Building b = dbh.getBuildingByID(Integer.valueOf(s[s.length - 1]));
            BuildingPartialFragment buildingFrag = new BuildingPartialFragment();
            buildingFrag.setBuilding(b);

            //Move map to the building location
            LatLng loc = GeojsonFileParser.getCoordinates(b.getAllCoordinates()); //obtains coordinates from query

            //Check for null loc
            if (loc != null) {
                moveMap(loc, IconUtil.MarkerType.BUILDING);
            }

            //Open new singleBuilding fragment
            FragmentManager fm = this.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(R.id.fragment_container, buildingFrag);
            ft.addToBackStack(null);
            ft.commit();
        }
    }
}