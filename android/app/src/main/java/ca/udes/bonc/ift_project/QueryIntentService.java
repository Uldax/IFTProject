package ca.udes.bonc.ift_project;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.net.URLEncoder;

public class QueryIntentService extends IntentService {
    private static final String ACTION_GET_MARKERS = "ca.udes.bonc.ift_project.action.getMarkers";
    private static final String ACTION_CREATE_MARKER = "ca.udes.bonc.ift_project.action.createMarkers";

    private static final String ACTION_GET_EVENT_CATEG = "ca.udes.bonc.ift_project.action.getEventByCateg";
    private static final String ACTION_GET_ONE = "ca.udes.bonc.ift_project.action.getOneEvent";
    private static final String ACTION_ADD_EVENT_PARTICIPANT = "ca.udes.bonc.ift_project.action.addParticipant";
    private static final String ACTION_CREATE_EVENT = "ca.udes.bonc.ift_project.action.createEvent";

    private static final String EXTRA_LNG = "ca.udes.bonc.ift_project.extra.lng";
    private static final String EXTRA_RECEIVER = "ca.udes.bonc.ift_project.extra.receiver";
    private static final String EXTRA_LAT = "ca.udes.bonc.ift_project.extra.lat";
    private static final String EXTRA_CATEGORY = "ca.udes.bonc.ift_project.extra.category";
    private static final String EXTRA_EVENT_DATE = "ca.udes.bonc.ift_project.extra.date";
    private static final String EXTRA_EVENT_TITLE = "ca.udes.bonc.ift_project.extra.title";


    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 0;

    private String ApiToken = "";
    private static final String TAG = "QueryIntetnService";
    private IFTApplication myApplication = null;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionGetMarkers(Context context,ResultReceiver mReceiver, String longitude, String latitude) {
        //binding to the service with startService()
        Intent intent = new Intent(context, QueryIntentService.class);
        intent.setAction(ACTION_GET_MARKERS);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_LNG, longitude);
        intent.putExtra(EXTRA_LAT, latitude);
        context.startService(intent);
    }

    public static void startActionCreateMarkers(Context context,ResultReceiver mReceiver, String longitude, String latitude, String title, String category) {
        //binding to the service with startService()
        Log.d(TAG,"Start createMarkers");
        Intent intent = new Intent(context, QueryIntentService.class);
        intent.setAction(ACTION_CREATE_MARKER);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_LNG, longitude);
        intent.putExtra(EXTRA_LAT, latitude);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_EVENT_TITLE, title);
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
        if(getApiToken().equals("") ) {
            setApiToken(myApplication.getApiToken());
        }
        URL src = new URL(HttpHelper.LOCALHOST + url);
        HttpURLConnection conn = (HttpURLConnection)src.openConnection();
        conn.addRequestProperty("X-Access-Token", getApiToken());
        return conn;
    }
    // String urlParameters  = "param1=a&param2=b&param3=c";
    private HttpURLConnection createPostURLConnextion(String url,String urlParameters) throws MalformedURLException,IOException{
        //Buffer from post data
        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
        int    postDataLength = postData.length;
        //Create  connextion
        HttpURLConnection conn= this.createURLConnexion(url);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
        wr.write(postData);
        return conn;
    }

    public QueryIntentService() {
        super("QueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if( myApplication == null) {
            myApplication = (IFTApplication)getApplication();
        }
        if (intent != null) {
            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            String action = intent.getAction();
            Bundle b = new Bundle();
            if(action.equals(ACTION_GET_MARKERS)) {
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                //receiver.send will call onReceiveResult in the activity
                try {
                    final String longitude = intent.getStringExtra(EXTRA_LNG);
                    final String lat = intent.getStringExtra(EXTRA_LAT);
                    String result = handleActionGetMarkers(lat,longitude);
                   // b.putParcelableArrayList("results", result);
                    b.putString("results", result);
                    receiver.send(STATUS_FINISHED, b);
                    Log.i(TAG, "receiver send");
                } catch(Exception e) {
                    b.putString(Intent.EXTRA_TEXT, e.toString());
                    receiver.send(STATUS_ERROR, b);
                }
            } else if(action.equals(ACTION_CREATE_MARKER)) {
                try {
                    String dataPost = formatCreateParameters(intent);
                    String result = handleActionCreateEvent(dataPost);
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
     * Handle action Markers in the provided background thread with the provided
     * parameters.
     */
    //Markers
    private String handleActionGetMarkers(String lat, String longitude) throws MalformedURLException,IOException {
        HttpURLConnection conn = createURLConnexion("/api/markers?lat="+lat+"&long="+longitude);
        Log.i(TAG,"call to/api/markers?lat="+lat+"&long="+longitude);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    private String formatCreateParameters(Intent intent) throws UnsupportedEncodingException{
        final String userId = myApplication.getUserId();
        //todo add
        final String eventDate = intent.getStringExtra(EXTRA_EVENT_DATE);
        String postData = "category=" + URLEncoder.encode(intent.getStringExtra(EXTRA_CATEGORY), "UTF-8") +
                "&createBy="+ URLEncoder.encode(userId, "UTF-8") +
                "&admin=" + URLEncoder.encode(userId, "UTF-8") +
                "&eventDate=" + URLEncoder.encode("01/01/2016", "UTF-8") +
                "&lat=" + URLEncoder.encode(intent.getStringExtra(EXTRA_LAT), "UTF-8") +
                "&lng=" + URLEncoder.encode(intent.getStringExtra(EXTRA_LNG), "UTF-8") +
                "&title=" + URLEncoder.encode(intent.getStringExtra(EXTRA_EVENT_TITLE), "UTF-8");
        return postData;
    }

    private String handleActionCreateEvent(String dataPost) throws MalformedURLException,IOException,JSONException {
        Log.d(TAG,"Call createEvent with " + dataPost);
        HttpURLConnection conn = createPostURLConnextion("/api/events",dataPost);
        JSONObject json = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
        String message = json.getString("message");
        Log.d(TAG,message);
        conn.disconnect();
        return message;
    }

    private String handleActionGetMarkers(String lat, String longitude, String radius) throws MalformedURLException,IOException {
        HttpURLConnection conn = createURLConnexion("/api/markers?lat="+lat+"&long="+longitude+"&radius="+radius);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    //Events
    private String handleActionGetOneEvent(String id) throws MalformedURLException,IOException {
        HttpURLConnection conn = createURLConnexion("/api/events/"+ id);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }
    private String handleActionGetEventByCateg(String categ) throws MalformedURLException,IOException {
        HttpURLConnection conn = createURLConnexion("/api/categ/"+ categ);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

}
