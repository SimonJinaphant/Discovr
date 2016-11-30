package org.cpen321.discovr.fragment;


import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerViewOptions;
import com.mapbox.mapboxsdk.annotations.PolygonOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import org.cpen321.discovr.EventClientManager;
import org.cpen321.discovr.MainActivity;
import org.cpen321.discovr.R;
import org.cpen321.discovr.model.Building;
import org.cpen321.discovr.model.EventInfo;
import org.cpen321.discovr.model.MapPolygon;
import org.cpen321.discovr.model.MapTransitStation;
import org.cpen321.discovr.fragment.partial.BuildingPartialFragment;
import org.cpen321.discovr.fragment.partial.EventPartialFragment;
import org.cpen321.discovr.fragment.partial.TransitPartialFragment;
import org.cpen321.discovr.utility.PolygonUtil;

import java.util.List;
import java.util.ListIterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The MapViewFragment contains the MapView and relays actions taken on the map itself.
 * If possible, use these member functions rather than modifying the map yourself.
 */
public class MapViewFragment extends Fragment {

    MapboxMap map;
    MapView mapView;
    Marker userPositionMarker;
    Marker pointOfInterestMarker;
    LocationServices locationServices;
    Location userLocation;
    Polyline routeLine;
    List<Building> buildings;
    List<MapTransitStation> stations;
    List<MapPolygon> constructions;

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("location", "Initializing location services");
        locationServices = LocationServices.getLocationServices(getContext());
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout view = (FrameLayout) inflater.inflate(R.layout.fragment_map_view, container, false);
        //Creates and initializes the map view
        mapView = (MapView) view.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.setStyleUrl("mapbox://styles/sansnickel/ciuhw415o001k2iqo1mnjtjj2");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(49.262330, -123.248738))
                        .bearing(0.0)
                        .tilt(50)
                        .zoom(16)
                        .build();

                map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 3000);
                map.setMyLocationEnabled(true);
                //Polygon callback on touch
                // Implement an onclick which cycles through all buildings and attempt to match a single LatLng point
                // into a polygon based on the above algorithm.
                map.setOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng point) {
                        onLongClickMapCallback(point);
                    }
                });

                for (MapTransitStation station : stations) {
                    map.addMarker(new MarkerViewOptions()
                            .position(station.location)
                            .title("[" + station.identifier + "]" + station.name)
                    );
                }

                for (MapPolygon constructionZone : constructions) {
                    map.addPolygon(new PolygonOptions()
                            .addAll(constructionZone.vertices)
                            .alpha(0.35f)
                            .fillColor(Color.parseColor("#FEDA7A"))
                    );
                }

                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (marker.getTitle().startsWith("[")) {
                            String station = marker.getTitle().split("]")[0];
                            station = station.split("\\[")[1];
                            createTransitPanel(station);

                            return true;
                        }
                        return false;
                    }
                });
            }
        });

        //Initializes button to focus on user location
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("location fab", "fab clicked");
                if (userLocation != null) {
                    moveMapToLocation(new LatLng(userLocation));
                }
            }
        });

        return view;
    }

    /**
     * Callback for a long click on the map, creates a building fragment if
     * the point clicked is a building* Remove debugging map polygon overlay
                for (Building building : buildings) {
                    map.addPolygon(new PolygonOptions()
                            .addAll(building.getAllCoordinates())
                            .alpha(0.35f)
                            .strokeColor(Color.parseColor("#000000"))
                            .fillColor(Color.parseColor("#3bb2d0"))
                    );
                }
     *
     * @param point the point clicked on the map
     */
    public void onLongClickMapCallback(LatLng point) {
        for (Building b : buildings) {
            LatLng[] vertices = b.getAllCoordinates().toArray(new LatLng[b.getAllCoordinates().size()]);
            if (PolygonUtil.pointInPolygon(point, vertices)) {
                //Toast.makeText(getActivity(), "You pressed on "+b.name, Toast.LENGTH_SHORT).show()
                BuildingPartialFragment buildingFrag = new BuildingPartialFragment();
                buildingFrag.setBuilding(b);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.add(R.id.fragment_container, buildingFrag);
                ft.addToBackStack(null);
                ft.commit();
                break;
            }
        }
    }

    /**
     * Initializes the list of buildings associated with the map
     *
     * @param buildings
     */
    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public void setTransitStation(List<MapTransitStation> stations) {
        this.stations = stations;
    }

    public void setConstructionZones(List<MapPolygon> constructions) {
        this.constructions = constructions;
    }

    public void displayTransitStation() {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                for (MapTransitStation station : stations) {
                    map.addMarker(new MarkerViewOptions()
                            .position(station.location)
                            .title("[" + station.identifier + "]" + station.name)
                    );
                }
                map.setOnMarkerClickListener(new MapboxMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull Marker marker) {
                        if (marker.getTitle().startsWith("[")) {
                            String station = marker.getTitle().split("]")[0];
                            station = station.split("\\[")[1];
                            createTransitPanel(station);

                            return true;
                        }
                        return false;
                    }
                });
            }
        });

    }

    /**
     * Getter for the user location
     */
    public Location getUserLocation() {
        return userLocation;
    }

    /**
     * Obtain route from one position to another
     * Obtained from: https://www.mapbox.com/android-sdk/examples/directions/
     *
     * @param origin      the starting point
     * @param destination the endpoint
     * @throws ServicesException
     */
    public void getRoute(Position origin, Position destination) throws ServicesException {
        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_CYCLING)
                .setAccessToken(getResources().getString(R.string.mapbox_key))
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d("direction", "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e("direction", "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e("direction", "No routes found");
                    return;
                }

                // Print some info about the route
                DirectionsRoute currentRoute = response.body().getRoutes().get(0);
                Log.d("direction", "Distance: " + currentRoute.getDistance());
                Toast.makeText(getActivity(),
                        "Route is " + currentRoute.getDistance() + " meters long.",
                        Toast.LENGTH_SHORT).show();

                // Draw the route on the map
                drawRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e("direction", "Error: " + throwable.getMessage());
                Toast.makeText(getActivity(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Draws a route on the map
     * Obtained from: https://www.mapbox.com/android-sdk/examples/directions/
     *
     * @param route the map
     */
    public void drawRoute(DirectionsRoute route) {
        // Convert LineString coordinates into LatLng[]
        LineString lineString = LineString.fromPolyline(route.getGeometry(), Constants.OSRM_PRECISION_V5);
        List<Position> coordinates = lineString.getCoordinates();
        LatLng[] points = new LatLng[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            points[i] = new LatLng(
                    coordinates.get(i).getLatitude(),
                    coordinates.get(i).getLongitude());
        }
        if (routeLine != null) {
            removeRoute();
        }
        // Draw Points on MapView
        routeLine = map.addPolyline(new PolylineOptions()
                .add(points)
                .color(Color.parseColor("green"))
                .alpha((float) 0.50)
                .width(2));
    }

    /**
     * Removes all the markers on the map
     */
    public void removeAllMarkers() {
        List<Marker> allMarkers = map.getMarkers();
        ListIterator<Marker> li = allMarkers.listIterator();
        while (li.hasNext()) {
            Marker marker = li.next();
            marker.remove();
            li.remove();
        }
    }

    /**
     * @return whether the map has markers and routes on it
     */
    public boolean isMapDirty() {
        return ((map.getMarkers().size() > 0) || (routeLine != null));
    }

    /**
     * Removes the routeline
     */
    public void removeRoute() {
        if (routeLine != null) {
            routeLine.remove();
            routeLine = null;
        }
    }


    /**
     * Moves the map to a location
     *
     * @return true if the map is moved, false otherwise
     */
    public boolean moveMapToLocation(LatLng loc) {
        Log.d("map", "Moving map to location: " + loc);
        if (loc != null) {
            CameraPosition position = new CameraPosition.Builder().target(loc).zoom(17).tilt(30).build();
            map.animateCamera(CameraUpdateFactory.newCameraPosition(position), 7000);
            Log.d("map", "Map is at location: " + position);
            return true;
        }
        return false;
    }


    /**
     * Moves the point of interest marker to a specified location and creates
     * it if it doesn't exist
     *
     * @param loc the location to place the marker
     */
    public void movePointOfInterestMarker(LatLng loc) {
        Log.d("map", "Point of interest marker to: " + loc);
        if (loc != null) {
            if (pointOfInterestMarker == null) {
                Log.d("map", "Creating point of interest marker at " + loc);
                MarkerViewOptions marker = new MarkerViewOptions().position(loc);
                pointOfInterestMarker = map.addMarker(marker);
            } else {
                Log.d("map", "Moving point of interest marker to " + loc);
                pointOfInterestMarker.setPosition(loc);
            }
        }
    }

    /**
     * Removes the point of interest marker
     */
    public void removePointOfInterestMarker() {
        Log.d("map", "Removing point of interest marker");
        if (pointOfInterestMarker != null) {
            pointOfInterestMarker.remove();
            pointOfInterestMarker = null;
        }
    }

    /**
     * Adds a marker to the map
     *
     * @param loc
     */
    public Marker addMarker(LatLng loc) {
        MarkerViewOptions marker = new MarkerViewOptions().position(loc);
        return map.addMarker(marker);
    }

    /**
     * Creates an event panel given an eventID
     *
     * @param eventID
     */
    public void createEventPanel(int eventID) {
        EventClientManager ecm = ((MainActivity) getActivity()).getEventClientManager();
        EventInfo event = ecm.findEvent(eventID);
        EventPartialFragment fragment = new EventPartialFragment();
        fragment.setEvent(event);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment currentFrag = fm.findFragmentById(R.id.fragment_container);
        Log.d("backstack", "From Subscribed Events: currFragment = " + currentFrag);
        FragmentTransaction transaction = fm.beginTransaction();
        fragment.setEvent(event);
        //hide current fragment, will reopen when back key pressed
        transaction.add(R.id.fragment_container, fragment, String.valueOf(eventID));
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Creates an transit panel
     *
     * @param stationNumber - Station number
     */
    public void createTransitPanel(String stationNumber) {
        TransitPartialFragment fragment = new TransitPartialFragment();
        fragment.setStation(stationNumber);

        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        //hide current fragment, will reopen when back key pressed
        transaction.add(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * Returns a reference to the map
     *
     * @return
     */
    public MapboxMap getMap() {
        return map;
    }

}
