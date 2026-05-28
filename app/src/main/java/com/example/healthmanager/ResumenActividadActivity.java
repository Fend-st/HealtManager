package com.example.healthmanager;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
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

/**
 * Clase encargada de gestionar la pantalla de Resumen de Actividad.
 * En esta actividad se visualizan los progresos diarios del usuario mediante barras de progreso animadas.
 */
public class ResumenActividadActivity extends AppCompatActivity {

    // Referencias a los componentes de la interfaz de usuario
    protected TextView tvCaminar_RA, tvCorrer_RA, tvGimnasio_RA, tvCiclismo_RA, tvYoga_RA;
    protected ProgressBar progressCaminar_RA, progressCorrer_RA, progressGimnasio_RA, progressCiclismo_RA, progressYoga_RA;
    protected ImageButton btnDeleteCaminar, btnDeleteCorrer, btnDeleteGimnasio, btnDeleteCiclismo, btnDeleteYoga;
    protected GestorBD gbd;

    // Constante para el máximo de la barra (reducido a 1 hora para que sea apreciable en el vídeo explicativo)
    private static final int MAX_SEGUNDOS_BARRA = 3600;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Habilitamos el diseño EdgeToEdge para una experiencia más inmersiva
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resumen_actividad);

        // Ajustamos los paddings para que el contenido no quede oculto tras las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializamos las referencias de las vistas
        inicializarVistas();

        // 2. Instanciamos el gestor de la base de datos local
        gbd = new GestorBD(this);

        // 3. Verificamos si es un nuevo día para resetear los contadores
        comprobarReinicioDiario();

        // 4. Cargamos los datos almacenados en la base de datos
        cargarDatosActividad();

        // 5. Configuramos los eventos de clic para los botones de reinicio individual
        configurarBotonesBorrado();

        // 6. Configuramos la lógica de navegación inferior
        configurarNavegacion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Aseguramos que el ítem del resumen esté seleccionado al volver a esta actividad
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_summary);
        }

        // Refrescamos los datos por si se ha registrado actividad nueva desde el cronómetro
        comprobarReinicioDiario();
        cargarDatosActividad();
    }

    /**
     * Método para enlazar las variables Java con los elementos del layout XML.
     */
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

        // Establecemos el valor máximo de las barras basándonos en nuestra constante
        progressCaminar_RA.setMax(MAX_SEGUNDOS_BARRA);
        progressCorrer_RA.setMax(MAX_SEGUNDOS_BARRA);
        progressGimnasio_RA.setMax(MAX_SEGUNDOS_BARRA);
        progressCiclismo_RA.setMax(MAX_SEGUNDOS_BARRA);
        progressYoga_RA.setMax(MAX_SEGUNDOS_BARRA);

        btnDeleteCaminar = findViewById(R.id.btnDeleteCaminar);
        btnDeleteCorrer = findViewById(R.id.btnDeleteCorrer);
        btnDeleteGimnasio = findViewById(R.id.btnDeleteGimnasio);
        btnDeleteCiclismo = findViewById(R.id.btnDeleteCiclismo);
        btnDeleteYoga = findViewById(R.id.btnDeleteYoga);
    }

    /**
     * Configura los listeners para los botones de papelera, permitiendo borrar el tiempo de una actividad.
     */
    private void configurarBotonesBorrado() {
        btnDeleteCaminar.setOnClickListener(v -> reiniciarYRefrescar("Caminar"));
        btnDeleteCorrer.setOnClickListener(v -> reiniciarYRefrescar("Correr"));
        btnDeleteGimnasio.setOnClickListener(v -> reiniciarYRefrescar("Gimnasio"));
        btnDeleteCiclismo.setOnClickListener(v -> reiniciarYRefrescar("Ciclismo"));
        btnDeleteYoga.setOnClickListener(v -> reiniciarYRefrescar("Yoga"));
    }

    /**
     * Reinicia en la base de datos la actividad seleccionada y actualiza la vista.
     */
    private void reiniciarYRefrescar(String actividad) {
        gbd.reiniciarActividad(actividad);
        cargarDatosActividad();
    }

    /**
     * Define el comportamiento de los ítems del menú de navegación inferior.
     */
    private void configurarNavegacion() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

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
                return true; 
            }
            return false;
        });
    }

    /**
     * Comprueba mediante SharedPreferences si la fecha actual difiere de la última registrada
     * para reiniciar los contadores de actividad automáticamente al cambiar de día.
     */
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

    /**
     * Obtiene los datos de la base de datos, procesa el tiempo total por actividad y los muestra.
     */
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

    /**
     * Actualiza el texto del tiempo y anima la barra de progreso de cada actividad.
     * Se utiliza ObjectAnimator para lograr una transición visual suave.
     */
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

    /**
     * Método de utilidad para convertir el formato HH:MM:SS almacenado en la BD a segundos totales.
     */
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
