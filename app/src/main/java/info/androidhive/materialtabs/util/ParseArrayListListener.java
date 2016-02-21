package info.androidhive.materialtabs.util;

import java.util.ArrayList;

/**
 * Created by ido on 14/12/2015.
 */
public interface ParseArrayListListener <T extends Parsable>{
    /**
     * for MVC when finished collection the relational data
     * call the listeners
     */
    public void AddList(ArrayList<T> array);
}
