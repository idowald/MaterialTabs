package info.androidhive.materialtabs.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import info.androidhive.materialtabs.R;
import info.androidhive.materialtabs.objects.Conversation;
import info.androidhive.materialtabs.objects.User;
import info.androidhive.materialtabs.util.AbstractParseObject;
import info.androidhive.materialtabs.util.AddParseObject;

/**
 * Created by ido on 06/02/2016.
 */
public class ContactAdapter extends BaseAdapter implements AddParseObject<AbstractParseObject> {

    ArrayList<AbstractParseObject> contacts = new ArrayList<>();

    private Context context= null;
    private User current_user= null;
    private boolean isList= true;
    public ContactAdapter( Context context, User current_user,Boolean isList) {
        this.isList= isList;
        this.context = context;
        this.current_user = current_user;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int position) {
        return contacts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_contacts_case, parent, false);
       TextView textView = (TextView) rowView.findViewById(R.id.contact_txtLabel);
        AbstractParseObject object = contacts.get(position);
        String text= "";
        if (object instanceof User){
                text= ((User)object).getFirstName() +" "+((User)object).getLastName();
        } else{
            text= ((Conversation)object).getConversationName()+" (Group)";
        }
        textView.setText(text);

        return rowView;
    }

    @Override
    public void AddObject(AbstractParseObject object) {
        synchronized (contacts){
            contacts.add(object);
            this.notifyDataSetChanged();
        }
    }

    public void RemoveItem(AbstractParseObject object){
        contacts.remove(object);
        this.notifyDataSetChanged();
    }
    public boolean Contains(AbstractParseObject object){
        return contacts.contains(object);
    }


}
