package com.example.healthmanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PerfilUsuarioActivity extends AppCompatActivity {

    protected TextView texto1;
    protected EditText caja1;
    protected EditText caja2;
    protected EditText caja3;
    protected EditText caja4;
    protected EditText caja5;
    protected EditText caja6;
    protected Button boton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_usuario);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        texto1 = findViewById(R.id.texto1_perfil);
        caja1 = findViewById(R.id.caja1_perfil);
        caja2 = findViewById(R.id.caja2_perfil);
        caja3 = findViewById(R.id.caja3_perfil);
        caja4 = findViewById(R.id.caja4_perfil);
        caja5 = findViewById(R.id.caja5_perfil);
        caja6 = findViewById(R.id.caja6_perfil);
        boton1 = findViewById(R.id.boton1_perfil);

        boton1.setOnClickListener(v -> {
            if (caja1.getText().toString().isEmpty()) {
                Toast.makeText(PerfilUsuarioActivity.this, "Por favor, introduzca su nombre", Toast.LENGTH_SHORT).show();
            }
            if (caja2.getText().toString().isEmpty()) {
                Toast.makeText(PerfilUsuarioActivity.this, "Por favor, introduzca su edad", Toast.LENGTH_SHORT).show();
            }
            if (caja3.getText().toString().isEmpty()) {
                Toast.makeText(PerfilUsuarioActivity.this, "Por favor, introduzca su sexo", Toast.LENGTH_SHORT).show();
            }
            if (caja4.getText().toString().isEmpty()) {
                Toast.makeText(PerfilUsuarioActivity.this, "Por favor, introduzca su altura", Toast.LENGTH_SHORT).show();
            }
            if (caja5.getText().toString().isEmpty()) {
                Toast.makeText(PerfilUsuarioActivity.this, "Por favor, introduzca su peso", Toast.LENGTH_SHORT).show();
            }
            if (caja6.getText().toString().isEmpty()) {
                Toast.makeText(PerfilUsuarioActivity.this, "Por favor, introduzca su tipo de sangre", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PerfilUsuarioActivity.this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(PerfilUsuarioActivity.this, MainActivity.class));
            }
        });
    }
}