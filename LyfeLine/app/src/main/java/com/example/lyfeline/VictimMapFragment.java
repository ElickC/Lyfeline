package com.example.lyfeline;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.example.lyfeline.util.Constants.MAPVIEW_BUNDLE_KEY;


/**
 * A simple {@link Fragment} subclass.
 */
public class VictimMapFragment extends Fragment implements OnMapReadyCallback {
    MapView mMapView;
    private static final String TAG = "VictimMapFragment";
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15f;
    TextView textViewStatus;

    public VictimMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_victim_map, container, false);

        textViewStatus = view.findViewById(R.id.textViewStatus);

        mMapView = view.findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);


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
    public void onMapReady(final GoogleMap map) {
        Log.d(TAG, "onMapReady: Getting victims location" );
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        mMap = map;
        mMap.setMyLocationEnabled(true);

        DocumentReference docRef = mDb.collection("HelpVics").document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    HelpVics helpVic = documentSnapshot.toObject(HelpVics.class);
                    //set status based on booleans
                    if(helpVic.emtOnTheWay) {
                        textViewStatus.setText("EMT is on the way");
                    }
                    else if(helpVic.emtHasArrived) {
                        textViewStatus.setText("EMT has arrived");
                    }
                    else {
                        textViewStatus.setText("EMT has been notified");
                    }

                    if (helpVic.getEmtAssigned() != null ) {
                        queryEmtLocation(helpVic.getEmtAssigned());
                    }

                }
            }
        });

        CollectionReference vicRef = mDb.collection("Vics_Location");
        Query vicQuery = vicRef.whereEqualTo("victimUser.user_id", FirebaseAuth.getInstance()
                .getCurrentUser().getUid());

        vicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.exists()) {
                            Log.d(TAG, "onMapReady: Successfully got  victims location" );
                            VicLocation user = document.toObject(VicLocation.class);
                            GeoPoint userLoc = user.getGeo_point();
                            String firstName = user.getVictimUser().getFirstName();
                            addMarker(userLoc, firstName, DEFAULT_ZOOM);
                        }
                    }
                }
            }
        });

    }

    public void queryEmtLocation(String emtUserID) {
        FirebaseFirestore mDb = FirebaseFirestore.getInstance();

        DocumentReference docRef = mDb.collection("EMTs_Location").document(emtUserID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "queryEMT: adding EMT's location marker to victim's map");
                    EmtLocation emtAssigned = task.getResult().toObject(EmtLocation.class);
                    GeoPoint emtLocation = emtAssigned.getGeo_point();
                    BitmapDescriptor emtIcon = BitmapDescriptorFactory.fromResource(R.drawable.outline_directions_car_black_18dp);
                    LatLng emtLatLng = new LatLng(emtLocation.getLatitude(), emtLocation.getLongitude());
                    MarkerOptions emtMarkerOptions = new MarkerOptions().position(emtLatLng)
                            .title("EMT")
                            .icon(emtIcon);
                    mMap.addMarker(emtMarkerOptions);
                    //addMarker(emtLocation, "EMT", DEFAULT_ZOOM);
                }
            }
        });

        listenForLocationChanges(emtUserID);


    }

    public void listenForLocationChanges(String emtUserID) {

        final FirebaseFirestore mDb = FirebaseFirestore.getInstance();
        DocumentReference emtLocRef = mDb.collection("EMTs_Location").document(emtUserID);

        emtLocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d(TAG, "listenForLocationChanges: detecting change to EMT's location, clearing map");
                    mMap.clear();
                    Log.d(TAG, "listenForLocationChanges: changing marker for emt");
                    EmtLocation emtAssigned = documentSnapshot.toObject(EmtLocation.class);
                    GeoPoint emtLocation = emtAssigned.getGeo_point();
                    BitmapDescriptor emtIcon = BitmapDescriptorFactory.fromResource(R.drawable.outline_directions_car_black_18dp);
                    LatLng emtLatLng = new LatLng(emtLocation.getLatitude(), emtLocation.getLongitude());
                    MarkerOptions emtMarkerOptions = new MarkerOptions().position(emtLatLng)
                            .title("EMT")
                            .icon(emtIcon);
                    mMap.addMarker(emtMarkerOptions);
                    //addMarker(emtLocation, "EMT", DEFAULT_ZOOM);

                    CollectionReference vicRef = mDb.collection("Vics_Location");
                    Query vicQuery = vicRef.whereEqualTo("victimUser.user_id", FirebaseAuth.getInstance()
                            .getCurrentUser().getUid());

                    vicQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.exists()) {
                                        Log.d(TAG, "onMapReady: Successfully got  victims location" );
                                        VicLocation user = document.toObject(VicLocation.class);
                                        GeoPoint userLoc = user.getGeo_point();
                                        String firstName = user.getVictimUser().getFirstName();
                                        addMarker(userLoc, firstName, DEFAULT_ZOOM);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });


    }

    public void addMarker(GeoPoint loc, String name, float zoom) {
        Log.d(TAG, "addMarker: setting marker on: lat: " + loc.getLatitude() + ", lng: " + loc.getLongitude() );
        mMap.addMarker(new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude())).title(name));
        Log.d(TAG, "addMarker: moving the camera to: lat: " + loc.getLatitude() + ", lng: " + loc.getLongitude() );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), zoom));
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
}
