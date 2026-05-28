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

    // Vistas del resumen (Copiado de ResumenActividadActivity)
    protected TextView texto1_RA, texto2_RA, texto3_RA, texto4_RA, texto5_RA;
    protected ProgressBar progressCaminar_RA, progressCorrer_RA, progressGimnasio_RA, progressCiclismo_RA, progressYoga_RA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gbd = new GestorBD(this);

        // --- DATOS BIOMÉTRICOS ---
        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvPeso = findViewById(R.id.tvPeso);
        TextView tvAltura = findViewById(R.id.tvAltura);
        TextView tvIMC = findViewById(R.id.tvIMC);

        Cursor curUsuario = gbd.obtenerUsuario();
        if (curUsuario.moveToFirst()) {
            String nombre = curUsuario.getString(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_NOMBRE));
            double peso = curUsuario.getDouble(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_PESO));
            double altura = curUsuario.getDouble(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_ALTURA));
            double alturaEnMetros = altura / 100;
            double imc = peso / (alturaEnMetros * alturaEnMetros);

            tvNombre.setText("Nombre: " + nombre);
            tvPeso.setText("Peso: " + peso + " kg");
            tvAltura.setText("Altura: " + altura + " cm");
            tvIMC.setText("IMC: " + String.format("%.2f", imc));
        }
        curUsuario.close();

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
        texto1_RA = findViewById(R.id.tvCaminarMain);
        texto2_RA = findViewById(R.id.tvCorrerMain);
        texto3_RA = findViewById(R.id.tvGimnasioMain);
        texto4_RA = findViewById(R.id.tvCiclismoMain);
        texto5_RA = findViewById(R.id.tvYogaMain);
        
        progressCaminar_RA = findViewById(R.id.progressCaminarMain);
        progressCorrer_RA = findViewById(R.id.progressCorrerMain);
        progressGimnasio_RA = findViewById(R.id.progressGimnasioMain);
        progressCiclismo_RA = findViewById(R.id.progressCiclismoMain);
        progressYoga_RA = findViewById(R.id.progressYogaMain);
    }

    private void cargarDatosActividad() {
        Cursor cursor = gbd.obtenerActividad();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_NOMBRE));
                int segundos = cursor.getInt(cursor.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_TIEMPO));

                switch (nombre) {
                    case "Caminar":
                        if (texto1_RA != null) texto1_RA.setText("Caminar: " + segundos + " seg");
                        if (progressCaminar_RA != null) progressCaminar_RA.setProgress(segundos);
                        break;
                    case "Correr":
                        if (texto2_RA != null) texto2_RA.setText("Correr: " + segundos + " seg");
                        if (progressCorrer_RA != null) progressCorrer_RA.setProgress(segundos);
                        break;
                    case "Gimnasio":
                        if (texto3_RA != null) texto3_RA.setText("Gimnasio: " + segundos + " seg");
                        if (progressGimnasio_RA != null) progressGimnasio_RA.setProgress(segundos);
                        break;
                    case "Ciclismo":
                        if (texto4_RA != null) texto4_RA.setText("Ciclismo: " + segundos + " seg");
                        if (progressCiclismo_RA != null) progressCiclismo_RA.setProgress(segundos);
                        break;
                    case "Yoga":
                        if (texto5_RA != null) texto5_RA.setText("Yoga: " + segundos + " seg");
                        if (progressYoga_RA != null) progressYoga_RA.setProgress(segundos);
                        break;
                }
            }
            cursor.close();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
}
