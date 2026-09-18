package com.example.tresenrayakotlin.modelo

/**
 * Gestiona la lógica principal del juego de tres en raya
 */
class Juego {

    private val tablero = Tablero()
    private val validador = ValidadorMovimiento()
    private val detectorVictoria = DetectorVictoria()

    private var jugadorActual: Jugador
    private val jugador1 = Jugador.crearJugadorX()
    private val jugador2 = Jugador.crearJugadorO()

    private var juegoActivo = true
    private var ganador: Jugador? = null

    init {
        jugadorActual = jugador1 // El jugador X siempre empieza
    }

    /**
     * Realiza un movimiento en el tablero
     * @param fila la fila donde hacer el movimiento
     * @param columna la columna donde hacer el movimiento
     * @return true si el movimiento fue exitoso
     */
    fun realizarMovimiento(fila: Int, columna: Int): Boolean {
        if (!juegoActivo) return false

        if (!validador.esMovimientoValido(tablero, fila, columna)) {
            return false
        }

        // Realizar el movimiento
        val casilla = tablero.obtenerCasilla(fila, columna)
        casilla?.asignarValor(jugadorActual.simbolo)

        // Verificar si hay ganador
        val simboloGanador = detectorVictoria.verificarGanador(tablero)
        if (simboloGanador != null) {
            ganador = if (simboloGanador == jugador1.simbolo) jugador1 else jugador2
            juegoActivo = false
        } else if (detectorVictoria.esEmpate(tablero)) {
            juegoActivo = false
        } else {
            // Cambiar de jugador
            cambiarTurno()
        }

        return true
    }

    /**
     * Cambia el turno al siguiente jugador
     */
    private fun cambiarTurno() {
        jugadorActual = if (jugadorActual == jugador1) jugador2 else jugador1
    }

    /**
     * Obtiene el jugador que debe jugar en el turno actual
     * @return el jugador actual
     */
    fun obtenerJugadorActual(): Jugador {
        return jugadorActual
    }

    /**
     * Obtiene el estado actual del tablero
     * @return el tablero del juego
     */
    fun obtenerTablero(): Tablero {
        return tablero
    }

    /**
     * Verifica si el juego está activo
     * @return true si el juego continúa
     */
    fun estaJuegoActivo(): Boolean {
        return juegoActivo
    }

    /**
     * Obtiene el ganador del juego
     * @return el jugador ganador o null si no hay ganador aún
     */
    fun obtenerGanador(): Jugador? {
        return ganador
    }

    /**
     * Verifica si el juego terminó en empate
     * @return true si es empate
     */
    fun esEmpate(): Boolean {
        return !juegoActivo && ganador == null
    }

    /**
     * Reinicia el juego para comenzar una nueva partida
     */
    fun reiniciarJuego() {
        tablero.limpiar()
        jugadorActual = jugador1
        juegoActivo = true
        ganador = null
    }

    /**
     * Obtiene el motivo por el cual un movimiento no es válido
     * @param fila la fila del movimiento
     * @param columna la columna del movimiento
     * @return el mensaje de error o null si el movimiento es válido
     */
    fun obtenerMotivoMovimientoInvalido(fila: Int, columna: Int): String? {
        if (!juegoActivo) {
            return "El juego ya ha terminado"
        }

        return validador.obtenerMotivoMovimientoInvalido(tablero, fila, columna)
    }

    /**
     * Obtiene el primer jugador (X)
     */
    fun obtenerJugador1(): Jugador {
        return jugador1
    }

    /**
     * Obtiene el segundo jugador (O)
     */
    fun obtenerJugador2(): Jugador {
        return jugador2
    }

    /**
     * Obtiene el símbolo en una casilla específica
     * @param fila la fila de la casilla
     * @param columna la columna de la casilla
     * @return el símbolo en la casilla o cadena vacía si está vacía
     */
    fun obtenerSimboloEnCasilla(fila: Int, columna: Int): String {
        val casilla = tablero.obtenerCasilla(fila, columna)
        return casilla.valor
    }

    /**
     * Verifica si una casilla específica está ocupada
     * @param fila la fila de la casilla
     * @param columna la columna de la casilla
     * @return true si la casilla está ocupada
     */
    fun esCasillaOcupada(fila: Int, columna: Int): Boolean {
        return !tablero.obtenerCasilla(fila, columna).estaVacia()
    }

    /**
     * Verifica si hay un ganador en el juego
     * @return true si hay un ganador
     */
    fun hayGanador(): Boolean {
        return ganador != null
    }

    /**
     * Serializa el tablero en 9 caracteres (fila a fila): "X", "O" o "-" si la casilla está vacía.
     */
    fun serializarTablero(): String = buildString {
        for (fila in 0 until Tablero.TAMANO) {
            for (columna in 0 until Tablero.TAMANO) {
                val valor = tablero.obtenerCasilla(fila, columna).valor
                append(if (valor.isEmpty()) '-' else valor[0])
            }
        }
    }

    /**
     * Restaura el estado desde un tablero serializado y el símbolo del jugador con el turno.
     * Recalcula ganador y estado activo. Si los datos no son válidos no modifica nada.
     * @return true si se restauró el estado
     */
    fun restaurarEstado(tableroSerializado: String?, simboloTurno: String?): Boolean {
        val total = Tablero.TAMANO * Tablero.TAMANO
        if (tableroSerializado == null || tableroSerializado.length != total) return false
        if (simboloTurno != Jugador.SIMBOLO_X && simboloTurno != Jugador.SIMBOLO_O) return false
        if (tableroSerializado.any { it != 'X' && it != 'O' && it != '-' }) return false

        tablero.limpiar()
        tableroSerializado.forEachIndexed { indice, caracter ->
            if (caracter != '-') {
                tablero.obtenerCasilla(indice / Tablero.TAMANO, indice % Tablero.TAMANO)
                    .asignarValor(caracter.toString())
            }
        }
        jugadorActual = if (simboloTurno == jugador1.simbolo) jugador1 else jugador2

        val simboloGanador = detectorVictoria.verificarGanador(tablero)
        ganador = when (simboloGanador) {
            jugador1.simbolo -> jugador1
            jugador2.simbolo -> jugador2
            else -> null
        }
        juegoActivo = simboloGanador == null && !tablero.estaLleno()
        return true
    }
}
