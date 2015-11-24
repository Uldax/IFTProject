package ca.udes.bonc.ift_project.utils;

import android.util.Log;

import ca.udes.bonc.ift_project.dataObject.Place;
import ca.udes.bonc.ift_project.dataObject.PlacesList;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.io.IOException;

/**
 * Created by pcontat on 29/10/2015.
 */
public class GooglePlaces {
    /** Global instance of the HTTP transport. */
    private static final HttpTransport transport = new ApacheHttpTransport();
    private final String TAG = "GooglePlaces";

    // Google API Key
    private static final String API_KEY = "AIzaSyCGGP1rzqfcMAFXRe48qpt1w8KTeag91v4";

    // Google Places serach url's
    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

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
}
