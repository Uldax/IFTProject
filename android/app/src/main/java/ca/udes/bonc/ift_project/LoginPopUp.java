package ca.udes.bonc.ift_project;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import ca.udes.bonc.ift_project.communication.HttpHelper;

public class LoginPopUp extends Activity {

    private Button signIn ;
    private AutoCompleteTextView emailEditText;
    private EditText passwordEditText;
    private ProgressDialog mProgressDialog;
    private IFTApplication myApp;
    private String TAG = "LoginPopUp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pop_up);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        myApp = (IFTApplication)getApplication();

        Double width =  dm.widthPixels * 0.8;
        Double height = dm.heightPixels* 0.4;
        getWindow().setLayout( width.intValue() , height.intValue());
        emailEditText = (AutoCompleteTextView) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.email_sign_in_button);
        signIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    showProgressDialog();
                    String password = passwordEditText.getText().toString();
                    String email = emailEditText.getText().toString();
                    Log.d(TAG, "email = " + email + "password = " + password);
                    Thread login = new LoginThread(email, password);
                    login.start();
                    login.join();
                    hideProgressDialog();
                    //if loged (boolean parameter)
                    startActivity(new Intent(LoginPopUp.this, MainActivity.class));
                    finish();
                } catch(InterruptedException e){
                    Log.e(TAG,e.getMessage());
                }
            }
        });
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

    public class LoginThread extends Thread {
        String email ="";
        String password = "";
        LoginThread(String email,String password){
            this.email = email;
            this.password = password;
        }
        @Override
        public void run() {
            try {
                String dataPost = HttpHelper.encodeParamUTF8("email",email)+"&"+ HttpHelper.encodeParamUTF8("password",password);
                HttpURLConnection conn = HttpHelper.createPostURLConnection(HttpHelper.LOCALHOST + "/login", dataPost);
                JSONObject JSONResponse = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));

                Log.d(TAG, JSONResponse.toString());
                //Set token into applicationClass
                String token = JSONResponse.getString("token");
                String userId = JSONResponse.getJSONObject("user").getString("_id");
                String pictureUri = JSONResponse.getJSONObject("user").getString("picture");
                //todo add picture
                Log.d(TAG,"Token acquired : "+ token);
                myApp.setApiToken(token);
                myApp.setUserId(userId);
                if(pictureUri != null){
                    myApp.setPictureUri(pictureUri);
                }
                //TODO handle error

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

}
