package com.example.tresenrayakotlin.modelo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class JuegoTest {

    private fun jugar(j: Juego, vararg movs: Pair<Int, Int>) {
        movs.forEach { (f, c) -> assertTrue("movimiento ($f,$c)", j.realizarMovimiento(f, c)) }
    }

    @Test
    fun empiezaXYEstaActivo() {
        val j = Juego()
        assertEquals("X", j.obtenerJugadorActual().simbolo)
        assertTrue(j.estaJuegoActivo())
        assertFalse(j.hayGanador())
        assertFalse(j.esEmpate())
    }

    @Test
    fun losTurnosAlternan() {
        val j = Juego()
        jugar(j, 0 to 0)
        assertEquals("O", j.obtenerJugadorActual().simbolo)
        jugar(j, 1 to 1)
        assertEquals("X", j.obtenerJugadorActual().simbolo)
    }

    @Test
    fun casillaOcupadaSeRechazaSinCambiarTurnoNiSobrescribir() {
        val j = Juego()
        jugar(j, 1 to 1)
        assertFalse(j.realizarMovimiento(1, 1))
        assertEquals("X", j.obtenerSimboloEnCasilla(1, 1))
        assertEquals("O", j.obtenerJugadorActual().simbolo)
        assertEquals("La casilla ya está ocupada", j.obtenerMotivoMovimientoInvalido(1, 1))
    }

    @Test
    fun fueraDeRangoSeRechazaSinExcepcion() {
        val j = Juego()
        assertFalse(j.realizarMovimiento(-1, 0))
        assertFalse(j.realizarMovimiento(0, 3))
        assertFalse(j.realizarMovimiento(3, 3))
        assertEquals("La posición está fuera del tablero", j.obtenerMotivoMovimientoInvalido(5, 5))
        assertTrue(j.estaJuegoActivo())
    }

    @Test
    fun victoriaDeXTerminaElJuegoYBloqueaMovimientos() {
        val j = Juego()
        jugar(j, 0 to 0, 1 to 0, 0 to 1, 1 to 1, 0 to 2)
        assertTrue(j.hayGanador())
        assertEquals("X", j.obtenerGanador()?.simbolo)
        assertFalse(j.estaJuegoActivo())
        assertFalse(j.esEmpate())
        assertFalse(j.realizarMovimiento(2, 2))
        assertEquals("", j.obtenerSimboloEnCasilla(2, 2))
        assertEquals("El juego ya ha terminado", j.obtenerMotivoMovimientoInvalido(2, 2))
    }

    @Test
    fun victoriaDeOSeAtribuyeAO() {
        val j = Juego()
        jugar(j, 0 to 0, 1 to 0, 0 to 1, 1 to 1, 2 to 2, 1 to 2)
        assertEquals("O", j.obtenerGanador()?.simbolo)
    }

    @Test
    fun empateSoloConTableroLlenoSinGanador() {
        val j = Juego()
        // X O X / X O O / O X X
        jugar(j, 0 to 0, 0 to 1, 0 to 2, 1 to 1, 1 to 0, 1 to 2, 2 to 1, 2 to 0)
        assertTrue(j.estaJuegoActivo())
        assertFalse(j.esEmpate())
        jugar(j, 2 to 2)
        assertFalse(j.estaJuegoActivo())
        assertTrue(j.esEmpate())
        assertFalse(j.hayGanador())
        assertNull(j.obtenerGanador())
    }

    @Test
    fun victoriaEnLaNovenaJugadaEsVictoriaYNoEmpate() {
        val j = Juego()
        // X O X / O X O / O X X -> X gana con la diagonal al llenar el tablero
        jugar(j, 0 to 0, 0 to 1, 0 to 2, 1 to 0, 1 to 1, 1 to 2, 2 to 1, 2 to 0, 2 to 2)
        assertTrue(j.hayGanador())
        assertFalse(j.esEmpate())
        assertNotNull(j.obtenerGanador())
    }

    @Test
    fun reiniciarRestauraTodo() {
        val j = Juego()
        jugar(j, 0 to 0, 1 to 0, 0 to 1, 1 to 1, 0 to 2)
        j.reiniciarJuego()
        assertTrue(j.estaJuegoActivo())
        assertFalse(j.hayGanador())
        assertEquals("X", j.obtenerJugadorActual().simbolo)
        assertTrue(j.obtenerTablero().obtenerTodasLasCasillas().all { it.estaVacia() })
        assertTrue(j.realizarMovimiento(0, 0))
    }
}
