package ca.udes.bonc.ift_project.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.udes.bonc.ift_project.R;
import ca.udes.bonc.ift_project.dataObject.Place;
import ca.udes.bonc.ift_project.dataObject.PlacesList;
import ca.udes.bonc.ift_project.utils.AlertDialogManager;
import ca.udes.bonc.ift_project.utils.ConnectionDetector;
import ca.udes.bonc.ift_project.utils.GPSTracker;
import ca.udes.bonc.ift_project.utils.GooglePlaces;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements
        GoogleMap.OnMyLocationButtonClickListener,
        OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback {


    private PlacesList nearPlaces;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private ViewGroup container;

    // GPS Location with callback overwrite
    private GPSTracker gps  = new GPSTracker(container.getContext()){
        @Override
        public void onLocationChanged(Location location) {
            Log.i("map", "location change");
            if(mMap != null) {
                mMap.clear();
                MarkerOptions mp = new MarkerOptions();
                mp.position(new LatLng(location.getLatitude(), location.getLongitude()));
                mp.title("my position");
                mMap.addMarker(mp);
                Log.d("map", "Perform scearch");
                // calling background Async task to load Google Places
                // After getting places from Google all the data is shown in listview
                new LoadPlaces(location).execute();
            } else {
                Log.e("location change","map is not already defined");
            }
        }
    };
    // Connection detector class
    private ConnectionDetector cd;
    // Alert Dialog Manager
    private AlertDialogManager alert = new AlertDialogManager();
    // Google Places helper
    private GooglePlaces googlePlaces;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        this.container = container;

        // Do a null check to confirm that we have not already instantiated the map
        /*if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) view.findViewById(R.id.map);
            mapFragment.getMapAsync(this);
        }*/

        cd = new ConnectionDetector(container.getContext());
        // Check if Internet present
        if (! (cd.isConnectingToInternet())) {
            // Internet Connection is not present
            alert.showAlertDialog(container.getContext(), "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            // stop executing code by return
            return null;
        }
        gps.start();
        // check if GPS location can get
        if (gps.canGetLocation()) {
            Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
        } else {
            // Can't get user's current location
            alert.showAlertDialog(container.getContext(), "GPS Status",
                    "Couldn't get location information. Please enable GPS",
                    false);
            // stop executing code by return
            return null;
        }

        return view;
    }

    //OnMapReadyCallback
    //Callback interface for when the map is ready to be used.
    @Override
    public void onMapReady(GoogleMap map) {
        Log.e("maps", " Call to on map ready");
        mMap = map;
        mMap.setOnMyLocationButtonClickListener(this);
        googlePlaces = new GooglePlaces();
        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        //Context compat : Helper for accessing features in Context introduced after API level 4 in a backwards compatible fashion.
        if (ContextCompat.checkSelfPermission(container.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
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

    //OnMyLocationButtonClickListener
    //Callback interface for when the My Location button is clicked.
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * Background Async Task to Load Google places
     * */
    class LoadPlaces extends AsyncTask<String, String, String> {

        ProgressDialog pDialog = null;
        Location location =null;

        public LoadPlaces(Location location){
            this.location = location;
        }
        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog
            pDialog = new ProgressDialog(getContext());
            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Places JSON
         */
        protected String doInBackground(String... args) {
            // creating Places class object

            try {
                // Separeate your place types by PIPE symbol "|"
                // If you want all types places make it as null
                String types = "cafe|restaurant"; // Listing places only cafes, restaurants
                // Radius in meters
                double radius = 1500;
                // get nearest places
                nearPlaces = googlePlaces.search(location.getLatitude(), location.getLongitude(), radius, types);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * and show the data in UI
         * Always use runOnUiThread(new Runnable()) to update UI from background
         * thread, otherwise you will get error
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // Get json response status
            String status = nearPlaces.status;
            //case don't respond
            if(status == null){
                alert.showAlertDialog(getContext(), "Places Error", "Sorry error occured.", false);
                return;
            }
            switch (status) {
                case "OK":
                    // Successfully got places details
                    if (nearPlaces.results != null && nearPlaces.results.size() > 0 ) {
                        Log.d("AsycTask", String.valueOf(nearPlaces.results.size()));
                        for (Place p : nearPlaces.results) {
                            MarkerOptions mp = new MarkerOptions();
                            mp.position(new LatLng(p.geometry.location.lat, p.geometry.location.lng));
                            mp.title(p.name);
                            mMap.addMarker(mp);
                        }
                        //TODO clear near place
                        nearPlaces.results.clear();
                    }
                    break;

                case "ZERO_RESULTS":
                    alert.showAlertDialog(getContext(), "Near Places",
                            "Sorry no places found. Try to change the types of places",
                            false);
                    break;

                case "UNKNOWN_ERROR":
                    alert.showAlertDialog(getContext(), "Places Error",
                            "Sorry unknown error occured.",
                            false);
                    break;


                case "OVER_QUERY_LIMIT":
                    alert.showAlertDialog(getContext(), "Places Error",
                            "Sorry query limit to google places is reached",
                            false);
                    break;

                case "REQUEST_DENIED":
                    alert.showAlertDialog(getContext(), "Places Error",
                            "Sorry error occured. Request is denied",
                            false);
                    break;

                case "INVALID_REQUEST":
                    alert.showAlertDialog(getContext(), "Places Error",
                            "Sorry error occured. Invalid Request",
                            false);
                    break;

                default:
                    alert.showAlertDialog(getContext(), "Places Error",
                            "Sorry error occured.",
                            false);
                    break;
            }
        }
    }
}
