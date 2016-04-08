package info.androidhive.materialtabs.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import info.androidhive.materialtabs.MessagingService;
import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.adapter.ConversationMessagesAdapter;
import info.androidhive.materialtabs.objects.Case;
import info.androidhive.materialtabs.objects.Conversation;

import info.androidhive.materialtabs.objects.Duty;
import info.androidhive.materialtabs.objects.Message;
import info.androidhive.materialtabs.objects.Remark;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;
import info.androidhive.materialtabs.util.AddParseObjects;
import info.androidhive.materialtabs.util.ParseArrayListListener;


/**
 * Created by ido on 16/11/2015.
 */

public class MessagingActivity extends AppCompatActivity {
    private Toolbar toolbar = null;
    //todo add search on toolbar, add title of the conversation, add settings


    private User MyUser= null;

    private Case MyCase =null;

    //this two are for sending messages and binding between service to Activity
    private MessagingService mService;
    private boolean mBound = false;
    private BroadcastReceiver receiver = null;
    public static final String BROADCAST_ACTION = "info.androidhive.tabsswipe.MessagingActivity.Receiver";

    private Conversation conversation = null;

    private ArrayList<User> recipientsIds = new ArrayList<User>(); //the other user you speak with
    private EditText messageBodyField= null;
    private String messageBody= ""; //sending new message body

    private ListView messagesList= null;
    private ConversationMessagesAdapter messageAdapter = null;



    //for adapter

