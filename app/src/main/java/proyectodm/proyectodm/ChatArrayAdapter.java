package proyectodm.proyectodm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class ChatArrayAdapter extends ArrayAdapter {
    public ChatArrayAdapter(Context context, ArrayList<Chat> chats) {
        super(context, 0, chats);
    }

    public View getView(int position, View view, ViewGroup parent) {
        final Context context = this.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final Chat chat = (Chat) this.getItem(position);

        if(view == null) {
            view = inflater.inflate(R.layout.listview_chat, null);
        }

        final TextView lblContacto = view.findViewById(R.id.lblContacto);
        final TextView lblFecha = view.findViewById(R.id.lblFecha);

        lblContacto.setText(chat.getContacto());
        lblFecha.setText(chat.getFechaString());

        return view;
    }
}
