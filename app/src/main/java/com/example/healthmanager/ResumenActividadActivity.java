package com.example.healthmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ResumenActividadActivity extends AppCompatActivity {

    protected TextView texto1_RA, texto2_RA, texto3_RA, texto4_RA, texto5_RA;
    protected ProgressBar progressCaminar_RA, progressCorrer_RA, progressGimnasio_RA, progressCiclismo_RA, progressYoga_RA;
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

        // 1. Inicializar vistas
        inicializarVistas();

        // 2. Instanciar BD
        gbd = new GestorBD(this);

        // 3. Comprobar si hay que poner a 0 (reinicio diario)
        comprobarReinicioDiario();

        // 4. Cargar los segundos actuales de la BD
        cargarDatosActividad();
    }

    private void inicializarVistas() {
        texto1_RA = findViewById(R.id.texto1_RA);
        texto2_RA = findViewById(R.id.texto2_RA);
        texto3_RA = findViewById(R.id.texto3_RA);
        texto4_RA = findViewById(R.id.texto4_RA);
        texto5_RA = findViewById(R.id.texto5_RA);
        progressCaminar_RA = findViewById(R.id.progressCaminar_RA);
        progressCorrer_RA = findViewById(R.id.progressCorrer_RA);
        progressGimnasio_RA = findViewById(R.id.progressGimnasio_RA);
        progressCiclismo_RA = findViewById(R.id.progressCiclismo_RA);
        progressYoga_RA = findViewById(R.id.progressYoga_RA);
    }

    private void comprobarReinicioDiario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaHoy = sdf.format(new Date());

        SharedPreferences prefs = getSharedPreferences("ControlDiario", Context.MODE_PRIVATE);
        String ultimaFecha = prefs.getString("ultima_fecha_uso", "");

        if (!fechaHoy.equals(ultimaFecha)) {
            // Si el día ha cambiado, llamamos al método de tu GestorBD que pone los segundos a 0
            gbd.reiniciarSegundosActividades();
            // Actualizamos la fecha de referencia
            prefs.edit().putString("ultima_fecha_uso", fechaHoy).apply();
        }
    }

    private void cargarDatosActividad() {
        Cursor cursor = gbd.obtenerActividad(); // Usamos tu método que devuelve todo

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Sacamos el nombre y el tiempo (en segundos) de la fila actual
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_NOMBRE));
                int segundos = cursor.getInt(cursor.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_TIEMPO));

                // Asignamos según el nombre de la actividad
                switch (nombre) {
                    case "Caminar":
                        texto1_RA.setText("Caminar: " + segundos + " seg");
                        progressCaminar_RA.setProgress(segundos);
                        break;
                    case "Correr":
                        texto2_RA.setText("Correr: " + segundos + " seg");
                        progressCorrer_RA.setProgress(segundos);
                        break;
                    case "Gimnasio":
                        texto3_RA.setText("Gimnasio: " + segundos + " seg");
                        progressGimnasio_RA.setProgress(segundos);
                        break;
                    case "Ciclismo":
                        texto4_RA.setText("Ciclismo: " + segundos + " seg");
                        progressCiclismo_RA.setProgress(segundos);
                        break;
                    case "Yoga":
                        texto5_RA.setText("Yoga: " + segundos + " seg");
                        progressYoga_RA.setProgress(segundos);
                        break;
                }
            }
            cursor.close();
        }
    }
}