package com.example.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import FernandoDiaz.CalendarActivity;
import FernandoDiaz.crono.Cronometro;

public class ResumenActividadActivity extends AppCompatActivity {

    protected TextView texto1_RA;
    protected TextView texto2_RA;
    protected TextView texto3_RA;
    protected TextView texto4_RA;
    protected TextView texto5_RA;
    protected ProgressBar progressDormir_RA;
    protected ProgressBar progressTrabajar_RA;
    protected ProgressBar progressEjercicio1_RA;
    protected ProgressBar progressEjercicio2_RA;
    protected ProgressBar progressEjercicio3_RA;
    protected GestorBD gbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumen_actividad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle("Resumen Actividad");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_summary);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_calendar) {
                startActivity(new Intent(this, CalendarActivity.class));
                return true;
            } else if (itemId == R.id.nav_timer) {
                startActivity(new Intent(this, Cronometro.class));
                return true;
            } else if (itemId == R.id.nav_summary) {
                return true;
            }
            return false;
        });

        //Inicializamos los botones:
        texto1_RA = findViewById(R.id.texto1_RA);
        texto2_RA = findViewById(R.id.texto2_RA);
        texto3_RA = findViewById(R.id.texto3_RA);
        texto4_RA = findViewById(R.id.texto4_RA);
        texto5_RA = findViewById(R.id.texto5_RA);
        progressDormir_RA = findViewById(R.id.progressDormir_RA);
        progressTrabajar_RA = findViewById(R.id.progressTrabajar_RA);
        progressEjercicio1_RA = findViewById(R.id.progressEjercicio1_RA);
        progressEjercicio2_RA = findViewById(R.id.progressEjercicio2_RA);
        progressEjercicio3_RA = findViewById(R.id.progressEjercicio3_RA);

        //Instanciamos la BD:
        gbd = new GestorBD(this);

        //RECOGER NOMBRE DE ACTIVIDAD:
        /*String nombreActividad = gbd.consultarNombreActividad();
        texto1_RA.setText(nombreActividad);*/


    }
}