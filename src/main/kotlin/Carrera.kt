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
class Carrera(private val nombreCarrera: String,
              private val distanciaTotal: Float,
              private val participantes: List<Vehiculo> = listOf(),
              private var estadoCarrera: Boolean) {

    private var historialAcciones: MutableMap<String, MutableList<String>> = mutableMapOf()
    private var posiciones: MutableMap<String, Float> = mutableMapOf()

    init {
        require(distanciaTotal >= 1000) { "La distancia total de la carrera debe ser al menos 1000 km." }
        participantes.forEach { vehiculo -> inicializaDatosParticipante(vehiculo) }
    }

    /*
    * Contiene la constante de los kilómetros por tramo.
     */
    companion object {
        const val KM_TRAMO = 20f
    }

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
     * Proporciona una representación en cadena de texto de la instancia de la carrera, incluyendo detalles clave como
     * el nombre de la carrera, la distancia total a recorrer, la lista de participantes, el estado actual de la carrera
     * (en curso o finalizada), el historial de acciones realizadas por los vehículos durante la carrera y las posiciones
     * actuales de los participantes.
     *
     * @return Una cadena de texto que describe los atributos principales de la carrera, incluyendo el nombre,
     * distancia total, participantes, estado actual, historial de acciones y posiciones de los vehículos participantes.
     */
    override fun toString(): String {
        return "NombreCarrera: $nombreCarrera, DistanciaTotal: $distanciaTotal, Participantes: $participantes, EstadoCarrera: $estadoCarrera, HistorialAcciones: $historialAcciones, Posiciones: $posiciones." }

    /**
     * Inicializa los datos de un participante en la carrera, preparando su historial de acciones y estableciendo
     * su posición inicial. Este método se llama automáticamente al agregar un nuevo vehículo a la carrera.
     *
     * @param vehiculo El [Vehiculo] cuyos datos se inicializan.
     */
    private fun inicializaDatosParticipante(vehiculo: Vehiculo) {
        historialAcciones[vehiculo.nombre] = mutableListOf()
        posiciones[vehiculo.nombre] = 0f
    }

    /**
     * Función para iniciar la carrera.
     * Esta función ejecuta la carrera hasta que se determina un ganador.
     */
    fun iniciarCarrera() {
        estadoCarrera = true
        println("¡Comienza la carrera!")
        while (estadoCarrera) {
            val vehiculo = participantes.random()
            avanzarVehiculo(vehiculo)
            actualizarPosiciones(vehiculo)
            determinarGanador()
        }
        mostrarClasificacion()
    }

    /**
    * Muestra la clasificación por posición.
     */
    private fun mostrarClasificacion() {
        println("\n* Clasificación:\n")
        var posicion = 1
        posiciones.toList().sortedByDescending { it.second }.forEach { (nombre, kilometros) ->
            println("${posicion++} -> $nombre ($kilometros kms)")
        }
        println()
    }
    /**
    * El vehículo avanza una distancia y registra la acción.
    *
    * @param vehiculo El vehiculo que avanza.
     * @param distanciaTramo La distancia que avanza el vehiculo.
     */
    private fun avanzarTramo(vehiculo: Vehiculo, distanciaTramo: Float) {
        var distanciaNoRecorrida = vehiculo.realizaViaje(distanciaTramo).redondeo()
        registraAccion(vehiculo.nombre, "${vehiculo.nombre} ha recorrido ${distanciaTramo - distanciaNoRecorrida} km."
        )
        while (distanciaNoRecorrida > 0) {

            repostarVehiculo(vehiculo)

            val distanciaRestante = distanciaNoRecorrida
            distanciaNoRecorrida = vehiculo.realizaViaje(distanciaRestante).redondeo()
            registraAccion(vehiculo.nombre, "${vehiculo.nombre} ha recorrido ${distanciaRestante - distanciaNoRecorrida} km.")
        }
    }

    /**
     * Obtiene una distancia aleatoria entre 10 y 200 kilómetros.
     *
     * @param vehiculo El vehículo que recorre dicha distancia.
     * @return La distancia aleatoria.
     */
    private fun obtenerDistanciaAleatoria(vehiculo: Vehiculo): Float {
        val distanciaAleatoria = (1000..20000).random().toFloat() / 100

        return if (distanciaAleatoria + vehiculo.kilometrosActuales > distanciaTotal) {
            distanciaTotal - vehiculo.kilometrosActuales
        } else {
            distanciaAleatoria
        }
    }

    /**
     * Avanza un vehículo en la carrera.
     *
     * @param vehiculo El vehículo que avanza.
     */
    private fun avanzarVehiculo(vehiculo: Vehiculo) {
        val distanciaAleatoria = obtenerDistanciaAleatoria(vehiculo).redondeo()
        val numTramos = (distanciaAleatoria / KM_TRAMO).toInt()
        val distanciaRestante = distanciaAleatoria % KM_TRAMO

        for (i in (1..numTramos)) {
            avanzarTramo(vehiculo, KM_TRAMO)
            realizarFiligrana(vehiculo)
        }
        avanzarTramo(vehiculo, distanciaRestante)
    }


    /**
     * Realiza el repostaje de combustible a un vehículo.
     *
     * @param vehiculo El vehículo que realiza el repostaje.
     * @param cantidad La cantidad de combustible a repostar.
     */
    /*private fun repostarVehiculo(vehiculo: Vehiculo, cantidad: Float) {
        vehiculo.repostar(cantidad)
        val accionRespostar = "${vehiculo.nombre} ha repostado $cantidad litros."
        registraAccion(vehiculo.nombre, accionRespostar)
        vehiculo.paradas++
    }*/

    /**
     * Realiza el repostaje de combustible completo a un vehículo.
     *
     * @param vehiculo El vehículo que realiza el repostaje.
     */
    private fun repostarVehiculo(vehiculo: Vehiculo) {
        val combustibleRepostado = vehiculo.repostar().redondeo()

        val accionRespostar = "${vehiculo.nombre} ha repostado $combustibleRepostado litros."

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
                vehiculo.realizaDerrape().redondeo()
                registraAccion(vehiculo.nombre, "Derrape: Combustible restante ${vehiculo.combustibleActual} L.")
            }

            is Motocicleta -> {
                vehiculo.realizaCaballito().redondeo()
                registraAccion(vehiculo.nombre, "Caballito: Combustible restante ${vehiculo.combustibleActual} L.")
            }
        }
    }

    /**
     * Actualiza las posiciones de los vehículos en la carrera.
     */
    private fun actualizarPosiciones(vehiculo: Vehiculo) {
        posiciones[vehiculo.nombre] = vehiculo.kilometrosActuales.redondeo()
    }

    /**
     * Determina al ganador de la carrera.
     */
    private fun determinarGanador() {
        for (vehiculo in participantes) {
            if (vehiculo.kilometrosActuales >= distanciaTotal) {
                estadoCarrera = false
                break
            }
        }

        for ((nombre, km) in posiciones) {
            if (km >= distanciaTotal) {
                println("\n¡Carrera finalizada!")
                println("\n$nombre gana la carrera.\n")
            }
        }
    }


    /**
     * Obtiene los resultados finales de la carrera para todos los vehículos.
     *
     * @return Una lista de resultados de la carrera para cada vehículo.
     */
    fun obtenerResultados(): MutableList<ResultadoCarrera> {
        val listaParticipantes = participantes.sortedByDescending { it.kilometrosActuales }
        val resultadoCarrera: MutableList<ResultadoCarrera> = mutableListOf()
        for (vehiculo in listaParticipantes) {
            resultadoCarrera.add(
                ResultadoCarrera(
                    vehiculo,
                    listaParticipantes.indexOf(vehiculo),
                    vehiculo.kilometrosActuales,
                    vehiculo.paradas,
                    historialAcciones[vehiculo.nombre]
                )
            )
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

    /**
     * Muestra el historial de acciones de cada vehículo según la clasificación.
     */
    fun obtenerHistorialDetallado() {
        println("\n* Historial Detallado:\n")
        var posicion = 1
        obtenerResultados().forEach {
            print("${posicion++} -> ")
            println(it.vehiculo.nombre + "\n" + (it.historialAcciones?.joinToString("\n")))
            println()
        }
    }
}