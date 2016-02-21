package info.androidhive.materialtabs.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Duty;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.ParseArrayListListener;

/**
 * Created by ido on 05/01/2016.
 */
public class GroupProfileActivity extends AppCompatActivity {
    private Toolbar toolbar = null;
    private LinearLayout praticipating_layout = null;
    private Conversation conversation = null;
    private ArrayList<User> readers= null;


    protected void onCreate(Bundle savedInstanceState) {
        // this program will get the intent with a code: http://stackoverflow.com/questions/14695537/android-update-activity-ui-from-service

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        praticipating_layout = (LinearLayout)findViewById(R.id.praticipating_linear);
        ReadBundle();


        getSupportActionBar().setTitle(conversation.getConversationName());
        ((TextView) findViewById(R.id.group_name)).setText(conversation.getConversationName());
        setConversationReaders();
    }

    public void setConversationReaders(){
        /*
        put all the users in the conversation on the layout.
        then for each user get his duty in the conversation and attach it to his right part of the name
         */
        //1. get all readers from conversation
        //2. for each reader fetch all his duties
        //3. if duty name contain the conversation name than it's a match.

        conversation.getReaders(new ParseArrayListListener<User>() {
            @Override
            public void AddList(ArrayList<User> array) {
                readers= array;
                ParseQuery<ParseObject> getDuties = ParseQuery.getQuery("Dutys");
                getDuties.whereEqualTo("conversation",conversation.ToParseObject());

                getDuties.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(final List<ParseObject> objects, ParseException e) {
                        if (e== null)
                        {

                            for (ParseObject dutyobject : objects) {
                                //creating duties
                               final Duty duty= new Duty(dutyobject);
                                duty.getUser(new AddParseObject<User>() {
                                    @Override
                                    public void AddObject(User user) {

                                            addReader(user, duty);
                                            readers.remove(user);



                                    }
                                });

                            }
                                for (User user2 : readers)
                                    addReader(user2,null);

                        }else{
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

    }
    ArrayList<String> childs = new ArrayList<>();
    public void addReader(User user, Duty duty){

        View child = getLayoutInflater().inflate(R.layout.conversation_user_item, null);
        TextView text_name = (TextView)child.findViewById(R.id.user_name);
        String user_name= user.getFirstName()+ " ";
        if (user.getLastName().length()>3)
            user_name+=user.getLastName().substring(0,3)+"."; //setting last name shorter
        else
            user_name+= user.getLastName();
        text_name.setText(user_name);


        TextView role_name = (TextView)child.findViewById(R.id.role_name);

        if (duty != null)
        {
            role_name.setText(duty.getDutyName());
        }
        else
            role_name.setText("");
        if (childs.contains(user.getUserName())) {
            praticipating_layout.removeViewAt(childs.indexOf(user.getUserName()));

        } else
        childs.add(user.getUserName());
        praticipating_layout.addView(child);
    }
    public void ReadBundle(){
        Bundle b= getIntent().getExtras();
        conversation = (Conversation) sendingObjects.getElement((String) b.getSerializable("conversation"));

    }
  /*  public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_private_chat, menu);
        return super.onCreateOptionsMenu(menu);


    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
