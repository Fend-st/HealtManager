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

    //Creación de la Base de Datos
    public GestorBD(@Nullable Context context) {
        super(context, NOMBRE_BD, null, VERSION);

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


        //Introducción de valores de prueba:
        db.execSQL("INSERT INTO " + TABLA_USUARIO + " VALUES (null, 'Nombre Prueba', 25, 'Masculino', 1.80, 80.0, 'O+')");
        db.execSQL("INSERT INTO " + TABLA_EVENTO + " VALUES (null, 'Evento Prueba', 'Descripcion Evento Prueba', 0, 1)");
        db.execSQL("INSERT INTO " + TABLA_DIA + " VALUES (null, '2026-01-01', 'Feliz', 1)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //¡Este código SOLO se ejecuta si aumenta la version de la BD!

        //Activamos las Foreign Keys:
        db.execSQL("PRAGMA foreign_keys = ON");
    }


    //MÉTODOS DE PRUEBA PARA MOSTRAR POR CONSOLA LOS DATOS DE PRUEBA:
    public void consultarUsuario(){
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
    public void consultarEvento(){
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
    public void consultarDia(){
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

    //METODO PARA INSERTAR LOS DATOS DEL USUARIO EN LA BD:
    public boolean insertarUsuario(String nombre, int edad, String sexo, double altura, double peso, String tipoSangre) {
        //Abrimos la BD en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un objeto ContentValues para almacenar los valores a insertar
        //Esto evita la inyección de SQL!
        ContentValues datos = new ContentValues();
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

    //METODO PARA INSERTAR UN EVENTO EN LA BD:
    public boolean insertarEvento(String nombre, String descripcion, int seRepite, int idUsuario) {
        //Abrimos la BD en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();

        //Creamos un objeto ContentValues para almacenar los valores a insertar
        //Esto evita la inyección de SQL!
        ContentValues datos = new ContentValues();
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




}
