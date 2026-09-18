package com.example.tresenrayakotlin.modelo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Serialización/restauración del estado, usada al recrearse la actividad. */
class JuegoEstadoTest {

    @Test
    fun tableroVacioSeSerializaConGuiones() {
        assertEquals("---------", Juego().serializarTablero())
    }

    @Test
    fun idaYVueltaConservaTableroTurnoYActividad() {
        val origen = Juego()
        origen.realizarMovimiento(0, 0)
        origen.realizarMovimiento(1, 1)

        val copia = Juego()
        assertTrue(copia.restaurarEstado(origen.serializarTablero(), origen.obtenerJugadorActual().simbolo))
        assertEquals("X---O----", copia.serializarTablero())
        assertEquals("X", copia.obtenerJugadorActual().simbolo)
        assertTrue(copia.estaJuegoActivo())
        assertFalse(copia.realizarMovimiento(1, 1))
        assertTrue(copia.realizarMovimiento(2, 2))
    }

    @Test
    fun restaurarPartidaGanadaLaDejaTerminada() {
        val j = Juego()
        assertTrue(j.restaurarEstado("XXXOO----", "X"))
        assertTrue(j.hayGanador())
        assertEquals("X", j.obtenerGanador()?.simbolo)
        assertFalse(j.estaJuegoActivo())
        assertFalse(j.realizarMovimiento(2, 2))
    }

    @Test
    fun restaurarTableroLlenoSinGanadorEsEmpate() {
        val j = Juego()
        assertTrue(j.restaurarEstado("XOXXOOOXX", "O"))
        assertTrue(j.esEmpate())
        assertNull(j.obtenerGanador())
    }

    @Test
    fun datosInvalidosNoModificanElEstado() {
        val j = Juego()
        j.realizarMovimiento(1, 1)
        assertFalse(j.restaurarEstado(null, "X"))
        assertFalse(j.restaurarEstado("XO", "X"))
        assertFalse(j.restaurarEstado("XO?------", "X"))
        assertFalse(j.restaurarEstado("---------", "Z"))
        assertFalse(j.restaurarEstado("---------", null))
        assertEquals("----X----", j.serializarTablero())
    }
}
