package ca.udes.bonc.ift_project;


import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import ca.udes.bonc.ift_project.communication.QueryIntentService;
import ca.udes.bonc.ift_project.communication.RegistrationIntentService;
import ca.udes.bonc.ift_project.communication.RestApiResultReceiver;
import ca.udes.bonc.ift_project.fragment.MapFragment;
import ca.udes.bonc.ift_project.fragment.MyEventFragment;
import ca.udes.bonc.ift_project.fragment.NewEventFragment;
import ca.udes.bonc.ift_project.fragment.OnFragmentInteractionListener;
import ca.udes.bonc.ift_project.fragment.SearchFragment;
import ca.udes.bonc.ift_project.utils.AlertDialogManager;
import ca.udes.bonc.ift_project.utils.ConnectionDetector;


public class MainActivity extends FragmentActivity
implements NavigationView.OnNavigationItemSelectedListener,
        OnFragmentInteractionListener,
        GoogleApiClient.OnConnectionFailedListener,
        RestApiResultReceiver.Receiver {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static FragmentManager fragmentManager;
    public RestApiResultReceiver mReceiver;
    private String TAG = "mainActivity";

    // Connection detector class
    private ConnectionDetector cd;
    // Alert Dialog Manager
    private AlertDialogManager alert = new AlertDialogManager();

    GoogleApiClient mGoogleApiClient;
    boolean mSignInClicked;


    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mReceiver = new RestApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        fragmentManager = getSupportFragmentManager();

        // [START configure_signin]
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]


        cd = new ConnectionDetector(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //Create error
        //setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Fragment fragment = new MapFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });*/

        //test
        //TODO define category and get date from datepicker
        //QueryEventService.startActionCreateEvent(this, mReceiver, "-71.92839615046978", "45.385734837128865", "Go to play44", Categories.FOOTBALL, 12, Types.LOISIR);
        //QueryEventService.startActionCreateEvent(this, mReceiver, "-71.92839615046978", "44.385734837128865", "EVERYTHING IS AWSOMER", Categories.HOCKEY, 12, Types.LOISIR);
        //QueryEventService.startActionCreateEvent(this, mReceiver, "-71.92839615046978", "46.385734837128865", "Let's talk dirty", Categories.SOCIAL, 12, Types.LOISIR);
        //QueryEventService.startActionCreateEvent(this, mReceiver, "-71.92839615046978", "45.395734837128865", "Let's talk dirtier", Categories.RANDO, 12, Types.COMPETITIF);
        //QueryEventService.startActionGetMarkers(this, mReceiver, "10.2", "23");
        if(savedInstanceState == null) {
            if (checkPlayServices()) {
                Log.i(TAG, "service disponible");

                // Start IntentService to register this application with GCM.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                startService(intent);
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        //Show dialog if not connected to internet
        handleConnexion();

    }
    private void handleConnexion(){
        if (! (cd.isConnectingToInternet())) {
            Log.e(TAG, "Internet Connection is not present");
            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                    // Setting Dialog Message
                    .setTitle("Internet Connection Error")
                    .setCancelable(false)
                    .setPositiveButton("Retry", null) //Set to null. We override the onclick
                    .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
            //Have to do this to keep the dialog
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(cd.isConnectingToInternet()) {
                                alertDialog.dismiss();
                            }
                        }
                    });
                }
            });
            alertDialog.show();
        }
    }

    //call when service send to receiver
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case QueryIntentService.STATUS_RUNNING:
                //show progress
                break;
            case QueryIntentService.STATUS_FINISHED:
                String results = resultData.getString("results");
                Log.i(TAG, results);
                // do something interesting
                // hide progress
                break;
            case QueryIntentService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Log.d(TAG , error.trim());
                if( ! error.isEmpty() && !error.trim().equals("[]")) {
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void signOut() {
        //handle case with default type account
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.i(TAG, "signout");
                        Log.i(TAG, status.toString());
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
        finish();
    }

    //Warning : If the user deletes their account, you must delete the information that your app obtained from the Google APIs.
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content_frame);
        Fragment selectedFragment = null;

        switch (id){
            case R.id.nav_maps:
                selectedFragment = new MapFragment();
                break;
            case R.id.nav_list:
                selectedFragment = new SearchFragment();
                break;
            case R.id.nav_newevent:
                selectedFragment = new NewEventFragment();
                break;
            case R.id.nav_my_event:
                selectedFragment = new MyEventFragment();
                break;
            case R.id.nav_logout:
                signOut();
                break;
            default:
                break;
        }
        setTitle(item.getTitle());

        if((selectedFragment!= null) && (selectedFragment != currentFragment))
            fragmentManager.beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.content_frame, selectedFragment)
                    .commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void switchFragment(Fragment selectedFragment){
        if(selectedFragment!= null)
            fragmentManager.beginTransaction()
                    .addToBackStack("tag")
                    .replace(R.id.content_frame, selectedFragment)
                    .commit();
    }

    public void onFragmentInteraction(Uri uri) {
        Log.i(this.toString(), uri.toString());
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
