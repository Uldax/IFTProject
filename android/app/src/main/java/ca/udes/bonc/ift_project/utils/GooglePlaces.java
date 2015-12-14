package ca.udes.bonc.ift_project.utils;

import android.nfc.Tag;
import android.util.Log;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import ca.udes.bonc.ift_project.communication.HttpHelper;
import ca.udes.bonc.ift_project.dataObject.Location;
import ca.udes.bonc.ift_project.dataObject.Place;
import ca.udes.bonc.ift_project.dataObject.PlacesList;

/**
 * Created by pcontat on 29/10/2015.
 */
public class GooglePlaces {
    /** Global instance of the HTTP transport. */
    private static final HttpTransport transport = new ApacheHttpTransport();
    private final static String TAG = "GooglePlaces";

    // Google API Key
    private static final String API_KEY = "AIzaSyBO2R9MekEDkHQNX_5L6s0Y2N4KARLs8Qk";

    // Google Places serach url's
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private double latitude;
    private double longitude;
    private double radius;

    public PlacesList search(double latitude, double longitude, double radius, String types) throws Exception {
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        PlacesList places = null;
        try {
            HttpTransport transport = new ApacheHttpTransport();
            HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
            HttpRequest request = httpRequestFactory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("location", latitude + "," + longitude);
            request.getUrl().put("radius", radius);
            request.getUrl().put("sensor", "false");
            //All place type : https://developers.google.com/places/supported_types
            //here restaurant
            if (types != null) {
                request.getUrl().put("types", types);
            }
            Log.i(TAG, "Request = " + request.toString());
            places = request.execute().parseAs(PlacesList.class);
            Log.i(TAG, "STATUS = " + places.status);

        } catch (HttpResponseException e) {
            Log.e(TAG, e.getMessage());
            ;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }  catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        finally {
            return places;
        }
    }

    //Also part of the Google APIs Client Library for Java, it offers us a factory to create HTTP GET or POST requests. The method is implemented like this
    public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                HttpHeaders headers = new HttpHeaders();
                headers.setUserAgent("Google-Places-DemoApp");
                request.setHeaders(headers);
                JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
                request.setParser(parser);
            }
        });
    }

    //get json for autocomplete input
    public ArrayList<Place> autocomplete (String input) {
        ArrayList<Place> resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //Maybe modify this to get exact adress
            sb.append("&types=(cities)");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
             Log.d(TAG, jsonResults.toString());

            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(new Place(predsJsonArray.getJSONObject(i).getString("description"),predsJsonArray.getJSONObject(i).getString("place_id")));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Cannot process JSON results", e);
        }

        return resultList;
    }

    //getLocation from place
    public static Location getPlacesLocation(String name){
        Location placeLocation = null;
        try {
            String url = PLACES_DETAILS_URL + "placeid=" + name + "&sensor=false&key=" + API_KEY;
            HttpURLConnection conn = HttpHelper.createGetURLConnection(url);
            JSONObject Json = HttpHelper.readAllJSON(conn.getInputStream(), HttpHelper.getEncoding(conn));
            //Todo handle error here
            String lat = Json.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lat");
            String lng = Json.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getString("lng");
            placeLocation = new Location(lat,lng);

        } catch(MalformedURLException e){
            Log.e(TAG,e.getMessage());
        } catch(IOException e){
            Log.e(TAG,e.getMessage());
        } catch(JSONException e){
            Log.e(TAG,e.getMessage());
        }
        return placeLocation;
    }
}
