package info.androidhive.materialtabs.fragments;

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
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.activity.MessagingActivity;
import info.androidhive.materialtabs.adapter.ConversationAdapter;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;


public class GroupTab extends Fragment{
    User current_user = null;
    private ConversationAdapter conversationArrayAdapter = null;
    private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
    private ListView conversationListView = null;
    public GroupTab(User current_user) {
        this.current_user = current_user;
    }

    public GroupTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setConversationsList();
        return inflater.inflate(R.layout.fragment_group_chat, container, false);

    }
    private void setConversationsList() {

        conversationArrayAdapter =
                new ConversationAdapter(getActivity().getApplicationContext(), conversations, current_user);

        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");

        innerquery.whereEqualTo("userName",current_user.getUserName());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversations");
        query.whereMatchesQuery("Users", innerquery);
        query.whereEqualTo("conversation_type", Conversation.Conversation_type.GROUP.getString());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> conversationList, ParseException e) {
                if (e== null) {

                    for (ParseObject conversation : conversationList) //todo only after inflating list you can continue the code
                    {
                        conversationArrayAdapter.addConversation(new Conversation(conversation));
                    }

                    conversationListView = (ListView) getView().findViewById(R.id.listViewGroupChat);
                    conversationListView.setAdapter(conversationArrayAdapter);
                    conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int pos, long l) {
                            openConversation(conversations.get(pos));
                        }
                    });

                }
                else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }


            }
        });


    }
    //open a conversation with group
    public void openConversation(Conversation conversation) {
        Intent intent = new Intent(getActivity().getApplicationContext(), MessagingActivity.class);
        Bundle bundle = new Bundle();
        sendingObjects.myObject.put(conversation.getConversationName(), conversation);
        bundle.putSerializable("conversation", conversation.getConversationName());
        //TODO throw error at start if it's not instanciated
        bundle.putSerializable("my_user", current_user);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
