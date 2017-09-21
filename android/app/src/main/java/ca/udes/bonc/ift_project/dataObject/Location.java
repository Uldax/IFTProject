package ca.udes.bonc.ift_project.dataObject;

/**
 * Created by pcontat on 14/12/2015.
 */
public class Location {
    public String getLng() {
        return lng;
    }

    public String getLat() {
        return lat;
    }

    private String lat;
    private String lng;
    public Location(String lat , String lng){
        this.lat = lat;
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
