package com.example.tresenrayakotlin.modelo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class TableroYValidadorTest {

    @Test
    fun tableroNuevoTiene9CasillasVaciasYNoEstaLleno() {
        val t = Tablero()
        assertEquals(9, t.obtenerTodasLasCasillas().size)
        assertTrue(t.obtenerTodasLasCasillas().all { it.estaVacia() })
        assertFalse(t.estaLleno())
    }

    @Test
    fun estaLlenoSoloConTodasOcupadas() {
        val t = Tablero()
        t.obtenerTodasLasCasillas().take(8).forEach { it.asignarValor("X") }
        assertFalse(t.estaLleno())
        t.obtenerCasilla(2, 2).asignarValor("O")
        assertTrue(t.estaLleno())
    }

    @Test
    fun limpiarVaciaElTablero() {
        val t = Tablero()
        t.obtenerCasilla(1, 1).asignarValor("X")
        t.limpiar()
        assertTrue(t.obtenerCasilla(1, 1).estaVacia())
    }

    @Test
    fun posicionValidaYObtenerCasillaFueraDeRango() {
        val t = Tablero()
        assertTrue(t.posicionValida(0, 0))
        assertTrue(t.posicionValida(2, 2))
        assertFalse(t.posicionValida(3, 0))
        assertFalse(t.posicionValida(0, -1))
        try {
            t.obtenerCasilla(3, 3)
            fail("Debe lanzar IndexOutOfBoundsException")
        } catch (e: IndexOutOfBoundsException) {
            // esperado
        }
    }

    @Test
    fun filasColumnasYDiagonalesDevuelvenLasCasillasCorrectas() {
        val t = Tablero()
        assertEquals(listOf(1 to 0, 1 to 1, 1 to 2), t.obtenerFila(1)!!.map { it.fila to it.columna })
        assertEquals(listOf(0 to 2, 1 to 2, 2 to 2), t.obtenerColumna(2)!!.map { it.fila to it.columna })
        assertEquals(listOf(0 to 0, 1 to 1, 2 to 2), t.obtenerDiagonalPrincipal().map { it.fila to it.columna })
        assertEquals(listOf(0 to 2, 1 to 1, 2 to 0), t.obtenerDiagonalSecundaria().map { it.fila to it.columna })
        assertNull(t.obtenerFila(3))
        assertNull(t.obtenerColumna(-1))
    }

    @Test
    fun validadorRechazaOcupadasYFueraDeRango() {
        val v = ValidadorMovimiento()
        val t = Tablero()
        assertTrue(v.esMovimientoValido(t, 0, 0))
        t.obtenerCasilla(0, 0).asignarValor("X")
        assertFalse(v.esMovimientoValido(t, 0, 0))
        assertFalse(v.esMovimientoValido(t, 3, 0))
        assertNull(v.obtenerMotivoMovimientoInvalido(t, 1, 1))
    }

    @Test
    fun jugadoresPredefinidos() {
        assertTrue(Jugador.crearJugadorX().esJugadorX())
        assertTrue(Jugador.crearJugadorO().esJugadorO())
        assertFalse(Jugador.crearJugadorX().esJugadorO())
    }
}
