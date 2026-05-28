package com.example.healthmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

//SISTEMA GESTOR DE LA BD DE LA APP HEALTH MANAGER
public class GestorBD extends SQLiteOpenHelper {

    //Variables de la Base de Datos:
    private static final String NOMBRE_BD = "HealthManagerDB.db";
    private static final int VERSION = 1;


    //Variables de la tabla Usuario y sus columnas:
    public static final String TABLA_USUARIO = "usuario";
    public static final String USUARIO_ID = "id_usuario";
    public static final String USUARIO_NOMBRE = "nombre";
    public static final String USUARIO_EDAD = "edad";
    public static final String USUARIO_SEXO = "sexo";
    public static final String USUARIO_ALTURA = "altura";
    public static final String USUARIO_PESO = "peso";
    public static final String USUARIO_SANGRE = "tipo_sangre";


    //Variables de la tabla Evento y sus columnas:
    public static final String TABLA_EVENTO = "evento";
    public static final String EVENTO_ID = "id_evento";
    public static final String EVENTO_NOMBRE = "nombre";
    public static final String EVENTO_DESCRIPCION = "descripcion";
    public static final String EVENTO_REPITE = "se_repite";
    public static final String EVENTO_ID_USUARIO = "id_usuario";


    //Variables de la tabla Dia y sus columnas:
    public static final String TABLA_DIA = "dia";
    public static final String DIA_ID = "id_dia";
    public static final String DIA_FECHA = "fecha";
    public static final String DIA_EMOCION = "emocion";
    public static final String DIA_ID_EVENTO = "id_evento";


    //Variables de la tabla Actividad y sus columnas:
    public static final String TABLA_ACTIVIDAD = "actividad";
    public static final String ACTIVIDAD_ID = "id_actividad";
    public static final String ACTIVIDAD_NOMBRE = "nombre";
    public static final String ACTIVIDAD_TIEMPO = "tiempo";
    public static final String ACTIVIDAD_FECHA = "fecha";
    public static final String ACTIVIDAD_ID_USUARIO = "id_usuario";


    //Creación de la Base de Datos
    public GestorBD(@Nullable Context context) {
        super(context, NOMBRE_BD, null, VERSION);

    }

