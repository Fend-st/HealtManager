package com.example.healthmanager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import FernandoDiaz.calendar.CalendarActivity;
import FernandoDiaz.crono.Cronometro;

public class ResumenActividadActivity extends AppCompatActivity {

    protected TextView tvCaminar_RA, tvCorrer_RA, tvGimnasio_RA, tvCiclismo_RA, tvYoga_RA;
    protected ProgressBar progressCaminar_RA, progressCorrer_RA, progressGimnasio_RA, progressCiclismo_RA, progressYoga_RA;
    protected GestorBD gbd;

    // Constante para el máximo de 24 horas en segundos
    private static final int SEGUNDOS_24H = 86400;

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

        // 5. Configurar la barra de navegación
        configurarNavegacion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_summary);
        }
    }

    private void inicializarVistas() {
        tvCaminar_RA = findViewById(R.id.tvCaminar_RA);
        tvCorrer_RA = findViewById(R.id.tvCorrer_RA);
        tvGimnasio_RA = findViewById(R.id.tvGimnasio_RA);
        tvCiclismo_RA = findViewById(R.id.tvCiclismo_RA);
        tvYoga_RA = findViewById(R.id.tvYoga_RA);

        progressCaminar_RA = findViewById(R.id.progressCaminar_RA);
        progressCorrer_RA = findViewById(R.id.progressCorrer_RA);
        progressGimnasio_RA = findViewById(R.id.progressGimnasio_RA);
        progressCiclismo_RA = findViewById(R.id.progressCiclismo_RA);
        progressYoga_RA = findViewById(R.id.progressYoga_RA);

        // Aseguramos que el máximo sea 24 horas (86400 segundos)
        progressCaminar_RA.setMax(SEGUNDOS_24H);
        progressCorrer_RA.setMax(SEGUNDOS_24H);
        progressGimnasio_RA.setMax(SEGUNDOS_24H);
        progressCiclismo_RA.setMax(SEGUNDOS_24H);
        progressYoga_RA.setMax(SEGUNDOS_24H);
    }

    private void configurarNavegacion() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Marcamos que estamos en la pestaña de Resumen
        bottomNavigationView.setSelectedItemId(R.id.nav_summary);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_calendar) {
                Intent intent = new Intent(this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_timer) {
                Intent intent = new Intent(this, Cronometro.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (itemId == R.id.nav_summary) {
                return true; // Ya estamos aquí
            }
            return false;
        });
    }

    private void comprobarReinicioDiario() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String fechaHoy = sdf.format(new Date());

        SharedPreferences prefs = getSharedPreferences("ControlDiario", Context.MODE_PRIVATE);
        String ultimaFecha = prefs.getString("ultima_fecha_uso", "");

        if (!fechaHoy.equals(ultimaFecha)) {
            gbd.reiniciarSegundosActividades();
            prefs.edit().putString("ultima_fecha_uso", fechaHoy).apply();
        }
    }

    private void cargarDatosActividad() {
        Cursor cursor = gbd.obtenerActividad();

        int segundosCaminar = 0;
        int segundosCorrer = 0;
        int segundosGimnasio = 0;
        int segundosCiclismo = 0;
        int segundosYoga = 0;

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_NOMBRE));
                String tiempoString = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_TIEMPO));
                int segundos = convertirHHMMSSaSegundos(tiempoString);

                switch (nombre) {
                    case "Caminar": segundosCaminar += segundos; break;
                    case "Correr": segundosCorrer += segundos; break;
                    case "Gimnasio": segundosGimnasio += segundos; break;
                    case "Ciclismo": segundosCiclismo += segundos; break;
                    case "Yoga": segundosYoga += segundos; break;
                }
            }
            cursor.close();
        }

        actualizarVistaActividad(tvCaminar_RA, progressCaminar_RA, "Caminar", segundosCaminar);
        actualizarVistaActividad(tvCorrer_RA, progressCorrer_RA, "Correr", segundosCorrer);
        actualizarVistaActividad(tvGimnasio_RA, progressGimnasio_RA, "Gimnasio", segundosGimnasio);
        actualizarVistaActividad(tvCiclismo_RA, progressCiclismo_RA, "Ciclismo", segundosCiclismo);
        actualizarVistaActividad(tvYoga_RA, progressYoga_RA, "Yoga", segundosYoga);
    }

    private void actualizarVistaActividad(TextView textView, ProgressBar progressBar, String etiqueta, int segundosTotales) {
        int h = segundosTotales / 3600;
        int m = (segundosTotales % 3600) / 60;
        int s = segundosTotales % 60;
        String tiempoFormateado = String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s);

        textView.setText(etiqueta + ": " + tiempoFormateado);

        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, segundosTotales);
        animation.setDuration(1500);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }

    private int convertirHHMMSSaSegundos(String tiempo) {
        try {
            if (tiempo == null || tiempo.isEmpty() || tiempo.equals("0")) return 0;
            if (tiempo.contains(":")) {
                String[] partes = tiempo.split(":");
                if (partes.length == 3) {
                    int h = Integer.parseInt(partes[0]);
                    int m = Integer.parseInt(partes[1]);
                    int s = Integer.parseInt(partes[2]);
                    return (h * 3600) + (m * 60) + s;
                }
            }
            return Integer.parseInt(tiempo);
        } catch (Exception e) {
            return 0;
        }
    }
}
