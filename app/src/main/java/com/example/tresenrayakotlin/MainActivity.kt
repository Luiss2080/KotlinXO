package com.example.tresenrayakotlin

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.tresenrayakotlin.modelo.Juego
import com.example.tresenrayakotlin.modelo.EstadisticasJuego

/**
 * Actividad principal optimizada del juego de Tres en Raya
 */
class MainActivity : AppCompatActivity() {

    private var juego: Juego? = null
    private var estadisticas: EstadisticasJuego? = null
    private var textoMensajeEstado: TextView? = null
    private var textoMensajeSecundario: TextView? = null
    private var cardMensajeEstado: View? = null
    private var botonReiniciarJuego: Button? = null
    private var botonReiniciarStats: Button? = null
    private var botonesCasillas: Array<Array<Button?>>? = null
    private var textoVictoriasX: TextView? = null
    private var textoVictoriasO: TextView? = null
    private var textoEmpates: TextView? = null
    private var textoPartidasTotales: TextView? = null

    companion object {
        private const val CLAVE_TABLERO = "tablero"
        private const val CLAVE_TURNO = "turno"
    }

    private val mensajesVictoriaX = listOf(
        "JUGADOR X DOMINA EL TABLERO",
        "X STRIKES AGAIN",
        "VICTORIA ÉPICA DE X",
        "X ES IMPARABLE",
        "MAESTRÍA TOTAL DE X"
    )

    private val mensajesVictoriaO = listOf(
        "JUGADOR O CONQUISTA",
        "O RULES THE GAME",
        "TRIUNFO ESPECTACULAR DE O",
        "O ES INVENCIBLE",
        "DOMINIO ABSOLUTO DE O"
    )

    private val mensajesEmpate = listOf(
        "BATALLA ÉPICA TERMINADA EN EMPATE",
        "NADIE PUEDE VENCER AL OTRO",
        "EQUILIBRIO PERFECTO",
        "EMPATE DE CAMPEONES",
        "FUERZA EQUIVALENTE"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_principal)
        initGame(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        juego?.let {
            outState.putString(CLAVE_TABLERO, it.serializarTablero())
            outState.putString(CLAVE_TURNO, it.obtenerJugadorActual().simbolo)
        }
    }

    private fun initGame(estadoGuardado: Bundle? = null) {
        try {
            juego = Juego()
            estadisticas = EstadisticasJuego(this)
            initViews()
            setupEventListeners()
            restaurarPartida(estadoGuardado)
            updateUI()
            updateStats()
        } catch (e: Exception) {
            handleError("Error inicializando el juego", e)
        }
    }

    /**
     * Recupera la partida en curso tras recrearse la actividad (modo oscuro, idioma, etc.).
     * Las estadísticas ya persisten en SharedPreferences y no se vuelven a contabilizar.
     */
    private fun restaurarPartida(estado: Bundle?) {
        val game = juego ?: return
        if (estado == null) return
        if (!game.restaurarEstado(estado.getString(CLAVE_TABLERO), estado.getString(CLAVE_TURNO))) return

        when {
            game.hayGanador() -> showWinnerWithAnimation(game.obtenerGanador()?.esJugadorX() == true)
            game.esEmpate() -> showDrawWithAnimation()
        }
    }

    private fun initViews() {
        textoMensajeEstado = findViewById(R.id.textoMensajeEstado)
        textoMensajeSecundario = findViewById(R.id.textoMensajeSecundario)
        cardMensajeEstado = findViewById(R.id.cardMensajeEstado)
        botonReiniciarJuego = findViewById(R.id.botonReiniciarJuego)
        botonReiniciarStats = findViewById(R.id.botonReiniciarStats)

        textoVictoriasX = findViewById(R.id.textoVictoriasX)
        textoVictoriasO = findViewById(R.id.textoVictoriasO)
        textoEmpates = findViewById(R.id.textoEmpates)
        textoPartidasTotales = findViewById(R.id.textoPartidasTotales)

        val buttons = Array(3) { Array<Button?>(3) { null } }
        val buttonIds = arrayOf(
            arrayOf(R.id.botonCasilla00, R.id.botonCasilla01, R.id.botonCasilla02),
            arrayOf(R.id.botonCasilla10, R.id.botonCasilla11, R.id.botonCasilla12),
            arrayOf(R.id.botonCasilla20, R.id.botonCasilla21, R.id.botonCasilla22)
        )

        for (row in 0 until 3) {
            for (col in 0 until 3) {
                buttons[row][col] = findViewById(buttonIds[row][col])
            }
        }
        botonesCasillas = buttons
    }

