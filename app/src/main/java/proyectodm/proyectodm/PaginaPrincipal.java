package proyectodm.proyectodm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class PaginaPrincipal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.paginaprincipal);
        MaterialButton cerrarsesion = findViewById(R.id.botoncerrarsesion);
        MaterialButton conexion = findViewById(R.id.conexion);
        MaterialButton historico = findViewById(R.id.historico);

        historico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ShowChatsActivity.class);
                startActivity(intent);
            }
        });

        conexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),SalaChat.class);
                startActivity(intent);
            }
        });
        cerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PaginaPrincipal.this).setTitle("Cerrar sesión").setMessage("¿Está seguro?").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                    }
                }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(PaginaPrincipal.this, "Acción cancelada", Toast.LENGTH_SHORT).show();

                    }
                }).show();
               // findViewById(R.id.activitymain).setVisibility(View.VISIBLE);
               // findViewById(R.id.layoutregistro2).setVisibility(View.INVISIBLE);
                //findViewById(R.id.layoutprincipal).setVisibility(View.INVISIBLE);
            }
        });
    }
}