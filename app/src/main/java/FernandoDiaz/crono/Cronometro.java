package FernandoDiaz.crono;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.healthmanager.GestorBD;
import com.example.healthmanager.MainActivity;
import com.example.healthmanager.R;
import com.example.healthmanager.ResumenActividadActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import FernandoDiaz.calendar.CalendarActivity;

/**
 * Actividad que implementa un cronómetro funcional para registrar diferentes actividades físicas.
 * Utiliza un Handler para la actualización periódica de la interfaz y SQLite para la persistencia.
 */
public class Cronometro extends AppCompatActivity {
    // Definición de los componentes de la interfaz de usuario
    protected ProgressBar circularProgressBar;
    protected TextView tvTimer;
    protected TextView tvCurrentActivity;
    protected TextView tvTitle;
    protected ImageButton btnActivity1, btnActivity2, btnActivity3, btnActivity4, btnActivity5;
    protected Button btnActivity6, btnActivity7;

    // Variables de control de la lógica del cronómetro
    protected int posicion = 0;
    protected int progress = 0;
    protected boolean isPlaying = false;
    protected Handler handler; // Manejador para ejecutar tareas en el hilo principal
    protected String nombreActividad = "";
    protected BottomNavigationView menuNavegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cronometro);
        
        // Configuración de los Insets para el diseño EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializamos las vistas y el manejador del hilo
        inicializarVistas();
        handler = new Handler();

        // Configuramos el menú de navegación y establecemos el estado inicial
        configurarNavegacion();
        estadoInicial();

        // Asociamos la lógica a los selectores de actividad y botones de control
        configurarSelectoresActividad();
        configurarBotonesControl();
    }

    /**
     * Enlaza las variables Java con los IDs definidos en el layout XML.
     */
    private void inicializarVistas() {
        circularProgressBar = findViewById(R.id.circularProgressBar);
        tvTimer = findViewById(R.id.tvTimer);
        tvCurrentActivity = findViewById(R.id.tvCurrentActivity);
        tvTitle = findViewById(R.id.tvTitle);
        btnActivity1 = findViewById(R.id.btnActivity1);
        btnActivity2 = findViewById(R.id.btnActivity2);
        btnActivity3 = findViewById(R.id.btnActivity3);
        btnActivity4 = findViewById(R.id.btnActivity4);
        btnActivity5 = findViewById(R.id.btnActivity5);
        btnActivity6 = findViewById(R.id.btnActivity6);
        btnActivity7 = findViewById(R.id.btnActivity7);
        menuNavegacion = findViewById(R.id.bottom_navigation);
    }

    /**
     * Restablece los valores visuales por defecto.
     */
    private void estadoInicial() {
        tvTimer.setText("00:00:00");
        tvCurrentActivity.setText("ACTIVIDAD ACTUAL: NINGUNA");
        circularProgressBar.setProgress(0);
        btnActivity6.setEnabled(false); // El botón PLAY se habilita al elegir una actividad
    }

    /**
     * Configura el listener para el BottomNavigationView y gestiona el cambio entre actividades.
     */
    private void configurarNavegacion() {
        menuNavegacion.setSelectedItemId(R.id.nav_timer);
        menuNavegacion.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_calendar) {
                Intent intent = new Intent(this, CalendarActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_timer) {
                return true;
            } else if (itemId == R.id.nav_summary) {
                Intent intent = new Intent(this, ResumenActividadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    /**
     * Define los eventos de selección para los diferentes tipos de ejercicio físico.
     */
    private void configurarSelectoresActividad() {
        btnActivity1.setOnClickListener(v -> seleccionarActividad(1, "Caminar"));
        btnActivity2.setOnClickListener(v -> seleccionarActividad(2, "Correr"));
        btnActivity3.setOnClickListener(v -> seleccionarActividad(3, "Gimnasio"));
        btnActivity4.setOnClickListener(v -> seleccionarActividad(4, "Ciclismo"));
        btnActivity5.setOnClickListener(v -> seleccionarActividad(5, "Yoga"));
    }

    /**
     * Actualiza el nombre de la actividad actual y habilita el botón de inicio.
     */
    private void seleccionarActividad(int p, String nombre) {
        posicion = p;
        nombreActividad = nombre;
        tvCurrentActivity.setText("ACTIVIDAD ACTUAL: " + nombreActividad);
        btnActivity6.setEnabled(true);
    }

    /**
     * Define el comportamiento de los botones PLAY y STOP del cronómetro.
     */
    private void configurarBotonesControl() {
        btnActivity6.setOnClickListener(v -> {
            btnActivity6.setEnabled(false);
            ejecutarCronometro();
        });

        btnActivity7.setOnClickListener(v -> {
            if (progress == 0) {
                Toast.makeText(this, "No hay ninguna actividad en curso", Toast.LENGTH_SHORT).show();
                return;
            }
            isPlaying = false;
            btnActivity6.setEnabled(true);

            // Almacenamos el registro en la base de datos antes de resetear
            guardarActividadEnBD();
            reiniciarCronometro();
        });
    }

    /**
     * Recupera el ID del usuario y persiste el registro de actividad en SQLite.
     */
    private void guardarActividadEnBD() {
        String tiempo = tvTimer.getText().toString();
        String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        GestorBD gbd = new GestorBD(this);
        int idUsuario = 1; 
        
        Cursor cursorUser = gbd.obtenerUsuario();
        if (cursorUser != null && cursorUser.moveToFirst()) {
            idUsuario = cursorUser.getInt(cursorUser.getColumnIndexOrThrow(GestorBD.USUARIO_ID));
            cursorUser.close();
        }

        boolean insertado = gbd.insertarActividad(nombreActividad, tiempo, fecha, idUsuario);
        if (insertado) {
            Toast.makeText(this, "Actividad guardada correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al guardar la actividad", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Limpia las variables de progreso y actualiza los elementos visuales al estado inicial.
     */
    private void reiniciarCronometro() {
        progress = 0;
        tvTimer.setText("00:00:00");
        posicion = 0;
        btnActivity6.setEnabled(false);
        circularProgressBar.setProgress(0);
        tvCurrentActivity.setText("ACTIVIDAD ACTUAL: NINGUNA");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (menuNavegacion != null) {
            menuNavegacion.setSelectedItemId(R.id.nav_timer);
        }
    }

    /**
     * Lógica recursiva que utiliza postDelayed para incrementar el contador cada segundo.
     */
    private void ejecutarCronometro() {
        if (isPlaying) return; 
        isPlaying = true; 

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String tiempoFormateado = formatearTiempo(progress);
                tvTimer.setText(tiempoFormateado);
                circularProgressBar.setProgress(progress);

                if (isPlaying) {
                    progress++;
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    /**
     * Formatea un valor entero de segundos en una cadena con formato HH:MM:SS.
     */
    public static String formatearTiempo(int segundosTotales) {
        int horas = segundosTotales / 3600;
        int minutos = (segundosTotales % 3600) / 60;
        int segs = segundosTotales % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segs);
    }

    /**
     * Método auxiliar para obtener el texto descriptivo de la actividad según su ID.
     */
    public static String obtenerTextoActividad(int idActividad) {
        if (idActividad == 0) return "ACTIVIDAD ACTUAL: NINGUNA";
        if (idActividad >= 1 && idActividad <= 5) {
            return "ACTIVIDAD ACTUAL: " + idActividad;
        }
        return "ACTIVIDAD ACTUAL: NINGUNA";
    }
}
