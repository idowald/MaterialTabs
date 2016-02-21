package info.androidhive.materialtabs.objects;

import java.util.HashMap;

/**
 * Created by ido on 06/01/2016.
 */
public class sendingObjects {

    public static HashMap<String,Object> myObject= new HashMap<>();

    public static Object getElement(String s){
        Object ob= myObject.get(s);
        myObject.remove(s);
        return ob;

    }



}
