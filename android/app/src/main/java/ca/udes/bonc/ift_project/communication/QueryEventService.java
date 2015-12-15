package ca.udes.bonc.ift_project.communication;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import java.io.UnsupportedEncodingException;
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
    private String handleActionGetMarkers(String lat, String longitude) {
        String jsonAnswer = this.handleGETResponse("/api/events?lat=" + lat + "&lng=" + longitude);
        return jsonAnswer;
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
    private String handleActionGetMarkersByRadius(String lat, String longitude,int radius) {
        String jsonAnswer = this.handleGETResponse("/api/events?lat=" + lat + "&lng=" + longitude + "&radius=" + radius);
        return jsonAnswer;
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
    private String handleActionFindEvent(String searchString) {
        String jsonAnswer = this.handleGETResponse("/api/find" + searchString);
        return jsonAnswer;
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
    private String handleActionGetOneEvent(String id) {
        String jsonAnswer = this.handleGETResponse("/api/events/" + id);
        return jsonAnswer;
    }

    public static void startActionCreateEvent(Context context, ResultReceiver mReceiver, String longitude, String latitude, String title, String category, int maxPart,String type,String placeName) {
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
        intent.putExtra(EXTRA_PLACE_NAME, placeName) ;
        context.startService(intent);
    }

    private String handleActionCreateEvent(String dataPOST) {
        Log.d(TAG, "Call createEvent with " + dataPOST);
        String jsonAnswer = this.handlePOSTResponse("/api/events/create", dataPOST);
        return jsonAnswer;
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
    private String handleActionDeleteEvent(String id) {
        String jsonAnswer = this.handleDELETEResponse("/api/events/del/" + id);
        return jsonAnswer;
    }

    //POST
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
    private String handleActionAddParticipant(String idEvent,String idParticipant) {
        String jsonAnswer = this.handlePOSTResponse("/api/events/" + idEvent + "/addParticipant", HttpHelper.encodeParamUTF8("idParticipant", idParticipant));
        return jsonAnswer;
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

    private String handleActionCreateTeam(String idEvent,String name) {
        String jsonAnswer = this.handlePOSTResponse("/api/events/" + idEvent + "/createTeam", HttpHelper.encodeParamUTF8("name", name));
        return jsonAnswer;
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



    private String handleActionShuffleParticipants(String idEvent) {
        String jsonAnswer = this.handlePOSTResponse("/api/events/" + idEvent + "/shuffleParticipants","");
        return jsonAnswer;
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

    private String handleActionAddAdmin(String idEvent,String idAdmin) {
        String jsonAnswer = this.handlePOSTResponse("/api/events/" + idEvent + "/addAdmin", HttpHelper.encodeParamUTF8("idAdmin", idAdmin));
        return jsonAnswer;
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
    //POST
    private String handleActionRemoveParticipant(String idEvent,String idParticipant) {
        String jsonAnswer = this.handlePOSTResponse("/api/events/" + idEvent + "/removeParticipant", HttpHelper.encodeParamUTF8("idParticipant", idParticipant));
        return jsonAnswer;
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

    private String handleActionFindForUser(String idUser) {
        String jsonAnswer = this.handleGETResponse("/api/events/user/" + idUser);
        return jsonAnswer;
    }

    public static void startActionFind(Context context,ResultReceiver mReceiver, String category, String name, String date, String author, String mode) {
        //binding to the service with startService()
        Intent intent = new Intent(context, QueryEventService.class);
        intent.setAction(ACTION_FIND);
        intent.putExtra(EXTRA_RECEIVER, mReceiver);
        intent.putExtra(EXTRA_EVENT_CATEGORIE, category);
        intent.putExtra(EXTRA_EVENT_NAME, name);
        intent.putExtra(EXTRA_EVENT_DATE, date);
        intent.putExtra(EXTRA_EVENT_AUTHOR, author);
        intent.putExtra(EXTRA_EVENT_MODE, mode);
        context.startService(intent);
    }

    private String handleActionFind(String category, String name,String date, String author, String mode)  {
        String path = "/api/events/find?";
        if(!category.equals(""))
            path+="category=" + category + "&";
        if(!date.equals(""))
            path+="date=" + date + "&";
        if(!name.equals(""))
            path+="name=" + name + "&";
        if(!author.equals(""))
            path+="lastName=" + author + "&";
        if(!mode.equals(""))
            path+="type=" + mode + "&";

        Log.i(TAG, path);
        String html = this.handleGETResponse(path);
        return html;
    }

    /**
     * Handle action Markers in the provided background thread with the provided
     * parameters.
     */

    //TODO : handle date
    //Take the intent and return the correct dataPOST string to create an event
    private String formatCreateEventParameters(Intent intent)  {
        final String userId = myApplication.getUserId();
        String postData = null;
        //todo add date
        try {
            final String eventDate = intent.getStringExtra(EXTRA_EVENT_DATE);
            postData = "category=" + URLEncoder.encode(intent.getStringExtra(EXTRA_CATEGORY), "UTF-8") +
                    "&createBy=" + URLEncoder.encode(userId, "UTF-8") +
                    "&admin=" + URLEncoder.encode(userId, "UTF-8") +
                    "&start=" + URLEncoder.encode("01/01/2016", "UTF-8") +
                    "&lat=" + URLEncoder.encode(intent.getStringExtra(EXTRA_LAT), "UTF-8") +
                    "&lng=" + URLEncoder.encode(intent.getStringExtra(EXTRA_LNG), "UTF-8") +
                    "&title=" + URLEncoder.encode(intent.getStringExtra(EXTRA_EVENT_TITLE), "UTF-8") +
                    "&maxParticipants=" + URLEncoder.encode(String.valueOf(intent.getIntExtra(EXTRA_MAX_PARTICIPANTS, 1)), "UTF-8") +
                    HttpHelper.encodeParamUTF8("&placeName",intent.getStringExtra(EXTRA_PLACE_NAME)) +
                    "&type=" + URLEncoder.encode(intent.getStringExtra(EXTRA_EVENT_TYPE), "UTF-8");
        } catch (UnsupportedEncodingException e){
            Log.e(TAG,e.getMessage());
        }
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
            String response = null;
            JSONObject responseJsonObject;
            try{
                //receiver.send will call onReceiveResult in the activity
                //switch on string only in java 1.7
                if(receiver!=null){
                receiver.send(STATUS_RUNNING, Bundle.EMPTY);
                switch (action) {
                    case ACTION_GET_MARKERS: {
                        final String longitude = intent.getStringExtra(EXTRA_LNG);
                        final String lat = intent.getStringExtra(EXTRA_LAT);
                        response = handleActionGetMarkers(lat, longitude);
                        break;
                    }
                    case ACTION_GET_MARKERS_RADIUS: {
                        final String longitude = intent.getStringExtra(EXTRA_LNG);
                        final String lat = intent.getStringExtra(EXTRA_LAT);
                        final int radius = intent.getIntExtra(EXTRA_RADIUS, 100);
                        response = handleActionGetMarkersByRadius(lat, longitude, radius);
                        break;
                    }
                    case ACTION_CREATE_EVENT:
                        final String dataPOST = formatCreateEventParameters(intent);
                        response = handleActionCreateEvent(dataPOST);
                        break;
                    case ACTION_FIND_EVENT:
                        final String searchString = intent.getStringExtra(EXTRA_SEARCH);
                        response = handleActionFindEvent(searchString);
                        break;
                    case ACTION_GET_ONE: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        response = handleActionGetOneEvent(eventID);
                        break;
                    }
                    case ACTION_DELETE_EVENT: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        response = handleActionDeleteEvent(eventID);
                        break;
                    }
                    case ACTION_ADD_EVENT_PARTICIPANT: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        response = handleActionAddParticipant(eventID, userID);
                        break;
                    }
                    case ACTION_ADD_EVENT_ADMIN: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        response = handleActionAddAdmin(eventID, userID);
                        break;
                    }
                    case ACTION_REMOVE_EVENT_PARTICIPANT: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        response = handleActionRemoveParticipant(eventID, userID);
                        break;
                    }
                    case ACTION_FIND_EVENT_USER: {
                        final String userID = intent.getStringExtra(EXTRA_USER_ID);
                        response = handleActionFindForUser(userID);
                        break;
                    }
                    case ACTION_FIND:
                        final String categorie = intent.getStringExtra(EXTRA_EVENT_CATEGORIE);
                        final String name = intent.getStringExtra(EXTRA_EVENT_NAME);
                        final String date = intent.getStringExtra(EXTRA_EVENT_DATE);
                        final String author = intent.getStringExtra(EXTRA_EVENT_AUTHOR);
                        final String mode = intent.getStringExtra(EXTRA_EVENT_MODE);
                        response = handleActionFind(categorie, name, date, author, mode);
                        break;
                    case ACTION_CREATE_TEAM: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        final String teamName = intent.getStringExtra(EXTRA_TEAM_NAME);
                        response = handleActionCreateTeam(eventID, teamName);
                        break;
                    }
                    case ACTION_SHUFFLE_PARTICIPANTS: {
                        final String eventID = intent.getStringExtra(EXTRA_EVENT_ID);
                        response = handleActionShuffleParticipants(eventID);
                        //Return the response
                        break;
                    }
                }
                if(response == null){
                    b.putString(Intent.EXTRA_TEXT, "internal error");
                    receiver.send(STATUS_ERROR, b);
                  } else {
                    //convert to json to check error
                    Object json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        responseJsonObject = (JSONObject) json;
                        if (responseJsonObject.has("error")) {
                            b.putString(Intent.EXTRA_TEXT, responseJsonObject.getString("error"));
                            receiver.send(STATUS_ERROR, b);
                        } else {
                            b.putString("action", action);
                            b.putString("jsonType", "object");
                            b.putString("results", response);
                            receiver.send(STATUS_FINISHED, b);
                        }
                    } else if (json instanceof JSONArray) {
                        //empty array , if array no error
                        b.putString("results", response);
                        b.putString("jsonType", "array");
                        b.putString("action", action);
                        receiver.send(STATUS_FINISHED, b);
                    }
                }

            }
            } catch (JSONException e){
                Log.e(TAG, e.getMessage() );
                receiver.send(STATUS_ERROR, b);
            }

        }
    }
}

