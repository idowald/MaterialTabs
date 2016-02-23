package info.androidhive.materialtabs.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TreeSet;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.MessagingActivity;
import info.androidhive.materialtabs.objects.Case;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.ParseArrayListListener;

/**
 * Created by ido on 08/02/2016.
 */
public class SearchAdapter  extends BaseAdapter implements AddParseObject{
    /*
    this adapter is for the main screen, it contains 3 type of search items:
    1. contacts (user name)
    2. name of conversations
    3. messages inside each chat
     */
    ArrayList<AbstractParseObject> values= new ArrayList<>();
    private User current_user = null;
    private Context context= null;

    private Comparator<AbstractParseObject> comparator= new Comparator<AbstractParseObject>() {
        @Override
        public int compare(AbstractParseObject lhs, AbstractParseObject rhs) {
            int powerLeft =0,powerRight= 0; //1 stronger than 2/3
            /*
            gets degree-> 3 the highest is users
                           2 for conversations
                           1 for messages
             */
            if (lhs instanceof Case)
                powerLeft=4;

            if (lhs instanceof  User)
                powerLeft=3;
            if (lhs instanceof Conversation)
                powerLeft=2;
            if (lhs instanceof Message)
                powerLeft=1;

            if (rhs instanceof Case)
                powerRight=4;
            if (rhs instanceof  User)
                powerRight=3;
            if (rhs instanceof Conversation)
                powerRight=2;
            if (rhs instanceof Message)
                powerRight=1;

            if (powerLeft != powerRight)
                return powerLeft-powerRight;
            else{
                switch (powerLeft) {
                    case 4: //case
                        Case left1= (Case)lhs;
                        Case right1= (Case)rhs;
                        return (left1.getFirst_name() +" "+left1.getLast_name()).compareTo(right1.getFirst_name() +" "+right1.getLast_name());
                    case 3: //User
                        User left= (User)lhs;
                        User right= (User)rhs;
                        return (left.getFirstName() +" "+left.getLastName()).compareTo(right.getFirstName()+" "+right.getLastName());
                    case 2: //Conversation
                        Conversation left2= (Conversation)lhs;
                        Conversation right2= (Conversation)rhs;
                        return left2.getConversationName().compareTo(right2.getConversationName());
                    case 1: // Messages
                        Message left3= (Message)lhs;
                        Message right3= (Message)rhs;
                        return left3.getDate().compareTo(right3.getDate());

                }

            }
            return 0;
        }
    };

    public SearchAdapter( User my_user, Context context) {

        this.current_user = my_user;
        this.context = context;




    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView= null;
        rowView  = inflater.inflate(R.layout.userlist, parent, false);
        TextView upperText = (TextView) rowView.findViewById(R.id.username);
        TextView bottomText = (TextView) rowView.findViewById(R.id.lastmessage);
        if (values.get(position) instanceof  User){
            User user= (User)values.get(position) ;
            upperText.setText(user.getFirstName()+" "+user.getLastName());
            bottomText.setText("");

        }else if(values.get(position) instanceof  Conversation){
            Conversation conversation= (Conversation)values.get(position) ;
            AddMessage addMessage= new AddMessage(upperText,context);
            addMessage.AddObject(conversation);
            upperText.setText(conversation.getConversationName());
            bottomText.setText("");

        } else if(values.get(position) instanceof  Message){
            Message message= (Message)values.get(position) ;
            message.getConversation(new AddMessage(upperText,context));
            //upperText.setText(message.getConversationName());
            bottomText.setText(message.getText());
        } else if (values.get(position) instanceof Case){
            Case c= (Case)values.get(position);
            c.getConversation(new AddMessage(upperText,context));

        }

        return rowView;
    }

    @Override
    public void AddObject(AbstractParseObject object) {

        synchronized (values) {
            values.add(object);
            Collections.sort(values, comparator);
        }
        this.notifyDataSetChanged();
    }

    public void clear() {
        values.clear();
        this.notifyDataSetChanged();
    }


    class AddMessage implements AddParseObject{

        Context context= null;
        TextView row = null;
        Conversation conversation = null;

        public AddMessage(TextView row, Context context) {
            this.row = row;
            this.context= context;

        }

        @Override
        public void AddObject(AbstractParseObject object) {
            conversation= (Conversation ) object;
            if (!conversation.IsPrivate()) {
                row.setText(conversation.getConversationName());
                if (conversation.IsCASE()){
                    //nothing special right now

                }else if (conversation.IsGROUP()){
                //nothing special right now
                }
            } else if (conversation.IsPrivate()){
                conversation.getReaders(new ParseArrayListListener<User>() {
                    @Override
                    public void AddList(ArrayList<User> array) {
                        for (User user: array)
                            if (!user.getUserName().matches(current_user.getUserName()))
                                row.setText(user.getFirstName()+" "+ user.getLastName());

                    }
                });
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(context, MessagingActivity.class);
                    Bundle b= new Bundle();
                    b.putString("conversation",conversation.getConversationName());
                    sendingObjects.myObject.put(conversation.getConversationName(),conversation);
                    b.putSerializable("my_user",current_user);
                    context.startActivity(intent);

                }
            });
        }
    }

}
