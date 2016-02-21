package info.androidhive.materialtabs.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.Duty;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.Parsable;


public class ProfileTab extends Fragment{
    User current_user =null;
    ArrayList<ListenToRow> listeners = new ArrayList<>();
    View rootView= null;

    HashMap<String, ArrayList<AbstractParseObject>> map= new HashMap<>();

    Boolean finishedMap= false;
    Integer conversationSize= 0;
    Integer dutySize = 0;


    public ProfileTab() {

        // Required empty public constructor
    }
    public ProfileTab(User user) {
        // Required empty public constructor
        current_user = user;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView =inflater.inflate(R.layout.fragment_tab0_profile, container, false) ;
        Button updateBtn =(Button) rootView.findViewById(R.id.updatebtn);

        final EditText first_name =(EditText) rootView.findViewById(R.id.first_name_profile);
        final EditText last_name =(EditText) rootView.findViewById(R.id.last_name_profile);
        first_name.setText(current_user.getFirstName());
        last_name.setText(current_user.getLastName());
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //update all duties
                for (ListenToRow listener : listeners)
                    listener.SaveDuty();
                if(first_name.getText().toString().trim().length()* last_name.getText().toString().trim().length()>0) {
                    current_user.setFirstName(first_name.getText().toString());
                    current_user.setLastName(last_name.getText().toString());
                    current_user.CreateAndSaveNewParseObject();
                    Snackbar.make(v, "updated profile", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        setTableRoles();
        return rootView;


    }
    void setTableRoles(){

        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");

        innerquery.whereEqualTo("userName",current_user.getUserName());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversations");
        query.whereMatchesQuery("Users", innerquery);
        query.whereEqualTo("conversation_type", Conversation.Conversation_type.GROUP.getString());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    conversationSize = objects.size();
                    for (ParseObject object : objects){

                        Conversation conversation= new Conversation(object,  new AddParseobject<Conversation>());
                    }

                }else
                {
                    e.printStackTrace();
                }


            }
        });

        ParseQuery<ParseObject> queryDuties= ParseQuery.getQuery("Dutys");
        queryDuties.whereMatchesQuery("user", innerquery);
        queryDuties.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e== null){
                    dutySize = objects.size();

                    for (ParseObject object : objects){
                        Duty d = new Duty(object, new AddParseobject<Duty>());

                    }
                }else{
                    e.printStackTrace();
                }

            }
        });



    }

    public void AddMap(){
        if (finishedMap)
            return;
        else
            finishedMap= true;
        listeners.clear();

        Activity act= getActivity();
        TableLayout stk = (TableLayout) rootView.findViewById(R.id.table);


        for (String key: map.keySet()){
            Conversation conversation= (Conversation)(map.get(key).get(0));
            Duty duty = null;
            if (map.get(key).get(1) != null) {
                duty =(Duty)(map.get(key).get(1));
            }
            TableRow tbrow = new TableRow(act);
            TextView t1v = new TextView(act);
            t1v.setText(conversation.getConversationName());
            tbrow.addView(t1v);
            EditText t2v = new EditText(act);

            if (duty != null) {

                t2v.setText(duty.getDutyName());

                Log.v("duty", duty.getDutyName());
            }else{
                Log.v("duty", "null duty");
            }
            tbrow.addView(t2v);
            CheckBox rowbox= new CheckBox(act);
            if (duty!= null)
                rowbox.setChecked(duty.getInDuty().equals("true"));
            else {
                rowbox.setEnabled(false);
                rowbox.setChecked(false);
            }

            ListenToRow listen = new ListenToRow(t2v,rowbox,duty,conversation);
            t2v.addTextChangedListener(listen);
            rowbox.setOnClickListener(listen);
            tbrow.addView(rowbox);
            stk.addView(tbrow);

            listeners.add(listen);

        }


    }

    class ListenToRow implements View.OnClickListener, TextWatcher {
        //listener to both the checkbox and the editText
        //when clicked Finish, it will save the data within it's objects..
        //if duty isn't exist it will create one


        EditText RoleText = null;
        CheckBox IsOnDuty =null;
        Duty duty= null;
        Conversation conv= null;

        public ListenToRow( EditText roleText, CheckBox isOnDuty, Duty duty, Conversation conversation) {
            this.duty= duty;
            RoleText = roleText;
            IsOnDuty = isOnDuty;
            conv= conversation;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (duty== null)
                this.duty= new Duty("",false,current_user,conv);

            duty.setDutyName(RoleText.getText().toString().trim());
            IsOnDuty.setEnabled(true);
            if (RoleText.getText().toString().trim().length()==0)
            { //deleted duty
                IsOnDuty.setEnabled(false);
                duty= null;
            }

            //  Toast.makeText(getActivity(),RoleText.getText().toString().toLowerCase().trim().endsWith(inDuty) +"",Toast.LENGTH_LONG).show();
            //  IsOnDuty.setChecked(RoleText.getText().toString().toLowerCase().trim().endsWith(inDuty));
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void onClick(View v) {
            if (duty!= null){
                duty.setInDuty(IsOnDuty.isChecked());
            }

        }

        public void SaveDuty(){
            if (duty!= null)
                this.duty.CreateAndSaveNewParseObject();
        }
    }

    class AddParseobject <T extends AbstractParseObject> implements  AddParseObject<T>{
        T Myobject= null;
        @Override
        public void AddObject(T object) {
            this.Myobject= object;
            if (Myobject instanceof  Duty)
                ((Duty)Myobject).getConversation(new AddParseObject<Conversation>() {
                @Override
                public void AddObject(Conversation object) {

                    synchronized (finishedMap) {

                        ArrayList<AbstractParseObject> list = new ArrayList<AbstractParseObject>();
                        list.add(object);
                        list.add((AbstractParseObject)Myobject);
                        map.put(object.getConversationObjectId(), list);
                        synchronized (dutySize) {
                            dutySize--;
                            if (conversationSize + dutySize == 0) {
                                AddMap();
                            }
                        }
                    }
                }
            });
            else { //Conversation
                        synchronized (finishedMap) {
                            ArrayList<AbstractParseObject> list = new ArrayList<AbstractParseObject>();
                            list.add((AbstractParseObject)object);
                            list.add(null);
                            if (!map.containsKey(((Conversation)object).getConversationObjectId())) //in case already found duty
                                map.put(((Conversation)object).getConversationObjectId(), list);
                            synchronized (conversationSize) {
                                conversationSize--;
                                if (conversationSize + dutySize == 0) {
                                    AddMap();
                                }
                            }
                        }
                    }
                };


            }



}
