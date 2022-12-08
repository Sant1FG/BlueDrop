package proyectodm.proyectodm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

public class ListaDispositivosBlueDrop extends AppCompatActivity {

    private ListView listaVinculados;
    private final int LOCATION_CODE = 200;
    private final int SCAN_CODE = 201;
    private final int CONNECT_CODE = 202;
    private final int ADVERTISE_CODE = 203;
    private ListView listaDisponibles;
    private ArrayAdapter<String> adaptadorVinculados;
    private ArrayAdapter<String> adaptadorDisponibles;
    private BluetoothAdapter bluetoothAdapter;
    Set<BluetoothDevice> dispositivosVinculados;
    private String duplicados;

    private BroadcastReceiver bluetoothListener = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String accion = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(accion)) {
                BluetoothDevice dispositivo = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(ListaDispositivosBlueDrop.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (dispositivo.getBondState() != BluetoothDevice.BOND_BONDED && !dispositivo.getName().equals(duplicados)) {
                    adaptadorDisponibles.add(dispositivo.getName() + "\n" + dispositivo.getAddress());
                    duplicados = dispositivo.getName();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(accion)) {
                if (adaptadorDisponibles.getCount() == 0) {
                    Toast.makeText(ListaDispositivosBlueDrop.this, "No se han encontrado nuevos dispositivos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ListaDispositivosBlueDrop.this, "Nuevos dispositivos encontrados, seleccionalos para iniciar el chat", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_dispositivos_blue_drop);
        ActivityCompat.requestPermissions(ListaDispositivosBlueDrop.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, CONNECT_CODE);
        ActivityCompat.requestPermissions(ListaDispositivosBlueDrop.this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, SCAN_CODE);
        ActivityCompat.requestPermissions(ListaDispositivosBlueDrop.this, new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, ADVERTISE_CODE);
        start();

    }

    private void start() {
        bluetoothAdapter = SalaChat.getBluetoothInstance();
        listaVinculados = findViewById(R.id.lista_vinculados);
        listaDisponibles = findViewById(R.id.lista_disponibles);
        dispositivosVinculados = new HashSet<>();
        adaptadorVinculados = new ArrayAdapter<String>(this, R.layout.lista_dispositivos);
        adaptadorDisponibles = new ArrayAdapter<String>(this, R.layout.lista_dispositivos);
        listaVinculados.setAdapter(adaptadorVinculados);
        listaDisponibles.setAdapter(adaptadorDisponibles);

        listaDisponibles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String direccion = info.substring(info.length() - 17);
                Intent intent = new Intent();
                intent.putExtra("direccionDispositivo", direccion);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        dispositivosVinculados = bluetoothAdapter.getBondedDevices();

        if (dispositivosVinculados != null && dispositivosVinculados.size() > 0) {
            for (BluetoothDevice dispositivo : dispositivosVinculados) {
                adaptadorVinculados.add(dispositivo.getName() + "\n" + dispositivo.getAddress());
            }
        }

        IntentFilter filtroIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothListener, filtroIntent);
        IntentFilter filtroIntent2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothListener, filtroIntent2);

        listaVinculados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (ActivityCompat.checkSelfPermission(ListaDispositivosBlueDrop.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                bluetoothAdapter.cancelDiscovery();

                String info = ((TextView) view).getText().toString();
                String direccion = info.substring(info.length() - 17);

                Log.d("Direccion", direccion);

                Intent intent = new Intent();
                intent.putExtra("direccionDispositivo", direccion);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_lista_dispositivos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_escanear:
                escanearDispositivos();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void escanearDispositivos() {
        adaptadorDisponibles.clear();
        Toast.makeText(this, "Iniciando escaneado, puede llevar un rato", Toast.LENGTH_LONG).show();


        try{
            if (bluetoothAdapter.isDiscovering()) {
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothAdapter.startDiscovery();
        }catch(SecurityException e){
            System.err.println("Ha ocurrido un error inesperado");
        }

    }

}