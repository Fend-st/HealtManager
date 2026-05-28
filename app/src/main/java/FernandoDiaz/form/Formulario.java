package FernandoDiaz.form;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthmanager.GestorBD;
import com.example.healthmanager.MainActivity;
import com.example.healthmanager.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/***
 * @author fend CC
 * @version 1.0.0
 *
 * Formulario para healtManager
 * todo: el envio de datos todavia no esta implementado
 *
 */


public class Formulario extends AppCompatActivity {

    // --- Campos de texto
    private TextInputLayout tilNombre, tilApellido, tilEdad, tilPeso, tilAltura;
    private TextInputEditText etNombre, etApellido, etEdad, etPeso, etAltura;

    // --- RadioGroups
    private RadioGroup rgSexo, rgSangre;

    // --- Botón de confirmacion
    private Button btnConfirmar;

    private boolean modoEdicion = false;
    private GestorBD gestorBD;

    // logica principal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        inicializarVistas();

        gestorBD = new GestorBD(this);

        // Comprobamos si venimos en modo edición
        if (getIntent().getBooleanExtra("EDIT_MODE", false)) {
            modoEdicion = true;
            btnConfirmar.setText("Actualizar Datos");
            cargarDatosParaEditar();
        }

        btnConfirmar.setOnClickListener(v -> {
            if (validarFormulario()) {
                // Todos los campos son válidos
                String nombre    = etNombre.getText().toString().trim();
                String apellido  = etApellido.getText().toString().trim();
                int    edad      = Integer.parseInt(etEdad.getText().toString().trim());
                double peso      = Double.parseDouble(etPeso.getText().toString().trim());
                double altura    = Double.parseDouble(etAltura.getText().toString().trim());

                int sexoId   = rgSexo.getCheckedRadioButtonId();
                int sangreId = rgSangre.getCheckedRadioButtonId();

                String sexo   = (sexoId   == R.id.rbMasculino) ? "Masculino" : "Femenino";
                String sangre = obtenerTipoSangre(sangreId);

                boolean exito;
                if (modoEdicion) {
                    exito = gestorBD.actualizarUsuario(
                            nombre + " " + apellido,
                            edad,
                            sexo,
                            altura,
                            peso,
                            sangre
                    );
                } else {
                    exito = gestorBD.insertarUsuario(
                            nombre + " " + apellido,
                            edad,
                            sexo,
                            altura,
                            peso,
                            sangre
                    );
                }

                //Si los datos se guardan correctamente
                if (exito) {
                    if (modoEdicion) {
                        Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Bienvenido/a " + nombre + " " + apellido + "!", Toast.LENGTH_LONG).show();
                        Intent pasarPantalla = new Intent(this, MainActivity.class);
                        startActivity(pasarPantalla);
                        finish();
                    }
                } else { //Si no se guardan, mostramos el mensaje de error
                    Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        setTitle(modoEdicion ? "Editar Perfil" : "Formulario");
    }

    private void cargarDatosParaEditar() {
        Cursor cursor = gestorBD.obtenerUsuario();
        if (cursor != null && cursor.moveToFirst()) {
            String nombreCompleto = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_NOMBRE));
            String[] partes = nombreCompleto.split(" ", 2);
            if (partes.length > 0) etNombre.setText(partes[0]);
            if (partes.length > 1) etApellido.setText(partes[1]);

            etEdad.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_EDAD))));
            etPeso.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_PESO))));
            etAltura.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_ALTURA))));

            String sexo = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_SEXO));
            if ("Masculino".equalsIgnoreCase(sexo)) rgSexo.check(R.id.rbMasculino);
            else if ("Femenino".equalsIgnoreCase(sexo)) rgSexo.check(R.id.rbFemenino);

            String sangre = cursor.getString(cursor.getColumnIndexOrThrow(GestorBD.USUARIO_SANGRE));
            if ("A".equals(sangre)) rgSangre.check(R.id.rbSangreA);
            else if ("B".equals(sangre)) rgSangre.check(R.id.rbSangreB);
            else if ("O".equals(sangre)) rgSangre.check(R.id.rbO);
            else if ("AB".equals(sangre)) rgSangre.check(R.id.rbSangreAB);

            cursor.close();
        }
    }

    // ---------------------------
    // iniciando las vistas...
    // --------------------------
    private void inicializarVistas() {
        tilNombre   = findViewById(R.id.textInputLayoutUsuario);
        tilApellido = findViewById(R.id.textInputLayoutApellido);
        tilEdad     = findViewById(R.id.textInputLayouEdad);
        tilPeso     = findViewById(R.id.textInputLayoutpeso);
        tilAltura   = findViewById(R.id.textInputLayoutAltura);


        etNombre   = findViewById(R.id.etUsuario);
        etApellido = findViewById(R.id.etApellido);
        etEdad     = findViewById(R.id.etEdad);
        etPeso     = findViewById(R.id.etpeso);
        etAltura   = findViewById(R.id.etAltura);

        rgSexo   = findViewById(R.id.rgSexo);
        rgSangre = findViewById(R.id.rgSangre);

        btnConfirmar = findViewById(R.id.btnConfirmar);
    }

    // ---------------------------------------------------
    // Validación  — devuelve true sólo si todo es correcto
    // -------------------------------------------------
    private boolean validarFormulario() {
        boolean valido = true;

        // 1. Nombre
        String nombre = etNombre.getText().toString().trim();
        if (TextUtils.isEmpty(nombre)) {
            tilNombre.setError("El nombre no puede estar vacío");
            valido = false;
        } else if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            tilNombre.setError("El nombre solo puede contener letras");
            valido = false;
        } else {
            tilNombre.setError(null);
            tilNombre.setErrorEnabled(false);
        }

        // 2. Apellido
        String apellido = etApellido.getText().toString().trim();
        if (TextUtils.isEmpty(apellido)) {
            tilApellido.setError("El apellido no puede estar vacío");
            valido = false;
        } else if (!apellido.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            tilApellido.setError("El apellido solo puede contener letras");
            valido = false;
        } else {
            tilApellido.setError(null);
            tilApellido.setErrorEnabled(false);
        }

        // 3. Edad
        String edadStr = etEdad.getText().toString().trim();
        if (TextUtils.isEmpty(edadStr)) {
            tilEdad.setError("La edad no puede estar vacía");
            valido = false;
        } else {
            try {
                int edad = Integer.parseInt(edadStr);
                if (edad < 1 || edad > 120) {
                    tilEdad.setError("Introduce una edad válida (1-120)");
                    valido = false;
                } else {
                    tilEdad.setError(null);
                    tilEdad.setErrorEnabled(false);
                }
            } catch (NumberFormatException e) {
                tilEdad.setError("La edad debe ser un número entero");
                valido = false;
            }
        }

        // 4. Peso
        String pesoStr = etPeso.getText().toString().trim();
        if (TextUtils.isEmpty(pesoStr)) {
            tilPeso.setError("El peso no puede estar vacío");
            valido = false;
        } else {
            try {
                double peso = Double.parseDouble(pesoStr);
                if (peso <= 0 || peso > 500) {
                    tilPeso.setError("Introduce un peso válido (kg)");
                    valido = false;
                } else {
                    tilPeso.setError(null);
                    tilPeso.setErrorEnabled(false);
                }
            } catch (NumberFormatException e) {
                tilPeso.setError("El peso debe ser un número");
                valido = false;
            }
        }

        // 5. Altura
        String alturaStr = etAltura.getText().toString().trim();
        if (TextUtils.isEmpty(alturaStr)) {
            tilAltura.setError("La altura no puede estar vacía");
            valido = false;
        } else {
            try {
                double altura = Double.parseDouble(alturaStr);
                if (altura < 50 || altura > 250) {
                    tilAltura.setError("Introduce una altura válida (cm)");
                    valido = false;
                } else {
                    tilAltura.setError(null);
                    tilAltura.setErrorEnabled(false);
                }
            } catch (NumberFormatException e) {
                tilAltura.setError("La altura debe ser un número");
                valido = false;
            }
        }

        // 6. Sexo (RadioGroup)
        if (rgSexo.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona tu sexo", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        // 7. Tipo de sangre (RadioGroup)
        if (rgSangre.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Por favor selecciona tu tipo de sangre", Toast.LENGTH_SHORT).show();
            valido = false;
        }

        return valido;
    }

    // -------------------------------------------------------------------------
    //  manejador para obtener los dato del tipo de sangre...
    // -------------------------------------------------------------------------
    private String obtenerTipoSangre(int id) {
        if (id == R.id.rbSangreA)  return "A";
        if (id == R.id.rbSangreB)  return "B";
        if (id == R.id.rbO)        return "O";
        if (id == R.id.rbSangreAB) return "AB";
        return "";
    }
}
