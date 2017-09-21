package ca.udes.bonc.ift_project;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import ca.udes.bonc.ift_project.communication.HttpHelper;
import ca.udes.bonc.ift_project.utils.ConnectionDetector;


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
    private ProgressDialog mProgressDialog;
    private IFTApplication myApp;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_layout);

        cd = new ConnectionDetector(getApplicationContext());
        // Check if Internet present

        myApp = (IFTApplication)getApplication();

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.login_button).setOnClickListener(this);
        findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);

        // [START configure_signin]
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
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
        handleConnexion();
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
        Log.d(TAG, "handleSignInResult:" + result.getStatus().toString());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            //GoogleSignInAccount object that contains information about the signed-in user, such as the user's name.
            GoogleSignInAccount acct = result.getSignInAccount();
            //call server to verify token and create a token for REST API cf https://developers.google.com/identity/sign-in/android/backend-auth
            String idToken = acct.getIdToken();
            if (idToken != null) {
                    TokenVerifier tv = new TokenVerifier(idToken, this);
                    tv.execute();
                    Uri personPhoto = acct.getPhotoUrl();
                    if (personPhoto != null) {
                        myApp.setPictureUri(personPhoto.toString());
                    }
            } else {
                // Signed out, show unauthenticated UI.
            }
        }
    }

    public class TokenVerifier extends AsyncTask<Void, JSONObject, Boolean>
    {
        private String idToken;
        private Activity mActivity;
        private String errorMessage;

        public TokenVerifier(String idToken, Activity myActivity) {
            this.idToken = idToken;
            mActivity = myActivity;
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                Log.d(TAG,"token send :" + idToken);
                String param = HttpHelper.encodeParamUTF8("token",idToken);
                HttpURLConnection conn = HttpHelper.createPostURLConnection(HttpHelper.LOCALHOST + "/tokensignin", param);
                JSONObject JSONResponse = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
                Log.d(TAG, JSONResponse.toString());

                //Failed to connect
                if( JSONResponse.has("status") && JSONResponse.getInt("status") == 401 ){
                    return false;
                } else {
                    //Set token into applicationClass
                    String token = JSONResponse.getString("token");
                    String userId = JSONResponse.getJSONObject("user").getString("_id");
                    Log.d(TAG, "Token acquired : " + token);
                    myApp.setApiToken(token);
                    myApp.setUserId(userId);
                    return true;
                }
            } catch(MalformedURLException ex){
                Log.e("thread", ex.toString());
            } catch(JSONException ex){
                Log.e("thread", ex.toString());
            } catch(SocketTimeoutException ex){
                errorMessage=ex.toString();
                Log.e("thread", ex.toString());
            } catch(ConnectException ex){
                //failed to connect
                errorMessage=ex.toString();
                Log.e("thread", ex.toString());
            }
            catch (IOException e) {
                Log.e(TAG, "Error sending ID token to backend.", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if( ! result) {
                if( errorMessage.isEmpty()){
                    errorMessage = "Something append with our server";
                }
                Toast.makeText(getApplicationContext(),errorMessage , Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                mActivity.finish();
            }
        }
    }


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

    private void callLoginDialog()
    {
       startActivity(new Intent(LoginActivity.this,LoginPopUp.class));
    }

    private void handleConnexion(){
        if (! (cd.isConnectingToInternet())) {
            Log.e(TAG, "Internet Connection is not present");
            final AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this)
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.login_button:
                callLoginDialog();
                break;
        }
    }
}
