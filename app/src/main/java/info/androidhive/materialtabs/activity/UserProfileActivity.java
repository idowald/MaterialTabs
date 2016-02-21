package info.androidhive.materialtabs.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.Conversation;

import info.androidhive.materialtabs.objects.Duty;
import info.androidhive.materialtabs.objects.User;

/**
 * Created by ido on 26/12/2015.
 */
public class UserProfileActivity extends AppCompatActivity{
    private LinearLayout roles_layout= null;
    private   LinearLayout department_layout = null;
    private Toolbar toolbar = null;
    private User user = null;
    protected void onCreate(Bundle savedInstanceState) {
        // this program will get the intent with a code: http://stackoverflow.com/questions/14695537/android-update-activity-ui-from-service

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        roles_layout = (LinearLayout)findViewById(R.id.roles_linear);


        department_layout = (LinearLayout)findViewById(R.id.departments_linear);

        ReadBundle();
        getSupportActionBar().setTitle(user.getFirstName()+" " + user.getLastName());
        setUserName();
        findRolesAndDepartments();







    }
    public void setUserName(){
        ((TextView) findViewById(R.id.first_name_profile)).setText(user.getFirstName());
        ((TextView)findViewById(R.id.last_name_profile)).setText(user.getLastName());
        ((TextView) findViewById(R.id.phone_number)).setText(user.getUserName());

    }
    public void findRolesAndDepartments(){
        ParseQuery<ParseObject> query= ParseQuery.getQuery("Dutys");
        query.whereEqualTo("user",user.ToParseObject());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e== null)
                for (ParseObject item : list){
                    Duty duty = new Duty(item);
                    duty.setUser(user); //todo test if not dangerous
                    addRole(duty);
                }
                else {
                    e.printStackTrace();
                }
            }
        });




        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");

        innerquery.whereEqualTo("userName",user.getUserName());
        ParseQuery<ParseObject> query2_conversation = ParseQuery.getQuery("Conversations");
        query2_conversation.whereMatchesQuery("Users", innerquery);
        query2_conversation.whereEqualTo("conversation_type", Conversation.Conversation_type.GROUP.getString());

        query2_conversation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e== null)
                for (ParseObject item : list){
                    Conversation conv = new Conversation(item);

                    addConversation(conv);
                } else{
                    e.printStackTrace();
                }
            }
        });
    }
    public void ReadBundle(){
        Bundle b= getIntent().getExtras();
        user = (User) b.getSerializable("user");

    }
    /*
    group conversations are departments
     */
    public void addConversation(Conversation conversation){
        View child = getLayoutInflater().inflate(R.layout.department_item, null);
        TextView text = (TextView)child.findViewById(R.id.department_name);
        text.setText(conversation.getConversationName());
        department_layout.addView(child);
    }
    public void addRole(Duty duty){

        View child = getLayoutInflater().inflate(R.layout.role_item, null);
        TextView text = (TextView)child.findViewById(R.id.role_name);
        text.setText(duty.getDutyName());
        roles_layout.addView(child);

    }
/*    public boolean onCreateOptionsMenu(Menu menu) {
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
