package com.example.healthmanager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.database.Cursor;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPagerTips;

    GestorBD gbd;

    Handler sliderHandler = new Handler(Looper.getMainLooper());

    Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gbd = new GestorBD(this);

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvPeso = findViewById(R.id.tvPeso);
        TextView tvAltura = findViewById(R.id.tvAltura);
        TextView tvIMC = findViewById(R.id.tvIMC);

        Cursor curUsuario = gbd.obtenerUsuario();

        if (curUsuario.moveToFirst()) {

            String nombre = curUsuario.getString(
                    curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_NOMBRE));

            double peso = curUsuario.getDouble(
                    curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_PESO));

            double altura = curUsuario.getDouble(
                    curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_ALTURA));

            double alturaEnMetros = altura / 100;

            double imc = peso / (alturaEnMetros * alturaEnMetros);

            tvNombre.setText("Nombre: " + nombre);
            tvPeso.setText("Peso: " + peso + " kg");
            tvAltura.setText("Altura: " + altura + " cm");
            tvIMC.setText("IMC: " + String.format("%.2f", imc));
        }

        curUsuario.close();

        viewPagerTips = findViewById(R.id.viewPagerTips);

        List<TipModel> tipsList = new ArrayList<>();

        tipsList.add(new TipModel(
                "Recuerda dormir 8h",
                R.drawable.tip_sleep
        ));

        tipsList.add(new TipModel(
                "Consume alimentos naturales",
                R.drawable.tip_food
        ));

        tipsList.add(new TipModel(
                "Reduce el sedentarismo",
                R.drawable.tip_walk
        ));

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

    }

    @Override
    protected void onPause() {
        super.onPause();

        sliderHandler.removeCallbacks(sliderRunnable);
    }

}