    private fun setupEventListeners() {
        try {
            botonesCasillas?.let { buttons ->
                for (row in 0 until 3) {
                    for (col in 0 until 3) {
                        buttons[row][col]?.setOnClickListener {
                            handleCellClick(row, col)
                        }
                    }
                }
            }

            botonReiniciarJuego?.setOnClickListener { resetGame() }
            botonReiniciarStats?.setOnClickListener { showResetStatsDialog() }
        } catch (e: Exception) {
            handleError("Error configurando eventos", e)
        }
    }

    private fun handleCellClick(row: Int, col: Int) {
        try {
            val game = juego ?: return

            if (game.realizarMovimiento(row, col)) {
                updateUI()

                when {
                    game.hayGanador() -> {
                        val winner = game.obtenerGanador()
                        if (winner?.esJugadorX() == true) {
                            estadisticas?.registrarVictoriaX()
                            showWinnerWithAnimation(true)
                        } else {
                            estadisticas?.registrarVictoriaO()
                            showWinnerWithAnimation(false)
                        }
                        updateStats()
                    }
                    game.esEmpate() -> {
                        estadisticas?.registrarEmpate()
                        showDrawWithAnimation()
                        updateStats()
                    }
                }
            }
        } catch (e: Exception) {
            handleError("Error procesando movimiento", e)
        }
    }

    private fun updateUI() {
        try {
            val game = juego ?: return

            botonesCasillas?.let { buttons ->
                for (row in 0 until 3) {
                    for (col in 0 until 3) {
                        val button = buttons[row][col]
                        val symbol = game.obtenerSimboloEnCasilla(row, col)
                        button?.text = symbol
                        button?.isEnabled = symbol.isEmpty() && game.estaJuegoActivo()

                        when (symbol) {
                            "X" -> button?.setTextColor(getColor(R.color.player_x_color))
                            "O" -> button?.setTextColor(getColor(R.color.player_o_color))
                            else -> button?.setTextColor(getColor(R.color.primary_text))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            handleError("Error actualizando interfaz", e)
        }
    }

    private fun showWinnerWithAnimation(isPlayerX: Boolean) {
        try {
            val message = if (isPlayerX) {
                mensajesVictoriaX.random()
            } else {
                mensajesVictoriaO.random()
            }

            val stats = estadisticas!!
            val secondaryMessage = if (isPlayerX) {
                "Victorias totales de X: ${stats.obtenerVictoriasX()}"
            } else {
                "Victorias totales de O: ${stats.obtenerVictoriasO()}"
            }

            showAnimatedMessage(message, secondaryMessage)
            disableAllButtons()
        } catch (e: Exception) {
            handleError("Error mostrando ganador", e)
        }
    }

    private fun showDrawWithAnimation() {
        try {
            val message = mensajesEmpate.random()
            val stats = estadisticas!!
            val secondaryMessage = "Empates totales: ${stats.obtenerEmpates()}"

            showAnimatedMessage(message, secondaryMessage)
            disableAllButtons()
        } catch (e: Exception) {
            handleError("Error mostrando empate", e)
        }
    }

    private fun showAnimatedMessage(message: String, secondary: String) {
        textoMensajeEstado?.text = message
        textoMensajeSecundario?.text = secondary
        cardMensajeEstado?.visibility = View.VISIBLE
        cardMensajeEstado?.alpha = 0f
        cardMensajeEstado?.animate()?.alpha(1f)?.duration = 500
    }

    private fun updateStats() {
        try {
            val stats = estadisticas ?: return
            textoVictoriasX?.text = stats.obtenerVictoriasX().toString()
            textoVictoriasO?.text = stats.obtenerVictoriasO().toString()
            textoEmpates?.text = stats.obtenerEmpates().toString()
            textoPartidasTotales?.text = "PARTIDAS JUGADAS: ${stats.obtenerPartidasTotales()}"
        } catch (e: Exception) {
            handleError("Error actualizando estadísticas", e)
        }
    }

    private fun disableAllButtons() {
        botonesCasillas?.let { buttons ->
            for (row in 0 until 3) {
                for (col in 0 until 3) {
                    buttons[row][col]?.isEnabled = false
                }
            }
        }
    }

    private fun resetGame() {
        try {
            juego = Juego()
            cardMensajeEstado?.visibility = View.GONE
            updateUI()
        } catch (e: Exception) {
            handleError("Error reiniciando juego", e)
        }
    }

    private fun showResetStatsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Reiniciar Estadísticas")
            .setMessage("¿Estás seguro de que quieres reiniciar todas las estadísticas?")
            .setPositiveButton("Sí") { _, _ ->
                estadisticas?.reiniciarEstadisticas()
                updateStats()
                Toast.makeText(this, "Estadísticas reiniciadas", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun handleError(message: String, e: Exception) {
        e.printStackTrace()
        Toast.makeText(this, "$message: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
