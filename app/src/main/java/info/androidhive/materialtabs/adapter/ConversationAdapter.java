package info.androidhive.materialtabs.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import info.androidhive.materialtabs.DB.DbHelper;
import info.androidhive.materialtabs.DB.MessagesDB;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.fragments.PrivateChatTab;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.ParseArrayListListener;


/**
 * Created by ido on 14/12/2015.
 */
public class ConversationAdapter extends BaseAdapter implements AddParseObject {
    /**
     *  this class supoorts showing group and private conversations
     */
    private Context context= null;

    private ArrayList<Touple> values = new ArrayList<Touple>(); //list contains on 0 conversation on 1 the messaageDB
    private Comparator<Touple> comparator = new Comparator<Touple>() {
        @Override
        public int compare(Touple lhs, Touple rhs) {
            MessagesDB left_message = lhs.getMessagesDB();
            MessagesDB right_message = rhs.getMessagesDB();
            if (left_message == null)
                return 1;
            if (right_message == null)
                return -1;

            return -left_message.date.compareTo(right_message.date);
        }
    };
    private User my_user = null;

    public ConversationAdapter(Context context , User my_user) {
        this.context = context;
        //this.values = new ArrayList<>();
        this.my_user = my_user;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView= null;
        // Change the icon for Windows and iPhone
        int i= 0;
        Touple list= null;
        for (Touple list2: values){
            if (i== position) {
                list= list2;
                break;
            }
            i++;
        }
        final Conversation conversation_item = (Conversation ) list.getConversation();

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

        MessagesDB messagesDB =  list.getMessagesDB();
        if (messagesDB == null) {

            messageView.setText("");
        }
        else{
            if (messagesDB.is_new >0) {
                userView.setTextColor(Color.BLACK);
                messageView.setTextColor(Color.BLACK);
            }
            else{
                messageView.setTextColor(Color.GRAY);
            }
                messageView.setText(messagesDB.Text);
        }
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivateChatTab.openConversation(context, conversation_item);
            }
        });





        return rowView;
    }
    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {

        int i= 0;
        Touple list= null;
        for (Touple list2: values){
            if (i== position) {
                list= list2;
                break;
            }
            i++;
        }
        return list;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addConversation(Conversation conversation) {
        Log.v("adding user to listview", conversation.toString());

        MessagesDB messagesDB = DbHelper.getLastMessage(conversation.getConversationObjectId());

        Touple touple = new Touple(conversation, messagesDB);

        values.add(touple);

        Collections.sort(values,comparator);
        notifyDataSetChanged();
    }

    @Override
    public void AddObject(AbstractParseObject object) {
        this.addConversation((Conversation) object);
    }


    class Touple {
        Conversation conversation = null;
        MessagesDB messagesDB = null;

        public Touple(Conversation conversation, MessagesDB messagesDB) {
            this.conversation = conversation;
            this.messagesDB = messagesDB;
        }

        public Conversation getConversation() {
            return conversation;
        }

        public void setConversation(Conversation conversation) {
            this.conversation = conversation;
        }

        public MessagesDB getMessagesDB() {
            return messagesDB;
        }

        public void setMessagesDB(MessagesDB messagesDB) {
            this.messagesDB = messagesDB;
        }

    }
    public void addMessage(Message message){
        String conversation_id= message.getConversationObjectId();

        for (Touple touple : values){
            if (touple.getConversation().getConversationObjectId().matches(conversation_id)){

                touple.setMessagesDB(MessagesDB.convertMessageToMessageDB(message));
                notifyDataSetChanged();
            }
        }

    }
}
