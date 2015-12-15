package ca.udes.bonc.ift_project.dataObject;


import java.util.ArrayList;
/**
 * Created by juasp on 14/12/2015.
 */
public class Team {

    private String id;
    private String name;
    private ArrayList<String> participantsList = new ArrayList<String>();
    private String image;

    public Team(String id, String name ){

        this.id = id;
        this.name= name;

    }

    public Team(String id, String name,ArrayList<String> participantsList, String image ){

        this.id = id;
        this.name= name;
        this.participantsList = participantsList;
        this.image = image;


    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getParticipantsList() {
        return participantsList;
    }

    public void setParticipantsList(ArrayList<String> participantsList) {
        this.participantsList = participantsList;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}