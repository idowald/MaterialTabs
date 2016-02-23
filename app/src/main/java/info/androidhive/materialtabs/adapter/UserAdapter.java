package info.androidhive.materialtabs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;

/**
 * Created by ido on 03/12/2015.
 */
public class UserAdapter  extends BaseAdapter implements AddParseObject {
    private Context context= null;
    private ArrayList<User> values= new ArrayList<User>();
    private User user= null;

    public UserAdapter(Context context, ArrayList<User> values, User user) {
        this.context = context;
        this.values = values;
        this.user = user;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView= null;
        // Change the icon for Windows and iPhone

        User message_user = values.get(position);
        Log.v("my values are","  "+message_user.getFirstName()+message_user.getLastName());

        rowView  = inflater.inflate(R.layout.userlist, parent, false);
        TextView  userView = (TextView) rowView.findViewById(R.id.username);
        TextView messageView = (TextView) rowView.findViewById(R.id.lastmessage);
        userView.setText(message_user.getFirstName()+" " +message_user.getLastName());


        messageView.setText("my message!");

        return rowView;
    }
    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int i) {
        return values.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addUser(User user) {
        Log.v("adding user to listview", user.toString());
        values.add(user);
        notifyDataSetChanged();
    }

    @Override
    public void AddObject(AbstractParseObject object) {
        this.addUser((User)object);
    }
}
