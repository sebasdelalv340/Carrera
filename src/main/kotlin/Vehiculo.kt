
/**
 * Clase abstracta que representa un vehículo genérico.
 * @param nombre El nombre del vehículo.
 * @param marca La marca del vehículo.
 * @param modelo El modelo del vehículo.
 * @param capacidadCombustible La capacidad de combustible del vehículo en litros.
 * @param combustibleActual El nivel actual de combustible del vehículo en litros.
 * @param kilometrosActuales Los kilómetros totales recorridos por el vehículo.
 */
open class Vehiculo(nombre: String, val marca: String, val modelo: String, capacidadCombustible: Float, combustibleActual: Float, var kilometrosActuales: Float) {
    // Nombre del vehículo. Se valida que no esté duplicado.
    val nombre: String = requireNombre(nombre)

    open var paradas: Int = 0

    open val capacidadCombustible: Float = capacidadCombustible.redondeo()
    open var combustibleActual: Float = combustibleActual.redondeo()

    init{
        // Verifica que la capacidad de combustible, el nivel de combustible y los kilómetros actuales no sean negativos.
        require(capacidadCombustible > 0) {"La capacidad debe ser mayor a cero."}
        require(combustibleActual >= 0) {"El combustible no puede ser negativo."}
        require(kilometrosActuales >= 0) {"El kilometraje no puede ser negativo."}
    }

    companion object {
        // Constante que representa la cantidad de kilómetros que puede recorrer el vehículo por litro de combustible.
        const val KM_LITRO: Float = 10.0f

        // Conjunto mutable que almacena los nombres de los vehículos para validar duplicados.
        private var listanombre: MutableSet<String> = mutableSetOf()

        /**
         * Función de validación para el nombre del vehículo.
         * @param nombre El nombre del vehículo a validar.
         * @return El nombre del vehículo si no está duplicado.
         * @throws IllegalArgumentException Si el nombre ya existe.
         */
        fun requireNombre(nombre: String): String {
            require(!listanombre.contains(nombre)) {"El $nombre ya existe."}
            listanombre.add(nombre)
            return nombre
        }
    }

    /**
     * Obtiene la información básica del vehículo.
     * @return Información sobre la marca, modelo y autonomía del vehículo.
     */
    open fun obtenerInfo(): String {
        return "El $marca $modelo puede recorrer ${calcularAutonomia()} km."
    }

    /**
     * Calcula la autonomía del vehículo en kilómetros.
     * @return La autonomía del vehículo en kilómetros.
     */
    open fun calcularAutonomia(): Float {
        return combustibleActual * KM_LITRO
    }

    /**
     * Realiza un viaje con el vehículo.
     * @param distancia La distancia del viaje en kilómetros.
     * @return La distancia restante que no pudo ser recorrida debido a la falta de combustible.
     */
    open fun realizaViaje(distancia: Float): Float {
        if (distancia >= calcularAutonomia()) {
            val kmRecorre = calcularAutonomia()
            combustibleActual = 0.0f
            kilometrosActuales += calcularAutonomia()
            return distancia - kmRecorre
        } else {
            combustibleActual -= (distancia / KM_LITRO)
            kilometrosActuales += distancia
            return  0.0f
        }
    }

    /**
     * Reposta combustible al vehículo.
     * @param cantidad La cantidad de combustible a repostar en litros.
     * @return La cantidad de combustible que se pudo repostar.
     */
    fun repostar(cantidad: Float): Float {
        val combustiblePrevio = combustibleActual
        if ((combustibleActual + cantidad) >= capacidadCombustible) {
            combustibleActual = capacidadCombustible
            return capacidadCombustible - combustiblePrevio
        } else {
            combustibleActual += cantidad
            return cantidad
        }
    }

    /**
     * Redondea un número de tipo Float a dos decimales.
     * @param num El número a redondear.
     * @return El número redondeado.
     */
    private fun Float.redondeo(): Float {
        return (Math.round(this * 100.0f) / 100.0f)
    }
}

