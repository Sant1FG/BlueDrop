package proyectodm.proyectodm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.ArrayList;

public class ShowMessagesActivity extends AppCompatActivity {

    ImageButton btVolver;
    private ArrayList<Message> mensajes;
    private MessageArrayAdapter messageArrayAdapter;
    Chat chatSelected;
    TextView conctactoTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_messages);

        Intent messagesRecuperado = getIntent();
        String chatJsonString = messagesRecuperado.getStringExtra("chatJsonString");

        chatSelected = loadChatFromJson(chatJsonString);

        conctactoTitle = (TextView) findViewById(R.id.textViewContactoTitle);
        conctactoTitle.setText(chatSelected.getContacto());

        crearListaMensajes();

        btVolver = (ImageButton) findViewById(R.id.imageButtonBack);
        btVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });



    }

    private Chat loadChatFromJson(String chatJsonString) {
        Gson gson = new Gson();
        Chat chatFromJson = gson.fromJson(chatJsonString, Chat.class);
        return chatFromJson;
    }

    private void crearListaMensajes() {
        final ListView lvMessages = this.findViewById(R.id.listMessages);
        this.mensajes = this.chatSelected.getMensajes();
        this.messageArrayAdapter = new MessageArrayAdapter(this, this.mensajes);
        lvMessages.setAdapter(messageArrayAdapter);

    }
}