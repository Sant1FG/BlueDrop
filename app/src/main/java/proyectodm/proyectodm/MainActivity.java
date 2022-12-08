package proyectodm.proyectodm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String USERNAME;
    public static ArrayList<Chat> chats;
    BaseDeDatos db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chats = new ArrayList<>();
        //findViewById(R.id.activitymain).setVisibility(View.VISIBLE);
        //findViewById(R.id.layoutregistro2).setVisibility(View.INVISIBLE);
        //findViewById(R.id.layoutprincipal).setVisibility(View.INVISIBLE);

        TextView nombre = findViewById(R.id.Nombre);
        USERNAME =  nombre.getText().toString();
        TextView contraseña = findViewById(R.id.Contraseña);
        MaterialButton login = findViewById(R.id.botonlogin);
        MaterialButton registro = findViewById(R.id.botonRegistro);
        db = new BaseDeDatos(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombrelogin = nombre.getText().toString();
                String contraseñalogin = contraseña.getText().toString();
                if(nombrelogin.equals("") || contraseñalogin.equals("")){
                    Toast.makeText(MainActivity.this, "Campo vacío", Toast.LENGTH_SHORT).show();
                }else{
                   Boolean comprobar = db.comprobarCredenciales(nombrelogin,contraseñalogin);
                   if(comprobar==true){
                       Toast.makeText(MainActivity.this, "Login Correcto", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(getApplicationContext(),PaginaPrincipal.class);
                       startActivity(intent);
                       //findViewById(R.id.activitymain).setVisibility(View.INVISIBLE);
                       //findViewById(R.id.layoutregistro2).setVisibility(View.INVISIBLE);
                       //findViewById(R.id.layoutprincipal).setVisibility(View.VISIBLE);

                   }else{
                       Toast.makeText(MainActivity.this, "Combinación Nombre-Contraseña incorrecta", Toast.LENGTH_SHORT).show();

                   }
                }
                }
        });
        registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                //findViewById(R.id.layoutregistro2).setVisibility(View.VISIBLE);
                //findViewById(R.id.activitymain).setVisibility(View.INVISIBLE);
                //findViewById(R.id.layoutprincipal).setVisibility(View.INVISIBLE);
            }
        });
    }
}