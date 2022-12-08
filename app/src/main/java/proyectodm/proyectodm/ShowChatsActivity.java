package proyectodm.proyectodm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


public class ShowChatsActivity extends AppCompatActivity {
    private ArrayList<Chat> chats = new ArrayList<Chat>();
    private ChatArrayAdapter chatArrayAdapter;
    private static final String CHATS = "chats";

    ImageButton btSearch;
    EditText contactCampo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chats);
        /*
        //////////////////////////////DATOS PARA PROBAR///////////////////////////
        Message msj1 = new Message("silvana", new Date(), "hola");
        Message msj2 = new Message("alvaro", new Date(), "jiji");
        Message msj3 = new Message("dani", new Date(), "aaaa");
        ArrayList<Message> mensajes = new ArrayList<Message>();
        mensajes.add(msj1);
        mensajes.add(msj2);
        mensajes.add(msj3);
        for(int i=0; i<30; i++) {
            Chat chat = new Chat("silvana"+i, new Date(), mensajes);
            chats.add(chat);
        }
        //////////////////////////////////////////////////////////////////////////
        */


        contactCampo = (EditText) findViewById(R.id.editTextContactFilter);
        this.crearLista();
        btSearch = (ImageButton) findViewById(R.id.imageButtonSearch);

        contactCampo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                chatArrayAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contactCampo.getVisibility()==View.GONE) {
                    contactCampo.setVisibility(View.VISIBLE);
                } else if(contactCampo.getVisibility()==View.VISIBLE) {
                    contactCampo.setVisibility(View.GONE);
                }
            }
        });
    }

    private void crearLista() {
        final ListView lvChats = this.findViewById(R.id.listChats);
        this.chatArrayAdapter = new ChatArrayAdapter(this, this.chats);
        lvChats.setAdapter(this.chatArrayAdapter);

        //configurar listener en los elementos de lista
        lvChats.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent messagesIntent = new Intent(view.getContext(), ShowMessagesActivity.class);
                        Chat chatSelected = (Chat) lvChats.getItemAtPosition(i);
                        Gson gson = new Gson();
                        String chatJsonString = gson.toJson(chatSelected);
                        messagesIntent.putExtra("chatJsonString", chatJsonString);
                        startActivity(messagesIntent);
                    }
                }
        );

        //Registrar menú contextual
        registerForContextMenu(lvChats);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu( menu );
        this.getMenuInflater().inflate( R.menu.main_menu, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        boolean toret = false;
        switch( menuItem.getItemId() ) {
            case R.id.opEliminarTodo:
                //diálogo para eliminar todos los elementos
                AlertDialog.Builder dia_builder = new AlertDialog.Builder(ShowChatsActivity.this);
                dia_builder.setTitle("Eliminar todas las conversaciones");
                dia_builder.setMessage("¿Está seguro?");
                dia_builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        doClear();
                    }
                });
                dia_builder.setNegativeButton("Cancelar", null);
                dia_builder.create().show();

                toret = true;
                break;
        }
        return toret;
    }

    private void doClear() {
        chats.clear();
        chatArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateContextMenu(ContextMenu contmenu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if(v.getId() == R.id.listChats) {
            getMenuInflater().inflate(R.menu.itemlist_menu, contmenu);
        }

        super.onCreateContextMenu(contmenu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean toret = false;
        switch (item.getItemId()) {
            case R.id.context_opDelete:
                //diálogo para eliminar el elemento
                AlertDialog.Builder dia_builder = new AlertDialog.Builder(ShowChatsActivity.this);
                dia_builder.setTitle("Eliminar conversación");
                dia_builder.setMessage("¿Está seguro?");
                dia_builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int positionToRemove = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).position;
                        doRemove(positionToRemove);
                    }
                });
                dia_builder.setNegativeButton("Cancelar", null);
                dia_builder.create().show();

                toret = true;
                break;
        }
        return toret;
    }

    private void doRemove(int position) {
        this.chats.remove(position);
        this.chatArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadChatsFromInternalStorage();
    }

    private void loadChatsFromInternalStorage() {
        try {
            FileInputStream fileInputStream = this.openFileInput(CHATS);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));
            String chatJsonString = reader.readLine();
            loadChatsFromJson(chatJsonString);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadChatsFromJson(String chatJsonString) {
        Gson gson = new Gson();
        Chat[] chatFromJson = gson.fromJson(chatJsonString, Chat[].class);
        this.chats.clear();
        this.chats.addAll(Arrays.asList(chatFromJson));
        this.chatArrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveChatAtInternalStorage();
    }

    private void saveChatAtInternalStorage() {
        try {
            FileOutputStream fileOutputStream = this.openFileOutput(CHATS, Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fileOutputStream);
            writer.println(getChatsJsonString());
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getChatsJsonString() {
        Gson gson = new Gson();
        String chatJsonString = gson.toJson(this.chats);
        return chatJsonString;
    }

}