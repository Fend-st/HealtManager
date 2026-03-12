package com.example.healthmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

//SISTEMA GESTOR DE LA BD DE LA APP HEALTH MANAGER
public class GestorBD extends SQLiteOpenHelper {

    //Variables de la Base de Datos:
    private static final String NOMBRE_BD = "HealthManagerDB.db";
    private static final int VERSION = 1;


    //Variables tabla Usuario y sus columnas:
    public static final String TABLA_USUARIO = "usuario";
    public static final String USUARIO_ID = "id";
    public static final String USUARIO_NOMBRE = "nombre";
    public static final String USUARIO_EDAD = "edad";
    public static final String USUARIO_SEXO = "sexo";
    public static final String USUARIO_ALTURA = "altura";
    public static final String USUARIO_PESO = "peso";
    public static final String USUARIO_SANGRE = "tipo_sangre";


    //Variables tabla Evento y sus columnas:
    public static final String TABLA_EVENTO = "evento";
    public static final String EVENTO_ID = "id";
    public static final String EVENTO_NOMBRE = "nombre";
    public static final String EVENTO_DESCRIPCION = "descripcion";
    public static final String EVENTO_REPITE = "se_repite";
    public static final String EVENTO_ID_USUARIO = "id_usuario";


    //Variables tabla Dia y sus columnas:
    public static final String TABLA_DIA = "dia";
    public static final String DIA_ID = "id";
    public static final String DIA_FECHA = "fecha";
    public static final String DIA_EMOCION = "emocion";
    public static final String DIA_ID_EVENTO = "id_evento";

    //Creacion de la Base de Datos
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


        //Introduccion de valores de prueba:
        db.execSQL("INSERT INTO " + TABLA_USUARIO + " VALUES (null, 'Nombre Prueba', 25, 'Masculino', 1.80, 80.0, 'O+')");
        db.execSQL("INSERT INTO " + TABLA_EVENTO + " VALUES (null, 'Evento Prueba', 'Descripcion Evento Prueba', 0, 1)");
        db.execSQL("INSERT INTO " + TABLA_DIA + " VALUES (null, '2026-01-01', 'Feliz', 1)");




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //Solo se ejecuta si aumenta la version de la BD!
    }
}
