package info.androidhive.materialtabs.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
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
import info.androidhive.materialtabs.activity.NewCaseActivity;
import info.androidhive.materialtabs.adapter.ConversationAdapter;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.objects.sendingObjects;


public class CaseTab extends Fragment{
    User current_user = null;
    FloatingActionButton NewCaseButton= null;
    private ConversationAdapter conversationArrayAdapter = null;
    private ArrayList<Conversation> conversations = new ArrayList<Conversation>();
    private ListView conversationListView = null;
    public CaseTab(User current_user) {
        this.current_user = current_user;
    }

    public CaseTab() {
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
        View rootview= inflater.inflate(R.layout.fragment_case_chat, container, false);
        NewCaseButton = (FloatingActionButton)rootview.findViewById(R.id.new_case);
        NewCaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), NewCaseActivity.class);
                Bundle bundle= new Bundle();
                bundle.putSerializable("user",current_user);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        setConversationsList();
        return rootview;

    }
    private void setConversationsList() {
        conversations.clear();
        conversationArrayAdapter =
                new ConversationAdapter(getActivity().getApplicationContext(), current_user);

        ParseQuery<ParseObject> innerquery = ParseQuery.getQuery("Users");

        innerquery.whereEqualTo("userName",current_user.getUserName());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Conversations");
        query.whereMatchesQuery("Users", innerquery);
        query.whereEqualTo("conversation_type", Conversation.Conversation_type.CASE.getString());

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
                          //  openConversation(conversations.get(pos));
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