    public GestorBD() {
        super(null, NOMBRE_BD, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //Activamos las Foreign Keys:
        db.execSQL("PRAGMA foreign_keys = ON");

        //Creación de la tabla Usuario:
        db.execSQL("CREATE TABLE " + TABLA_USUARIO + " (" + USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USUARIO_NOMBRE + " TEXT, " + USUARIO_EDAD + " INTEGER, " + USUARIO_SEXO + " TEXT, "
                + USUARIO_ALTURA + " REAL, " + USUARIO_PESO + " REAL, " + USUARIO_SANGRE + " TEXT)");

        //Creación de la tabla Evento:
        db.execSQL("CREATE TABLE " + TABLA_EVENTO + " ( " + EVENTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EVENTO_NOMBRE + " TEXT, " + EVENTO_DESCRIPCION + " TEXT, " + EVENTO_REPITE + " INTEGER, "
                + EVENTO_ID_USUARIO + " INTEGER, FOREIGN KEY (" + EVENTO_ID_USUARIO + ") REFERENCES "
                + TABLA_USUARIO + "(" + USUARIO_ID + "))");

        //Creación de la tabla Dia:
        db.execSQL("CREATE TABLE " + TABLA_DIA + " ( " + DIA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DIA_FECHA + " TEXT, " + DIA_EMOCION + " TEXT, " + DIA_ID_EVENTO + " INTEGER, FOREIGN KEY ("
                + DIA_ID_EVENTO + ") REFERENCES " + TABLA_EVENTO + "(" + EVENTO_ID + "))");

        //Creación de la tabla Actividad:
        db.execSQL("CREATE TABLE " + TABLA_ACTIVIDAD + " (" + ACTIVIDAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ACTIVIDAD_NOMBRE + " TEXT, " + ACTIVIDAD_TIEMPO + " TEXT, " + ACTIVIDAD_FECHA + " TEXT, "
                + ACTIVIDAD_ID_USUARIO + " INTEGER, FOREIGN KEY (" + ACTIVIDAD_ID_USUARIO + ") REFERENCES "
                + TABLA_USUARIO + "(" + USUARIO_ID + "))");

        //Introducción de valores de prueba:
        //db.execSQL("INSERT INTO " + TABLA_USUARIO + " VALUES (null, 'Nombre Prueba', 25, 'Masculino', 1.80, 80.0, 'O+')");
        //db.execSQL("INSERT INTO " + TABLA_EVENTO + " VALUES (null, 'Evento Prueba', 'Descripción Evento Prueba', 0, 1)");
        //db.execSQL("INSERT INTO " + TABLA_DIA + " VALUES (null, '2026-01-01', 'Feliz', 1)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //¡Este código SOLO se ejecuta si aumenta la version de la BD!

        //Activamos las Foreign Keys:
        db.execSQL("PRAGMA foreign_keys = ON");
    }

    //METODO PARA COMPROBAR SI EXISTE UN REGISTRO (USUARIO) EN LA TABLA USUARIO
    public boolean existeUsuario() {
        SQLiteDatabase db = this.getReadableDatabase();

        //Consultamos el número de registros de la tabla Usuario
        Cursor cur = db.rawQuery("SELECT COUNT(*) FROM " + TABLA_USUARIO, null);
        cur.moveToFirst();

        //Obtenemos el número de registros
        int registros = cur.getInt(0);

        //Cerramos el cursor y la base de datos
        cur.close();
        db.close();

        //Devolvemos true si hay al menos un registro (un Usuario)
        return registros > 0;
    }

    // -------------------------- METODOS PARA INSERTAR DATOS ------------------------------------

    //METODO PARA INSERTAR LOS DATOS DEL USUARIO EN LA TABLA_USUARIO:
    public boolean insertarUsuario(String nombre, int edad, String sexo, double altura, double peso, String tipoSangre) {
        //Abrimos la BD en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un objeto ContentValues para almacenar los valores a insertar
        ContentValues datos = new ContentValues(); //Esto evita la inyección de SQL!
        datos.put(USUARIO_NOMBRE, nombre);
        datos.put(USUARIO_EDAD, edad);
        datos.put(USUARIO_SEXO, sexo);
        datos.put(USUARIO_ALTURA, altura);
        datos.put(USUARIO_PESO, peso);
        datos.put(USUARIO_SANGRE, tipoSangre);

        //Ejecutamos la inserción
        //Devuelve el ID del registro insertado. Si devuelve -1, hubo un error
        long resultado = db.insert(TABLA_USUARIO, null, datos);

        //Cerramos la BD
        db.close();

        //Devolvemos true si se ha insertado correctamente
        return resultado != -1;
    }

    //METODO PARA ACTUALIZAR LOS DATOS DEL USUARIO EN LA TABLA_USUARIO:
    public boolean actualizarUsuario(String nombre, int edad, String sexo, double altura, double peso, String tipoSangre) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues datos = new ContentValues();
        datos.put(USUARIO_NOMBRE, nombre);
        datos.put(USUARIO_EDAD, edad);
        datos.put(USUARIO_SEXO, sexo);
        datos.put(USUARIO_ALTURA, altura);
        datos.put(USUARIO_PESO, peso);
        datos.put(USUARIO_SANGRE, tipoSangre);

        // Actualizamos todos los registros (normalmente solo hay uno)
        int resultado = db.update(TABLA_USUARIO, datos, null, null);
        db.close();
        return resultado > 0;
    }

    //METODO PARA INSERTAR UN EVENTO EN LA TABLA_EVENTO:
    public boolean insertarEvento(String nombre, String descripcion, int seRepite, int idUsuario) {
        //Abrimos la BD en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un objeto ContentValues para almacenar los valores a insertar
        ContentValues datos = new ContentValues(); //Esto evita la inyección de SQL!
        datos.put(EVENTO_NOMBRE, nombre);
        datos.put(EVENTO_DESCRIPCION, descripcion);
        datos.put(EVENTO_REPITE, seRepite);
        datos.put(EVENTO_ID_USUARIO, idUsuario);

        //Ejecutamos la inserción
        //Devuelve el ID del registro insertado. Si devuelve -1, hubo un error
        long resultado = db.insert(TABLA_EVENTO, null, datos);

        //Cerramos la BD
        db.close();

        //Devolvemos true si se ha insertado correctamente
        return resultado != -1;
    }

