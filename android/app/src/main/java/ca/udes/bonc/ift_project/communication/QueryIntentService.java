package ca.udes.bonc.ift_project.communication;

import android.app.IntentService;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import ca.udes.bonc.ift_project.IFTApplication;

public abstract class QueryIntentService extends IntentService {

    public static final String ACTION_FIND = "ca.udes.bonc.ift_project.action.find";
    public static final String ACTION_GET_MARKERS = "ca.udes.bonc.ift_project.action.getMarkers";
    public static final String ACTION_FIND_EVENT = "ca.udes.bonc.ift_project.action.findEvent";
    public static final String ACTION_GET_ONE = "ca.udes.bonc.ift_project.action.getOneEvent";
    public static final String ACTION_CREATE_TEAM = "ca.udes.bonc.ift_project.action.createTeam";
    public static final String ACTION_SHUFFLE_PARTICIPANTS = "ca.udes.bonc.ift_project.action.shuffleParticipants";
    public static final String ACTION_ADD_EVENT_PARTICIPANT = "ca.udes.bonc.ift_project.action.addParticipant";
    public static final String ACTION_REMOVE_EVENT_PARTICIPANT = "ca.udes.bonc.ift_project.action.removeParticipant";
    public static final String ACTION_ADD_EVENT_ADMIN = "ca.udes.bonc.ift_project.action.addAdmin";
    public static final String ACTION_CREATE_EVENT = "ca.udes.bonc.ift_project.action.createEvent";
    public static final String ACTION_DELETE_EVENT = "ca.udes.bonc.ift_project.action.deleteEvent";
    public static final String ACTION_FIND_EVENT_USER = "ca.udes.bonc.ift_project.action.findEventForUser";
    public static final String ACTION_GET_MARKERS_RADIUS = "ca.udes.bonc.ift_project.action.getMarkersWithRadius";
    public static final String ACTION_CLOSE_EVENT = "ca.udes.bonc.ift_project.action.close.event";




    protected static final String EXTRA_LNG = "ca.udes.bonc.ift_project.extra.lng";
    protected static final String EXTRA_RECEIVER = "ca.udes.bonc.ift_project.extra.receiver";
    protected static final String EXTRA_RADIUS = "ca.udes.bonc.ift_project.extra.radius";
    protected static final String EXTRA_LAT = "ca.udes.bonc.ift_project.extra.lat";
    protected static final String EXTRA_CATEGORY = "ca.udes.bonc.ift_project.extra.category";
    protected static final String EXTRA_SEARCH = "ca.udes.bonc.ift_project.extra.searchString";
    protected static final String EXTRA_EVENT_DATE = "ca.udes.bonc.ift_project.extra.date";
    protected static final String EXTRA_EVENT_TITLE = "ca.udes.bonc.ift_project.extra.title";
    protected static final String EXTRA_MAX_PARTICIPANTS = "ca.udes.bonc.ift_project.event.max";
    protected static final String EXTRA_EVENT_CATEGORIE = "ca.udes.bonc.ift_project.extra.event.categorie";
    protected static final String EXTRA_EVENT_TYPE = "ca.udes.bonc.ift_project.extra.event.type";
    protected static final String EXTRA_EVENT_NAME = "ca.udes.bonc.ift_project.extra.event.name";
    protected static final String EXTRA_EVENT_ID = "ca.udes.bonc.ift_project.extra.event.id";
    protected static final String EXTRA_EVENT_AUTHOR = "ca.udes.bonc.ift_project.extra.event.author";
    protected static final String EXTRA_EVENT_MODE = "ca.udes.bonc.ift_project.extra.event.mode";
    protected static final String EXTRA_USER_ID = "ca.udes.bonc.ift_project.extra.user.id";
    protected static final String EXTRA_TEAM_NAME = "ca.udes.bonc.ift_project.extra.team.name";
    public static final String EXTRA_PLACE_NAME = "ca.udes.bonc.ift_project.extra.place.name";
    public static final String EXTRA_EVENT_DATE_STRING = "ca.udes.bonc.ift_project.extra.date.string";


