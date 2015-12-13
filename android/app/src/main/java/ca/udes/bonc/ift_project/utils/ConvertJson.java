package ca.udes.bonc.ift_project.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import ca.udes.bonc.ift_project.dataObject.Event;

/**
 * Created by cbongiorno on 12/12/2015.
 */
public class ConvertJson {
    public static List<Event> convert_event(String input){
        List<Event> listEvent = new ArrayList<Event>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        try {
            JSONArray arrayListJson = new JSONArray(input);
            for (int i=0; i < arrayListJson.length(); i++)
            {
                JSONObject oneObject = arrayListJson.getJSONObject(i);
                Event event = new Event();
                event.setId(oneObject.getString("_id"));
                event.setTitle(oneObject.getString("title"));
                event.setCategory(oneObject.getJSONObject("detail").getString("category"));
                event.setAuthor(oneObject.getJSONObject("detail").getString("createBy"));
                event.setType(oneObject.getJSONObject("detail").getString("type"));
                event.setDate(formatter.parse(oneObject.getJSONObject("detail").getString("start")));
                event.setLatitude(oneObject.getJSONObject("position").getDouble("lat"));
                event.setLongitude(oneObject.getJSONObject("position").getDouble("lng"));

                listEvent.add(event);
            }

        }catch (JSONException e){
            Log.e("Json - convert_event",e.getMessage());
        } catch (ParseException e) {
            Log.e("Json - convert_event", e.getMessage());
        }
        return listEvent;
    }
}
