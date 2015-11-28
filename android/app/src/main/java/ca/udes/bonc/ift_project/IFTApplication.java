package ca.udes.bonc.ift_project;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by pcontat on 28/11/2015.
 */
public class IFTApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences prefs;
    private String TAG = "IFTApplication";
    private String TOKEN_KEY = "apiToken";

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

    // méthode déplacée de StatusActivity vers YambaApplication
    public synchronized void onSharedPreferenceChanged( SharedPreferences sharedPrefs,
                                                        String key )
    {
        Log.i(TAG,"Prefference change for " + key);
    }


}
