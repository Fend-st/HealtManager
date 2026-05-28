package com.example.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.database.Cursor;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import FernandoDiaz.calendar.CalendarActivity;
import FernandoDiaz.crono.Cronometro;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPagerTips;
    GestorBD gbd;
    Handler sliderHandler = new Handler(Looper.getMainLooper());
    Runnable sliderRunnable;

    // Vistas del resumen de actividad en el Main
    protected TextView tvCaminar_Main, tvCorrer_Main, tvGimnasio_Main, tvCiclismo_Main, tvYoga_Main;
    protected ProgressBar progressCaminar_Main, progressCorrer_Main, progressGimnasio_Main, progressCiclismo_Main, progressYoga_Main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gbd = new GestorBD(this);

        // --- COMPROBAR REINICIO DIARIO ---
        comprobarReinicioDiario();

        // --- DATOS BIOMÉTRICOS ---
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvPeso = findViewById(R.id.tvPeso);
        TextView tvAltura = findViewById(R.id.tvAltura);
        TextView tvIMC = findViewById(R.id.tvIMC);

        Cursor curUsuario = gbd.obtenerUsuario();
        if (curUsuario != null) {
            if (curUsuario.moveToFirst()) {
                String nombre = curUsuario.getString(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_NOMBRE));
                double peso = curUsuario.getDouble(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_PESO));
                double altura = curUsuario.getDouble(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_ALTURA));
                double alturaEnMetros = altura / 100;
                double imc = peso / (alturaEnMetros * alturaEnMetros);

                tvNombre.setText("Nombre: " + nombre);
                tvPeso.setText("Peso: " + peso + " kg");
                tvAltura.setText("Altura: " + altura + " cm");
                tvIMC.setText("IMC: " + String.format(java.util.Locale.getDefault(), "%.2f", imc));
            }
            curUsuario.close();
        }

        // --- RESUMEN DE ACTIVIDAD (Lógica calcada de ResumenActividadActivity) ---
        inicializarVistasResumen();
        cargarDatosActividad();

        // --- CARRUSEL ---
        viewPagerTips = findViewById(R.id.viewPagerTips);
        List<TipModel> tipsList = new ArrayList<>();
        tipsList.add(new TipModel("Recuerda dormir 8h", R.drawable.tip_sleep));
        tipsList.add(new TipModel("Consume alimentos naturales", R.drawable.tip_food));
        tipsList.add(new TipModel("Reduce el sedentarismo", R.drawable.tip_walk));

        TipsAdapter adapter = new TipsAdapter(tipsList);
        viewPagerTips.setAdapter(adapter);

        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = viewPagerTips.getCurrentItem() + 1;
                if (nextItem >= tipsList.size()) {
                    nextItem = 0;
                }
                viewPagerTips.setCurrentItem(nextItem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        };
        sliderHandler.postDelayed(sliderRunnable, 3000);

        // --- BARRA DE NAVEGACIÓN ---
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_calendar) {
                startActivity(new Intent(this, CalendarActivity.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_timer) {
                startActivity(new Intent(this, Cronometro.class));
                finish();
                return true;
            } else if (itemId == R.id.nav_summary) {
                startActivity(new Intent(this, ResumenActividadActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    private void inicializarVistasResumen() {
        // Vinculamos los IDs del main con la lógica del resumen
        tvCaminar_Main = findViewById(R.id.tvCaminarMain);
        tvCorrer_Main = findViewById(R.id.tvCorrerMain);
        tvGimnasio_Main = findViewById(R.id.tvGimnasioMain);
        tvCiclismo_Main = findViewById(R.id.tvCiclismoMain);
        tvYoga_Main = findViewById(R.id.tvYogaMain);

        progressCaminar_Main = findViewById(R.id.progressCaminarMain);
        progressCorrer_Main = findViewById(R.id.progressCorrerMain);
        progressGimnasio_Main = findViewById(R.id.progressGimnasioMain);
        progressCiclismo_Main = findViewById(R.id.progressCiclismoMain);
        progressYoga_Main = findViewById(R.id.progressYogaMain);
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

        actualizarVistaActividad(tvCaminar_Main, progressCaminar_Main, "Caminar", segundosCaminar);
        actualizarVistaActividad(tvCorrer_Main, progressCorrer_Main, "Correr", segundosCorrer);
        actualizarVistaActividad(tvGimnasio_Main, progressGimnasio_Main, "Gimnasio", segundosGimnasio);
        actualizarVistaActividad(tvCiclismo_Main, progressCiclismo_Main, "Ciclismo", segundosCiclismo);
        actualizarVistaActividad(tvYoga_Main, progressYoga_Main, "Yoga", segundosYoga);
    }

    private void actualizarVistaActividad(TextView textView, ProgressBar progressBar, String etiqueta, int segundosTotales) {
        if (textView == null || progressBar == null) return;

        int h = segundosTotales / 3600;
        int m = (segundosTotales % 3600) / 60;
        int s = segundosTotales % 60;
        String tiempoFormateado = String.format(java.util.Locale.getDefault(), "%02d:%02d:%02d", h, m, s);

        textView.setText(etiqueta + ": " + tiempoFormateado);
        progressBar.setProgress(segundosTotales);
    }

    private void comprobarReinicioDiario() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String fechaHoy = sdf.format(new java.util.Date());

        android.content.SharedPreferences prefs = getSharedPreferences("ControlDiario", android.content.Context.MODE_PRIVATE);
        String ultimaFecha = prefs.getString("ultima_fecha_uso", "");

        if (!fechaHoy.equals(ultimaFecha)) {
            gbd.reiniciarSegundosActividades();
            prefs.edit().putString("ultima_fecha_uso", fechaHoy).apply();
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}
