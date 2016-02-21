package info.androidhive.materialtabs.util;/*
package info.androidhive.tabsswipe.util;

import android.util.Log;

import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import info.androidhive.tabsswipe.User;

*/
/**
 * Created by ido on 07/12/2015.
 *//*

  class MyList<T extends Parsable> extends TreeSet<T>{
    */
/*
        //algorithm for objects in parse cloud.
        1.Compare each object by the the default compareTo- using the objectId to remove duplicates.
        2. Order the list by the second degree when it's given by user.

        -in order to give an element to this class you can use the callback method as example:
        new User(parseObject, myListObject);
        without mylistObject.add(new User..)

     *//*



    public MyList() {
        super();
        Log.v("new instance of MyList a", "");
    }

    @Override
    public Iterator<T> iterator() {
        Log.v("new instance of MyList iterator", "");
        return this.iterator();
    }

    @Override
    public boolean add(T object) {

        boolean success= super.add(object);

        return success;
    }

    @Override
    public boolean remove(Object object) {


        boolean success = super.remove(object);

        return success;
    }

    @Override
    public int size() {

        int size = this.size();

        return size;
    }



    public T get(int i){
        //tested in Eclipse= good results
        if (i>=size() || i<0)
            return null;
        Iterator<T> iterator= this.iterator();
        for (int j= 0; j<i-1; j++)
            iterator.next();
        return iterator.next();


    }




}
*/
