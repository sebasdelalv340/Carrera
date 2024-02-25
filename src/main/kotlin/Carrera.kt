import kotlin.random.Random

/**
 * Clase que representa una carrera entre varios vehículos.
 *
 * @property nombreCarrera El nombre de la carrera.
 * @property distanciaTotal La distancia total de la carrera en kilómetros.
 * @property participantes La lista de vehículos que participan en la carrera.
 * @property estadoCarrera Indica si la carrera está en curso o ha finalizado.
 * @property historialAcciones Un mapa que almacena el historial de acciones realizadas por cada vehículo durante la carrera.
 * @property posiciones Una lista mutable de pares que representa las posiciones actuales de los vehículos en la carrera.
 */
class Carrera(val nombreCarrera: String, val distanciaTotal: Float, val participantes: List<Vehiculo>, var estadoCarrera: Boolean) {

    var historialAcciones: MutableMap<String, MutableList<String>> = mutableMapOf()
    var posiciones: MutableList<Pair<String, Int>> = mutableListOf()

    /**
     * Representa el resultado final de un vehículo en la carrera, incluyendo su posición final, el kilometraje total recorrido,
     * el número de paradas para repostar, y un historial detallado de todas las acciones realizadas durante la carrera.
     *
     * @property vehiculo El [Vehiculo] al que pertenece este resultado.
     * @property posicion La posición final del vehículo en la carrera, donde una posición menor indica un mejor rendimiento.
     * @property kilometraje El total de kilómetros recorridos por el vehículo durante la carrera.
     * @property paradasRepostaje El número de veces que el vehículo tuvo que repostar combustible durante la carrera.
     * @property historialAcciones Una lista de cadenas que describen las acciones realizadas por el vehículo a lo largo de la carrera, proporcionando un registro detallado de su rendimiento y estrategias.
     */
    data class ResultadoCarrera(
        val vehiculo: Vehiculo,
        val posicion: Int,
        val kilometraje: Float,
        val paradasRepostaje: Int,
        val historialAcciones: MutableList<String>?
    )


    /**
     * Función para iniciar la carrera.
     * Esta función ejecuta la carrera hasta que se determina un ganador.
     */
    fun iniciarCarrera() {
        estadoCarrera = true

        while (estadoCarrera) {
            determinarGanador()
            avanzarVehiculo(participantes.random())
            actualizarPosiciones()
        }

        println("* Clasificación:")
        for (posicion in posiciones) {
            println("${posiciones.indexOf(posicion)} -> ${posicion.first} (${posicion.second} kms)")
        }
        println(obtenerResultados())
    }

    /**
     * Avanza un vehículo en la carrera.
     *
     * @param vehiculo El vehículo que avanza.
     */
    private fun avanzarVehiculo(vehiculo: Vehiculo) {
        val distanciaAleatoria: Float = (1000..20000).random().toFloat() / 100
        val iteraciones = (distanciaAleatoria / 20).toInt()
        val resto = distanciaAleatoria % 20

        for (i in (0..iteraciones)) {
            if (vehiculo.combustibleActual == 0f) {
                val cantidad = (10..20).random().toFloat()
                repostarVehiculo(vehiculo, cantidad)
            }
            vehiculo.realizaViaje(20.0f)
            registraAccion(vehiculo.nombre, "${vehiculo.nombre} ha recorrido 20 km.")
            realizarFiligrana(vehiculo)
        }
        vehiculo.realizaViaje(resto)
        registraAccion(vehiculo.nombre, "${vehiculo.nombre} ha recorrido $resto km.")
    }


    /**
     * Realiza el repostaje de combustible a un vehículo.
     *
     * @param vehiculo El vehículo que realiza el repostaje.
     * @param cantidad La cantidad de combustible a repostar.
     */
    private fun repostarVehiculo(vehiculo: Vehiculo, cantidad: Float) {
        vehiculo.repostar(cantidad)
        val accionRespostar = "${vehiculo.nombre} ha repostado $cantidad litros."
        registraAccion(vehiculo.nombre, accionRespostar)
        vehiculo.paradas++
    }


    /**
     * Realiza una acción de filigrana con el vehículo.
     *
     * @param vehiculo El vehículo que realiza la filigrana.
     */
    private fun realizarFiligrana(vehiculo: Vehiculo) {
        when (vehiculo) {
            is Automovil -> {
                vehiculo.realizaDerrape()
                registraAccion(vehiculo.nombre, "Derrape: Combustible restante ${vehiculo.combustibleActual} L.")
            }
            is Motocicleta -> {
                vehiculo.realizaCaballito()
                registraAccion(vehiculo.nombre, "Caballito: Combustible restante ${vehiculo.combustibleActual} L.")
            }
        }
    }

    /**
     * Actualiza las posiciones de los vehículos en la carrera.
     */
    private fun actualizarPosiciones() {
        posiciones.sortByDescending { it.second }
    }


    /**
     * Determina al ganador de la carrera.
     */
    private fun determinarGanador() {
        for ((nombre, km) in posiciones) {
            if (km >= distanciaTotal) {
                println("$nombre gana la carrera.")
                estadoCarrera = false
            }
        }
    }


    /**
     * Obtiene los resultados finales de la carrera para todos los vehículos.
     *
     * @return Una lista de resultados de la carrera para cada vehículo.
     */
    private fun obtenerResultados(): MutableList<ResultadoCarrera> {
        participantes.sortedByDescending { it.kilometrosActuales }
        val resultadoCarrera: MutableList<ResultadoCarrera> = mutableListOf()
        for (vehiculo in participantes) {
            resultadoCarrera.add(ResultadoCarrera(vehiculo, participantes.indexOf(vehiculo), vehiculo.kilometrosActuales, vehiculo.paradas, historialAcciones[vehiculo.nombre]))
        }
        return resultadoCarrera
    }

    /**
     * Registra una acción realizada por un vehículo durante la carrera.
     *
     * @param vehiculo El nombre del vehículo.
     * @param accion La acción realizada por el vehículo.
     */
    private fun registraAccion(vehiculo: String, accion: String) {
        val listaAcciones = historialAcciones[vehiculo]
        if (listaAcciones != null) {
            listaAcciones.add(accion)
            historialAcciones[vehiculo] = listaAcciones
        } else {
            historialAcciones[vehiculo] = mutableListOf(accion)
        }
    }
}