    //METODO PARA INSERTAR UN DIA EN LA TABLA_DIA:
    public boolean insertarDia(String fecha, String emocion, int idEvento) {
        //Abrimos la BD en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un objeto ContentValues para almacenar los valores a insertar
        ContentValues datos = new ContentValues(); //Esto evita la inyección de SQL!
        datos.put(DIA_FECHA, fecha);
        datos.put(DIA_EMOCION, emocion);
        datos.put(DIA_ID_EVENTO, idEvento);

        //Ejecutamos la inserción
        //Devuelve el ID del registro insertado. Si devuelve -1, hubo un error
        long resultado = db.insert(TABLA_DIA, null, datos);

        //Cerramos la BD
        db.close();

        //Devolvemos true si se ha insertado correctamente
        return resultado != -1;
    }

    //METODO PARA INSERTAR UNA ACTIVIDAD EN LA TABLA_ACTIVIDAD:
    public boolean insertarActividad(String nombre, String tiempo, String fecha, int idUsuario) {
        //Abrimos la BD en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un objeto ContentValues para almacenar los valores a insertar
        ContentValues datos = new ContentValues(); //Esto evita la inyección de SQL!
        datos.put(ACTIVIDAD_NOMBRE, nombre);
        datos.put(ACTIVIDAD_TIEMPO, tiempo);
        datos.put(ACTIVIDAD_FECHA, fecha);
        datos.put(ACTIVIDAD_ID_USUARIO, idUsuario);

        //Ejecutamos la inserción
        //Devuelve el ID del registro insertado. Si devuelve -1, hubo un error
        long resultado = db.insert(TABLA_ACTIVIDAD, null, datos);

        //Cerramos la BD
        db.close();

        //Devolvemos true si se ha insertado correctamente
        return resultado != -1;
    }


    // ------------------------ METODOS PARA OBTENER DATOS ------------------------------------

    //METODO PARA OBTENER LOS DATOS DEL USUARIO DE LA TABLA_USUARIO:
    public Cursor obtenerUsuario() {
        //Abrimos la BD en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Guardamos la consulta en un cursor y lo devolvemos
        Cursor curUsuario = db.rawQuery("SELECT * FROM " + TABLA_USUARIO, null);
        return curUsuario;
    }


    /*
      CÓDIGO PENDIENTE DE INTEGRAR EN EL DASHBOARD!

      //Inicializar TextViews o similar:
      TextView tvNombre = findViewById(R.id.tvNombre);
      TextView tvPeso = findViewById(R.id.tvPeso);
      TextView tvAltura = findViewById(R.id.tvAltura);
      TextView tvIMC = findViewById(R.id.tvIMC);

      //Obtener y mostrar los datos del usuario:
      Cursor curUsuario = gbd.obtenerUsuario();
      if (curUsuario !=null && curUsuario.moveToFirst()) {
          String nombre = curUsuario.getString(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_NOMBRE));
          double peso = curUsuario.getDouble(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_PESO));
          double altura = curUsuario.getDouble(curUsuario.getColumnIndexOrThrow(GestorBD.USUARIO_ALTURA));
          double alturaEnMetros = altura / 100;
          double imc = peso / (alturaEnMetros * alturaEnMetros);

          //Actualizar TextViews o similar con los datos del usuario:
          tvNombre.setText("Nombre: " + nombre);
          tvPeso.setText("Peso: " + peso + " kg");
          tvAltura.setText("Altura: " + altura + " cm");
          tvIMC.setText("IMC: " + String.format("%.2f", imc));
      } else {
          //Si no hay datos poner texto por defecto
          tvNombre.setText("Nombre: Sin datos");
          tvPeso.setText("Peso: Sin datos");
          tvAltura.setText("Altura: Sin datos");
          tvIMC.setText("IMC: Sin datos");
      }
    //Cerramos Cursor solo si NO es null para evitar NullPointerException
    if (curUsuario !=null) {
        curUsuario.close();
    }
      }
   */



    //METODO PARA OBTENER LOS DATOS DEL EVENTO DE LA TABLA_EVENTO:
    public Cursor obtenerEvento() {
        //Abrimos la BD en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Guardamos la consulta en un cursor y lo devolvemos
        Cursor curEvento = db.rawQuery("SELECT * FROM " + TABLA_EVENTO, null);
        return curEvento;
    }

