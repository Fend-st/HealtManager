package com.example.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import fend.crono.Cronometro;

public class MainActivity extends AppCompatActivity {

    protected Intent pasarPantalla;
    protected Button boton1_main;
    protected Button boton2_main;
    protected Button boton3_main;
    //protected GestorBD gbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle("HealthManager");

        //Instanciamos la BD:
        /*gbd = new GestorBD(this);

        //LLAMAMOS A LOS MÉTODOS DE PRUEBA:
        gbd.consultarUsuario();
        gbd.consultarEvento();
        gbd.consultarDia();*/

        //Inicializamos los botones:
        boton1_main = findViewById(R.id.boton1_main);
        boton2_main = findViewById(R.id.boton2_main);
        boton3_main = findViewById(R.id.boton3_main);

        boton1_main.setOnClickListener(v -> {//Pasamos a la actividad calendario
            pasarPantalla = new Intent(this, CalendarioActivity.class);
            startActivity(pasarPantalla);

        });
        boton2_main.setOnClickListener(v -> {//Pasamos a la actividad cronometro
            pasarPantalla = new Intent(this, Cronometro.class);
            startActivity(pasarPantalla);

        });

        boton3_main.setOnClickListener(v -> {//Pasamos a la actividad registro
            pasarPantalla = new Intent(this, ResumenActividadActivity.class);
            startActivity(pasarPantalla);

        });

    }
    @Override //Método para crear el menú. Falta hacer el evento onClick en el item para que vaya a la actividad correspondiente.
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
}