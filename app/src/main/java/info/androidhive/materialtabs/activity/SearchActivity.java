package info.androidhive.materialtabs.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.adapter.SearchAdapter;
import info.androidhive.materialtabs.objects.Case;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;

public class SearchActivity extends AppCompatActivity {
    User current_user= null;
    EditText search_Text= null;
    Button search_btn =null;
    ListView search_list= null;
    SearchAdapter<AbstractParseObject> searchAdapter= null;
    int search_max= 3;
    Integer search= search_max;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        readBundle();

        search_Text = (EditText)findViewById(R.id.search_input);
        search_btn = (Button)findViewById(R.id.button);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchAdapter.clear();

               final ProgressDialog progress = new ProgressDialog(v.getContext());
                progress.setTitle("searching");
                progress.setMessage("Wait while loading...");
                progress.show();
// To dismiss the dialog
                String search_str = search_Text.getText().toString();

                ParseQuery<ParseObject> query_messages = new ParseQuery<ParseObject>("Messages");
                query_messages.whereContains("text",search_str);
                query_messages.fromLocalDatastore();
                query_messages.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e== null){
                            for (ParseObject object :objects){
                                Message message= new Message(object);

                                message.getConversation(new AddParseObject<Conversation>() {
                                    @Override
                                    public void AddObject(Conversation object) {
                                        searchAdapter.AddObject(object);
                                    }
                                });

                            }

                        }else{
                            e.printStackTrace();
                        }
                        synchronized (search){
                            if (--search == 0) {
                                progress.dismiss();
                                search=search_max;
                            }
                        }

                    }
                });
                ParseQuery<ParseObject> query_users = new ParseQuery<ParseObject>("Users");
                query_users.whereNotEqualTo("userName",current_user.getUserName());
                query_users.whereContains("firstName",search_str);
                ParseQuery<ParseObject> query_users_last_name = new ParseQuery<ParseObject>("Users");
                query_users_last_name.whereNotEqualTo("userName",current_user.getUserName());
                query_users_last_name.whereContains("lastName",search_str);
                ArrayList<ParseQuery<ParseObject>> list = new ArrayList<ParseQuery<ParseObject>>();
                list.add(query_users);
                list.add(query_users_last_name);
                ParseQuery.or(list).findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null)
                        {
                            for (ParseObject object: objects){
                                User user= new User(object);
                                searchAdapter.AddObject(user);
                            }
                        }else{
                            e.printStackTrace();

                        }
                        synchronized (search){
                            if (--search == 0) {
                                progress.dismiss();
                                search=search_max;
                            }
                        }
                    }
                });

                ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");
                innerquery.whereEqualTo("userName",current_user.getUserName());
                ParseQuery<ParseObject> query_conversation = new ParseQuery<ParseObject>("Conversation");
                query_conversation.whereContains("conversationName",search_str);
                query_conversation.whereNotEqualTo("conversation_type",Conversation.Conversation_type.PRIVATE.getString());
                query_conversation.whereMatchesQuery("Users", innerquery);
                query_conversation.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null){
                            for (ParseObject object: objects){
                                Conversation conversation= new Conversation(object);
                                if (conversation.IsGROUP())
                                searchAdapter.AddObject(conversation);
                                else if (conversation.IsCASE())
                                {
                                    ParseQuery<ParseObject> queryCase= new ParseQuery<ParseObject>("Cases");
                                    queryCase.whereEqualTo("conversation",conversation.ToParseObject());
                                    queryCase.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null)
                                                searchAdapter.AddObject( new Case(object));
                                            else{
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }else{
                            e.printStackTrace();
                        }
                        synchronized (search){
                            if (--search == 0) {
                                progress.dismiss();
                                search=search_max;
                            }

                        }
                    }
                });






            }
        });
        search_list= (ListView)findViewById(R.id.list_search);
        searchAdapter = new SearchAdapter<>(current_user,this);
        search_list.setAdapter(searchAdapter);









    }

    void readBundle(){
       Bundle bundle= getIntent().getExtras();
        current_user =(User) bundle.getSerializable("user");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_icon_text_tabs, menu);
        return super.onCreateOptionsMenu(menu);
    }


}