    ArrayList<Message> values = new ArrayList<Message>();
    protected void onCreate(Bundle savedInstanceState) {
        // this program will get the intent with a code: http://stackoverflow.com/questions/14695537/android-update-activity-ui-from-service

        super.onCreate(savedInstanceState);
        setContentView(R.layout.messaging);

        toolbar = (Toolbar) findViewById(R.id.toolbar);



        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ReadBundle();






        messageAdapter = new ConversationMessagesAdapter(this,values,MyUser);


        //a receiver that listen to the Service
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v("got new intent for ui", intent.toString());

                Message message = (Message)intent.getExtras().getSerializable("message");
                //check if it's the correct conversation
                if (message.getConversationObjectId().compareTo( conversation.getConversationObjectId()) !=0)
                    return; // not my message
                User sender = (User)intent.getExtras().getSerializable("senderuser");


                messageAdapter.addMessage(message, sender);

            }
        };






        //currentUserId = ParseUser.getCurrentUser().getObjectId();

        messagesList = (ListView) findViewById(R.id.listMessages);

        messagesList.setAdapter(messageAdapter);
        populateMessageHistory();
        populateReaders();

       // Message s =new Message("test2", "my message");
      //  messageAdapter.addMessage(s);
        messageBodyField = (EditText) findViewById(R.id.messageBodyField);

        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }


    public void populateMessageHistory(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Messages");
        //query.fromLocalDatastore();
        query.whereEqualTo("conversation",conversation.ToParseObject());
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {

                if (e == null) {

                    for (ParseObject message : messages) {
                        final Message newMessage = new Message(message);
                        AddParseObject users = new AddParseObject() {
                            @Override
                            public void AddObject(AbstractParseObject object) {
                                User user = (User) object;
                                newMessage.fromUserName = user.getUserName();
                                messageAdapter.addMessage(newMessage, user);

                            }
                        };
                        newMessage.getFrom(users);

                    }
                    Log.v("populate"," finished");

                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    public void populateReaders(){
        conversation.getReaders(new ParseArrayListListener<User>() {
            User second_user= null;
            @Override
            public void AddList(ArrayList<User> array) {
                recipientsIds= array;
                if (!conversation.IsPrivate()) //group chat
                {
                    getSupportActionBar().setTitle(conversation.getConversationName());
                    toolbar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                                        Intent intent = new Intent(getApplicationContext(), GroupProfileActivity.class);
                                        Bundle bundle = new Bundle();
                                        sendingObjects.myObject.put(conversation.getConversationName(), conversation);
                                        bundle.putSerializable("conversation", conversation.getConversationName());
                                        intent.putExtras(bundle);
                                        startActivity(intent);

                        }
                    });

                } else {

                    if (recipientsIds.get(0).getUserName().matches(MyUser.getUserName())) //the first is me
                    {
                        second_user = recipientsIds.get(1);

                    }
                    else { //the second is me
                        second_user = recipientsIds.get(0);

                    }
                    getSupportActionBar().setTitle(second_user.getFirstName() + " " + second_user.getLastName());

                    //this to open a profile in the title click
                    toolbar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent( getApplicationContext(),UserProfileActivity.class);
                            Bundle bundle= new Bundle();
                            bundle.putSerializable("user",second_user);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });

                }
            }
        });

    }
    public void sendMessage(){
        Log.v("sending message","");
        messageBody = messageBodyField.getText().toString();
        if (messageBody.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_LONG).show();
            return;
        }
        boolean urgent = (conversation.IsPrivate()) ;

        Message message = new Message(MyUser,recipientsIds,messageBody,urgent,conversation);
        message.setNew(false);
        message.setExternal_key(new Date().toString()+ new Random().nextDouble());
        //Message m= new Message(conversation_name,Myusername,recipientsIds, messageBody, conversation_type,case_number, urgent);
        mService.sendMessage(message, recipientsIds);
        messageBodyField.setText("");
        message.fromUserName = MyUser.getUserName();
        messageAdapter.addMessage(message, MyUser);

    }


    /** this is for sending to the Service new messages*/
    @Override
    protected void onStart() {
        super.onStart();
        //to send messages
        Intent intent = new Intent(this, MessagingService.class);
        bindService(intent, mConnection, this.BIND_AUTO_CREATE);

        /* this is for getting intents from the service */
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(MessagingActivity.BROADCAST_ACTION)
        );
    }

    @Override
    protected void onDestroy() { //TODO change to pause? or remove it
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            //more info in http://developer.android.com/guide/components/bound-services.html
            MessagingService.LocalBinder binder = (MessagingService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
/* this is to read bundles from intents from notification or from main activity listview*/
   public void ReadBundle(){
       Bundle b= getIntent().getExtras();
       conversation = (Conversation) b.getSerializable("conversation");

       // String conversation_str =(String)b.getSerializable("conversation");
      // conversation= (Conversation) sendingObjects.getElement(conversation_str);
       if (conversation.IsCASE())
       {
           ParseQuery<ParseObject> queryCase= new ParseQuery<ParseObject>("Cases");
           queryCase.whereEqualTo("conversation",conversation.ToParseObject());
           queryCase.getFirstInBackground(new GetCallback<ParseObject>() {
               @Override
               public void done(ParseObject object, ParseException e) {
                   if (e == null) {
                       MyCase = new Case(object, new AddParseObject() {
                           @Override
                           public void AddObject(AbstractParseObject object) {

                               ParseQuery<ParseObject> query_remarks= new ParseQuery<ParseObject>("Remarks");
                               query_remarks.whereEqualTo("case",MyCase.ToParseObject());
                               query_remarks.findInBackground(new FindCallback<ParseObject>() {
                                   @Override
                                   public void done(List<ParseObject> objects, ParseException e) {
                                       if (e== null)
                                       {

                                           getRemarks getRemarks= new getRemarks();
                                           getRemarks.setSize(objects.size());
                                           for (ParseObject object: objects){
                                                new Remark(object,getRemarks);
                                           }

                                       }else{
                                           e.printStackTrace();
                                       }
                                   }
                               });
                           }
                       });


                   }
                   else{
                       e.printStackTrace();
                   }
               }
           });


       }
       MyUser = (User) b.getSerializable("my_user");




   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_private_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_name:
            if (conversation.IsCASE()){
                Intent intent = new Intent(getApplicationContext(), NewCaseActivity.class);
                Bundle b = new Bundle();
                b.putSerializable("user", MyUser);

                sendingObjects.myObject.put("case", MyCase);
                intent.putExtras(b);
                startActivity(intent);
            }
                return true;
            case R.id.search:

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    class getRemarks implements  AddParseObject{
        private ArrayList<Remark> remarks= new ArrayList<>();
        int size= 0;

        public void setSize(int size) {
            this.size = size;
            if (size== 0)
                finished();
        }

        @Override
        public void AddObject(AbstractParseObject object) {
            remarks.add((Remark) object);
            synchronized (remarks) {
                size--;
                if (size == 0)
                    finished();
            }
        }

        public void finished() {
            //set case remarks if needed

    //setting the remarks button
            LayoutInflater inflater= null;
            View remarks_toolbar_view= null;
            boolean havingUrgentRemark= false;
            for (Remark remark: remarks){
                if (remark.isUrgent())
                {
                    havingUrgentRemark= true;
                    break;
                }
            }
            if (havingUrgentRemark){
                 inflater = getLayoutInflater();
                remarks_toolbar_view= inflater.inflate(R.layout.case_remarks_view,null);
            } else{
                 inflater = getLayoutInflater();
                remarks_toolbar_view= inflater.inflate(R.layout.case_remarks_view_clear,null);
            }
            //adding the remarks toolbar
            ((AppBarLayout)findViewById(R.id.app_bar_layout)).addView(remarks_toolbar_view); //making instance tool bar with remarks on it

            //setting instance of alert dialog with all remarks
            View remarks_view = inflater.inflate(R.layout.remarks_dialog_layout,null);
            final TableLayout remarks_table= (TableLayout)remarks_view.findViewById(R.id.remarks_table);
            //setting add new Remark button


            //setting the remarks from the cloud in the view
            for (Remark remark: remarks){
            createRemark(remarks_table,remark, inflater);


            }
            final AlertDialog.Builder builder = new AlertDialog.Builder(MessagingActivity.this);
            builder.setView(remarks_view);
            builder.setTitle(getApplicationContext().getResources().getString(R.string.remarks));
            builder.setPositiveButton("OK", null);
            final AlertDialog dialog= builder.create();
            remarks_toolbar_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.show();
                }
            });

            ImageView addNewRemark = (ImageView) remarks_view.findViewById(R.id.add_new_remark_button);
            TextView create_by= (TextView) remarks_view.findViewById(R.id.new_remark_created_by);
            final EditText remark_text = (EditText) remarks_view.findViewById(R.id.remark_text);
            final CheckBox is_urgent = (CheckBox) remarks_view.findViewById(R.id.is_urgent_remark);
            String user_name= MyUser.getFirstName();
            if (MyUser.getLastName().length()>3)
                user_name+= MyUser.getLastName().substring(0,3);
            else
                 user_name+=MyUser.getLastName();
            create_by.setText(user_name);


            addNewRemark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Remark new_remark = new Remark(MyUser,MyCase,remark_text.getText().toString(),is_urgent.isChecked(),new Date());
                    new_remark.CreateAndSaveNewParseObject();
                    //dismiss alert
                    dialog.dismiss();

                }
            });



        }


        public void createRemark(TableLayout remarks_table, Remark remark, LayoutInflater inflater) {
            View remark_item= inflater.inflate(R.layout.reamarks_dialog_item,null);
            TableRow item_row = (TableRow) remark_item.findViewById(R.id.remark_row);
            ImageView delete_button = (ImageView) item_row.findViewById(R.id.delete_remark_button);
            TextView creator_text= (TextView)  item_row.findViewById(R.id.remark_created_by);
            TextView remark_text= (TextView)  item_row.findViewById(R.id.remark_text);
            ImageView is_urgent = (ImageView) item_row.findViewById(R.id.is_urgent_remark);

            delete_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Snackbar.make(v,"future development",Snackbar.LENGTH_LONG).show();
                }
            });

            remark.getUser(new getRemarkCreator(creator_text));
            remark_text.setText(remark.getText());

            if (remark.isUrgent()){
                is_urgent.setImageDrawable(getResources().getDrawable(R.mipmap.ic_remark));
                is_urgent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v,getResources().getString(R.string.urgent),Snackbar.LENGTH_LONG).show();
                    }
                });
            } else{
                is_urgent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(v,getResources().getString(R.string.not_urgent),Snackbar.LENGTH_LONG).show();
                    }
                });
            }
            remarks_table.addView(remark_item);
        }

        }


        class getRemarkCreator implements AddParseObject{
            TextView creator_text= null;

            public getRemarkCreator(TextView creator_text) {
                this.creator_text = creator_text;
            }

            @Override
            public void AddObject(AbstractParseObject object) {
                User user = (User) object;
                if (user.getLastName().length()>3)
                creator_text.setText(user.getFirstName()+" "+ user.getLastName().substring(0,3)+".");
                else
                    creator_text.setText(user.getFirstName()+" "+ user.getLastName());

            }
        }
    }



