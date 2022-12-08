package proyectodm.proyectodm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
public class Login extends AppCompatActivity {
    //********Esta actividad se encarga de registrar nuevos usuarios

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layoutregistro);

        MaterialButton volver = findViewById(R.id.botonvolver);
        MaterialButton registar = findViewById(R.id.botonRegistro2);
        TextView nombreRegistrar = findViewById(R.id.Nombre2);
        TextView contraseñaRegistrar = findViewById(R.id.Contraseña2);
        BaseDeDatos db = new BaseDeDatos(this);
        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
               // findViewById(R.id.activitymain).setVisibility(View.VISIBLE);
               // findViewById(R.id.layoutregistro2).setVisibility(View.INVISIBLE);
                // findViewById(R.id.layoutprincipal).setVisibility(View.INVISIBLE);
            }
        });
        registar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String nombre = nombreRegistrar.getText().toString();
                String contraseña = contraseñaRegistrar.getText().toString();
                if(nombre.equals("")||contraseña.equals("")){
                    Toast.makeText(Login.this, "Campo vacío", Toast.LENGTH_SHORT).show();
                }else{
                    Boolean comprobarUsuario = db.comprobarExistenciaUsuario(nombre);
                    if(comprobarUsuario==false){
                        Boolean insertar = db.registrar(nombre,contraseña);
                        if(insertar==true){
                            Toast.makeText(Login.this, "Registrado correctamente", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                          //  findViewById(R.id.activitymain).setVisibility(View.VISIBLE);
                           // findViewById(R.id.layoutregistro2).setVisibility(View.INVISIBLE);
                           // findViewById(R.id.layoutprincipal).setVisibility(View.INVISIBLE);

                        }
                    }else{
                        Toast.makeText(Login.this, "Este usuario ya existe", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
    }
}