    public static final int STATUS_RUNNING = 1;
    public static final int STATUS_FINISHED = 2;
    public static final int STATUS_ERROR = 0;

    protected String ApiToken = "";
    private static final String TAG = "QueryIntentService";
    protected IFTApplication myApplication = null;

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */

    public QueryIntentService(String name) {
        super(name);
    }
    public String getApiToken() {
        return ApiToken;
    }

    protected void setApiToken(String apiToken) {
        ApiToken = apiToken;
    }

    //Create request with token
    private HttpURLConnection createGetURLConnection(String url) throws MalformedURLException,IOException{
        if(getApiToken().equals("") ) {
            setApiToken(myApplication.getApiToken());
        }
        HttpURLConnection conn = HttpHelper.createGetURLConnection(HttpHelper.LOCALHOST + url);
        conn.addRequestProperty("X-Access-Token", getApiToken());
        return conn;
    }
    // String urlParameters  = "param1=a&param2=b&param3=c";
    private HttpURLConnection createPostURLConnection(String url,String urlParameters) throws MalformedURLException,IOException{
        if(getApiToken().equals("") ) {
            setApiToken(myApplication.getApiToken());
        }
        //Buffer from post data
        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
        int    postDataLength = postData.length;
        //Create connection
        URL src = new URL(HttpHelper.LOCALHOST + url);
        HttpURLConnection conn = (HttpURLConnection)src.openConnection();
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.addRequestProperty("X-Access-Token", getApiToken());
        DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
        wr.write(postData);
        return conn;
    }


    protected JSONObject handleJSONPostResponse(String url,String dataPost){
        JSONObject json= null;
        try {
            HttpURLConnection conn = createPostURLConnection(url, dataPost);
            json = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
            conn.disconnect();
        } catch(JSONException e){
            Log.e(TAG, e.getMessage());
        } catch(MalformedURLException e){
            Log.e(TAG, e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        } finally {
            return json;
        }
    }

    protected String handlePOSTResponse(String url,String dataPost){
        String response= null;
        try {
            HttpURLConnection conn = createPostURLConnection(url, dataPost);
            response = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
            conn.disconnect();
        } catch(MalformedURLException e){
            Log.e(TAG, e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        } finally {
            return response;
        }
    }

    protected String handleGETResponse(String url){
        String response= null;
        try {
            HttpURLConnection conn = createGetURLConnection(url);
            response = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
            conn.disconnect();
        } catch(MalformedURLException e){
            Log.e(TAG, e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        } finally {
            return response;
        }
    }

    protected String handleDELETEResponse(String url){
        String response= null;
        try {
            HttpURLConnection conn = createGetURLConnection(url);
            conn.setRequestMethod("DELETE");
            response = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
            conn.disconnect();
        } catch(MalformedURLException e){
            Log.e(TAG, e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        } finally {
            return response;
        }
    }

    protected JSONObject handleJSONGetResponse(String url){
        JSONObject json= null;
        try {
            HttpURLConnection conn = createGetURLConnection(url);
            json = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
            conn.disconnect();
        } catch(JSONException e){
            Log.e(TAG, e.getMessage());
        } catch(MalformedURLException e){
            Log.e(TAG, e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        } finally {
            return json;
        }
    }

    protected JSONObject handleJSONDeleteResponse(String url){
        JSONObject json= null;
        try {
            HttpURLConnection conn = createGetURLConnection(url);
            conn.setRequestMethod("DELETE");
            json = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
            conn.disconnect();
        } catch(JSONException e){
            Log.e(TAG, e.getMessage());
        } catch(MalformedURLException e){
            Log.e(TAG, e.getMessage());
        }catch(IOException e){
            Log.e(TAG,e.getMessage());
        } finally {
            return json;
        }
    }




}
