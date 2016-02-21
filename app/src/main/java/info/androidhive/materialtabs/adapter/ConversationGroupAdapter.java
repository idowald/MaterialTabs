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
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.ParseArrayListListener;


/**
 * Created by ido on 14/12/2015.
 */
public class ConversationGroupAdapter extends BaseAdapter implements AddParseObject<Conversation> {

    private Context context= null;
    private ArrayList<Conversation> values= new ArrayList<Conversation>();
    private User my_user = null;

    public ConversationGroupAdapter(Context context, ArrayList<Conversation> values , User my_user) {
        this.context = context;
        this.values = values;
        this.my_user = my_user;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView= null;
        // Change the icon for Windows and iPhone

        Conversation conversation_item = values.get(position);
        Log.v("generating conversation",  conversation_item.getConversationName() + conversation_item.getConversation_type());

        rowView  = inflater.inflate(R.layout.userlist, parent, false);
        final TextView userView = (TextView) rowView.findViewById(R.id.username);
        TextView messageView = (TextView) rowView.findViewById(R.id.lastmessage);
        if (conversation_item.IsPrivate()) {
            ParseArrayListListener<User> callback = new ParseArrayListListener<User>() {
                @Override
                public void AddList(ArrayList<User> array) {
                    for (User user: array){
                        if (user.compareTo(my_user) !=0)
                            userView.setText(user.getFirstName() + " " + user.getLastName());
                    }
                }
            };

            conversation_item.getReaders(callback);
        }
        else{
            userView.setText(conversation_item.getConversationName());
        }



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

    public void addConversation(Conversation conversation) {
        Log.v("adding user to listview", conversation.toString());
        values.add(conversation);
        notifyDataSetChanged();
    }

    @Override
    public void AddObject(Conversation object) {
        this.addConversation(object);
    }

}
