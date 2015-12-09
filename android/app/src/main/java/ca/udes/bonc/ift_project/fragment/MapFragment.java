package ca.udes.bonc.ift_project.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.utils.AlertDialogManager;
import ca.udes.bonc.ift_project.utils.GPSTracker;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View contener;
    private FloatingActionButton fabLoc;
    private FloatingActionButton fabList;
    private ListView listMap;
    private View myView;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AlertDialogManager alert = new AlertDialogManager();

    // GPS Location with callback overwrite
    private GPSTracker gps  = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

       gps = new GPSTracker(getActivity()){
            @Override
            public void onLocationChanged(Location location) {
                Log.i("map", "location change");
                if(mMap != null) {
                    mMap.clear();
                    //TodoRemove
                    MarkerOptions mp = new MarkerOptions();
                    mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
                    mp.title("my position");
                    mMap.addMarker(mp);
                    Log.d("map", "Perform scearch");
                    // calling background Async task to load Google Places
                    // After getting places from Google all the data is shown in listview
                    //TODO perform search in asycTask
                    //new LoadPlaces(location).execute();
                } else {
                    Log.e("location change","map is not already defined");
                }
            }
        };

        /*gps.start();
        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            // Can't get user's current location
            alert.showAlertDialog(getActivity(), "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return;
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.myView = inflater.inflate(R.layout.fragment_map, container, false);
        this.contener = container;
        this.fabList = (FloatingActionButton) myView.findViewById(R.id.fabList);
        this.fabLoc = (FloatingActionButton) myView.findViewById(R.id.fabLoc);
        this.listMap = (ListView) myView.findViewById(R.id.listMap);

        fabLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localisateMe();
            }
        });

        fabList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleListMap();
            }
        });

        // Do a null check to confirm that we have not already instantiated the map
        //Not working now
        /*if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map);
            FragmentManager fragmentManager = getFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }*/


        return myView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //OnMapReadyCallback
    //Callback interface for when the map is ready to be used.
    //call by getMapAsync()
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("maps", " Call to on map ready");
        mMap = googleMap;
        mMap.setOnMyLocationButtonClickListener(this);
        //Enables the My Location layer if the fine location permission has been granted.
        //Context compat : Helper for accessing features in Context introduced after API level 4 in a backwards compatible fashion.
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e("maps", " Permission to access the location is missing.");
            // Permission to access the location is missing.

        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            //Enables or disables the my-location layer.
            //While enabled, the my-location layer continuously draws an indication of a user's current location and bearing,
            // and displays UI controls that allow a user to interact with their location (for example, to enable or disable camera tracking of their location and bearing).
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    protected void toggleListMap(){
        if(listMap.getVisibility() == View.GONE){
            listMap.setVisibility(View.VISIBLE);
            fabList.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        }else {
            listMap.setVisibility(View.GONE);
            fabList.setImageResource(android.R.drawable.ic_menu_more);
        }
    }

    protected void localisateMe(){
        Snackbar.make(this.myView, "Localisation TODO", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
