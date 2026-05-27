package com.example.healthmanager;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import FernandoDiaz.crono.Cronometro;

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
    @Test
    public void testBotonActividad() {
        // Probamos el estado inicial o nulo
        assertEquals("ACTIVIDAD ACTUAL: NINGUNA", Cronometro.obtenerTextoActividad(0));

        // Probamos las actividades válidas
        assertEquals("ACTIVIDAD ACTUAL: 1", Cronometro.obtenerTextoActividad(1));
        assertEquals("ACTIVIDAD ACTUAL: 2", Cronometro.obtenerTextoActividad(2));
        assertEquals("ACTIVIDAD ACTUAL: 3", Cronometro.obtenerTextoActividad(3));

        // Prueba extra: ¿Qué pasa si llega un número inesperado?
        assertEquals("ACTIVIDAD ACTUAL: NINGUNA", Cronometro.obtenerTextoActividad(99));
    }
}