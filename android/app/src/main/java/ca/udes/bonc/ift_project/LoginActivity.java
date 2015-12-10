package ca.udes.bonc.ift_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ca.udes.bonc.ift_project.communication.HttpHelper;


/**
 * Created by pcontat on 20/11/2015.
 * Activity to demonstrate basic retrieval of the Google user's ID, email address, and basic
 * profile.
 */


public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private TextView mStatusTextView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);

        // Views
        mStatusTextView = (TextView) findViewById(R.id.status);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();
        // [END configure_signin]

        // [START build_client]
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client]

        // [START customize_button]
        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        // [END customize_button]
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }


    //Starting the intent prompts the user to select a Google account to sign in with.
    //If you requested scopes beyond profile, email, and openid, the user is also prompted to grant access to the requested resources.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //retrieve the sign-in result with getSignInResultFromIntent.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        Log.d(TAG, "handleSignInResult:" + result.getStatus().getStatusMessage());
        Log.d(TAG, "handleSignInResult:" + result.getStatus().toString());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            //GoogleSignInAccount object that contains information about the signed-in user, such as the user's name.
            GoogleSignInAccount acct = result.getSignInAccount();
            //call server to verify token and create a token for REST API cf https://developers.google.com/identity/sign-in/android/backend-auth
            String idToken = acct.getIdToken();
            if( idToken != null) {
                try {
                    Thread t = new TokenVerifier(idToken);
                    t.start();
                    //Wait fir thread to finish to valide auth
                    t.join();  // wait for thread to finish
                    //TODO : handle error if server do no grant access
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } catch( InterruptedException e) {
                    //TODO handle error
                    Log.e(TAG,e.getMessage());
                }
                //this is test
                mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
                updateUI(true);
                //Uri personPhoto = acct.getPhotoUrl();
                //Log.d(TAG,personPhoto.toString());
            }
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    public class TokenVerifier extends Thread {
        private String idToken = "";

        public TokenVerifier(String idToken){
            this.idToken = idToken;
        }
        @Override
        public void run() {
            try {
                //for emulator we must use 10.0.3.2 instead of 127.0.0.1 for genymotion
                URL url = new URL("http://10.0.3.2:8080/tokensignin");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                String param = "token=" + URLEncoder.encode(idToken, "UTF-8");
                conn.setFixedLengthStreamingMode(param.getBytes().length);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                //send the POST out
                PrintWriter out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.close();



                JSONObject JSONResponse = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
                Log.d(TAG, JSONResponse.toString());
                String userRole = JSONResponse.getJSONObject("user").getString("role");


                //Set token into applicationClass
                String token = JSONResponse.getString("token");
                String userId = JSONResponse.getJSONObject("user").getString("_id");
                Log.d(TAG,"Token acquired : "+ token);
                IFTApplication myApp = (IFTApplication)getApplication();
                myApp.setApiToken(token);
                myApp.setUsrId(userId);

            } catch(MalformedURLException ex){
               Log.e("thread", ex.toString());
            } catch(JSONException ex){
                Log.e("thread", ex.toString());
            }
            catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
        }
    }

    //Warning : If the user deletes their account, you must delete the information that your app obtained from the Google APIs.
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
               // signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
        }
    }
}
