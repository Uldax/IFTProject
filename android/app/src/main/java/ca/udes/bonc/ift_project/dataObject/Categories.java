package ca.udes.bonc.ift_project.dataObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pcontat on 12/12/2015.
 */
public class Categories {
    public static final String CHOISIR ="Choisir";
    public static final String HOCKEY ="Hockey";
    public static final String FOOTBALL ="Football";
    public static final String SOCIAL ="Social";
    public static final String JEUXVIDEO ="Jeux video";
    public static final String RANDO ="Rando";

    public static final List<String> getCategories(){
        List<String> listCategories = new ArrayList<String>();
        listCategories.add(CHOISIR);
        listCategories.add(HOCKEY);
        listCategories.add(FOOTBALL);
        listCategories.add(SOCIAL);
        listCategories.add(JEUXVIDEO);
        listCategories.add(RANDO);
        return listCategories;
    }
}
