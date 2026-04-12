package com.example.healthmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fend.crono.Cronometro;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CronometroTest {//Test en la clase cronometro
    @Test
    public void testFormatoTiempo() {//Método para probar el formateo del tiempo
        // Prueba 1: 0 segundos
        assertEquals("00:00:00", Cronometro.formatearTiempo(0));

        // Prueba 2: 65 segundos (1 minuto y 5 segundos)
        assertEquals("00:01:05", Cronometro.formatearTiempo(65));

        // Prueba 3: 3661 segundos (1 hora, 1 minuto y 1 segundo)
        assertEquals("01:01:01", Cronometro.formatearTiempo(3661));
    }
}