package ca.udes.bonc.ift_project.dataObject;

/**
 * Created by pcontat on 29/10/2015.
 */

import java.io.Serializable;

/** Implement this class from "Serializable"
 * So that you can pass this class Object to another using Intents
 * Otherwise you can't pass to another actitivy
 * */
public class Place implements Serializable {


    private String id;
    private String description;

    public Place(String description, String id) {
        this.description = description;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return description;
    }

}
