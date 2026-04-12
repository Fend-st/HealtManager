package fend.crono;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.healthmanager.R;

import java.util.Locale;

public class Cronometro extends AppCompatActivity {
    protected ProgressBar circularProgressBar;
    protected TextView tvTimer;
    protected TextView tvCurrentActivity;
    protected TextView tvTitle;
    protected Button btnActivity1;
    protected Button btnActivity2;
    protected Button btnActivity3;
    protected Button btnActivity4;
    protected Button btnActivity5;
    protected Button btnActivity6;
    protected Button btnActivity7;
    protected int posicion = 0;
    protected int progress = 0;
    protected boolean isPlaying = false;
    protected Handler handler;

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
        handler = new Handler();

        //Inicializamos los valores:
        tvTimer.setText("00:00:00");
        tvCurrentActivity.setText("ACTIVIDAD ACTUAL: NINGUNA");
        circularProgressBar.setProgress(0);

        btnActivity1.setOnClickListener(v -> {//Selecionamos la actividad 1
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: 1");
            posicion = 1;

        });
        btnActivity2.setOnClickListener(v -> {//Selecionamos la actividad 2
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: 2");
            posicion = 2;

        });
        btnActivity3.setOnClickListener(v -> {//Selecionamos la actividad 3
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: 3");
            posicion = 3;

        });
        btnActivity4.setOnClickListener(v -> {//Selecionamos la actividad 4
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: 4");
            posicion = 4;

        });
        btnActivity5.setOnClickListener(v -> {//Selecionamos la actividad 5
            tvCurrentActivity.setText("ACTIVIDAD ACTUAL: 5");
            posicion = 5;

        });
        btnActivity6.setOnClickListener(v -> {//Comenzamos el cronometro en función de la actividad seleccionada

            if(posicion==1){
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
            }
        });
        btnActivity7.setOnClickListener(v -> {//Paramos el cronometro
            isPlaying = false;

            progress = 0;
            tvTimer.setText("00:00:00");

        });

    }
    //Método para ejecutar el cronometro
    private void ejecutarCronometro() {
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
}