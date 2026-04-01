package com.example.healthmanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class TemporizadorActivity extends AppCompatActivity {
    protected TextView texto1;
    protected ListView lista1;
    protected Button boton1;
    protected ArrayList<String> actividades=new ArrayList<>();
    protected ArrayAdapter<String> adaptador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_temporizador);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        texto1 = findViewById(R.id.texto1_temporizador);
        lista1 = findViewById(R.id.lista1_temporizador);
        boton1 = findViewById(R.id.boton1_temporizador);

        actividades.add("actividad1");
        actividades.add("actividad2");
        actividades.add("actividad3");

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, actividades);
        lista1.setAdapter(adaptador);

    }
}