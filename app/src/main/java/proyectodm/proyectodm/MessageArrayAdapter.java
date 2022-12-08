package proyectodm.proyectodm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class MessageArrayAdapter extends ArrayAdapter {
    public MessageArrayAdapter(Context context, ArrayList<Message> mensajes) {
        super(context, 0, mensajes);
    }

    public View getView(int position, View view, ViewGroup parent) {
        final Context context = this.getContext();
        final LayoutInflater inflater = LayoutInflater.from(context);
        final Message message = (Message) this.getItem(position);

        if(view == null) {
            view = inflater.inflate(R.layout.messages_view, null);
        }

        final TextView lblEmisor = view.findViewById(R.id.lblEmisor);
        final TextView lblHora = view.findViewById(R.id.lblHora);
        final TextView lblMessage = view.findViewById(R.id.lblMessage);

        lblEmisor.setText(message.getEmisor());
        lblHora.setText(message.getHoraString());
        lblMessage.setText(message.getMensaje());

        return view;
    }
}
