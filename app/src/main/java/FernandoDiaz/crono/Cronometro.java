package FernandoDiaz.crono;

import android.content.Intent;
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

import FernandoDiaz.CalendarActivity;

public class Cronometro extends AppCompatActivity {
    protected ProgressBar circularProgressBar;
    protected TextView tvTimer;
    protected TextView tvCurrentActivity;
    protected TextView tvTitle;
    protected ImageButton btnActivity1;
    protected ImageButton btnActivity2;
    protected ImageButton btnActivity3;
    protected ImageButton btnActivity4;
    protected ImageButton btnActivity5;
    protected Button btnActivity6;
    protected Button btnActivity7;
    protected int posicion = 0;
    protected int progress = 0;
    protected boolean isPlaying = false;
    protected Handler handler;
    protected String nombreActividad = "";
    protected BottomNavigationView menuNavegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cronometro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Inicializamos los elementos:
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
        handler = new Handler();

        //MENU DE NAVEGACIÓN
        //Marcamos el item en el que nos encontramos (Cronometro en este caso)
        menuNavegacion.setSelectedItemId(R.id.nav_timer);

        //Evento para cambiar de actividad segun el boton que pulsemos
        menuNavegacion.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_calendar) {
                startActivity(new Intent(this, CalendarActivity.class));
                return true;
            } else if (itemId == R.id.nav_timer) {
                return true;
            } else if (itemId == R.id.nav_summary) {
                startActivity(new Intent(this, ResumenActividadActivity.class));
                return true;
            }
            return false;
        });

        //Inicializamos los valores:
        tvTimer.setText("00:00:00");
        tvCurrentActivity.setText("ACTIVIDAD ACTUAL: NINGUNA");
        circularProgressBar.setProgress(0);

        //Desactivamos el botón Play del cronómetro hasta que el usuario seleccione una actividad
        btnActivity6.setEnabled(false);

        //Inicializamos los botones:
        btnActivity1.setOnClickListener(v -> {//Seleccionamos la actividad 1
            posicion = 1;
            nombreActividad = "Caminar";
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: " + nombreActividad);
            btnActivity6.setEnabled(true); //Activamos el botón Play del cronómetro

        });
        btnActivity2.setOnClickListener(v -> {//Selecionamos la actividad 2
            posicion = 2;
            nombreActividad = "Correr";
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: " + nombreActividad);
            btnActivity6.setEnabled(true); //Activamos el botón Play del cronómetro

        });
        btnActivity3.setOnClickListener(v -> {//Selecionamos la actividad 3
            posicion = 3;
            nombreActividad = "Gimnasio";
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: " + nombreActividad);
            btnActivity6.setEnabled(true); //Activamos el botón Play del cronómetro

        });
        btnActivity4.setOnClickListener(v -> {//Selecionamos la actividad 4
            posicion = 4;
            nombreActividad = "Ciclismo";
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: " + nombreActividad);
            btnActivity6.setEnabled(true); //Activamos el botón Play del cronómetro

        });
        btnActivity5.setOnClickListener(v -> {//Selecionamos la actividad 5
            posicion = 5;
            nombreActividad = "Yoga";
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: " + nombreActividad);
            btnActivity6.setEnabled(true); //Activamos el botón Play del cronómetro

        });
        btnActivity6.setOnClickListener(v -> {//Comenzamos el cronómetro (Botón PLAY)
            btnActivity6.setEnabled(false); //Desactivamos el botón Play del cronómetro
            ejecutarCronometro();

            /*if(posicion==1){
                isPlaying = true;
                ejecutarCronometro();

            }else if(posicion==2){
                isPlaying = true;
                ejecutarCronometro();

            }else if(posicion==3){
                isPlaying = true;
                ejecutarCronometro();

            }else if(posicion==4){
                isPlaying = true;
                ejecutarCronometro();

            }else if(posicion==5){
                isPlaying = true;
                ejecutarCronometro();
            }else {
                Toast.makeText(this, "No hay ninguna actividad registrada", Toast.LENGTH_SHORT).show();
            }*/
        });
        btnActivity7.setOnClickListener(v -> {//Paramos el cronómetro (Botón STOP)

            //Si no hay ninguna actividad en curso, no hacemos nada
            if (progress == 0) {
                Toast.makeText(this, "No hay ninguna actividad en curso", Toast.LENGTH_SHORT).show();
                return;
            }
            isPlaying = false;
            btnActivity6.setEnabled(true); //Activamos de nuevo el botón Play del cronómetro

            //Obtenemos el tiempo del cronómetro
            String tiempo = tvTimer.getText().toString();

            //Obtenemos la fecha del sistema
            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            //Guardamos los datos en la base de datos
            GestorBD gbd = new GestorBD(this);
            boolean insertado = gbd.insertarActividad(nombreActividad, tiempo, fecha, 1);

            if (insertado) {
                Toast.makeText(this, "Actividad guardada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar la actividad", Toast.LENGTH_SHORT).show();
            }

            //Reiniciamos los valores del cronómetro
            progress = 0;
            tvTimer.setText("00:00:00");

            //Reseteamos la posicion y deshabilitamos el Play
            posicion = 0;
            btnActivity6.setEnabled(false);
            circularProgressBar.setProgress(0);
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: NINGUNA");
        });

    }
    //Método para ejecutar el cronometro
    private void ejecutarCronometro() {
        if (isPlaying) return; //Si el cronómetro ya está en marcha, no hacemos nada
        isPlaying = true; //Si el cronómetro no está en marcha, lo iniciamos

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String tiempoFormateado = formatearTiempo(progress);
                tvTimer.setText(tiempoFormateado);
                circularProgressBar.setProgress(progress);

                if (isPlaying) {
                    progress++;
                    // Se vuelve a llamar a sí mismo en 1 segundo (1000ms)
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }
    //Método para formatear el tiempo y que aparezca como 00:00:00
    public static String formatearTiempo(int segundosTotales) {
        int horas = segundosTotales / 3600;
        int minutos = (segundosTotales % 3600) / 60;
        int segs = segundosTotales % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segs);
    }

    public static String obtenerTextoActividad(int idActividad) {
        if (idActividad == 0) return "ACTIVIDAD ACTUAL: NINGUNA";
        if (idActividad >= 1 && idActividad <= 5) {
            return "ACTIVIDAD ACTUAL: " + idActividad;
        }
        return "ACTIVIDAD ACTUAL: NINGUNA";
    }

}