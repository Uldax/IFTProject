package ca.udes.bonc.ift_project.communication;

import android.app.IntentService;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import ca.udes.bonc.ift_project.IFTApplication;

public abstract class QueryIntentService extends IntentService {
    protected static final String ACTION_GET_MARKERS = "ca.udes.bonc.ift_project.action.getMarkers";
    protected static final String ACTION_FIND_EVENT = "ca.udes.bonc.ift_project.action.findEvent";
    protected static final String ACTION_GET_ONE = "ca.udes.bonc.ift_project.action.getOneEvent";
    protected static final String ACTION_ADD_EVENT_PARTICIPANT = "ca.udes.bonc.ift_project.action.addParticipant";
    protected static final String ACTION_ADD_EVENT_ADMIN = "ca.udes.bonc.ift_project.action.addAdmin";
    protected static final String ACTION_CREATE_EVENT = "ca.udes.bonc.ift_project.action.createEvent";


    protected static final String EXTRA_LNG = "ca.udes.bonc.ift_project.extra.lng";
    protected static final String EXTRA_RECEIVER = "ca.udes.bonc.ift_project.extra.receiver";
    protected static final String EXTRA_RADIUS = "ca.udes.bonc.ift_project.extra.radius";
    protected static final String EXTRA_LAT = "ca.udes.bonc.ift_project.extra.lat";
    protected static final String EXTRA_CATEGORY = "ca.udes.bonc.ift_project.extra.category";
    protected static final String EXTRA_SEARCH = "ca.udes.bonc.ift_project.extra.searchString";
    protected static final String EXTRA_EVENT_DATE = "ca.udes.bonc.ift_project.extra.date";
    protected static final String EXTRA_EVENT_TITLE = "ca.udes.bonc.ift_project.extra.title";
    protected static final String EXTRA_MAX_PARTICIPANTS = "ca.udes.bonc.ift_project.event.max";
    protected static final String EXTRA_EVENT_TYPE = "ca.udes.bonc.ift_project.extra.event.type";
    protected static final String EXTRA_EVENT_ID = "ca.udes.bonc.ift_project.extra.event.id";


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
    protected HttpURLConnection createGetURLConnection(String url) throws MalformedURLException,IOException{
        if(getApiToken().equals("") ) {
            setApiToken(myApplication.getApiToken());
        }
        URL src = new URL(HttpHelper.LOCALHOST + url);
        HttpURLConnection conn = (HttpURLConnection)src.openConnection();
        conn.addRequestProperty("X-Access-Token", getApiToken());
        return conn;
    }
    // String urlParameters  = "param1=a&param2=b&param3=c";
    protected HttpURLConnection createPostURLConnection(String url,String urlParameters) throws MalformedURLException,IOException{
        //Buffer from post data
        byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
        int    postDataLength = postData.length;
        //Create connection
        HttpURLConnection conn= this.createGetURLConnection(url);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
        wr.write(postData);
        return conn;
    }
}
