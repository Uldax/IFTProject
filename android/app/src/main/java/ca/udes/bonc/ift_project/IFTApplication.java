package ca.udes.bonc.ift_project;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.URI;

/**
 * Created by pcontat on 28/11/2015.
 */
public class IFTApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences prefs;
    private String TAG = "IFTApplication";
    private String TOKEN_KEY = "apiToken";
    private String USER_ID_KEY = "apiUserId";
    private String USER_PICTURE_URI_KEY = "apiUserUriKey";
    //Todo add : default picture
    private String DEFAULT_URI = "";

    public void onCreate() {
        super.onCreate();
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        this.prefs.registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "onCreated");
    }

    // clean-up code
    public void onTerminate() {
        super.onTerminate();
        Log.i(TAG, "onTerminated");
    }

    public String getApiToken(){
        return this.prefs.getString(TOKEN_KEY, "");
    }
    public void setApiToken(String token){
        prefs.edit().putString(TOKEN_KEY, token).apply();
    }
    public String getUserId(){
        return this.prefs.getString(USER_ID_KEY, "");
    }
    public void setUserId(String userID){
        prefs.edit().putString(USER_ID_KEY, userID).apply();
    }

    public URI getPictureUri(){
        URI imageUri = null;
        try {
            imageUri = URI.create( prefs.getString(USER_ID_KEY, DEFAULT_URI));
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return imageUri;
    }

    public void setPictureUri(String stringUri){
        prefs.edit().putString(USER_PICTURE_URI_KEY, stringUri).apply();
    }



    public synchronized void onSharedPreferenceChanged( SharedPreferences sharedPrefs, String key )
    {
        Log.i(TAG,"Prefference change for " + key);
    }


}
