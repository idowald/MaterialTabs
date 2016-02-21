package info.androidhive.materialtabs.activity;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.adapter.AutoCompleteDBAdapter;
import info.androidhive.materialtabs.adapter.ContactAdapter;
import info.androidhive.materialtabs.objects.Case;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.Parsable;
import info.androidhive.materialtabs.util.ParseArrayListListener;

public class NewCaseActivity extends AppCompatActivity {
    static   ArrayList<String> AGES = new ArrayList<>();
    static final ArrayList<String> GENDERS= new ArrayList<>();

    User current_user = null;
    Case my_case =null;
    Conversation conversation= null;
    Semaphore lock_for_old_case= new Semaphore(2); /**
 first get all contacts from known case.
     then get all contacts from cloud,
     if you see a contact in list_adapter, don't add it
     */

    ArrayAdapter<String> drop_down_adapter = null;
    ListView contact_list= null;

    ContactAdapter list_adapter =null;
    TableLayout table = null;
    AutoCompleteTextView auto_complete_textview = null;
    EditText name = null;
    EditText last_name= null;
    EditText information= null;
    Spinner age_spinner = null;
    Spinner gender_spinner = null;
    ArrayAdapter<String> age_adapter= null;
    ArrayAdapter<String> gender_adapter = null;



    private HashMap<String,AbstractParseObject> all_contacts= new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AGES.size() ==0) {
            GENDERS.add("Male");
            GENDERS.add("Female");

            for (int i = 1940; i < 2016; i++)
                AGES.add(i + "");
        }




        table= (TableLayout) findViewById(R.id.table_layout);
        setContentView(R.layout.activity_new_case);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Create/Update case chat");
        setSupportActionBar(toolbar);

        contact_list = (ListView)findViewById(R.id.listView);
         name = (EditText)findViewById(R.id.first_name);
         last_name=  (EditText)findViewById(R.id.last_name);
         information= (EditText)findViewById(R.id.information);

        setListners();

        list_adapter = new ContactAdapter(getApplicationContext(),current_user,true);

        contact_list.setAdapter(list_adapter);

        drop_down_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);

         age_spinner = (Spinner)findViewById(R.id.age_spinner);
         gender_spinner = (Spinner)findViewById(R.id.gender_spinner);
        age_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,AGES);
         gender_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,GENDERS);

        age_spinner.setAdapter(age_adapter);
        gender_spinner.setAdapter(gender_adapter);


        auto_complete_textview = (AutoCompleteTextView)
                findViewById(R.id.search_contacts);
        auto_complete_textview.setAdapter(drop_down_adapter);

        auto_complete_textview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                list_adapter.AddObject(all_contacts.get(auto_complete_textview.getText().toString()));

                list_adapter.notifyDataSetChanged();
                drop_down_adapter.remove(auto_complete_textview.getText().toString()); //remove from drop down the option
                drop_down_adapter.notifyDataSetChanged();

                auto_complete_textview.setText(""); //clear text view



            }
        });
        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AbstractParseObject itemClicked=(AbstractParseObject) list_adapter.getItem(position);
                if (itemClicked instanceof Conversation) //Group clicked-> return item to auto complete
                {
                    drop_down_adapter.add(((Conversation) itemClicked) .getConversationName() +" (Group)");
                } else{
                    drop_down_adapter.add(((User)itemClicked).getFirstName()+" "+((User)itemClicked).getLastName());
                }
                list_adapter.RemoveItem(itemClicked); //remove item from list

                drop_down_adapter.notifyDataSetChanged();
                list_adapter.notifyDataSetChanged();

            }
        });

        getBundleCase();



        ParseQuery<ParseObject> users= ParseQuery.getQuery("Users");
        users.whereNotEqualTo("userName",current_user.getUserName());
        users.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    for (ParseObject parseobject: objects){
                        User user= new User(parseobject);
                        String user_name= user.getFirstName()+" "+ user.getLastName();
                        try {
                            lock_for_old_case.acquire();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        if (!list_adapter.Contains(user))
                            drop_down_adapter.add(user_name);
                        drop_down_adapter.notifyDataSetChanged();
                        lock_for_old_case.release();
                        all_contacts.put(user_name,user);

                    }


                }else{
                    e.printStackTrace();
                }
            }
        });


        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");

        innerquery.whereEqualTo("userName",current_user.getUserName());
        ParseQuery<ParseObject> query_groups = ParseQuery.getQuery("Conversations");
        query_groups.whereMatchesQuery("Users", innerquery);
        query_groups.whereNotEqualTo("conversation_type", Conversation.Conversation_type.PRIVATE.getString());
        if (my_case.GetObjectId()!= null) //not new case
        query_groups.whereNotEqualTo("objectId",my_case.GetObjectId()); //TODO test it
        query_groups.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    for (ParseObject parseobject: objects){
                        Conversation group= new Conversation(parseobject);
                       String group_name =group.getConversationName()+ " (Group)";
                        drop_down_adapter.add(group_name);
                        drop_down_adapter.notifyDataSetChanged();
                        all_contacts.put(group_name,group);

                    }


                }else{
                    e.printStackTrace();
                }
            }
        });


        //clicked save button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean checkFields= true;
                if(information.getText().length()==0)
                { //one of the text is empty


                    checkFields=false;
                }
                if (last_name.getText().length()==0)
                {

                    checkFields=false;
                }
                if (name.getText().length()==0)
                {

                    checkFields=false;
                }
                if (checkFields){
                    //todo create object new case and save
                    my_case.setFirst_name(name.getText().toString());
                    my_case.setLast_name(last_name.getText().toString());
                    my_case.setInformation(information.getText().toString());
                    my_case.setGender(gender_spinner.getSelectedItem().toString());
                    my_case.setAge(Integer.parseInt(age_spinner.getSelectedItem().toString()));
                    if (conversation== null){
                        conversation= new Conversation(Conversation.Conversation_type.CASE,name.getText().toString()+" "+last_name.getText().toString());
                    }
                   final TreeSet<User> readers= new TreeSet<>(new Comparator<User>(){
                       @Override
                       public int compare(User user1, User user2) {
                           return user1.getUserName().compareTo(user2.getUserName());
                       }
                   }
                   );

                    readers.add(current_user); //add youself to conversation
                    for (int i= 0; i< list_adapter.getCount(); i++)
                    {
                        AbstractParseObject item=(AbstractParseObject)list_adapter.getItem(i) ;
                        if (item instanceof  User){
                            synchronized (readers) {

                                readers.add((User)item);
                            }
                        }else{ //group selected
                            Conversation conversation= (Conversation)item;
                            conversation.getReaders(new ParseArrayListListener<User>() {
                                @Override
                                public void AddList(ArrayList<User> array) {
                                        synchronized (readers) {
                                            readers.addAll(array);
                                        }
                                }
                            });
                        }

                    }

                    conversation.setReaders(new ArrayList<User>(readers)); //setting the conversation to be with the readers
                    conversation.CreateAndSaveNewParseObject(new AddParseObject() {
                        @Override
                        public void AddObject(Parsable object) {
                            my_case.setConversation(conversation);
                            my_case.CreateAndSaveNewParseObject();
                        }
                    });
                    if (my_case.getCaseObjectId() == null)
                        Snackbar.make(view, "Created new Case Chat!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    else{
                        Snackbar.make(view, "Case was updated!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else{
                    Snackbar.make(view, "Please insert the missing fields", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        });
    }

    public void getBundleCase(){
        Bundle b= getIntent().getExtras();
        current_user = (User) b.getSerializable("user");
        Object c= sendingObjects.getElement("case");
        if ( c == null){

            my_case= new Case("male",0,""); //new empty case

        }else
        {

            my_case= (Case)c;
            try {
                lock_for_old_case.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            my_case.getConversation(new AddParseObject<Conversation>() {
                @Override
                public void AddObject(Conversation object) {
                    conversation = object;
                    conversation.getReaders(new ParseArrayListListener<User>() {
                        @Override
                        public void AddList(ArrayList<User> array) {
                            for (User user: array) {

                                list_adapter.AddObject(user);
                                lock_for_old_case.release();
                            }
                        }
                    });



                }
            });
            name.setText(my_case.getFirst_name());
            last_name.setText(my_case.getLast_name());
            information.setText(my_case.getInformation());
            Date today= new Date();
            gender_spinner.setSelection(GENDERS.indexOf(my_case.getGender()));
            age_spinner.setSelection(AGES.indexOf(my_case.getAge().getYear()));
        }
    }

    void setListners(){
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (name.getText().length()==0)
                {
                    name.setBackgroundColor(Color.RED);
                } else{
                    name.setBackgroundColor(Color.WHITE);
                }

            }
        });
        last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (last_name.getText().length()==0)
                {
                    last_name.setBackgroundColor(Color.RED);
                } else{
                    last_name.setBackgroundColor(Color.WHITE);
                }

            }
        });

        information.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (information.getText().length()==0)
                {
                    information.setBackgroundColor(Color.RED);
                } else{
                    information.setBackgroundColor(Color.WHITE);
                }

            }
        });

    }
}
