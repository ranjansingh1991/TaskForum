package in.enzen.taskforum.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.enzen.taskforum.R;
import in.enzen.taskforum.model.Chats;

/**
 * Created by Rupesh on 07-12-2017.
 */
@SuppressWarnings("ALL")
public class ChatListAdapter extends ArrayAdapter<Chats> {

    private TextView chatText;
    private List<Chats> chatMessageList = new ArrayList<Chats>();
    private Context context;

    @Override
    public void add(Chats object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatListAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public Chats getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        Chats chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.left) {
            row = inflater.inflate(R.layout.outgoing_chat_msg, parent, false);
        } else {
            row = inflater.inflate(R.layout.sender_chat_msg, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.tvMsg);
        chatText.setText(chatMessageObj.message);
        return row;
    }
}
