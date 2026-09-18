# KotlinXO

Juego de **Tres en Raya** (tres en línea) para Android, escrito en **Kotlin**, para **dos jugadores en el mismo dispositivo**. X empieza siempre; los jugadores se turnan tocando casillas vacías.

> Estado del proyecto: aplicación pequeña de aprendizaje/portafolio. La lógica del juego tiene tests unitarios; no está publicada ni pensada para producción (ver [Limitaciones](#limitaciones)).

## Características (verificadas en el código)

- Partida de dos jugadores por turnos en un tablero de 3x3, orientación **vertical** (bloqueada en el manifiesto).
- Detección de victoria en las **8 líneas** posibles (3 filas, 3 columnas, 2 diagonales).
- Detección de **empate**: solo con el tablero lleno y sin ganador. Ganar con la novena jugada es victoria, no empate.
- No se puede jugar en una casilla ocupada, fuera del tablero ni una vez terminada la partida (lo garantiza el modelo; además la interfaz deshabilita las casillas).
- **Estadísticas persistentes** (victorias de X, victorias de O, empates y partidas totales) guardadas con `SharedPreferences`; sobreviven al cierre de la app. El botón de reinicio de estadísticas pide confirmación.
- Botón para empezar una **nueva partida** sin tocar las estadísticas.
- Mensajes de fin de partida elegidos al azar de una lista fija.
- La partida en curso sobrevive a la recreación de la actividad (por ejemplo, cambiar entre modo claro y oscuro o de idioma): tablero y turno se guardan en `onSaveInstanceState`.

No hay inteligencia artificial: **no existe modo de un jugador** contra la máquina.

## Arquitectura

Paquete `com.example.tresenrayakotlin`. La lógica está separada de la interfaz, pero **no es un MVC completo**: no hay capa de controladores; `MainActivity` hace de vista y controlador a la vez.

| Parte | Archivos | Responsabilidad |
|-------|----------|-----------------|
| Modelo puro (`modelo`) | `Juego`, `Tablero`, `Casilla`, `Jugador`, `DetectorVictoria`, `ValidadorMovimiento` | Reglas y estado. Sin dependencias de Android; se prueba con JUnit en la JVM. |
| Modelo con Android | `EstadisticasJuego` | Persistencia de estadísticas en `SharedPreferences` (necesita `Context`). |
| Vista + control | `MainActivity`, `res/layout/actividad_principal.xml` | Dibuja el estado, captura los toques y llama al modelo. |

`Juego` coordina `Tablero`, `ValidadorMovimiento` y `DetectorVictoria`, y sabe serializar/restaurar su estado (9 caracteres `X`/`O`/`-`).

## Requisitos

- Android Studio reciente, **o** línea de comandos con:
- **JDK 17** (necesario para Android Gradle Plugin 8.x).
- Android SDK con la plataforma **API 33** (`compileSdk` = `targetSdk` = 33; `minSdk` = 24, Android 7.0).
- Gradle 8.11.1 lo descarga el wrapper; AGP 8.7.3 y Kotlin 2.1.0 (ver `gradle/libs.versions.toml`).

## Compilar y ejecutar

```bash
git clone <URL-del-repositorio>
cd TresEnRayaKotlin
./gradlew assembleDebug        # APK en app/build/outputs/apk/debug/
./gradlew installDebug         # instala en un dispositivo/emulador conectado
```

El build de *debug* usa el sufijo `.debug` en el `applicationId`. También puedes abrir el proyecto en Android Studio y pulsar **Run**. Si hace falta, crea `local.properties` con `sdk.dir=<ruta-a-tu-SDK>` (está ignorado por git).

## Tests

Tests unitarios de la lógica, en la JVM y sin emulador:

```bash
./gradlew test
```

Cubren `DetectorVictoria` (las 8 líneas para X y O, líneas incompletas o mezcladas, empate y victoria en la última casilla), `Juego` (turnos, casillas ocupadas o fuera de rango, bloqueo tras terminar, ganador, reinicio, serialización del estado), `Tablero`, `ValidadorMovimiento` y `Jugador`.

**No** hay tests de `MainActivity` ni de `EstadisticasJuego` (requieren Android); solo queda el `ExampleInstrumentedTest` de la plantilla.

Integración continua: `.github/workflows/ci.yml` ejecuta `./gradlew test` con JDK 17 en cada push y pull request.

## Limitaciones

- Solo dos jugadores locales; sin IA, sonidos, animaciones de movimiento ni multijugador en línea.
- La interfaz **no indica de quién es el turno** (los recursos `turno_jugador_x/o` existen pero no se usan); solo se ve el símbolo tras jugar.
- Solo modo vertical y tema claro (`Theme.AppCompat.Light`).
- Varios textos de la interfaz (mensajes de fin de partida, diálogos) están escritos directamente en el código Kotlin, no en `strings.xml`; solo hay un idioma (español).
- Las versiones de las dependencias en `app/build.gradle.kts` están escritas a mano y no usan el catálogo de versiones de `gradle/libs.versions.toml`.
- El `applicationId` es el de plantilla (`com.example.tresenrayakotlin`); el build de *release* no está firmado ni minificado.
- Solo se ha verificado la lógica del modelo con tests en la JVM; la compilación completa de la app y el comportamiento en un dispositivo real dependen de tu entorno Android.

## Licencia

Este repositorio **no incluye ningún archivo de licencia**. Sin licencia explícita, todos los derechos quedan reservados por el autor y no se concede permiso de uso, copia ni redistribución. Si quieres que otros puedan reutilizarlo, añade un archivo `LICENSE` (por ejemplo MIT o Apache-2.0).
