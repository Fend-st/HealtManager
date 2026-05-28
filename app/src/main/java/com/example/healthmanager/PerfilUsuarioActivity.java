package com.example.healthmanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import FernandoDiaz.form.Formulario;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private TextView tvNombre, tvEdad, tvSexo, tvAltura, tvPeso, tvSangre;
    private Button btnVolver, btnEditar;
    private GestorBD gbd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        tvNombre = findViewById(R.id.tvNombrePerfil);
        tvEdad = findViewById(R.id.tvEdadPerfil);
        tvSexo = findViewById(R.id.tvSexoPerfil);
        tvAltura = findViewById(R.id.tvAlturaPerfil);
        tvPeso = findViewById(R.id.tvPesoPerfil);
        tvSangre = findViewById(R.id.tvSangrePerfil);
        btnVolver = findViewById(R.id.btnVolver);
        btnEditar = findViewById(R.id.btnEditar);

        gbd = new GestorBD(this);

        cargarDatosUsuario();

        btnVolver.setOnClickListener(v -> finish());

        btnEditar.setOnClickListener(v -> {
            Intent intent = new Intent(this, Formulario.class);
            intent.putExtra("EDIT_MODE", true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosUsuario();
    }

    private void cargarDatosUsuario() {
        Cursor cursor = gbd.obtenerUsuario();
        if (cursor != null && cursor.moveToFirst()) {
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_NOMBRE));
            int edad = cursor.getInt(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_EDAD));
            String sexo = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_SEXO));
            double altura = cursor.getDouble(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_ALTURA));
            double peso = cursor.getDouble(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_PESO));
            String sangre = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_SANGRE));

            tvNombre.setText("Nombre: " + nombre);
            tvEdad.setText("Edad: " + edad);
            tvSexo.setText("Sexo: " + sexo);
            tvAltura.setText("Altura: " + altura + " cm");
            tvPeso.setText("Peso: " + peso + " kg");
            tvSangre.setText("Tipo de Sangre: " + sangre);
            
            cursor.close();
        }
    }
}
