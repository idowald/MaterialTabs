package info.androidhive.materialtabs.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import info.androidhive.materialtabs.DB.DbHelper;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;


/**
 * Created by ido on 21/11/2015.
 */

public class ConversationMessagesAdapter extends BaseAdapter {
    //algorithm to attach users to messages:
    //for each message will have a key that describe to which user it belongs.
    //search in the users list the correct user to each key

    private  Context context;
    private ArrayList<Message> values= new ArrayList<Message>();
    private HashMap<String,User> users= new HashMap<String,User>();

    private  User user= null;

    public ConversationMessagesAdapter(Context context,ArrayList<Message> values,User my_user) {

        this.context = context;
        this.values = values;
        this.user = my_user;


    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        User sending_user = users.get(values.get(position).fromUserName);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView= null;
        rowView  = inflater.inflate(R.layout.message_left, parent, false);
        //for each message it checks if it's my user or other one
        TextView textView= null;
        TextView senderView = null;
        try {
            if (user.compareTo(sending_user) == 0) {
                rowView = inflater.inflate(R.layout.message_right, parent, false);
                textView = (TextView) rowView.findViewById(R.id.Sendertxt_right);
                senderView = (TextView) rowView.findViewById(R.id.SenderName_right);

            } else {
                rowView = inflater.inflate(R.layout.message_left, parent, false);
                textView = (TextView) rowView.findViewById(R.id.txtSender_left);
                senderView = (TextView) rowView.findViewById(R.id.SenderName_left);
                //TextView dateView = (TextView) rowView.findViewById(R.id.txtDate);
            }
        }catch (Exception e2){
                Log.v("what?","");
            }
        senderView.setText(sending_user.getFirstName() + sending_user.getLastName());
        textView.setText(values.get(position).getText());


       // values.get(position).getFrom(new inflateViewWithUsers(user,inflater,position, parent,rowView ));

        return rowView;
    }


   /* public class inflateViewWithUsers implements AddParseObject<User>{

        User my_user =null;
        LayoutInflater inflater = null;
        int position = 0;
        ViewGroup parent = null;
        View rowView = null;

        public inflateViewWithUsers(User my_user, LayoutInflater inflater, int position, ViewGroup parent ,View rowView ) {
            this.my_user = my_user;
            this.rowView = rowView;
            this.inflater = inflater;
            this.position = position;
            this.parent = parent;
        }

        @Override
        public void AddObject(User user) {





            ListView view= (ListView) rootView.findViewById(R.id.listMessages);
            rowView = view.getChildAt(position);
            TextView textView= null;
            TextView senderView = null;
            if (my_user.compareTo(user) ==0) {
                rowView  = inflater.inflate(R.layout.message_right, parent, false);
                textView = (TextView) rowView.findViewById(R.id.Sendertxt_right);
                senderView = (TextView) rowView.findViewById(R.id.SenderName_right);

            } else {
                rowView  = inflater.inflate(R.layout.message_left, parent, false);
                textView = (TextView) rowView.findViewById(R.id.txtSender_left);
                senderView = (TextView) rowView.findViewById(R.id.SenderName_left);
                //TextView dateView = (TextView) rowView.findViewById(R.id.txtDate);
            }
            senderView.setText(my_user.getFirstName() + my_user.getLastName());
            textView.setText(values.get(position).getText());

        }
    }*/
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

    public void addMessage(Message message, User user){
        /**
         * the new message and the sending user
         */
        Log.v("adding message", message.toString());
        values.add(message);
        users.put(user.getUserName(),user);

        notifyDataSetChanged(); //todo to make faster with parent.getChildAt

        DbHelper.ReadMessage(message.GetObjectId());



    }


}
