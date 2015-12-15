package ca.udes.bonc.ift_project.dataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pcontat on 12/12/2015.
 */
public class Categories {
    public static final String CHOISIR ="Choisir";
    public static final String HOCKEY ="hockey";
    public static final String FOOTBALL ="football";
    public static final String SOCIAL ="social";
    public static final String VIDEOGAME ="videogame";
    public static final String RANDO ="rando";

    public static final List<String> getCategories(){
        List<String> listCategories = new ArrayList<String>();
        listCategories.add(CHOISIR);
        listCategories.add(HOCKEY);
        listCategories.add(FOOTBALL);
        listCategories.add(SOCIAL);
        listCategories.add(VIDEOGAME);
        listCategories.add(RANDO);
        return listCategories;
    }
}
