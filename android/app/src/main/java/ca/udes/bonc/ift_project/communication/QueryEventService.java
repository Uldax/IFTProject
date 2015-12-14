package ca.udes.bonc.ift_project.communication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import ca.udes.bonc.ift_project.IFTApplication;

/**
 * Created by pcontat on 09/12/2015.
 */
public class QueryEventService extends QueryIntentService {

    private static final String TAG = "EventIntentService";
    public QueryEventService() {
        super("QueryEventService");
    }
    /**
    * Start Action
    */
    public static void startActionGetMarkers(Context context,ResultReceiver mReceiver, String longitude, String latitude) {
        //binding to the service with startService()
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_GET_MARKERS);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_LNG, longitude);
        intent.putExtra(EXTRA_LAT, latitude);
        context.startService(intent);
    }

    private String handleActionGetMarkers(String lat, String longitude) throws MalformedURLException,IOException {
        HttpURLConnection conn = createGetURLConnection("/api/events?lat=" + lat + "&lng=" + longitude);
        Log.i(TAG, "call to/api/events?lat=" + lat + "&long=" + longitude);
        String html = readAnswer(conn);
        conn.disconnect();
        return html;
    }

    public static void startActionGetMarkersByRadius(Context context,ResultReceiver mReceiver, String longitude, String latitude,int radius) {
        //binding to the service with startService()
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_GET_MARKERS_RADIUS);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_LNG, longitude);
        intent.putExtra(EXTRA_LAT, latitude);
        intent.putExtra(EXTRA_RADIUS, radius);
        context.startService(intent);
    }
    private String handleActionGetMarkersByRadius(String lat, String longitude,int radius) throws MalformedURLException,IOException {
        HttpURLConnection conn = createGetURLConnection("/api/events?lat=" + lat + "&lng=" + longitude + "&radius=" + radius);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    //note : searchString => ?type=X&category=Y&title=Z etc...
    public static void startActionFindEvent(Context context,ResultReceiver mReceiver, String searchString) {
        //binding to the service with startService()
        Log.d(TAG, "Start find event");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_FIND_EVENT);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_SEARCH, searchString);
        context.startService(intent);
    }

    private String handleActionFindEvent(String searchString) throws MalformedURLException,IOException {
        HttpURLConnection conn = createGetURLConnection("/api/find" + searchString);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionGetOneEvent(Context context,ResultReceiver mReceiver, String eventId) {
        //binding to the service with startService()
        Log.d(TAG, "Start getOne event");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_GET_ONE);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        context.startService(intent);
    }

    private String handleActionGetOneEvent(String id) throws MalformedURLException,IOException {
        HttpURLConnection conn = createGetURLConnection("/api/events/" + id);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionCreateEvent(Context context, ResultReceiver mReceiver, String longitude, String latitude, String title, String category, int maxPart,String type) {
        //binding to the service with startService()
        Log.d(TAG, "Start createMarkers");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_CREATE_EVENT);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_LNG, longitude);
        intent.putExtra(EXTRA_LAT, latitude);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_EVENT_TITLE, title);
        intent.putExtra(EXTRA_MAX_PARTICIPANTS, maxPart);
        intent.putExtra(EXTRA_EVENT_TYPE, type) ;
        context.startService(intent);
    }

    private String handleActionCreateEvent(String dataPost) throws MalformedURLException,IOException,JSONException {
        Log.d(TAG, "Call createEvent with " + dataPost);
        HttpURLConnection conn = createPostURLConnection("/api/events/create", dataPost);
        JSONObject json = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
        //Use getString if it's an error for the data to be missing, or optString if you're not sure if it will be there.
        String error = json.optString ("error");
        String message = null;
        if(error != ""){
            Log.e(TAG, "message receive from api = " + error);
        } else{
            message = json.getString ("message");
        }
        conn.disconnect();
        return message;
    }

    public static void startActionDeleteEvent(Context context, ResultReceiver mReceiver, String eventID) {
        //binding to the service with startService()
        Log.d(TAG, "Start delete event");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_DELETE_EVENT);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        context.startService(intent);
    }
    private String handleActionDeleteEvent(String id) throws MalformedURLException,IOException {
        HttpURLConnection conn = createGetURLConnection("/api/events/del/" + id);
        conn.setRequestMethod("DELETE");
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionAddParticipant(Context context, ResultReceiver mReceiver, String eventID,String userID) {
        //binding to the service with startService()
        Log.d(TAG, "Start add participant");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_ADD_EVENT_PARTICIPANT);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        intent.putExtra(EXTRA_USER_ID, userID);
        context.startService(intent);
    }
    //post

    private String handleActionAddParticipant(String idEvent,String idParticipant) throws MalformedURLException,IOException {
        HttpURLConnection conn = createPostURLConnection("/api/events/" + idEvent + "/addParticipant", HttpHelper.encodeParamUTF8("idParticipant", idParticipant));
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionCreateTeam(Context context, ResultReceiver mReceiver, String eventID,String teamName) {
        //binding to the service with startService()
        Log.d(TAG, "Start create team");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_CREATE_TEAM);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        intent.putExtra(EXTRA_TEAM_NAME, teamName);
        context.startService(intent);
    }

    private String handleActionCreateTeam(String idEvent,String name) throws MalformedURLException,IOException {
        HttpURLConnection conn = createPostURLConnection("/api/events/" + idEvent + "/createTeam", HttpHelper.encodeParamUTF8("name", name));
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionShuffleParticipants(Context context, ResultReceiver mReceiver, String eventID) {
        //binding to the service with startService()
        Log.d(TAG, "Start create team");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_SHUFFLE_PARTICIPANTS);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        context.startService(intent);
    }

    private String handleActionShuffleParticipants(String idEvent) throws MalformedURLException,IOException {
        HttpURLConnection conn = createPostURLConnection("/api/events/" + idEvent + "/shuffleParticipants", HttpHelper.encodeParamUTF8("name", ""));
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }



    public static void startActionAddAdmin(Context context, ResultReceiver mReceiver, String eventID,String userID) {
        //binding to the service with startService()
        Log.d(TAG, "Start add admin");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_ADD_EVENT_ADMIN);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        intent.putExtra(EXTRA_USER_ID, userID);
        context.startService(intent);
    }

    private String handleActionAddAdmin(String idEvent,String idAdmin) throws MalformedURLException,IOException {
        HttpURLConnection conn = createPostURLConnection("/api/events/" + idEvent + "/addAdmin", HttpHelper.encodeParamUTF8("idAdmin", idAdmin));
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionRemoveParticipant(Context context, ResultReceiver mReceiver, String eventID,String userID) {
        //binding to the service with startService()
        Log.d(TAG, "Start remove participant");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_REMOVE_EVENT_PARTICIPANT);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_ID, eventID);
        intent.putExtra(EXTRA_USER_ID, userID);
        context.startService(intent);
    }
    //post
    private String handleActionRemoveParticipant(String idEvent,String idParticipant) throws MalformedURLException,IOException {
        HttpURLConnection conn = createPostURLConnection("/api/events/" + idEvent + "/removeParticipant", HttpHelper.encodeParamUTF8("idParticipant", idParticipant));
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    public static void startActionFindForUser(Context context, ResultReceiver mReceiver,String userID) {
        //binding to the service with startService()
        Log.d(TAG, "Start find event for user");
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_FIND_EVENT_USER);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_USER_ID, userID);
        context.startService(intent);
    }

    private String handleActionFindForUser(String idUser) throws MalformedURLException,IOException {
        HttpURLConnection conn = createGetURLConnection("/api/events/user/" + idUser);
        String html = HttpHelper.readAll(conn.getInputStream(), HttpHelper.getEncoding(conn));
        conn.disconnect();
        return html;
    }

    /**
     * Handle action Markers in the provided background thread with the provided
     * parameters.
     */

    //TODO : handle date
    //Take the intent and return the correct dataPost string to create an event
    private String formatCreateEventParameters(Intent intent) throws UnsupportedEncodingException {
        final String userId = myApplication.getUserId();
        //todo add
        final String eventDate = intent.getStringExtra(EXTRA_EVENT_DATE);
        String postData = "category=" + URLEncoder.encode(intent.getStringExtra(EXTRA_CATEGORY), "UTF-8") +
                "&createBy="+ URLEncoder.encode(userId, "UTF-8") +
                "&admin=" + URLEncoder.encode(userId, "UTF-8") +
                "&start=" + URLEncoder.encode("01/01/2016", "UTF-8") +
                "&lat=" + URLEncoder.encode(intent.getStringExtra(EXTRA_LAT), "UTF-8") +
                "&lng=" + URLEncoder.encode(intent.getStringExtra(EXTRA_LNG), "UTF-8") +
                "&title=" + URLEncoder.encode(intent.getStringExtra(EXTRA_EVENT_TITLE), "UTF-8") +
                "&maxParticipants=" + URLEncoder.encode(String.valueOf(intent.getIntExtra(EXTRA_MAX_PARTICIPANTS, 1)), "UTF-8") +
                "&type=" + URLEncoder.encode(intent.getStringExtra(EXTRA_EVENT_TYPE), "UTF-8") ;
        return postData;
    }

    //Onandle
    @Override
    protected void onHandleIntent(Intent intent) {
        if( myApplication == null) {
            myApplication = (IFTApplication)getApplication();
        }
        if (intent != null) {
            final ResultReceiver receiver = intent.getParcelableExtra(EXTRA_RECEIVER);
            String action = intent.getAction();
            Bundle b = new Bundle();
            String result;
            try {
                //receiver.send will call onReceiveResult in the activity
                //switch on string only in java 1.7
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                if(action.equals(ACTION_GET_MARKERS)) {
                    final String longitude = intent.getStringExtra(EXTRA_LNG);
                    final String lat = intent.getStringExtra(EXTRA_LAT);
                    result = handleActionGetMarkers(lat,longitude);
                } else if(action.equals(ACTION_GET_MARKERS_RADIUS)) {
                    final String longitude = intent.getStringExtra(EXTRA_LNG);
                    final String lat = intent.getStringExtra(EXTRA_LAT);
                    final int radius = intent.getIntExtra(EXTRA_RADIUS,100);
                    result = handleActionGetMarkersByRadius(lat, longitude,radius);
                } else if(action.equals(ACTION_CREATE_EVENT)) {
                    final String dataPost = formatCreateEventParameters(intent);
                        result = handleActionCreateEvent(dataPost);
                } else if(action.equals(ACTION_FIND_EVENT)){
                    final String searchString = intent.getStringExtra(EXTRA_SEARCH);
                        result = handleActionFindEvent(searchString);
                } else if(action.equals(ACTION_GET_ONE)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        result = handleActionGetOneEvent(eventID);
                } else if(action.equals( ACTION_DELETE_EVENT)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        result = handleActionDeleteEvent(eventID);
                } else if(action.equals( ACTION_ADD_EVENT_PARTICIPANT)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                    final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        result = handleActionAddParticipant(eventID,userID);
                }else if(action.equals( ACTION_ADD_EVENT_ADMIN)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                    final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        result = handleActionAddAdmin(eventID, userID);
                } else if(action.equals( ACTION_REMOVE_EVENT_PARTICIPANT)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                    final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        result = handleActionRemoveParticipant(eventID,userID);
                } else if(action.equals( ACTION_FIND_EVENT_USER)){
                    final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        result = handleActionFindForUser(userID);
                } else if(action.equals( ACTION_CREATE_TEAM)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                    final String teamName = intent.getStringExtra(EXTRA_TEAM_NAME);
                    result = handleActionCreateTeam(eventID, teamName);
                } else if(action.equals( ACTION_SHUFFLE_PARTICIPANTS)){
                    final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                    result = handleActionShuffleParticipants(eventID);
                } else {
                    result = "action doesn't exists";
                }
                if(result == null){
                    b.putString(Intent.EXTRA_TEXT, "error");
                    receiver.send(STATUS_ERROR, b);
                }
                else {
                    b.putString("results", result);
                    receiver.send(STATUS_FINISHED, b);
                    Log.i(TAG, "receiver send");
                }
            } catch(Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }
        }
    }
}
