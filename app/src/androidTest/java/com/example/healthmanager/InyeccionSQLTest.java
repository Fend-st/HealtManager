package com.example.healthmanager;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

    @RunWith(AndroidJUnit4.class)
    public class InyeccionSQLTest {

        private GestorBD dbHelper;

        @Before
        public void setUp() {
            Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
            dbHelper = new GestorBD(context);
        }

        @Test
        public void testInyeccionSQL() {
            // Payload típico de inyección SQL
            String payload = "'; DROP TABLE usuario; --";

            // Intentar insertar un usuario con datos maliciosos
            boolean resultado = dbHelper.insertarUsuario(
                    payload,
                    25,
                    "M",
                    1.80,
                    75.0,
                    "A+"
            );

            // Debe devolver true: la inserción no debe fallar
            assertTrue(resultado);

            // Comprobar que la tabla sigue existiendo
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name='usuario'",
                    null
            );

            assertTrue(cursor.moveToFirst());

            cursor.close();
            db.close();
        }
    }

