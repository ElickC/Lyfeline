package com.example.lyfeline;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lyfeline.models.PolylineData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;


import java.util.ArrayList;
import java.util.List;


import static com.example.lyfeline.Constants.MAPVIEW_BUNDLE_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmtMapFragment extends Fragment implements OnMapReadyCallback,
                                                        View.OnClickListener,
                                                        GoogleMap.OnInfoWindowClickListener,
                                                        GoogleMap.OnPolylineClickListener{


    private MapView mMapView;
    private GoogleMap mMap;
    private FirebaseFirestore mDb = FirebaseFirestore.getInstance();
    private static final String TAG = "EmtMapFragment";
    private static final float DEFAULT_ZOOM = 15f;
    private GeoApiContext mGeoApiContext = null;
    private EmtLocation mEmtLocation =  null;
    private LatLngBounds mMapBoundary;
    private ArrayList<EmtUser> mEmtUserList = new ArrayList<>();
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private String vicName = null;



    public EmtMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emt_map, container, false);
        mMapView = view.findViewById(R.id.mapView);
        view.findViewById(R.id.btn_reset_map).setOnClickListener(this);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_maps_API_key)).build();
        }


        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady: Getting victim locations" );
        mMap = map;
        mMap.setOnPolylineClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setOnInfoWindowClickListener(this);


        CollectionReference vicHelpRef = mDb.collection("HelpVics");

        Query vicQuery = vicHelpRef;

        vicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d(TAG, "onMapReady: Successfully got victims location");
                            HelpVics helpVic = document.toObject(HelpVics.class);
                            VicLocation vicLocation = helpVic.getVicLocation();
                            GeoPoint userLoc = vicLocation.getGeo_point();
                            String firstName = vicLocation.getVictimUser().getFirstName();
                            addMarker(userLoc, firstName, DEFAULT_ZOOM);
                        }
                    }
                }
            }
        });

        // Allow background service to update location
        try {
            Thread.sleep(500);
            getEmtLocation();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addMarker(GeoPoint loc, String name, float zoom) {
        Log.d(TAG, "addMarker: setting marker on: lat: " + loc.getLatitude() + ", lng: " + loc.getLongitude() );
        mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(name));
        Log.d(TAG, "addMarker: moving the camera to: lat: " + loc.getLatitude() + ", lng: " + loc.getLongitude() );
     //   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), zoom));
    }

    // Retrieve EmtPoistion info from DB
    private void getEmtLocation(){

        Log.d(TAG, "getEmtLocation: Start mEmtLocation " + mEmtLocation);

        DocumentReference locationRef = mDb
                .collection("EMTs_Location")
                .document(FirebaseAuth.getInstance().getUid());

        locationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mEmtLocation = task.getResult().toObject(EmtLocation.class);
                    Log.d(TAG, "getEmtLocation: inside mEmtLocation " + mEmtLocation);

                    moveCamera(new LatLng(mEmtLocation.getGeo_point().getLatitude(),
                        mEmtLocation.getGeo_point().getLongitude()), DEFAULT_ZOOM);
                }
                else{
                    Log.d(TAG, "getEmtLocation: task not successful");
                }
            }
        });

        Log.d(TAG, "getEmtLocation: End mEmtLocation " + mEmtLocation );

    }


    private void calculateDirections(Marker marker){

        Log.d(TAG, "calculateDirections: calculating directions.");

        getEmtLocation();

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        Log.d(TAG, "calculateDirections: mEmTLocation ==> " + mEmtLocation);

                directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mEmtLocation.getGeo_point().getLatitude(),
                        mEmtLocation.getGeo_point().getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());

                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    // Adding polylines to the map for directions
    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                // if second time through this method need to remove old lines if looking for directions to a different location
                if(mPolylinesData.size() > 0){
                    for(PolylineData polylineData: mPolylinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }

                // set duration to very large number to find the shortest leg later
                double duration = 99999999;

                // get the result of each route leg, and store the decoded path into an arraylist
                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;

                    // Highlight polyline with shortest duration
                    if(tempDuration < duration){
                        duration = tempDuration;
                        onPolylineClick(polyline);
                        zoomRoute(polyline.getPoints());
                    }

                    // When polylines added to map, remove selected custom marker
                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }

    // when user clicks on a polyline
    @Override
    public void onPolylineClick(Polyline polyline) {

        // index for trip #'s to differentiate them
        int index = 0;
        // loop through each poly line
        for(PolylineData polylineData: mPolylinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());

            // if passed polyline has the same id as the one that was clicked, make it blue
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.blue));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );

                Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(endLocation)
                    .title("Trip to " + vicName +  ": #" + index)
                    .snippet("Duration: " + polylineData.getLeg().duration)
                );

                marker.showInfoWindow();
            }
            // make all other polylines grey
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.darkGrey));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }


    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    // For half screen if we have time
    @Override
    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.btn_full_screen_map:{
//                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED){
//                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
//                    expandMapAnimation();
//                }
//                else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED){
//                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
//                    contractMapAnimation();
//                }
//                break;
//            }
//        }
    }

    // When user clicks on marker alert dialog calculate directions
    @Override
    public void onInfoWindowClick(final Marker marker) {
        vicName = marker.getTitle();
        if(marker.getId().equals("self")){
            marker.hideInfoWindow();
        }
        else{

            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Navigate to " + marker.getTitle() + " ?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            mSelectedMarker = marker;
                            calculateDirections(marker);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 120;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


}
