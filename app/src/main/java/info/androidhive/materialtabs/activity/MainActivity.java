package info.androidhive.materialtabs.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.fragments.CaseTab;
import info.androidhive.materialtabs.fragments.GroupTab;
import info.androidhive.materialtabs.fragments.PrivateChatTab;
import info.androidhive.materialtabs.fragments.ProfileTab;
import info.androidhive.materialtabs.objects.Case;
import info.androidhive.materialtabs.objects.Conversation;
//import info.androidhive.materialtabs.objects.Department;
import info.androidhive.materialtabs.objects.Duty;
import info.androidhive.materialtabs.objects.Remark;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AddParseObject;

public class MainActivity extends AppCompatActivity  {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private int[] tabIcons = {
            R.mipmap.ic_profile,
            R.mipmap.ic_private,
            R.mipmap.ic_case,
            R.mipmap.ic_group
    };


    User my_user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_text_tabs);

        //startActivity(new Intent(this, UserProfileActivity.class));


        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setUser();
        //CreatingNewPrivateConversations();
        setupViewPager(viewPager);




/*creating department
       final Conversation conversation = new Conversation(Conversation.Conversation_type.GROUP, "critical dep");
        conversation.addReader(my_user);
        new User("8DW4DHv102", new AddParseObject<User>() {
            @Override
            public void AddObject(User object) {
                conversation.addReader(object);
                conversation.CreateAndSaveNewParseObject();
            }
        });*/



        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();






    }
    public void setUser(){

        SharedPreferences userDetails = getApplicationContext().getSharedPreferences("userdetails", MODE_PRIVATE);
        String username= userDetails.getString("username","");
        String first_name= userDetails.getString("first_name","");
        String last_name= userDetails.getString("last_name","");
        if (username.length()== 0)
        { //no user registered yet.
            startActivity(new Intent(this,LoginActivity.class));
            finish();
        }
        AddParseObject<User> callback = new AddParseObject<User>() {
            @Override
            public void AddObject(User object) {

               // Duty duty = new Duty("doing dishes", object);
               // duty.CreateAndSaveNewParseObject();
            }
        };
         my_user = new User(first_name,last_name,username, callback);
/*        final Department department = new Department("Clinic");
        department.GetConversation(new AddParseObject<Conversation>() {

              Integer  i= 0;
            @Override
            public void AddObject(Conversation object) {
                synchronized(i){
                    if (i++ ==0)
                        department.CreateAndSaveNewParseObject();
                }

            }
        });*/



    }
    private void setupTabIcons() {
        for (int i =0 ; i< tabIcons.length ; i++){
            tabLayout.getTabAt(i).setIcon(tabIcons[i]);
        }

    }

    private void setupViewPager(ViewPager viewPager) {

        String[]  tabs_titles = {getResources().getString(R.string.tab0_main), getResources().getString(R.string.tab1_private),
                getResources().getString(R.string.tab2_case),
                getResources().getString(R.string.tab3_group) };

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ProfileTab(my_user), tabs_titles[0]);
        adapter.addFrag(new PrivateChatTab(my_user), tabs_titles[1]);
        adapter.addFrag(new CaseTab(my_user), tabs_titles[2]);
        adapter.addFrag(new GroupTab(my_user), tabs_titles[3]);

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_icon_text_tabs, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:
                Intent intent = new Intent(this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user",my_user);
                intent.putExtras(bundle);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
/*
for later use only
 */
    public void CreatingNewPrivateConversations(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Users");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e== null)
                {
                    Log.v("found list of user", "");
                    ArrayList<User> users = new ArrayList<User>();
                    ArrayList<User> users2 = new ArrayList<User>();
                    for (ParseObject user : parseObjects)
                    {
                        users.add( new User(user));
                        users2.add( new User(user));
                    }
                    for (User user : users){
                        for (User user2: users2){
                            if (user.compareTo(user2) >0)
                            {
                                Log.v("user1 "+user.toString(), " user2" + user2.toString());
                                Conversation con= new Conversation(Conversation.Conversation_type.PRIVATE,
                                        user.GetObjectId() +"|"+ user2.GetObjectId() );
                                con.addReader(user);
                                con.addReader(user2);
                                con.CreateAndSaveNewParseObject();

                            }
                        }
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }
}