    /*
    //CÓDIGO PARA INTEGRAR EN LA ACTIVITY!

    //Inicializar TextViews o similares para mostrar los datos:
    TextView tvEventoNombre = findViewById(R.id.tvEventoNombre);
    TextView tvEventoDescripcion = findViewById(R.id.tvEventoDescripcion);
    TextView tvEventoRepite = findViewById(R.id.tvEventoRepite);
    TextView tvEventoIdUsuario = findViewById(R.id.tvEventoIdUsuario);

    //Obtener y mostrar los datos del evento
    GestorBD gbd = new GestorBD(this);
    Cursor curEvento = gbd.obtenerEvento();
    if(curEvento !=null && curEvento.moveToFirst()) {
        String nombre = curEvento.getString(curEvento.getColumnIndexOrThrow(GestorBD.EVENTO_NOMBRE));
        String descripcion = curEvento.getString(curEvento.getColumnIndexOrThrow(GestorBD.EVENTO_DESCRIPCION));
        int repite = curEvento.getInt(curEvento.getColumnIndexOrThrow(GestorBD.EVENTO_REPITE));
        int idUsuario = curEvento.getInt(curEvento.getColumnIndexOrThrow(GestorBD.EVENTO_ID_USUARIO));

        //Mostrar los datos en TextView o similar
        tvEventoNombre.setText("Evento: " + nombre);
        tvEventoDescripcion.setText("Descripción: " + descripcion);
        tvEventoRepite.setText("Repite: " + (repite == 1 ? "Sí" : "No")); //Si se repite, se muestra "Sí", si no, "No"
        tvEventoIdUsuario.setText("ID Usuario: " + idUsuario);
    } else {
        //Si no hay datos poner texto por defecto
        tvEventoNombre.setText("Evento: Sin datos");
        tvEventoDescripcion.setText("Descripción: Sin datos");
        tvEventoRepite.setText("Repite: Sin datos");
    }
    //Cerramos Cursor solo si NO es null para evitar NullPointerException
    if (curEvento !=null) {
        curEvento.close();
    }
     */


    //METODO PARA OBTENER LOS DATOS DEL DIA DE LA TABLA_DIA:
    public Cursor obtenerDia() {
        //Abrimos la BD en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Guardamos la consulta en un cursor y lo devolvemos
        Cursor curDia = db.rawQuery("SELECT * FROM " + TABLA_DIA, null);
        return curDia;
    }

/*

    //CODIGO PARA INTEGRAR EN LA ACTIVITY

    //Inicializar TextViews o similares para mostrar los datos:
    TextView tvDiaFecha = findViewById(R.id.tvDiaFecha);
    TextView tvDiaEmocion = findViewById(R.id.tvDiaEmocion);

    //Obtener y mostrar los datos del dia
    GestorBD gbd = new GestorBD(this);
    Cursor curDia = gbd.obtenerDia();
    if (curDia !=null && curDia.moveToFirst()) {
        String fecha = curDia.getString(curDia.getColumnIndexOrThrow(GestorBD.DIA_FECHA));
        String emocion = curDia.getString(curDia.getColumnIndexOrThrow(GestorBD.DIA_EMOCION));

        //Mostrar los datos en TextView o similar
        tvDiaFecha.setText("Fecha: " + fecha);
        tvDiaEmocion.setText("Emocion: " + emocion);
    } else {
        //Si no hay datos poner texto por defecto
        tvDiaFecha.setText("Fecha: Sin datos");
        tvDiaEmocion.setText("Emoción: Sin datos");
    }
    //Cerramos Cursor solo si NO es null para evitar NullPointerException
    if (curDia !=null) {
        curDia.close();
    }
*/


