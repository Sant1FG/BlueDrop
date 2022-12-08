package proyectodm.proyectodm;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

public class SalaChat extends AppCompatActivity {

    private static BluetoothAdapter bluetoothAdapter;
    private final int LOCATION_CODE = 200;
    private final int SCAN_CODE = 201;
    private final int CONNECT_CODE = 202;
    private final int SELECT_CODE = 203;
    private Context context;
    public static final int MESSAGE_STATE_CHANGED = 0;
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;

    public static final String DEVICE_NAME = "deviceName";
    public static final String TOAST = "toast";
    private String dispositivoConectado;
    private ChatUtils chatUtils;
    private ListView listaChatPrincipal;
    private EditText edCrearMensaje;
    private Button btnEnviarMensaje;
    private ArrayAdapter<String> adaptadorChat;
    private FileOutputStream fos;
    private PrintWriter writer;
    private Chat chat;
    private ArrayList<proyectodm.proyectodm.Message> mensajes;
    private String direccion;


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_STATE_CHANGED:
                    switch (message.arg1) {
                        case ChatUtils.STATE_NONE:
                            setState("No conectado");
                            break;
                        case ChatUtils.STATE_LISTEN:
                            setState("No conectado");
                            break;
                        case ChatUtils.STATE_CONNECTING:
                            setState("Conectando...");
                            break;
                        case ChatUtils.STATE_CONNECTED:
                            setState("Conectado: " + dispositivoConectado);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] buffer1 = (byte[]) message.obj;
                    String outputBuffer = new String(buffer1);
                    adaptadorChat.add("Yo: " + outputBuffer);
                    mensajes.add(new proyectodm.proyectodm.Message("Yo", new Date(System.currentTimeMillis()), outputBuffer));
                    break;
                case MESSAGE_READ:
                    byte[] buffer = (byte[]) message.obj;
                    String inputBuffer = new String(buffer, 0, message.arg1);
                    adaptadorChat.add(dispositivoConectado + ": " + inputBuffer);
                    mensajes.add(new proyectodm.proyectodm.Message(dispositivoConectado, new Date(System.currentTimeMillis()), inputBuffer));
                    break;
                case MESSAGE_DEVICE_NAME:
                    dispositivoConectado = message.getData().getString(DEVICE_NAME);
                    Toast.makeText(context, dispositivoConectado, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(context, message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });

    private void setState(CharSequence subTitle) {
        getSupportActionBar().setSubtitle(subTitle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sala_chat_layout);
        getBluetoothInstance();
        mensajes = new ArrayList<>();
        context = this;
        start();
        chatUtils = new ChatUtils(context, handler);
    }

    private void start() {
        listaChatPrincipal = findViewById(R.id.id_conversaciones);
        edCrearMensaje = findViewById(R.id.texto_mensaje);
        btnEnviarMensaje = findViewById(R.id.boton_enviar);
        adaptadorChat = new ArrayAdapter<String>(context, R.layout.layout_mensaje);
        listaChatPrincipal.setAdapter(adaptadorChat);

        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mensaje = edCrearMensaje.getText().toString();
                if (!mensaje.isEmpty()) {
                    edCrearMensaje.setText("");
                    chatUtils.write(mensaje.getBytes());
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.opcion_buscar:
                checkPermission();
                return true;
            case R.id.opcion_encender_bluetooth:
                activarBluetooth();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SalaChat.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_CODE);
        } else {
            Intent intent = new Intent(this, ListaDispositivosBlueDrop.class);
            startActivityForResult(intent, SELECT_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_CODE && resultCode == RESULT_OK) {
            direccion = data.getStringExtra("direccionDispositivo");
            chatUtils.connect(bluetoothAdapter.getRemoteDevice(direccion));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(this, ListaDispositivosBlueDrop.class);
                startActivityForResult(intent, SELECT_CODE);
            } else {
                new AlertDialog.Builder(this).setCancelable(false)
                        .setMessage("La aplicacion no funcionara sin el permiso solicitado").
                        setPositiveButton("Permitir", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                checkPermission();
                            }
                        })
                        .setNegativeButton("Denegar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SalaChat.this.finish();
                            }
                        }).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    public static BluetoothAdapter getBluetoothInstance() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            return bluetoothAdapter;
        } else {
            return bluetoothAdapter;
        }

    }

    private void activarBluetooth() {
        if (bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth ya esta activado", Toast.LENGTH_SHORT).show();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.enable();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SalaChat.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, SCAN_CODE);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SalaChat.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, CONNECT_CODE);
            } else {
                if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
                    Intent intentReconocimiento = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    intentReconocimiento.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                    startActivity(intentReconocimiento);
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        chat = new Chat(dispositivoConectado, new Date(System.currentTimeMillis()), mensajes);
        MainActivity.chats.add(chat);
        Gson gson = new Gson();
        String chatJsonString = gson.toJson(MainActivity.chats);
        try {
            FileOutputStream fileOutputStream = this.openFileOutput("chats", Context.MODE_PRIVATE);
            PrintWriter writer = new PrintWriter(fileOutputStream);
            writer.println(chatJsonString);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (chatUtils != null) {
            chatUtils.stop();
        }
        super.onDestroy();

    }

}