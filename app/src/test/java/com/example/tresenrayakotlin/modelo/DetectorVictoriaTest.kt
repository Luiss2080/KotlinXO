package com.example.tresenrayakotlin.modelo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DetectorVictoriaTest {

    private val detector = DetectorVictoria()

    private val lineas = listOf(
        listOf(0 to 0, 0 to 1, 0 to 2), listOf(1 to 0, 1 to 1, 1 to 2), listOf(2 to 0, 2 to 1, 2 to 2),
        listOf(0 to 0, 1 to 0, 2 to 0), listOf(0 to 1, 1 to 1, 2 to 1), listOf(0 to 2, 1 to 2, 2 to 2),
        listOf(0 to 0, 1 to 1, 2 to 2), listOf(0 to 2, 1 to 1, 2 to 0)
    )

    @Test
    fun tableroVacioNoTieneGanadorNiEmpate() {
        val t = Tablero()
        assertNull(detector.verificarGanador(t))
        assertFalse(detector.esEmpate(t))
    }

    @Test
    fun detectaLasOchoLineasParaXyO() {
        for (simbolo in listOf("X", "O")) {
            for (linea in lineas) {
                val t = Tablero()
                linea.forEach { (f, c) -> t.obtenerCasilla(f, c).asignarValor(simbolo) }
                assertEquals("$simbolo en $linea", simbolo, detector.verificarGanador(t))
            }
        }
    }

    @Test
    fun dosEnRayaNoGana() {
        for (linea in lineas) {
            val t = Tablero()
            linea.take(2).forEach { (f, c) -> t.obtenerCasilla(f, c).asignarValor("X") }
            assertNull(detector.verificarGanador(t))
        }
    }

    @Test
    fun lineaMezcladaNoGana() {
        val t = Tablero()
        t.obtenerCasilla(0, 0).asignarValor("X")
        t.obtenerCasilla(0, 1).asignarValor("O")
        t.obtenerCasilla(0, 2).asignarValor("X")
        assertNull(detector.verificarGanador(t))
    }

    @Test
    fun tableroLlenoSinLineaEsEmpate() {
        val t = tableroDe("XOX", "XOO", "OXX")
        assertNull(detector.verificarGanador(t))
        assertTrue(detector.esEmpate(t))
    }

    @Test
    fun tableroLlenoConLineaNoEsEmpate() {
        val t = tableroDe("XOX", "OXO", "OXX")
        assertEquals("X", detector.verificarGanador(t))
        assertFalse(detector.esEmpate(t))
    }

    private fun tableroDe(vararg filas: String): Tablero {
        val t = Tablero()
        filas.forEachIndexed { f, fila ->
            fila.forEachIndexed { c, ch -> if (ch != '-') t.obtenerCasilla(f, c).asignarValor(ch.toString()) }
        }
        return t
    }
}
