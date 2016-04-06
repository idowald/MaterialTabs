package info.androidhive.materialtabs.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.GroupProfileActivity;
import info.androidhive.materialtabs.activity.MessagingActivity;
import info.androidhive.materialtabs.adapter.ConversationAdapter;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;


public class PrivateChatTab extends Fragment{
   static private User currentUserId = null;
    private ConversationAdapter conversationArrayAdapter = null;
    private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
    private ListView conversationListView = null;
    private ProgressDialog progress = null;

    public PrivateChatTab() {
        // Required empty public constructor
    }
    public PrivateChatTab(User my_user){
        currentUserId= my_user;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progress = new ProgressDialog(getContext());
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_private_chat, container, false);

        setConversationsList();
        return rootView;
    }

    private void selectOldmessages(){
        ParseQuery<ParseObject> AllprivateConversation = ParseQuery.getQuery("Conversations");
        AllprivateConversation.whereEqualTo("conversation_type",Conversation.Conversation_type.PRIVATE.getString() );

        ParseQuery<ParseObject> Allmessages = ParseQuery.getQuery("Messages");




    }
    private void setConversationsList() {
        conversations.clear();
        conversationArrayAdapter =
                new ConversationAdapter(getActivity().getApplicationContext(), currentUserId);

        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");

        innerquery.whereEqualTo("userName",currentUserId.getUserName());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversations");
        query.whereMatchesQuery("Users", innerquery);
        query.whereEqualTo("conversation_type", Conversation.Conversation_type.PRIVATE.getString());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> conversationList, ParseException e) {
                if (e== null) {

                    for (ParseObject conversation : conversationList) //todo only after inflating list you can continue the code
                    {
                        conversationArrayAdapter.addConversation(new Conversation(conversation));
                    }

                    conversationListView = (ListView) getView().findViewById(R.id.listViewPrivateChat);
                    conversationListView.setAdapter(conversationArrayAdapter);
                    conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        Conversation selected_conversation= null;
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int pos, long l) {
                             selected_conversation =conversations.get(pos);
                            //openConversation(v.getContext() ,selected_conversation);
                        }
                    });

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            sleep(1000);
                            progress.dismiss();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }.start();


            }
        });


    }
    //open a conversation with one person
   static public void openConversation(Context context, Conversation conversation) {

       context.startActivity(prepareIntent( context,  conversation));


    }
    static public Intent prepareIntent(Context context, Conversation conversation){
        Intent intent = new Intent(context, MessagingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
        Bundle bundle = new Bundle();
        //sendingObjects.myObject.put(conversation.getConversationName(), conversation);
        bundle.putSerializable("conversation", conversation);
        //TODO throw error at start if it's not instanciated
        bundle.putSerializable("my_user", currentUserId);
        intent.putExtras(bundle);
        return intent;
    }

}
