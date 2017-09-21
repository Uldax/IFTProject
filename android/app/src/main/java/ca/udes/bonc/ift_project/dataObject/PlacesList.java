package ca.udes.bonc.ift_project.dataObject;

import com.google.api.client.util.Key;

import java.io.Serializable;
import java.util.List;

/**
 * Created by pcontat on 29/10/2015.
 */
public class PlacesList implements Serializable {

    @Key
    public String status;

    @Key
    public List<Place> results;

}
