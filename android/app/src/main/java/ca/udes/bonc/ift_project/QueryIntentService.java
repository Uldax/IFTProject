package ca.udes.bonc.ift_project;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class QueryIntentService extends IntentService {
    private static final String ACTION_GET_MARKERS = "ca.udes.bonc.ift_project.action.getMarkers";
    private static final String ACTION_CREATE_MARKER = "ca.udes.bonc.ift_project.action.createMarkers";
    private static final String ACTION_GET_EVENT = "ca.udes.bonc.ift_project.action.getEvent";

    // TODO: Rename parameters
    private static final String EXTRA_LONG = "ca.udes.bonc.ift_project.extra.longitude";
    private static final String EXTRA_LAT = "ca.udes.bonc.ift_project.extra.lat";

    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 0;

    private String ApiToken = "";
    private String TAG = "QueryIntetnService";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetMarkers(Context context,ResultReceiver mReceiver, String longitude, String latitude) {
        //binding to the service with startService()
        Intent intent = new Intent(context, QueryIntentService.class);
        intent.setAction(ACTION_GET_MARKERS);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra(EXTRA_LONG, longitude);
        intent.putExtra(EXTRA_LAT, latitude);
        context.startService(intent);
    }

    public String getApiToken() {
        return ApiToken;
    }

    public void setApiToken(String apiToken) {
        ApiToken = apiToken;
    }

    //Create request with token
    private HttpURLConnection createURLConnexion(String url) throws MalformedURLException,IOException{
        URL src = new URL(HttpHelper.LOCALHOST + url);
        HttpURLConnection conn = (HttpURLConnection)src.openConnection();
        conn.addRequestProperty("X-Access-Token", getApiToken());
        return conn;
    }

    public QueryIntentService() {
        super("QueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(getApiToken().equals("")){
            IFTApplication myApplication = (IFTApplication)getApplication();
            setApiToken(myApplication.getApiToken());
        }
        if (intent != null) {
            final ResultReceiver receiver = intent.getParcelableExtra("receiver");
            String action = intent.getAction();
            Bundle b = new Bundle();
            if(action.equals(ACTION_GET_MARKERS)) {
                final String longitude = intent.getStringExtra(EXTRA_LONG);
                final String lat = intent.getStringExtra(EXTRA_LAT);
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                //receiver.send will call onReceiveResult in the activity
                try {
                    String result = handleActionGetMarkers(lat,longitude);
                   // b.putParcelableArrayList("results", result);
                    b.putString("results", result);
                    receiver.send(STATUS_FINISHED, b);
                    Log.i(TAG, "receiver send");
                } catch(Exception e) {
                    b.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, b);
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private String handleActionGetMarkers(String lat, String longitude) throws MalformedURLException,IOException {
        HttpURLConnection conn = createURLConnexion("/api/markers?lat="+lat+"&long="+longitude);
        Log.i(TAG,"call to/api/markers?lat="+lat+"&long="+longitude);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }
    private String handleActionGetMarkers(String lat, String longitude, String radius) throws MalformedURLException,IOException {
        HttpURLConnection conn = createURLConnexion("/api/markers?lat="+lat+"&long="+longitude+"&radius="+radius);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