    //METODO PARA OBTENER LOS DATOS DE UNA ACTIVIDAD DE LA TABLA_ACTIVIDAD:
    public Cursor obtenerActividad() {
        //Abrimos la BD en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();

        //Guardamos la consulta en un cursor y lo devolvemos
        Cursor curActividad = db.rawQuery("SELECT * FROM " + TABLA_ACTIVIDAD, null);
        return curActividad;
    }

/*

    //CODIGO PARA INTEGRAR EN LA ACTIVITY

    //Inicializar TextViews o similares para mostrar los datos:
    TextView tvActividadNombre = findViewById(R.id.tvActividadNombre);
    TextView tvActividadTiempo = findViewById(R.id.tvActividadTiempo);
    TextView tvActividadFecha = findViewById(R.id.tvActividadFecha);

    //Obtener y mostrar los datos de la actividad
    GestorBD gbd = new GestorBD(this);
    Cursor curActividad = gbd.obtenerActividad();

    if (curActividad != null && curActividad.moveToFirst()) {
        String nombre = curActividad.getString(curActividad.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_NOMBRE));
        String tiempo = curActividad.getString(curActividad.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_TIEMPO));
        String fecha = curActividad.getString(curActividad.getColumnIndexOrThrow(GestorBD.ACTIVIDAD_FECHA));

        //Mostrar los datos en TextView o similar
        tvActividadNombre.setText("Actividad: " + nombre);
        tvActividadTiempo.setText("Tiempo: " + tiempo);
        tvActividadFecha.setText("Fecha: " + fecha);
    } else {
        //Si no hay datos poner texto por defecto
        tvActividadNombre.setText("Actividad: Sin datos");
        tvActividadTiempo.setText("Tiempo: Sin datos");
        tvActividadFecha.setText("Fecha: Sin datos");
        tvActividadIdUsuario.setText("ID Usuario: Sin datos");
    }
    //Cerramos Cursor solo si NO es null para evitar NullPointerException
    if (curActividad != null) {
        curActividad.close();
    }
*/






    // ------------------ METODOS DE PRUEBA PARA MOSTRAR DATOS POR CONSOLA --------------------

    //METODO DE PRUEBA PARA MOSTRAR POR CONSOLA LOS DATOS DEL USUARIO:
    public void consultarUsuario() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLA_USUARIO, null);

        if (cur.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros y mostramos los datos por consola
            while (!cur.isAfterLast()) {
                Log.d("GestorBD", "CONSULTA USUARIO DE PRUEBA: \n-ID: " + cur.getInt(0)
                        + "\n-Nombre: " + cur.getString(1)
                        + "\n-Edad: " + cur.getInt(2)
                        + "\n-Sexo: " + cur.getString(3)
                        + "\n-Altura: " + cur.getFloat(4)
                        + "\n-Peso: " + cur.getFloat(5)
                        + "\n-Tipo de Sangre: " + cur.getString(6));

                cur.moveToNext();
            }
        }
        cur.close();
        db.close();
    }

    //METODO DE PRUEBA PARA MOSTRAR POR CONSOLA LOS DATOS DE UN EVENTO:
    public void consultarEvento() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLA_EVENTO, null);

        if (cur.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros y mostramos los datos por consola
            while (!cur.isAfterLast()) {
                Log.d("GestorBD", "CONSULTA EVENTO DE PRUEBA: \n-ID: " + cur.getInt(0)
                        + "\n-Nombre: " + cur.getString(1)
                        + "\n-Descripcion: " + cur.getString(2)
                        + "\n-Se Repite: " + cur.getInt(3)
                        + "\n-ID Usuario: " + cur.getInt(4));

                cur.moveToNext();
            }
        }
        cur.close();
        db.close();
    }

    //METODO DE PRUEBA PARA MOSTRAR POR CONSOLA LOS DATOS DE UN DIA:
    public void consultarDia() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + TABLA_DIA, null);

        if (cur.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros y mostramos los datos por consola
            while (!cur.isAfterLast()) {
                Log.d("GestorBD", "CONSULTA DIA DE PRUEBA: \n-ID: " + cur.getInt(0)
                        + "\n-Fecha: " + cur.getString(1)
                        + "\n-Emocion: " + cur.getString(2)
                        + "\n-ID Evento: " + cur.getInt(3));

                cur.moveToNext();
            }
        }
        cur.close();
        db.close();
    }
    //Nandus
    //METODO PARA REINICIAR EL TIEMPO DE LAS ACTIVIDADES CADA DIA:
    public void reiniciarSegundosActividades() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Esto pone a 0 la columna tiempo para todas las filas de la tabla
        db.execSQL("UPDATE " + TABLA_ACTIVIDAD + " SET " + ACTIVIDAD_TIEMPO + " = 0");
    }
}
